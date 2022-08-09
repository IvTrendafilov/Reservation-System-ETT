package group18.eet.reservationsystem.reservable.device.dtos;

import group18.eet.reservationsystem.reservable.Position;
import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.device.repository.DeviceTypeRepository;
import group18.eet.reservationsystem.reservable.device.services.DeviceService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private Long id;
    private String code;
    private boolean disabled;
    private boolean reserved;
    private Position position;
    private DeviceTypeDto type;

    public DeviceDto(Device device) {
        if (device == null) throw new RuntimeException("This entity doesnt exist");
        this.id = device.getId();
        this.code = device.getCode();
        this.disabled = device.isDisabled();
        this.reserved = device.isReserved();
        this.position = device.getPosition();
        this.type = new DeviceTypeDto(device.getType());
    }

    public Device toEntity(DeviceService deviceService, DeviceTypeRepository deviceTypeRepository) {
        Device device = new Device();
        if (id != null) device = deviceService.findOrNull(id);

        device.setCode(this.code);
        device.setDisabled(this.disabled);
        device.setReserved(this.reserved);
        device.setPosition(this.position);
        device.setType(this.type.toEntity(deviceTypeRepository));
        return device;
    }
}
