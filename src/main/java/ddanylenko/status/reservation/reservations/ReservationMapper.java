package ddanylenko.status.reservation.reservations;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public Reservation toDomain(ReservationEntity reservationEntity){
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getReservationStatus());
    }
    public ReservationEntity toEntityReservation(Reservation reservation){
        return new ReservationEntity(
                reservation.id(),
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                reservation.reservationStatus());
    }
}
