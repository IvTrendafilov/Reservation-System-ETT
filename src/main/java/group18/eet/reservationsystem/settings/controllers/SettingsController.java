package group18.eet.reservationsystem.settings.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import group18.eet.reservationsystem.schedule.services.WeekScheduleService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import group18.eet.reservationsystem.settings.dtos.SettingsDto;
import group18.eet.reservationsystem.settings.services.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SettingsController {
    private final SettingsService settingsService;
    private final WeekScheduleService weekScheduleService;
    private final DayScheduleService dayScheduleService;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER') || hasAuthority('GLOBAL_ADMIN')")
    public SettingsDto findSettings() {
        return new SettingsDto(settingsService.findAll().get(0));
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('GLOBAL_ADMIN')")
    public SettingsDto editSettings(@RequestBody SettingsDto settingsDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new SettingsDto(settingsService.edit(settingsDto.toEntity(settingsService, weekScheduleService, dayScheduleService)));
    }

}
