package ddanylenko.status.reservation.reservations;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name ="userId", required = false) Long userId,
            @RequestParam(name ="pageSize", required = false) Integer pageSize,
            @RequestParam(name ="pageNumber", required = false) Integer pageNumber){
        logger.info("called getAllReservations");
        var filter = new ReservationSearchFilter(roomId, userId, pageSize, pageNumber);
        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(@RequestBody @Valid Reservation toCreate){
        logger.info("called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED ).header("test-header").body(reservationService.createReservation(toCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("id") Long id, @RequestBody @Valid Reservation toUpdate){
        logger.info("called updateReservation with id={}", id);
        Reservation reservation =  reservationService.updateReservation(id, toUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id){
        logger.info("called deleteReservation id={}", id);
        reservationService.cancelReservation(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable("id") Long id){
        logger.info("called approveReservation id={}", id);
        Reservation reservation = reservationService.approveReservation(id);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }
}
