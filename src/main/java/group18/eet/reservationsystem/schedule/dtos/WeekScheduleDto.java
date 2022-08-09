package group18.eet.reservationsystem.schedule.dtos;

import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import group18.eet.reservationsystem.schedule.services.WeekScheduleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekScheduleDto {
    private Long id;
    private String name;
    private DaySchedulesDto monday;
    private DaySchedulesDto tuesday;
    private DaySchedulesDto wednesday;
    private DaySchedulesDto thursday;
    private DaySchedulesDto friday;
    private DaySchedulesDto saturday;
    private DaySchedulesDto sunday;

    public WeekScheduleDto(WeekSchedule weekSchedule) {
        if (weekSchedule == null) throw new RuntimeException("This entity doesnt exist");
        this.id = weekSchedule.getId();
        this.name = weekSchedule.getName();
        this.monday = new DaySchedulesDto(weekSchedule.getMonday());
        this.tuesday = new DaySchedulesDto(weekSchedule.getTuesday());
        this.wednesday = new DaySchedulesDto(weekSchedule.getWednesday());
        this.thursday = new DaySchedulesDto(weekSchedule.getThursday());
        this.friday = new DaySchedulesDto(weekSchedule.getFriday());
        this.saturday = new DaySchedulesDto(weekSchedule.getSaturday());
        this.sunday = new DaySchedulesDto(weekSchedule.getSunday());
    }

    public WeekSchedule toEntity(WeekScheduleService weekScheduleService, DayScheduleService dayScheduleService) {
        WeekSchedule weekSchedule = new WeekSchedule();
        if (this.id != null) weekSchedule = weekScheduleService.findOrNull(id);
        weekSchedule.setName(this.name);
        weekSchedule.setMonday(this.monday.toEntity(dayScheduleService));
        weekSchedule.setTuesday(this.tuesday.toEntity(dayScheduleService));
        weekSchedule.setWednesday(this.wednesday.toEntity(dayScheduleService));
        weekSchedule.setThursday(this.thursday.toEntity(dayScheduleService));
        weekSchedule.setFriday(this.friday.toEntity(dayScheduleService));
        weekSchedule.setSaturday(this.saturday.toEntity(dayScheduleService));
        weekSchedule.setSunday(this.sunday.toEntity(dayScheduleService));
        return weekSchedule;
    }
}
