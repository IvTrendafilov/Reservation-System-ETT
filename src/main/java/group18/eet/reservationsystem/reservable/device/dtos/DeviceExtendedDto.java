package group18.eet.reservationsystem.reservable.device.dtos;

import group18.eet.reservationsystem.reservable.device.Device;
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
public class DeviceExtendedDto extends DeviceDto {
    private DeviceColorState color;
    private List<Tuple<LocalTime, LocalTime>> interferingReservationTimeslots = new ArrayList<>();

    public DeviceExtendedDto(Device device, DeviceColorState color) {
        super(device);
        this.color = color;
    }

    public void addTimeslot(Tuple<LocalTime, LocalTime> tuple) {
        interferingReservationTimeslots.add(tuple);
    }

    public enum DeviceColorState {
        RED,
        BLACK,
        YELLOW
    }
}
