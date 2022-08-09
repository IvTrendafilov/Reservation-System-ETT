package group18.eet.reservationsystem.reservation.entities;

import group18.eet.reservationsystem.reservable.device.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(DeviceReservation.DISCRIMINATOR)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReservation extends Reservation {

    public static final String DISCRIMINATOR = "DeviceReservation";

    @ManyToMany
    @JoinTable(
            name = "reservation_device",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    private List<Device> devices;
}
