package org.example.educonnectjavaproject.notifications;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class NotificationCleanUpService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCleanUpService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0  0 0 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        try {
            logger.info("Starting cleanup of old notifications...");

            LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
            Timestamp fiveDaysAgoTimestamp = Timestamp.from(fiveDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
            logger.debug("Deleting notifications older than: {}", fiveDaysAgoTimestamp);

            int deletedStudentNotifications = entityManager.createQuery(
                            "DELETE FROM StudentNotification n WHERE n.date < :fiveDaysAgo"
                    )
                    .setParameter("fiveDaysAgo", fiveDaysAgoTimestamp)
                    .executeUpdate();
            logger.info("Deleted {} student notifications", deletedStudentNotifications);

            int deletedTeacherNotifications = entityManager.createQuery(
                            "DELETE FROM TeacherNotification n WHERE n.date < :fiveDaysAgo"
                    )
                    .setParameter("fiveDaysAgo", fiveDaysAgoTimestamp)
                    .executeUpdate();
            logger.info("Deleted {} teacher notifications", deletedTeacherNotifications);

            logger.info("Cleanup completed successfully.");
        } catch (Exception e) {
            logger.error("Error during notification cleanup: {}", e.getMessage(), e);
            throw e;
        }
    }
}