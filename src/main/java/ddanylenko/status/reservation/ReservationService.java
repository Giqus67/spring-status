package ddanylenko.status.reservation;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

//    private final Map<Long, Reservation> reservations = Map.of(1L,new Reservation(1L, 101L, 40L, LocalDate.now(),
//            LocalDate.now().plusDays(5 ),  ReservationStatus.APPROVED), 2L,new Reservation(2L, 102L, 42L, LocalDate.now(),
//            LocalDate.now().plusDays(5 ),  ReservationStatus.APPROVED), 3L,new Reservation(3L, 103L, 45L, LocalDate.now(),
//            LocalDate.now().plusDays(5 ),  ReservationStatus.APPROVED) );

    private final Map<Long, Reservation> reservations;

    private final AtomicLong idCounter;
    public ReservationService() {
        this.reservations = new HashMap<>();
        this.idCounter = new AtomicLong();
    }

    public Reservation getReservationById(Long id) {
        if(!reservations.containsKey(id)) {
            throw new NoSuchElementException("Reservation not found");
        }
        return reservations.get(id);
    }

    public List<Reservation> findAllReservations() {
        return reservations.values().stream().toList();
    }

    public Reservation createReservation(Reservation toCreate) {
        if(toCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if(toCreate.reservationStatus() != null){
            throw new IllegalArgumentException("status should be empty");
        }
        Reservation newReservation = new Reservation(
                idCounter.incrementAndGet(),
                toCreate.userId(),
                toCreate.roomId(),
                toCreate.startDate(),
                toCreate.endDate(),
                ReservationStatus.PENDING);
        reservations.put(newReservation.id(), newReservation);
        return newReservation;
    }
}
