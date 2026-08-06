package service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class GpsCleanupService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void limpiarHistorialViejo() {

        int eliminados = entityManager.createNativeQuery("""
            DELETE FROM gps_history
            WHERE gps_timestamp < NOW() - INTERVAL '60 days'
        """).executeUpdate();

        System.out.println("GPS_HISTORY ELIMINADOS = " + eliminados);
    }
}