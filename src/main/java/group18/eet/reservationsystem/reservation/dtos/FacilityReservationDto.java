package group18.eet.reservationsystem.reservation.dtos;

import group18.eet.reservationsystem.reservable.facility.dtos.FacilityDto;
import group18.eet.reservationsystem.reservable.facility.services.FacilityService;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReservationDto extends ReservationDto {
    private Long facilityId;

    public FacilityReservationDto (FacilityReservation facilityReservation) {
        super(facilityReservation);
        this.facilityId = facilityReservation.getFacility().getId();
    }

    public FacilityReservation toEntity(ReservationService reservationService, FacilityService facilityService) {
        FacilityReservation facilityReservation = new FacilityReservation();
        if (this.getId() != null) facilityReservation = (FacilityReservation) reservationService.findOrNull(this.getId());
        facilityReservation.setFrom(LocalDateTime.parse(this.getFrom(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
        facilityReservation.setTo(LocalDateTime.parse(this.getTo(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
        facilityReservation.setRemarks(this.getRemarks());
        facilityReservation.setFacility(facilityService.findOrNull(this.getFacilityId()));
        return facilityReservation;
    }
}
