package group18.eet.reservationsystem.reservable.facility.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.reservable.facility.dtos.FacilityDto;
import group18.eet.reservationsystem.reservable.facility.dtos.FacilityExtendedDto;
import group18.eet.reservationsystem.reservable.facility.services.FacilityService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facility")
@PreAuthorize("isAuthenticated()")
public class FacilityController {

    private final FacilityService facilityService;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @Autowired
    public FacilityController(FacilityService facilityService, EttUserDetailsRepository ettUserDetailsRepository) {
        this.facilityService = facilityService;
        this.ettUserDetailsRepository = ettUserDetailsRepository;
    }


    @GetMapping("/")
    public List<FacilityDto> findAll() {
        return facilityService.findAll().stream().filter(d -> d.getDeletedOn() == null).map(FacilityDto::new).collect(Collectors.toList());
    }

    @GetMapping("/query")
    public List<FacilityDto> findAllQuery(
            @RequestParam(value = "disabled", required = false) boolean disabled,
            @RequestParam(value = "ids", required = false) Set<Long> ids,
            @RequestParam(value = "type", required = false) Facility.FacilityType type
            ) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin() && disabled) throw new UnauthorizedException();
        return facilityService.findByQuery(disabled, ids, type).stream().map(FacilityDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FacilityDto findById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new FacilityDto(facilityService.findOrNull(id));
    }

    @PostMapping("/")
    public FacilityDto createFacility(@RequestBody FacilityDto facilityDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new FacilityDto(facilityService.update(facilityDto.toEntity(facilityService)));
    }

    @PutMapping("/")
    public FacilityDto editFacility(@RequestBody FacilityDto facilityDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new FacilityDto(facilityService.update(facilityDto.toEntity(facilityService)));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        Facility facility = facilityService.findOrNull(id);
        if (facility == null) throw new NotFoundException();
        facility.setDeletedOn(Instant.now());
        facilityService.update(facility);

    }

    @GetMapping("/reservation")
    public List<FacilityExtendedDto> findAllForReservation(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("type") Facility.FacilityType type) {
        return facilityService.findAllForReservation(
                LocalDateTime.parse(from, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                LocalDateTime.parse(to, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                type);
    }
}

