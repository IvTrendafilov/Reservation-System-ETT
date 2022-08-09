package group18.eet.reservationsystem.schedule.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DaySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private boolean isClosed;

    @Type(type = "json")
    private List<LocalTime> workingTimes;

    public static DaySchedule fromDayScheduleException(DayScheduleException dayScheduleException) {
        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setClosed(dayScheduleException.isClosed());
        daySchedule.setWorkingTimes(dayScheduleException.getWorkingTimes());
        return daySchedule;
    }
    
    public boolean isValidDateTime(LocalDateTime from, LocalDateTime to) {
        if (this.isClosed) return false;
        LocalTime fromTime = from.toLocalTime();
        LocalTime toTime = to.toLocalTime();
        for (int i = 0; i < workingTimes.size(); i += 2) {
            if (!workingTimes.get(i).isAfter(fromTime) && !workingTimes.get(i + 1).isBefore(fromTime) &&
                    !workingTimes.get(i).isAfter(toTime) && !workingTimes.get(i + 1).isBefore(toTime)) {
                return true;
            }
        }
        return false;
    }
}
