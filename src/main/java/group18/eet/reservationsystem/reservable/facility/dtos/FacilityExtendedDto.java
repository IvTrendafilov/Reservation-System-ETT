package group18.eet.reservationsystem.reservable.facility.dtos;

import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.utils.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityExtendedDto extends FacilityDto {
    private FacilityColorState color;
    private List<Tuple<LocalTime, LocalTime>> interferingReservationTimeslots = new ArrayList<>();

    public FacilityExtendedDto(Facility facility, FacilityColorState color) {
        super(facility);
        this.color = color;
    }

    public void addTimeslot(Tuple<LocalTime, LocalTime> tuple) {
        interferingReservationTimeslots.add(tuple);
    }

    public enum FacilityColorState {
        RED,
        BLACK,
        YELLOW
    }
}