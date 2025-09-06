package ddanylenko.status.reservation;

import java.time.LocalDateTime;

public record ErrorResponseDTO (String message, String detailMessage, LocalDateTime errorTime) {
}
