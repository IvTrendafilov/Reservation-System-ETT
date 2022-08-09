package group18.eet.reservationsystem.schedule.repositories;

import group18.eet.reservationsystem.schedule.entities.DayScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DayScheduleExceptionRepository extends JpaRepository<DayScheduleException, Long>, JpaSpecificationExecutor<DayScheduleException> {
    List<DayScheduleException> findAllByDate(LocalDate date);
}
