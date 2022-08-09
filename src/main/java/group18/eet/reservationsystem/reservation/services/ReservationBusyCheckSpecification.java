package group18.eet.reservationsystem.reservation.services;

import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * This specification serves as finder for interfering reservations, such that we disable overlapping
 */
public class ReservationBusyCheckSpecification implements Specification<Reservation> {
    private LocalDateTime from;
    private LocalDateTime to;
    private Class<? extends Reservation> clazz;
    private List<Long> deviceIds = new ArrayList<>();
    private Long facilityId = null;

    public ReservationBusyCheckSpecification(LocalDateTime from, LocalDateTime to, Class<? extends Reservation> clazz) {
        this.from = from;
        this.to = to;
        this.clazz = clazz;
    }

    public ReservationBusyCheckSpecification(LocalDateTime from, LocalDateTime to, Class<? extends Reservation> clazz, List<Long> deviceIds) {
        this.from = from;
        this.to = to;
        this.deviceIds = deviceIds;
        this.clazz = clazz;
    }

    public ReservationBusyCheckSpecification(LocalDateTime from, LocalDateTime to, Class<? extends Reservation> clazz, Long facilityId) {
        this.from = from;
        this.to = to;
        this.facilityId = facilityId;
        this.clazz = clazz;
    }

    /**
     * There are 4 cases, which we are interested in <br/>
     *  1. if from is before the specified reserved date and to is after the end of the reservation (busy entity) <br/>
     *  2. if from is before the specified reserved date and to is in between the beginning and the end of the reservation (soon available entity) <br/>
     *  3. if the from is between the specified reserved start and end date and to is after the end (soon available entity) <br/>
     *  4. if there is a reservation contained in the from/to we are interested in
     */
    @Override
    public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (from == null || to == null || clazz == null) {
            throw new RuntimeException("Bad parameters for getting available reservable entities!");
        }
        Predicate predicate = criteriaBuilder.and();

        if (deviceIds != null && !deviceIds.isEmpty()) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.in(
                            criteriaBuilder.treat(root, DeviceReservation.class).join("devices").get("id")
                    ).value(deviceIds)
            );
        }

        if (facilityId != null) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(criteriaBuilder.treat(root, FacilityReservation.class).get("facility").get("id"), facilityId)
            );
        }

        return criteriaBuilder.and(
                predicate,
                criteriaBuilder.equal(root.type(), clazz),
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(root.get("from"), from),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("to"), to)
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(root.get("from"), from),
                                criteriaBuilder.between(root.get("to"), from.plus(1, ChronoUnit.MINUTES), to) // to remove the equals case
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.between(root.get("from"), from, to.minus(1, ChronoUnit.MINUTES)),  // to remove the equals case
                                criteriaBuilder.greaterThanOrEqualTo(root.get("to"), to)
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.between(root.get("from"), from, to),
                                criteriaBuilder.between(root.get("to"), from, to)
                        )
                ),
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("status"), (Reservation.ReservationStatus.APPROVED)),
                        criteriaBuilder.equal(root.get("status"), (Reservation.ReservationStatus.PENDING))
                )
        );
    }
}
