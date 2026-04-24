package com.mobili.backend.module.notification.service;

import com.mobili.backend.infrastructure.security.authentication.UserPrincipal;
import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.ticket.entity.Ticket;
import com.mobili.backend.module.booking.ticket.repository.TicketRepository;
import com.mobili.backend.module.notification.dto.InboxNotificationResponseDTO;
import com.mobili.backend.module.notification.entity.MobiliInboxNotification;
import com.mobili.backend.module.notification.entity.MobiliNotificationType;
import com.mobili.backend.module.notification.entity.TripChannelMessage;
import com.mobili.backend.module.notification.event.InboxRefreshEvent;
import com.mobili.backend.module.notification.repository.MobiliInboxNotificationRepository;
import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.repository.UserRepository;
import com.mobili.backend.module.user.service.UserService;
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InboxNotificationService {

    private static final DateTimeFormatter FR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MobiliInboxNotificationRepository inboxRepository;
    private final UserService userService;
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void notifyPassengerOnTicket(Ticket ticket) {
        if (ticket.getPassenger() == null) {
            return;
        }
        User recipient = userService.getReference(ticket.getPassenger().getId());
        Trip trip = ticket.getTrip();
        if (trip == null) {
            return;
        }
        String route = shortRoute(trip);
        String when = trip.getDepartureDateTime() != null ? trip.getDepartureDateTime().format(FR) : "";
        String title = "Billet prêt sur " + route;
        String pn = ticket.getPassengerName() != null ? ticket.getPassengerName() : "Passager";
        String sn = ticket.getSeatNumber() != null ? ticket.getSeatNumber() : "—";
        String body = "Votre billet n° " + ticket.getTicketNumber() + " est disponible. Passager " + pn
                + " — siège " + sn + (when.isEmpty() ? "" : " — départ le " + when);
        saveOne(recipient, MobiliNotificationType.TICKET_ISSUED, title, body, trip, null);
    }

    @Transactional
    public void notifyPartnerOnPaidBooking(Booking booking) {
        if (booking.getTrip() == null) {
            return;
        }
        Trip trip = tripRepository.findByIdWithPartnerAndStops(booking.getTrip().getId()).orElse(null);
        if (trip == null) {
            return;
        }
        Partner partner = trip.getPartner();
        String route = shortRoute(trip);
        String details = "Réservation n°" + booking.getId() + " : " + booking.getNumberOfSeats() + " place(s) sur " + route
                + " (client n°" + (booking.getCustomer() != null ? booking.getCustomer().getId() : "-") + ").";

        Long ownerId = null;
        if (partner != null && partner.getOwner() != null) {
            ownerId = partner.getOwner().getId();
            User owner = userService.getReference(ownerId);
            saveOne(owner, MobiliNotificationType.PARTNER_NEW_BOOKING, "Nouvelle réservation payée", details, trip, null);
        }

        if (trip.getStation() != null) {
            List<Long> gareUserIds = userRepository.findGareUserIdsByStationId(trip.getStation().getId());
            for (Long gid : gareUserIds) {
                if (ownerId != null && gid.equals(ownerId)) {
                    continue;
                }
                User gareUser = userService.getReference(gid);
                String gareTitle = "Nouvelle réservation (votre gare)";
                String gareBody = "Une réservation payée concerne un voyage lié à votre gare. " + details;
                saveOne(gareUser, MobiliNotificationType.GARE_STATION_NEW_BOOKING, gareTitle, gareBody, trip, null);
            }
        }
    }

    @Transactional
    public void fanOutChannelMessage(Trip trip, TripChannelMessage message) {
        List<Long> ids = ticketRepository.findDistinctPassengerIdsWithActiveTicket(trip.getId());
        Long authorId = message.getAuthor() != null ? message.getAuthor().getId() : null;
        if (ids.isEmpty()) {
            return;
        }
        if (authorId != null) {
            ids = ids.stream().filter(uid -> !uid.equals(authorId)).toList();
        }
        if (ids.isEmpty()) {
            return;
        }
        String route = shortRoute(trip);
        String title = "Annonce voyage " + route;
        String name = fullName(message.getAuthor());
        String body = (name == null || name.isBlank() ? "Organisateur" : name) + " : " + message.getBody();
        List<MobiliInboxNotification> batch = new ArrayList<>();
        for (Long uid : ids) {
            MobiliInboxNotification n = new MobiliInboxNotification();
            n.setUser(userService.getReference(uid));
            n.setType(MobiliNotificationType.TRIP_CHANNEL_MESSAGE);
            n.setTitle(title);
            n.setBody(body);
            n.setTrip(trip);
            n.setSourceChannelMessage(message);
            batch.add(n);
        }
        inboxRepository.saveAll(batch);
        eventPublisher.publishEvent(new InboxRefreshEvent(new HashSet<>(ids)));
    }

    @Transactional
    public void markRead(Long id, UserPrincipal principal) {
        MobiliInboxNotification n = inboxRepository.findById(id)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Notification introuvable"));
        if (!n.getUser().getId().equals(principal.getUser().getId())) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Accès refusé");
        }
        n.setReadAt(LocalDateTime.now());
        inboxRepository.save(n);
        eventPublisher.publishEvent(new InboxRefreshEvent(Set.of(principal.getUser().getId())));
    }

    @Transactional
    public int markAllRead(UserPrincipal principal) {
        int updated = inboxRepository.markAllReadForUser(principal.getUser().getId(), LocalDateTime.now());
        eventPublisher.publishEvent(new InboxRefreshEvent(Set.of(principal.getUser().getId())));
        return updated;
    }

    @Transactional(readOnly = true)
    public Page<InboxNotificationResponseDTO> listForUser(UserPrincipal principal, Pageable pageable) {
        return inboxRepository
                .findByUserIdOrderByCreatedAtDesc(principal.getUser().getId(), pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long countUnread(UserPrincipal principal) {
        return inboxRepository.countByUserIdAndReadAtIsNull(principal.getUser().getId());
    }

    @Transactional
    public void notifyPartnerGareCom(User recipient,
            com.mobili.backend.module.partnergarecom.entity.PartnerGareComThread thread,
            String title,
            String bodyPreview) {
        MobiliInboxNotification n = new MobiliInboxNotification();
        n.setUser(userService.getReference(recipient.getId()));
        n.setType(MobiliNotificationType.PARTNER_GARE_COM_MESSAGE);
        n.setTitle(title);
        n.setBody(bodyPreview);
        n.setPartnerGareComThread(thread);
        n.setTrip(null);
        n.setSourceChannelMessage(null);
        inboxRepository.save(n);
        eventPublisher.publishEvent(new InboxRefreshEvent(Set.of(recipient.getId())));
    }

    private void saveOne(User user, MobiliNotificationType type, String title, String body, Trip trip,
            TripChannelMessage link) {
        MobiliInboxNotification n = new MobiliInboxNotification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setTrip(trip);
        n.setSourceChannelMessage(link);
        inboxRepository.save(n);
        eventPublisher.publishEvent(new InboxRefreshEvent(Set.of(user.getId())));
    }

    private InboxNotificationResponseDTO toDto(MobiliInboxNotification n) {
        Optional<Trip> t = Optional.ofNullable(n.getTrip());
        return InboxNotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .read(n.getReadAt() != null)
                .createdAt(n.getCreatedAt())
                .tripId(t.map(Trip::getId).orElse(null))
                .tripRoute(t.map(this::shortRoute).orElse(null))
                .channelMessageId(
                        n.getSourceChannelMessage() == null ? null : n.getSourceChannelMessage().getId())
                .partnerGareComThreadId(
                        n.getPartnerGareComThread() == null ? null : n.getPartnerGareComThread().getId())
                .build();
    }

    private String shortRoute(Trip trip) {
        if (trip == null) {
            return "";
        }
        String a = nullToEmpty(trip.getDepartureCity());
        String b = nullToEmpty(trip.getArrivalCity());
        if (a.isEmpty() && b.isEmpty()) {
            return "trajet";
        }
        return a + " → " + b;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private String fullName(User u) {
        if (u == null) {
            return null;
        }
        String a = nullToEmpty(u.getFirstname());
        String c = nullToEmpty(u.getLastname());
        return (a + " " + c).trim();
    }
}