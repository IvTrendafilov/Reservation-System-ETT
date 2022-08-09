package group18.eet.reservationsystem.schedule.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "monday_id")
    private DaySchedule monday;

    @ManyToOne
    @JoinColumn(name = "tuesday_id")
    private DaySchedule tuesday;

    @ManyToOne
    @JoinColumn(name = "wednesday_id")
    private DaySchedule wednesday;

    @ManyToOne
    @JoinColumn(name = "thursday_id")
    private DaySchedule thursday;

    @ManyToOne
    @JoinColumn(name = "friday_id")
    private DaySchedule friday;

    @ManyToOne
    @JoinColumn(name = "saturday_id")
    private DaySchedule saturday;

    @ManyToOne
    @JoinColumn(name = "sunday_id")
    private DaySchedule sunday;



    public boolean checkScheduleValidity() {
        return this.monday != null && this.tuesday != null && this.wednesday != null &&
                this.thursday != null && this.friday != null && this.saturday != null && this.sunday != null;
    }

    public DaySchedule getDaySchedule(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return getMonday();
            case TUESDAY:
                return getTuesday();
            case WEDNESDAY:
                return getWednesday();
            case THURSDAY:
                return getThursday();
            case FRIDAY:
                return getFriday();
            case SATURDAY:
                return getSaturday();
            case SUNDAY:
                return getSunday();
            default:
                throw new RuntimeException("Non-existant day!");
        }
    }

}
