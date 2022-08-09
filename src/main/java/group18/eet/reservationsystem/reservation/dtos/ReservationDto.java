package group18.eet.reservationsystem.reservation.dtos;

import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.security.dtos.EttUserDto;
import group18.eet.reservationsystem.security.userdetails.EttUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private Long id;
    private EttUserDto reservee;
    private String from;
    private String to;
    private String remarks;
    private Reservation.ReservationStatus status;

    public ReservationDto(Reservation reservation) {
        if (reservation == null) throw new RuntimeException("This entity doesnt exist");
        this.id = reservation.getId();
        this.reservee = new EttUserDto(reservation.getReservee());
        this.from = reservation.getFrom().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
        this.to = reservation.getTo().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
        this.remarks = reservation.getRemarks();
        this.status = reservation.getStatus();
    }
}
