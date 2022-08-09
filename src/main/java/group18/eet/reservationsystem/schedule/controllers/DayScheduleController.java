package group18.eet.reservationsystem.schedule.controllers;


import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.schedule.dtos.DaySchedulesDto;
import group18.eet.reservationsystem.schedule.repositories.WeekScheduleRepository;
import group18.eet.reservationsystem.schedule.services.DayScheduleService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dayschedule")
@PreAuthorize("isAuthenticated()")
public class DayScheduleController {

    private final DayScheduleService dayScheduleService;
    private final EttUserDetailsRepository ettUserDetailsRepository;
    private final WeekScheduleRepository weekScheduleRepository;


    @Autowired
    public DayScheduleController(DayScheduleService dayScheduleService, EttUserDetailsRepository ettUserDetailsRepository, WeekScheduleRepository weekScheduleRepository) {
        this.dayScheduleService = dayScheduleService;
        this.ettUserDetailsRepository = ettUserDetailsRepository;
        this.weekScheduleRepository = weekScheduleRepository;
    }

    @GetMapping("/")
    public List<DaySchedulesDto> findAll() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return dayScheduleService.findAll().stream().map(DaySchedulesDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DaySchedulesDto findById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new DaySchedulesDto(dayScheduleService.findOrNull(id));
    }

    @PostMapping("/")
    public DaySchedulesDto createDaySchedule(@RequestBody DaySchedulesDto daySchedulesDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new DaySchedulesDto(dayScheduleService.update(daySchedulesDto.toEntity(dayScheduleService)));
    }

    @PutMapping("/")
    public DaySchedulesDto editDaySchedule(@RequestBody DaySchedulesDto daySchedulesDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new DaySchedulesDto(dayScheduleService.update(daySchedulesDto.toEntity(dayScheduleService)));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (weekScheduleRepository.existsByMondayIdOrTuesdayIdOrWednesdayIdOrThursdayIdOrFridayIdOrSundayIdOrSaturdayId(id, id, id, id, id, id, id)) {
            throw new RuntimeException("Cannot delete day schedule that is associated with week schedule!");
        }
        dayScheduleService.delete(id);
    }

}
