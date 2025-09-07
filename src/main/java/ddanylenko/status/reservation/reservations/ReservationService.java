package ddanylenko.status.reservation.reservations;

import ddanylenko.status.reservation.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    private final ReservationAvailabilityService availabilityService;

    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper, ReservationAvailabilityService availabilityService) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.availabilityService = availabilityService;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found by id=" + id));
        return reservationMapper.toDomain(entity);
    }

    public List<Reservation> searchAllByFilter(ReservationSearchFilter filter ) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        List<ReservationEntity> reservations = reservationRepository.searchAllByFilter(filter.roomId(), filter.userId(),  pageable);
        return reservations.stream().map(reservationMapper::toDomain).toList();
    }

    public Reservation createReservation(Reservation toCreate) {
        if(toCreate.reservationStatus() != null){
            throw new IllegalArgumentException("status should be empty");
        }
        if(!toCreate.endDate().isBefore(toCreate.startDate())){
            throw new IllegalArgumentException("End date should be after start date");
        }

        var reservationEntity = reservationMapper.toEntityReservation(toCreate);
        reservationEntity.setReservationStatus(ReservationStatus.PENDING);
        reservationEntity.setId(toCreate.id());
        var saved = reservationRepository.save(reservationEntity);
        return reservationMapper.toDomain(saved);
    }

    public Reservation updateReservation(Long id, Reservation toUpdate) {
        var reservationEntity = reservationRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("Reservation not found by id=" + id ));

        if(reservationEntity.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot modify reservation status: " + reservationEntity.getReservationStatus()) ;
        }
        if(!toUpdate.endDate().isBefore(toUpdate.startDate())){
            throw new IllegalArgumentException("End date should be after start date");
        }
        var updatedReservation = reservationMapper.toEntityReservation(toUpdate);
        updatedReservation.setId(reservationEntity.getId());
        updatedReservation.setReservationStatus(ReservationStatus.PENDING);
        var reservation = reservationRepository.save(updatedReservation);
        return reservationMapper.toDomain(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        var reservation = reservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found by id=" + id ));
        if(reservation.getReservationStatus().equals(ReservationStatus.APPROVED)){
            throw new IllegalArgumentException("Cannot cancel approved reservation. Contact Manager");
        }
        if(reservation.getReservationStatus().equals(ReservationStatus.CANCELED)){
            throw new IllegalArgumentException("Cannot cancel canceled reservation.");
        }
        reservationRepository.setStatus(id, ReservationStatus.CANCELED);
        log.info("successfully cancelled reservation id={}", id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = reservationRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("Reservation not found by id=" + id ));

        if(reservationEntity.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot approve reservation status: " + reservationEntity.getReservationStatus()) ;
        }
        var available = availabilityService.isReservationAvailable(reservationEntity.getRoomId(), reservationEntity.getStartDate(), reservationEntity.getEndDate());
        if(!available) {
            throw new IllegalArgumentException("Reservation is already in use");
        }
        reservationEntity.setReservationStatus(ReservationStatus.APPROVED);
        reservationRepository.save(reservationEntity);
        return reservationMapper.toDomain(reservationEntity);
    }


}
