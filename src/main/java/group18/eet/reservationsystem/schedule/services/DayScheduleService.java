package group18.eet.reservationsystem.schedule.services;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.schedule.repositories.DayScheduleRepository;
import group18.eet.reservationsystem.utils.BaseService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;

@Transactional
@Service
public class DayScheduleService extends BaseService<DaySchedule, DayScheduleRepository> {
    
    private final DayScheduleRepository dayScheduleRepository;
    
    @Autowired
    public DayScheduleService(DayScheduleRepository dayScheduleRepository) {
        super(dayScheduleRepository);
        this.dayScheduleRepository = dayScheduleRepository;
    }

    public DaySchedule update(DaySchedule dayschedule) {
        if (dayschedule.getName().trim().equals("")) {
            throw new RuntimeException("There is no name supplied!");
        }

        if (dayschedule.isClosed()) {
            return dayScheduleRepository.save(dayschedule);
        }

        // here we check whether there are conflicting working times such as 00:00 - 05:00 and then 03:00 - 08:00, this is a conflict between 05 and 03
        for (int index = 0; index < dayschedule.getWorkingTimes().size() - 1; index++) {
            List<LocalTime> workingTimes = dayschedule.getWorkingTimes();
            if (workingTimes.get(index).isAfter(workingTimes.get(index + 1))) {
                throw new RuntimeException("The day schedule contains time incompatibility!");
            }
        }

        return dayScheduleRepository.save(dayschedule);
    }

}
