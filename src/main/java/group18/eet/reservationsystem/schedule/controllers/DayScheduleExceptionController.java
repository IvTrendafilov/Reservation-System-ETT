package group18.eet.reservationsystem.schedule.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.schedule.dtos.DayScheduleExceptionDto;
import group18.eet.reservationsystem.schedule.entities.DayScheduleException;
import group18.eet.reservationsystem.schedule.repositories.DayScheduleExceptionRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dayscheduleexception")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class DayScheduleExceptionController {

    private final DayScheduleExceptionRepository dayScheduleExceptionRepository;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping("/")
    public List<DayScheduleExceptionDto> findAll(
            @QueryParam("date") String date,
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) {
        return dayScheduleExceptionRepository.findAll((Specification<DayScheduleException>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and();
            if (date != null && !date.trim().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(root.get("date"), LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
            }
            if (from != null && !from.trim().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("date"),
                                LocalDate.parse(from, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        ));
            }
            if (to != null && !to.trim().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("date"),
                                LocalDate.parse(to, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        ));
            }
            return predicate;
        }).stream().map(DayScheduleExceptionDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DayScheduleExceptionDto findDayScheduleExceptionById(@PathVariable("id") Long id) {
        return new DayScheduleExceptionDto(dayScheduleExceptionRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @PostMapping("/")
    public DayScheduleExceptionDto createDayScheduleException(@RequestBody DayScheduleExceptionDto dayScheduleExceptionDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (dayScheduleExceptionDto.getDate() == null || (!dayScheduleExceptionDto.isClosed() && (dayScheduleExceptionDto.getWorkingTimes() == null || dayScheduleExceptionDto.getWorkingTimes().isEmpty()))) {
            throw new RuntimeException("Invalid attributes");
        }
        return new DayScheduleExceptionDto(dayScheduleExceptionRepository.save(dayScheduleExceptionDto.toEntity(dayScheduleExceptionRepository)));
    }

    @PutMapping("/")
    public DayScheduleExceptionDto editDayScheduleException(@RequestBody DayScheduleExceptionDto dayScheduleExceptionDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (dayScheduleExceptionDto.getDate() == null || (!dayScheduleExceptionDto.isClosed() && (dayScheduleExceptionDto.getWorkingTimes() == null || dayScheduleExceptionDto.getWorkingTimes().isEmpty()))) {
            throw new RuntimeException("Invalid attributes");
        }
        return new DayScheduleExceptionDto(dayScheduleExceptionRepository.save(dayScheduleExceptionDto.toEntity(dayScheduleExceptionRepository)));
    }

    @DeleteMapping("/{id}")
    public void deleteDayScheduleExceptionById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        dayScheduleExceptionRepository.deleteById(id);
    }
}
