package ddanylenko.status.reservation.web;

import java.time.LocalDateTime;

public record ErrorResponseDTO (String message, String detailMessage, LocalDateTime errorTime) {
}
