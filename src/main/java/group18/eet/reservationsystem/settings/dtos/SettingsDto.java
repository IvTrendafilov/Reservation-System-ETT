package group18.eet.reservationsystem.settings.dtos;

import group18.eet.reservationsystem.schedule.dtos.WeekScheduleDto;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import group18.eet.reservationsystem.schedule.services.WeekScheduleService;
import group18.eet.reservationsystem.settings.entities.Settings;
import group18.eet.reservationsystem.settings.services.SettingsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {
    private Long id;
    private boolean autoAcceptanceOfDeviceReservations;
    private boolean autoAcceptanceOfFacilityReservations;
    private int maxDevicesPerPerson;
    private int maxBookingTimeLength;
    private WeekScheduleDto loungeSchedule;

    public SettingsDto(Settings settings) {
        this.id = settings.getId();
        this.autoAcceptanceOfDeviceReservations = settings.isAutoAcceptanceOfDeviceReservations();
        this.autoAcceptanceOfFacilityReservations = settings.isAutoAcceptanceOfFacilityReservations();
        this.maxDevicesPerPerson = settings.getMaxDevicesPerReservation();
        this.maxBookingTimeLength = settings.getMaxBookingTimeLength();
        this.loungeSchedule = settings.getLoungeSchedule() != null ? new WeekScheduleDto(settings.getLoungeSchedule()) : null;
    }

    public Settings toEntity(SettingsService settingsService, WeekScheduleService weekScheduleService, DayScheduleService dayScheduleService) {
        Settings settings = settingsService.findAll().get(0);
        settings.setAutoAcceptanceOfDeviceReservations(this.autoAcceptanceOfDeviceReservations);
        settings.setAutoAcceptanceOfFacilityReservations(this.autoAcceptanceOfFacilityReservations);
        settings.setMaxDevicesPerReservation(this.maxDevicesPerPerson);
        settings.setMaxBookingTimeLength(this.maxBookingTimeLength);
        settings.setLoungeSchedule(this.loungeSchedule != null ? this.loungeSchedule.toEntity(weekScheduleService, dayScheduleService) : null);
        return settings;
    }
}
