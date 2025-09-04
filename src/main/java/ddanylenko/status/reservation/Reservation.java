package ddanylenko.status.reservation;

import java.time.LocalDate;

 public record Reservation(Long id, Long userId, Long roomId,
                   LocalDate startDate, LocalDate endDate, ReservationStatus reservationStatus) {
}
