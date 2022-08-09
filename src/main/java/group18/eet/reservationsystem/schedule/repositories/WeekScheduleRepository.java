package group18.eet.reservationsystem.schedule.repositories;

import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekScheduleRepository extends JpaRepository<WeekSchedule, Long> {
    boolean existsByMondayIdOrTuesdayIdOrWednesdayIdOrThursdayIdOrFridayIdOrSundayIdOrSaturdayId(Long monday_id, Long tuesday_id, Long wednesday_id, Long thursday_id, Long friday_id, Long sunday_id, Long saturday_id);
}
