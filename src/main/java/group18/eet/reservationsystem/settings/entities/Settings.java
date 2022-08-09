package group18.eet.reservationsystem.settings.entities;

import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private boolean autoAcceptanceOfDeviceReservations;
    private boolean autoAcceptanceOfFacilityReservations;
    private int maxDevicesPerReservation;
    private int maxBookingTimeLength;

    @OneToOne
    @JoinColumn(name = "lounge_schedule_id")
    private WeekSchedule loungeSchedule;

}
