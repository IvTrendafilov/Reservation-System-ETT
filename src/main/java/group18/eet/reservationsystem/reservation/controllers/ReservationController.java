package group18.eet.reservationsystem.reservation.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.reservable.device.services.DeviceService;
import group18.eet.reservationsystem.reservable.facility.services.FacilityService;
import group18.eet.reservationsystem.reservation.dtos.DeviceReservationDto;
import group18.eet.reservationsystem.reservation.dtos.FacilityReservationDto;
import group18.eet.reservationsystem.reservation.dtos.ReservationDto;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;
    private final DeviceService deviceService;
    private final EttUserDetailsService userDetailsService;
    private final FacilityService facilityService;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping("/")
    public Page<? extends ReservationDto> findAllFiltered(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to,
            @RequestParam(name = "status", required = false) Reservation.ReservationStatus status,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "deviceIds", required = false) Set<Long> deviceIds,
            @RequestParam(name = "facilityIds", required = false) Set<Long> facilityIds,
            @RequestParam(name = "reserveeId", required = false) Long reserveeId,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "sortBy") List<String> sort) {
        EttUser currentPrincipal = EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository);
        Long afterAuthorityCheckReserveeId = currentPrincipal.isAdmin() ? reserveeId : currentPrincipal.getId();
        return reservationService.findAllFiltered(
                query,
                from != null ? LocalDateTime.parse(from, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null,
                to != null ? LocalDateTime.parse(to, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null,
                status,
                afterAuthorityCheckReserveeId,
                type,
                deviceIds,
                facilityIds,
                page,
                size,
                sort
        ).map(r -> {
            if (r.instanceOf(DeviceReservation.class)) return new DeviceReservationDto((DeviceReservation) r.getThis());
            if (r.instanceOf(FacilityReservation.class)) return new FacilityReservationDto((FacilityReservation) r.getThis());
            throw new RuntimeException("No such entity");
        });
    }

    @PostMapping("/device")
    public DeviceReservationDto createDeviceReservation(@RequestBody DeviceReservationDto deviceReservationDto) {
        return new DeviceReservationDto((DeviceReservation) reservationService.update(deviceReservationDto.toEntity(reservationService, deviceService)));
    }

    @PostMapping("/status/{id}/{status}")
    public ReservationDto updateReservationStatus(
            @PathVariable("id") Long id,
            @PathVariable("status") Reservation.ReservationStatus status
    ) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new ReservationDto(reservationService.updateReservationStatus(id, status));
    }
    @PutMapping("/device")
    public DeviceReservationDto editDeviceReservation(@RequestBody DeviceReservationDto deviceReservationDto) {
        return new DeviceReservationDto((DeviceReservation) reservationService.update(deviceReservationDto.toEntity(reservationService, deviceService)));
    }

    @PostMapping("/facility")
    public FacilityReservationDto createFacilityReservation(@RequestBody FacilityReservationDto facilityReservationDto) {
        return new FacilityReservationDto((FacilityReservation) reservationService.update(facilityReservationDto.toEntity(reservationService, facilityService)));
    }

    @PutMapping("/facility")
    public FacilityReservationDto editFacilityReservation(@RequestBody FacilityReservationDto facilityReservationDto) {
        return new FacilityReservationDto((FacilityReservation) reservationService.update(facilityReservationDto.toEntity(reservationService, facilityService)));
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable("id") Long id) {
        reservationService.delete(id);
    }
}
