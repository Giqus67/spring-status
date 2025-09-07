package ddanylenko.status.reservation.reservations;

import org.springframework.data.domain.Pageable;
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
            where r.id = :id and :startDate < r.endDate 
            and r.startDate <= :endDate AND r.reservationStatus = :reservationStatus
            """)
    void setStatus(@Param("id") Long id, @Param("status") ReservationStatus status);

    @Query("""
    SELECT r FROM ReservationEntity r 
    WHERE r.roomId = :roomId
""")
    List<Long>  findConflictReservations(@Param("roomId") Long roomId,@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate, @Param("reservationStatus") ReservationStatus status);

    @Query("""
    SELECT r FROM ReservationEntity r 
    WHERE (:roomId IS NULL OR  r.roomId = :roomId) 
    AND (:userId IS NULL OR r.userId = :userId)
""")
    List<ReservationEntity> searchAllByFilter(@Param("roomId") Long roomId, @Param("userId") Long userId, Pageable pageable);
}
