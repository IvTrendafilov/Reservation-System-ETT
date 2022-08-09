package group18.eet.reservationsystem.schedule.services;

import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import group18.eet.reservationsystem.schedule.repositories.WeekScheduleRepository;
import group18.eet.reservationsystem.utils.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class WeekScheduleService extends BaseService<WeekSchedule, WeekScheduleRepository> {
    private final WeekScheduleRepository weekScheduleRepository;

    @Autowired
    public WeekScheduleService(WeekScheduleRepository weekScheduleRepository) {
        super(weekScheduleRepository);
        this.weekScheduleRepository = weekScheduleRepository;
    }

    public WeekSchedule update(WeekSchedule weekSchedule) {
        if (weekSchedule.getName().trim().equals("") || weekSchedule.getMonday() == null || weekSchedule.getTuesday() == null
                || weekSchedule.getWednesday() == null || weekSchedule.getThursday() == null || weekSchedule.getFriday() == null
                || weekSchedule.getSaturday() == null || weekSchedule.getSunday() == null) {
            throw new RuntimeException("No name supplied or none of the fields are selected ");
        }
        return weekScheduleRepository.save(weekSchedule);
    }
}
