package group18.eet.reservationsystem.reservation.services;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * This class is the general filter specification for reservations
 * It can filter on all the fields you see below and if you want to omit it just pass null
 * In case you pass a "query" it will do a full-text search on reservee's email, name, reservation remarks and device codes in case of device reservation
 * The rest are self-explanatory
 */
public class ReservationFilterSpecification implements Specification<Reservation> {
    private String query;
    private LocalDateTime from;
    private LocalDateTime to;
    private Reservation.ReservationStatus status;
    private String type;
    private Long reserveeId;
    private Set<Long> deviceIds;
    private Set<Long> facilityIds;

    public ReservationFilterSpecification(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public ReservationFilterSpecification(String query, LocalDateTime from, LocalDateTime to, Reservation.ReservationStatus status, Long reserveeId, String type, Set<Long> deviceIds, Set<Long> facilityIds) {
        this.query = query;
        this.from = from;
        this.to = to;
        this.status = status;
        this.reserveeId = reserveeId;
        this.type = type;
        this.deviceIds = deviceIds;
        this.facilityIds = facilityIds;
    }

    @Override
    public Predicate toPredicate(Root<Reservation> r, CriteriaQuery<?> q, CriteriaBuilder cb) {
        Predicate predicate = cb.and();
        if (query != null && !query.trim().isEmpty()) {
            // subquery for device code check
            Subquery<Device> subquery = q.subquery(Device.class);
            Root<Device> device = subquery.from(Device.class);
            subquery.select(device)
                    .distinct(true)
                    .where(cb.like(device.get("code"), "%" + query + "%"));

            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.like(cb.lower(r.get("reservee").get("email")), "%" + query.toLowerCase() + "%"),
                            cb.like(cb.lower(r.get("reservee").get("name")), "%" + query.toLowerCase() + "%"),
                            cb.like(cb.lower(r.get("remarks")), "%" + query.toLowerCase() + "%"),
                            type.equals("Device") ? cb.in(cb.treat(r, DeviceReservation.class).join("devices")).value(subquery) : cb.or()
                    )
            );
        }

        if (from != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(r.get("from"), from));
        }

        if (to != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(r.get("to"), to));
        }

        if (status != null) {
            predicate = cb.and(predicate, cb.equal(r.get("status"), status));
        }

        if (reserveeId != null) {
            predicate = cb.and(predicate, cb.equal(r.get("reservee").get("id"), reserveeId));
        }

        if (type != null) {
            if (type.equals("Device")) {
                predicate = cb.and(predicate, cb.treat(r, DeviceReservation.class).isNotNull());
            } else if (type.equals("Facility")) {
                predicate = cb.and(predicate, cb.treat(r, FacilityReservation.class).isNotNull());
            }
        }

        if (deviceIds != null && !deviceIds.isEmpty() && (facilityIds == null || facilityIds.isEmpty())) {
            predicate = cb.and(predicate, cb.in(cb.treat(r, DeviceReservation.class).join("devices").get("id")).value(deviceIds));
        }

        if (facilityIds != null && !facilityIds.isEmpty() && (deviceIds == null || deviceIds.isEmpty())) {
            predicate = cb.and(predicate, cb.in(cb.treat(r, FacilityReservation.class).get("facility").get("id")).value(facilityIds));
        }

        return predicate;
    }
}
