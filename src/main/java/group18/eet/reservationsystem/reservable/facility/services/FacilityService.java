package group18.eet.reservationsystem.reservable.facility.services;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.reservable.facility.dtos.FacilityExtendedDto;
import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.reservable.facility.dtos.FacilityExtendedDto;
import group18.eet.reservationsystem.reservable.facility.repository.FacilityRepository;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.utils.BaseService;
import group18.eet.reservationsystem.utils.Tuple;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class FacilityService extends BaseService<Facility, FacilityRepository> {

    private final FacilityRepository facilityRepository;
    private final ReservationService reservationService;

    @Autowired
    public FacilityService(FacilityRepository facilityRepository, ReservationService reservationService) {
        super(facilityRepository);
        this.facilityRepository = facilityRepository;
        this.reservationService = reservationService;
    }

    /**
     * Updates or creates a faclity with field validation
     */
    public Facility update(Facility facility) {
        if (Strings.isEmpty(facility.getName()) || facility.getFacilityType() == null || facility.getFacilityType().isEmpty() || facility.getRoomId() == null) {
            throw new RuntimeException("There is a missing or wrong field!");
        }

        if (findByQuery(false, null, null).stream().anyMatch(fac -> fac.getRoomId().equals(facility.getRoomId()) && !fac.getId().equals(facility.getId()))) {
            throw new RuntimeException("This room is occupied by a non-disabled facility");
        }

        return facilityRepository.save(facility);
    }

    /**
     *
     * @param disabled - if we want to search for disabled or non-disabled facilities
     * @param ids - if we want to query a specific ids (null or empty for all)
     * @param type - the facility type we search for
     * @return List with all facilities
     */
    public List<Facility> findByQuery(boolean disabled, Set<Long> ids, Facility.FacilityType type) {
        return facilityRepository.findAll((Specification<Facility>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and();
            if (ids != null && !ids.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.in(root.get("id")).value(ids));
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(root.get("disabled"), disabled),
                    criteriaBuilder.isNull(root.get("deletedOn")));

            return predicate;
        }).stream().filter(f -> type == null || f.getFacilityType().contains(type)).collect(Collectors.toList());
    }

    /**
     *
     * @param from a DateTime for which we want to populate the facility like color and interfering reservation times
     * @param to a DateTime for which we want to populate the facility like color and interfering reservation times
     * @param type The facility type we want to search for (Broadcast, Tournament)
     * @return Data Transfer Objects for Device with populated color and interfering working times
     */
    public List<FacilityExtendedDto> findAllForReservation(LocalDateTime from, LocalDateTime to, Facility.FacilityType type) {
        Map<Long, FacilityExtendedDto> facilityMap = findByQuery(false, null, type).stream()
                .collect(Collectors.toMap(Facility::getId, (d) -> new FacilityExtendedDto(d, FacilityExtendedDto.FacilityColorState.BLACK)));
        List<? extends Reservation> conflictingReservations = reservationService.getAllConflictingReservations(from, to, FacilityReservation.class);
        conflictingReservations.forEach(cr -> {
            if (cr.instanceOf(FacilityReservation.class)) {
                FacilityReservation dr = (FacilityReservation) cr.getThis();
                FacilityExtendedDto currentRepresentation = facilityMap.get(dr.getFacility().getId());
                if (currentRepresentation == null || currentRepresentation.getColor().equals(FacilityExtendedDto.FacilityColorState.RED)) {
                    return;
                }

                // if there is a reservation which occupies the whole timeslot put a red color | all other cases occupy the slot partly (maybe combined occupy a whole timeslot)
                if (cr.getFrom().isBefore(from) && cr.getTo().isAfter(to) || cr.getFrom().isEqual(from) && cr.getTo().isEqual(to) ||
                        cr.getFrom().isEqual(from) && cr.getTo().isAfter(to) || cr.getFrom().isBefore(from) && cr.getTo().isEqual(to)) {
                    currentRepresentation.setColor(FacilityExtendedDto.FacilityColorState.RED);
                } else {
                    currentRepresentation.setColor(FacilityExtendedDto.FacilityColorState.YELLOW);
                    currentRepresentation.addTimeslot(Tuple.of(cr.getFrom().toLocalTime(), cr.getTo().toLocalTime()));
                }
            }
        });

        return facilityMap.values().stream().map(facilityDto -> {
            if (facilityDto.getColor().equals(FacilityExtendedDto.FacilityColorState.RED) || facilityDto.getColor().equals(FacilityExtendedDto.FacilityColorState.BLACK) ||
                    facilityDto.getInterferingReservationTimeslots().size() <= 1) return facilityDto;

            // check if there are yellow reservation to be made up red (in case of several reservations filling the whole from to timeslot)
            List<Tuple<LocalTime, LocalTime>> sortedTimeslot = facilityDto.getInterferingReservationTimeslots().stream()
                    .sorted((p1, p2) -> p1.getRight().compareTo(p2.getLeft()))
                    .collect(Collectors.toList());

            // skip the first and last one
            for (int i = 0; i < sortedTimeslot.size() - 1 ; i++) {
                if (sortedTimeslot.get(i).getRight().compareTo(sortedTimeslot.get(i + 1).getLeft()) == 0) {
                    continue;
                }
                // if there are timeslot making a gap between their end/beginning return the yellow color
                return facilityDto;
            }
            facilityDto.setColor(FacilityExtendedDto.FacilityColorState.RED);
            return facilityDto;
        }).collect(Collectors.toList());
    }
}
