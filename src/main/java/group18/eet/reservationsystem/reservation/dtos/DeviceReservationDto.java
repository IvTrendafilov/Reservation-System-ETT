package group18.eet.reservationsystem.reservation.dtos;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceDto;
import group18.eet.reservationsystem.reservable.device.services.DeviceService;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReservationDto extends ReservationDto {
    private List<Long> devices = new ArrayList<>();
    private List<String> deviceCodes = new ArrayList<>();

    public DeviceReservationDto(DeviceReservation deviceReservation) {
        super(deviceReservation);
        this.devices = deviceReservation.getDevices().stream().map(Device::getId).collect(Collectors.toList());
        this.deviceCodes = deviceReservation.getDevices().stream().map(Device::getCode).collect(Collectors.toList());
    }

    public DeviceReservation toEntity(ReservationService reservationService, DeviceService deviceService) {
        DeviceReservation deviceReservation = new DeviceReservation();
        if (this.getId() != null) deviceReservation = (DeviceReservation) reservationService.findOrNull(this.getId());
        deviceReservation.setFrom(LocalDateTime.parse(this.getFrom(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
        deviceReservation.setTo(LocalDateTime.parse(this.getTo(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
        deviceReservation.setRemarks(this.getRemarks());
        deviceReservation.setDevices(this.getDevices().stream().map(deviceService::findOrNull).collect(Collectors.toList()));
        return deviceReservation;
    }
}

