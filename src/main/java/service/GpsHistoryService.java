package service;

import Entidad.GpsHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.GpsHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GpsHistoryService {

    @Autowired
    private GpsHistoryRepository gpsHistoryRepository;

    public void guardarPunto(
            Long tabletId,
            Double latitude,
            Double longitude,
            Double accuracy) {

        GpsHistory gps = new GpsHistory();

        gps.setTabletId(tabletId);
        gps.setLatitude(latitude);
        gps.setLongitude(longitude);
        gps.setAccuracy(accuracy);
        gps.setGpsTimestamp(LocalDateTime.now());

        gpsHistoryRepository.save(gps);
    }

    public List<GpsHistory> obtenerRuta(Long tabletId) {

        return gpsHistoryRepository
                .findTop500ByTabletIdOrderByGpsTimestampDesc(tabletId);
    }
}