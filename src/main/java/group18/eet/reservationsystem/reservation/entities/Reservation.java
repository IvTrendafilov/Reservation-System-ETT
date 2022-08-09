package group18.eet.reservationsystem.reservation.entities;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.utils.Thisable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Reservation implements Thisable<Reservation> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservee_id")
    private EttUser reservee;

    @Column(name = "`from`")
    private LocalDateTime from;
    @Column(name = "`to`")
    private LocalDateTime to;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    public enum ReservationStatus {
        APPROVED,
        PENDING,
        REJECTED,
        CANCELLED
    }
}
