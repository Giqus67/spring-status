package ddanylenko.status.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    //List<ReservationEntity> findAllByReservationStatusIs(ReservationStatus status);
    //List<ReservationEntity> findAllByEndDateAndRoomIdAndStartDate(LocalDate endDate, Long roomId, LocalDate startDate);

    @Query("SELECT r FROM ReservationEntity r WHERE r.reservationStatus = :reservationStatus")
    List<ReservationEntity> findByReservationStatus(ReservationStatus reservationStatus);

    @Query("SELECT r FROM ReservationEntity r WHERE r.roomId = :roomId")
    List<ReservationEntity> findByRoomId(@Param("roomId") Long roomId);

    @Modifying
    @Query("""
            UPDATE ReservationEntity r
            set r.reservationStatus = :status
            where r.id = :id
            """)
    void setStatus(@Param("id") Long id, @Param("status") ReservationStatus status);
}
