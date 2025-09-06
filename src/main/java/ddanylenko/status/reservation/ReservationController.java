package ddanylenko.status.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class.getName());

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id){
        logger.info("called getReservationById=" + id);
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations(){
        logger.info("called getAllReservations");
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation toCreate){
        logger.info("called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED ).header("test-header").body(reservationService.createReservation(toCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("id") Long id, @RequestBody Reservation toUpdate){
        logger.info("called updateReservation with id={}", id);
        Reservation reservation =  reservationService.updateReservation(id, toUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id){
        logger.info("called deleteReservation id={}", id);
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable("id") Long id){
        logger.info("called approveReservation id={}", id);
        Reservation reservation = reservationService.approveReservation(id);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }
}
