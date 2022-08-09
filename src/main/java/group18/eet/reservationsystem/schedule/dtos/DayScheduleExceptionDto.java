package group18.eet.reservationsystem.schedule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import group18.eet.reservationsystem.schedule.entities.DayScheduleException;
import group18.eet.reservationsystem.schedule.repositories.DayScheduleExceptionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayScheduleExceptionDto {

    private Long id;
    private String date;
    private List<LocalTime> workingTimes;
    @JsonProperty("isClosed")
    private boolean isClosed;

    public DayScheduleExceptionDto(DayScheduleException dayScheduleException) {
        this.id = dayScheduleException.getId();
        this.date = dayScheduleException.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.workingTimes = dayScheduleException.getWorkingTimes();
        this.isClosed = dayScheduleException.isClosed();
    }

    public DayScheduleException toEntity(DayScheduleExceptionRepository dayScheduleExceptionRepository) {
        DayScheduleException dayScheduleException = new DayScheduleException();
        if (this.id != null) dayScheduleException = dayScheduleExceptionRepository.findById(this.id).orElse(null);
        assert dayScheduleException != null;
        dayScheduleException.setDate(LocalDate.parse(this.date, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dayScheduleException.setWorkingTimes(this.workingTimes);
        dayScheduleException.setClosed(this.isClosed);
        return dayScheduleException;
    }
}
