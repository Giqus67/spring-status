package ddanylenko.status.reservation.reservations.availability;

import ddanylenko.status.reservation.reservations.ReservationRepository;
import ddanylenko.status.reservation.reservations.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {
    private ReservationRepository reservationRepository;

    private static final Logger log = LoggerFactory.getLogger(ReservationAvailabilityService.class);

    public ReservationAvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean isReservationAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<Long> conflictingId = reservationRepository.findConflictReservations(roomId, startDate, endDate, ReservationStatus.APPROVED);
        if(!endDate.isBefore(startDate)){
            throw new IllegalArgumentException("End date should be after start date");
        }
        if(conflictingId.isEmpty()){
            return true;
        }
        log.info("conflict with ids={}", conflictingId);
        return false;
    }
}
