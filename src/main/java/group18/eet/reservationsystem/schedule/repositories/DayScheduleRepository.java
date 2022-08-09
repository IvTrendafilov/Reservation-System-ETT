package group18.eet.reservationsystem.schedule.repositories;

import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayScheduleRepository extends JpaRepository<DaySchedule, Long> {
}
