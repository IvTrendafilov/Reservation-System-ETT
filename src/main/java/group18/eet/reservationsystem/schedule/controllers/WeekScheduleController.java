package group18.eet.reservationsystem.schedule.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.schedule.dtos.WeekScheduleDto;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import group18.eet.reservationsystem.schedule.services.WeekScheduleService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import group18.eet.reservationsystem.settings.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weekschedule")
@PreAuthorize("isAuthenticated()")
public class WeekScheduleController {
    private final WeekScheduleService weekScheduleService;
    private final DayScheduleService dayScheduleService;
    private final EttUserDetailsRepository ettUserDetailsRepository;
    private final SettingsRepository settingsRepository;

    @Autowired
    public WeekScheduleController(WeekScheduleService weekScheduleService, DayScheduleService dayScheduleService, EttUserDetailsRepository ettUserDetailsRepository, SettingsRepository settingsRepository) {
        this.weekScheduleService = weekScheduleService;
        this.dayScheduleService = dayScheduleService;
        this.ettUserDetailsRepository = ettUserDetailsRepository;
        this.settingsRepository = settingsRepository;
    }

    @GetMapping("/")
    public List<WeekScheduleDto> findAll() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return weekScheduleService.findAll().stream().map(WeekScheduleDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WeekScheduleDto findById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new WeekScheduleDto(weekScheduleService.findOrNull(id));
    }

    @PostMapping("/")
    public WeekScheduleDto createWeekSchedule(@RequestBody WeekScheduleDto weekScheduleDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new WeekScheduleDto(weekScheduleService.update(weekScheduleDto.toEntity(weekScheduleService, dayScheduleService)));
    }

    @PutMapping("/")
    public WeekScheduleDto editWeekSchedule(@RequestBody WeekScheduleDto weekScheduleDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new WeekScheduleDto(weekScheduleService.update(weekScheduleDto.toEntity(weekScheduleService, dayScheduleService)));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (settingsRepository.existsByLoungeScheduleId(id)) throw new RuntimeException("Cannot delete week schedule that is the currently active one!");
        weekScheduleService.delete(id);
    }
}
