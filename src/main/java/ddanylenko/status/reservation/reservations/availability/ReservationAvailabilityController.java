package ddanylenko.status.reservation.reservations.availability;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations/availability")
public class ReservationAvailabilityController {

    private static final Logger log = LoggerFactory.getLogger(ReservationAvailabilityController.class);
    private final ReservationAvailabilityService reservationAvailabilityService;

    public ReservationAvailabilityController(ReservationAvailabilityService reservationAvailabilityService) {
        this.reservationAvailabilityService = reservationAvailabilityService;
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(@Valid CheckAvailabilityRequest  request ){
        log.info("Called method checkAvailability: request={}", request);
        boolean reservationAvailable = reservationAvailabilityService.isReservationAvailable(request.roomId(), request.startDate(), request.endDate());
        var message = reservationAvailable ? "Room available" : "Room not available";
        var status = reservationAvailable ?  AvailabilityStatus.AVAILABLE : AvailabilityStatus.RESERVED;
        return ResponseEntity.status(200).body(new CheckAvailabilityResponse(message, status));
    }
}
