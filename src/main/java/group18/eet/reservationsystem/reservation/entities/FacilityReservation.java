package group18.eet.reservationsystem.reservation.entities;

import group18.eet.reservationsystem.reservable.facility.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@DiscriminatorValue(FacilityReservation.DISCRIMINATOR)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReservation extends Reservation {

    public static final String DISCRIMINATOR = "FacilityReservation";

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;

}
