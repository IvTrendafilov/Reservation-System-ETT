package group18.eet.reservationsystem.reservable.device.controllers;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceTypeDto;
import group18.eet.reservationsystem.reservable.device.repository.DeviceRepository;
import group18.eet.reservationsystem.reservable.device.repository.DeviceTypeRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devicetypes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceRepository deviceRepository;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping("/")
    public List<DeviceTypeDto> findAll() {
        return deviceTypeRepository.findAll().stream().map(DeviceTypeDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeviceTypeDto findDeviceTypeById(@PathVariable("id") Long id) {
        return new DeviceTypeDto(deviceTypeRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @PostMapping("/")
    public DeviceTypeDto createDeviceType(@RequestBody DeviceTypeDto deviceTypeDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (deviceTypeDto.getImageClass() == null || deviceTypeDto.getImageClass().trim().isEmpty()
                || deviceTypeDto.getName() == null || deviceTypeDto.getName().trim().isEmpty()) {
            throw new RuntimeException("Invalid attributes");
        }
        return new DeviceTypeDto(deviceTypeRepository.save(deviceTypeDto.toEntity(deviceTypeRepository)));
    }

    @PutMapping("/")
    public DeviceTypeDto editDeviceType(@RequestBody DeviceTypeDto deviceTypeDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (deviceTypeDto.getImageClass() == null || deviceTypeDto.getImageClass().trim().isEmpty()
                || deviceTypeDto.getName() == null || deviceTypeDto.getName().trim().isEmpty()) {
            throw new RuntimeException("Invalid attributes");
        }
        return new DeviceTypeDto(deviceTypeRepository.save(deviceTypeDto.toEntity(deviceTypeRepository)));
    }

    @DeleteMapping("/{id}")
    public void deleteDeviceTypeById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        if (deviceRepository.existsByTypeId(id)) throw new RuntimeException("Cannot delete device type that is associated with a device!");
        deviceTypeRepository.deleteById(id);
    }
}
