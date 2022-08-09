package group18.eet.reservationsystem.reservable.device.dtos;

import group18.eet.reservationsystem.reservable.device.DeviceType;
import group18.eet.reservationsystem.reservable.device.repository.DeviceTypeRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeDto {
    private Long id;
    private String imageClass;
    private String name;

    public DeviceTypeDto(DeviceType type) {
        this.id = type.getId();
        this.imageClass = type.getImageClass();
        this.name = type.getName();
    }

    public DeviceType toEntity(DeviceTypeRepository deviceTypeRepository) {
        DeviceType deviceType = new DeviceType();
        if (id != null) deviceType = deviceTypeRepository.findById(id).orElse(null);
        assert deviceType != null;
        deviceType.setName(this.name);
        deviceType.setImageClass(this.imageClass);
        return deviceType;
    }
}
