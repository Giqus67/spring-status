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

    public Reservation updateReservation(Long id, Reservation toUpdate) {
        if(!reservations.containsKey(id)){
            throw new NoSuchElementException("Reservation not found by id=" + id);
        }
        Reservation reservation = reservations.get(id);
        if(reservation.reservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot modify reservation status: " + reservation.reservationStatus()) ;
        }
        Reservation updatedReservation = new Reservation(
                id,
                toUpdate.userId(),
                toUpdate.roomId(),
                toUpdate.startDate(),
                toUpdate.endDate(),
                ReservationStatus.PENDING);
        reservations.put(id, updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(Long id) {
        if(!reservations.containsKey(id)) {
            throw new NoSuchElementException("Reservation not found by id=" + id );
        }
        reservations.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if(!reservations.containsKey(id)) {
            throw new NoSuchElementException("Reservation not found by id=" + id );
        }
        var reservation = reservations.get(id);
        if(reservation.reservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot approve reservation status: " + reservation.reservationStatus()) ;
        }
        if(isReservationConflict(reservation)) {
            throw new IllegalArgumentException("Reservation is already in use");
        }
        Reservation approvedReservation = new Reservation(
                id,
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED);
        reservations.put(id, approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflict(Reservation reservation) {
        for(Reservation existing : reservations.values()) {
            if(existing.id().equals(reservation.id())) {
                continue;
            }
            if(!reservation.roomId().equals(existing.roomId())) {
                continue;
            }
            if(!existing.reservationStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if(reservation.startDate().isBefore(existing.endDate())
            && existing.startDate().isBefore(reservation.endDate())) {
                return true;
            }
        }
        return false;
    }
}
