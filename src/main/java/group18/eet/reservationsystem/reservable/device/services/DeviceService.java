package group18.eet.reservationsystem.reservable.device.services;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceDto;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceExtendedDto;
import group18.eet.reservationsystem.reservable.device.dtos.DeviceExtendedDto.DeviceColorState;
import group18.eet.reservationsystem.reservable.device.repository.DeviceRepository;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.utils.BaseService;
import group18.eet.reservationsystem.utils.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService extends BaseService<Device, DeviceRepository> {

    private final DeviceRepository deviceRepository;
    private final ReservationService reservationService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, ReservationService reservationService) {
        super(deviceRepository);
        this.deviceRepository = deviceRepository;
        this.reservationService = reservationService;
    }

    /**
     * Create or update a device with validation of fields
     */
    public Device update(Device device) {
        if (device.getCode().trim().equals("") || device.getType() == null) {
            throw new RuntimeException("There is no code or type supplied or the entity doesn't exist");
        }
        return deviceRepository.save(device);
    }

    /**
     * Saves the position of devices in the interactive map
     */
    public List<Device> savePositions(List<DeviceDto> deviceDtos) {
        Map<Long, Device> devices = deviceRepository.findAllById(deviceDtos.stream()
                .map(DeviceDto::getId).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(Device::getId, Function.identity()));

        deviceDtos.forEach(device -> devices.get(device.getId()).setPosition(device.getPosition()));
        return new ArrayList<>(devices.values());
    }

    /**
     *
     * @param disabled - if we want to query disabled/non-disabled devices
     * @param ids - if we want to filter on device ids, if we want for all ids we can pass null or empty list
     * @return All the devices matching that query
     */
    public List<Device> findByQuery(boolean disabled, Set<Long> ids) {
        return deviceRepository.findAll((Specification<Device>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and();
            if (ids != null && !ids.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.in(root.get("id")).value(ids));
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(root.get("disabled"), disabled),
                    criteriaBuilder.isNull(root.get("deletedOn")));

            return predicate;
        });
    }

    /**
     *
     * @param from a DateTime for which we want to populate the device like color and interfering reservation times
     * @param to a DateTime for which we want to populate the device like color and interfering reservation times
     * @return Data Transfer Objects for Device with populated color and interfering working times
     */
    public List<DeviceExtendedDto> findAllForReservation(LocalDateTime from, LocalDateTime to) {
        Map<Long, DeviceExtendedDto> devicesMap = findByQuery(false, null).stream()
                .collect(Collectors.toMap(Device::getId, (d) -> new DeviceExtendedDto(d, DeviceColorState.BLACK)));
        List<? extends Reservation> conflictingReservations = reservationService.getAllConflictingReservations(from, to, DeviceReservation.class);
        conflictingReservations.forEach(cr -> {
            if (cr.instanceOf(DeviceReservation.class)) {
                DeviceReservation dr = (DeviceReservation) cr.getThis();
                dr.getDevices().forEach(device -> {
                    DeviceExtendedDto currentRepresentation = devicesMap.get(device.getId());
                    if (currentRepresentation == null || currentRepresentation.getColor().equals(DeviceColorState.RED)) {
                        return;
                    }

                    currentRepresentation.addTimeslot(Tuple.of(cr.getFrom().toLocalTime(), cr.getTo().toLocalTime()));

                    // if there is a reservation which occupies the whole timeslot put a red color | all other cases occupy the slot partly (maybe combined occupy a whole timeslot)
                    currentRepresentation.setColor(
                            (cr.getFrom().isBefore(from) || cr.getFrom().isEqual(from)) && (cr.getTo().isAfter(to) || cr.getTo().isEqual(to)) ?
                                    DeviceColorState.RED : DeviceColorState.YELLOW);
                });
            }
        });

        return devicesMap.values().stream().map(deviceDto -> {
            if (deviceDto.getColor().equals(DeviceColorState.RED) || deviceDto.getColor().equals(DeviceColorState.BLACK) ||
                    deviceDto.getInterferingReservationTimeslots().size() <= 1) return deviceDto;

            // check if there are yellow reservation to be made up red (in case of several reservations filling the whole from to timeslot)
            List<Tuple<LocalTime, LocalTime>> sortedTimeslot = deviceDto.getInterferingReservationTimeslots().stream()
                    .sorted((p1, p2) -> p1.getRight().compareTo(p2.getLeft()))
                    .collect(Collectors.toList());

            // skip the first and last one
            for (int i = 0; i < sortedTimeslot.size() - 1 ; i++) {
                if (sortedTimeslot.get(i).getRight().compareTo(sortedTimeslot.get(i + 1).getLeft()) == 0) {
                    continue;
                }
                // if there are timeslot making a gap between their end/beginning return the yellow color
                return deviceDto;
            }
            int fromCompare = from.toLocalTime().compareTo(sortedTimeslot.get(0).getLeft());
            int toCompare = to.toLocalTime().compareTo(sortedTimeslot.get(sortedTimeslot.size() - 1).getRight());
            if (fromCompare >= 0 && toCompare <= 0) {
                deviceDto.setColor(DeviceColorState.RED);
            }
            return deviceDto;
        }).collect(Collectors.toList());
    }

}
