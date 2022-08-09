package group18.eet.reservationsystem.schedule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DaySchedulesDto {
    private Long id;
    private String name;
    private List<LocalTime> workingTimes;
    @JsonProperty("isClosed")
    private boolean isClosed;

    public DaySchedulesDto(DaySchedule daySchedule) {
        if (daySchedule == null) throw new RuntimeException("This entity doesnt exist");
        this.id = daySchedule.getId();
        this.name = daySchedule.getName();
        this.workingTimes = daySchedule.getWorkingTimes();
        this.isClosed = daySchedule.isClosed();
    }

    public DaySchedule toEntity(DayScheduleService dayScheduleService) {
        DaySchedule daySchedule = new DaySchedule();
        if (id != null) daySchedule = dayScheduleService.findOrNull(id);
        daySchedule.setName(this.name);
        daySchedule.setWorkingTimes(this.workingTimes);
        daySchedule.setClosed(this.isClosed);
        return daySchedule;
    }
}
