package com.mobili.backend.infrastructure.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Synchronise la contrainte CHECK {@code type} (PostgreSQL) avec
 * {@link com.mobili.backend.module.notification.entity.MobiliNotificationType}.
 * Les bases créées avant {@code PARTNER_GARE_COM_MESSAGE} rejetaient l’insert (23514) et
 * l’API renvoyait 409.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class InboxNotificationTypeCheckMigration implements ApplicationRunner {

    private static final String CONSTRAINT = "mobili_inbox_notifications_type_check";
    private static final String TABLE = "mobili_inbox_notifications";

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        if (!isPostgres() || !tableExists()) {
            return;
        }
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        try {
            jdbc.execute("ALTER TABLE " + TABLE + " DROP CONSTRAINT IF EXISTS " + CONSTRAINT);
            jdbc.execute(
                    "ALTER TABLE " + TABLE + " ADD CONSTRAINT " + CONSTRAINT
                            + " CHECK (type IN ("
                            + "'TICKET_ISSUED', 'TRIP_CHANNEL_MESSAGE', 'PARTNER_NEW_BOOKING', "
                            + "'GARE_STATION_NEW_BOOKING', 'PARTNER_GARE_COM_MESSAGE'))");
            log.info("Contrainte {} réalignée sur les types de notification (incl. PARTNER_GARE_COM_MESSAGE).", CONSTRAINT);
        } catch (Exception e) {
            log.warn("Migration contrainte inbox type ignorée : {}", e.getMessage());
        }
    }

    private boolean isPostgres() {
        try (var c = dataSource.getConnection()) {
            return "PostgreSQL".equals(c.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean tableExists() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        try {
            jdbc.queryForList("SELECT 1 FROM " + TABLE + " WHERE 1=0");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
