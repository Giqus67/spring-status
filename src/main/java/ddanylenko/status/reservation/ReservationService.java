package ddanylenko.status.reservation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        return toDomainReservation(entity);
    }

    public List<Reservation> findAllReservations() {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        return reservations.stream().map(this::toDomainReservation).toList();
    }

    public Reservation createReservation(Reservation toCreate) {
        if(toCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if(toCreate.reservationStatus() != null){
            throw new IllegalArgumentException("status should be empty");
        }
        ReservationEntity reservationEntity = new ReservationEntity(
                null,
                toCreate.userId(),
                toCreate.roomId(),
                toCreate.startDate(),
                toCreate.endDate(),
                ReservationStatus.PENDING);
        var saved = reservationRepository.save(reservationEntity);
        return toDomainReservation(saved);
    }

    public Reservation updateReservation(Long id, Reservation toUpdate) {
        var reservationEntity = reservationRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("Reservation not found by id=" + id ));

        if(reservationEntity.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot modify reservation status: " + reservationEntity.getReservationStatus()) ;
        }
        var updatedReservation = new ReservationEntity(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                ReservationStatus.PENDING);
        var reservation = reservationRepository.save(updatedReservation);
        return toDomainReservation(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        if(!reservationRepository.existsById(id)) {
            throw new NoSuchElementException("Reservation not found by id=" + id );
        }
        reservationRepository.setStatus(id, ReservationStatus.CANCELED);
        log.info("successfully cancelled reservation id={}", id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = reservationRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("Reservation not found by id=" + id ));

        if(reservationEntity.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot approve reservation status: " + reservationEntity.getReservationStatus()) ;
        }
        if(isReservationConflict(reservationEntity)) {
            throw new IllegalArgumentException("Reservation is already in use");
        }
        reservationEntity.setReservationStatus(ReservationStatus.APPROVED);
        reservationRepository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(ReservationEntity reservation) {
        List<ReservationEntity> reservations = reservationRepository.findAll();

        for(ReservationEntity existing : reservations) {
            if(existing.getId().equals(reservation.getId())) {
                continue;
            }
            if(!reservation.getRoomId().equals(existing.getRoomId())) {
                continue;
            }
            if(!existing.getReservationStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if(reservation.getStartDate().isBefore(existing.getEndDate())
            && existing.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainReservation(ReservationEntity reservationEntity){
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getReservationStatus());
    }
}
