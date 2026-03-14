package com.mobili.backend.module.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobili.backend.module.admin.dto.AdminStatsResponse;
import com.mobili.backend.module.booking.booking.repository.BookingRepository;
import com.mobili.backend.module.partner.repository.PartnerRepository;
import com.mobili.backend.module.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final BookingRepository bookingRepository;
    // private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse getGlobalStats() {
        long totalUsers = userRepository.count();
        long totalPartners = partnerRepository.count();

        // Exemple de calcul de revenu total (somme des prix des bookings confirmés)
        // Double revenue = bookingRepository.sumTotalRevenue();

        return new AdminStatsResponse(
                totalUsers,
                totalPartners,
                0, // totalTrips (à lier à ton repository de trajets)
                0, // activeBookings
                0.0 // revenue
        );
    }
}
