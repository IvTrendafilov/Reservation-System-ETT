package group18.eet.reservationsystem.reservable.device.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceDto;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceExtendedDto;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceTypeDto;
import group18.eet.reservationsystem.reservable.device.repository.DeviceTypeRepository;
import group18.eet.reservationsystem.reservable.device.services.DeviceService;
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
@RequestMapping("/api/device")
@PreAuthorize("isAuthenticated()")
public class DeviceController {

    private final DeviceService deviceService;
    private final EttUserDetailsRepository ettUserDetailsRepository;
    private final DeviceTypeRepository deviceTypeRepository;

    @Autowired
    public DeviceController(DeviceService deviceService, EttUserDetailsRepository ettUserDetailsRepository, DeviceTypeRepository deviceTypeRepository) {
        this.deviceService = deviceService;
        this.ettUserDetailsRepository = ettUserDetailsRepository;
        this.deviceTypeRepository = deviceTypeRepository;
    }

    @GetMapping("/")
    public List<DeviceDto> findAll() {
        return deviceService.findAll().stream().filter(d -> d.getDeletedOn() == null).map(DeviceDto::new).collect(Collectors.toList());
    }

    @GetMapping("/query")
    @PreAuthorize("hasAuthority('ADMIN') || (hasAuthority('USER') && !#disabled) || hasAuthority('GLOBAL_ADMIN')")
    public List<DeviceDto> findAllQuery(
            @RequestParam(value = "disabled", required = false) boolean disabled,
            @RequestParam(value = "ids", required = false) Set<Long> ids) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin() && disabled) throw new UnauthorizedException();
        return deviceService.findByQuery(disabled, ids).stream().map(DeviceDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeviceDto findById(@PathVariable("id") Long id) {
        return new DeviceDto(deviceService.findOrNull(id));
    }

    @PostMapping("/")
    public DeviceDto createDevice(@RequestBody DeviceDto deviceDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new DeviceDto(deviceService.create(deviceDto.toEntity(deviceService, deviceTypeRepository)));
    }

    @PutMapping("/")
    public DeviceDto editDevice(@RequestBody DeviceDto deviceDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return new DeviceDto(deviceService.update(deviceDto.toEntity(deviceService, deviceTypeRepository)));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();

        Device device = deviceService.findOrNull(id);
        if (device == null) throw new NotFoundException();
        device.setDeletedOn(Instant.now());
        deviceService.update(device);
    }

    @PutMapping("/positions")
    public List<DeviceDto> savePositions(@RequestBody List<DeviceDto> deviceDtos) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return deviceService.savePositions(deviceDtos).stream().map(DeviceDto::new).collect(Collectors.toList());
    }

    @GetMapping("/reservation")
    public List<DeviceExtendedDto> findAllForReservation(@QueryParam("from") String from, @QueryParam("to") String to) {
        return deviceService.findAllForReservation(
                LocalDateTime.parse(from, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                LocalDateTime.parse(to, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
    }
}
