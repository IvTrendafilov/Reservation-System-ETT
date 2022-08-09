package group18.eet.reservationsystem.reservation.services;

import group18.eet.reservationsystem.mail.MailEventListener;
import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.reservation.repository.ReservationRepository;
import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.schedule.entities.DayScheduleException;
import group18.eet.reservationsystem.schedule.repositories.DayScheduleExceptionRepository;
import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import group18.eet.reservationsystem.settings.entities.Settings;
import group18.eet.reservationsystem.settings.services.SettingsService;
import group18.eet.reservationsystem.utils.BaseService;
import group18.eet.reservationsystem.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class ReservationService extends BaseService<Reservation, ReservationRepository> {

    private final ReservationRepository reservationRepository;
    private final SettingsService settingsService;
    private final EttUserDetailsRepository ettUserDetailsRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DayScheduleExceptionRepository dayScheduleExceptionRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, SettingsService settingsService, EttUserDetailsRepository ettUserDetailsRepository, ApplicationEventPublisher applicationEventPublisher, DayScheduleExceptionRepository dayScheduleExceptionRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.settingsService = settingsService;
        this.ettUserDetailsRepository = ettUserDetailsRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.dayScheduleExceptionRepository = dayScheduleExceptionRepository;
    }

    public Page<? extends Reservation> findAllFiltered(
            String query,
            LocalDateTime from,
            LocalDateTime to,
            Reservation.ReservationStatus status,
            Long reserveeId,
            String type,
            Set<Long> deviceIds,
            Set<Long> facilityIds,
            Integer page,
            Integer size,
            List<String> sort) {
            EttUser currentPrincipal = EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository);
        if (!currentPrincipal.isAdmin() && !currentPrincipal.getId().equals(reserveeId)) {
            throw new RuntimeException("Cannot fetch bookings of another user!");
        }
        return reservationRepository.findAll(
                new ReservationFilterSpecification(query, from, to, status, reserveeId, type, deviceIds, facilityIds),
                PaginationUtils.createPaginationAndSorting(page, size, sort)
        );
    }

    /**
     *
     * @param from - from date time we want to search interfering reservations
     * @param to - to date time we want to search interfering reservations
     * @param clazz - the reservation subclass we are interested in (DeviceReservation or FacilityReseration)
     * @return a list of all interfering reservations
     */
    public List<? extends Reservation> getAllConflictingReservations(LocalDateTime from, LocalDateTime to, Class<? extends Reservation> clazz) {
        return reservationRepository.findAll(new ReservationBusyCheckSpecification(from, to, clazz));
    }

    /**
     *
     * @param reservation - The reservation we want to update/create
     * @return The new reservation
     */
    public Reservation update(Reservation reservation) {
        LocalDateTime from = reservation.getFrom();
        LocalDateTime to = reservation.getTo();
        EttUser currentPrincipal = EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository);
        // assign the correct status and check authorities in case of status change
        if (reservation.getId() == null) {
            reservation.setStatus(Reservation.ReservationStatus.PENDING);
        } else {
            Optional<Reservation> dr = reservationRepository.findById(reservation.getId());
            if (dr.isEmpty()) throw new RuntimeException("Cannot update non existing reservation!");
            if (!currentPrincipal.isAdmin() && (reservation.getStatus() == Reservation.ReservationStatus.REJECTED || reservation.getStatus() == Reservation.ReservationStatus.APPROVED)) {
                 throw new RuntimeException("You do not have the authorities to change reservation status!");
            }

        }

        if (from == null || to == null) {
            throw new RuntimeException("These is an empty mandatory field!");
        }

        if (from.isAfter(to) || from.isEqual(to)) {
            throw new RuntimeException("Cannot have a negative or equal timeslot as a reservation!");
        }

        Settings settings = settingsService.findSettings();
        if (ChronoUnit.DAYS.between(from, to) != 0) {
            throw new RuntimeException("Cannot reserve for more than 1 day");
        }
        
        List<DayScheduleException> fromDayScheduleExceptions = dayScheduleExceptionRepository.findAllByDate(from.toLocalDate());
        List<DayScheduleException> toDayScheduleExceptions = dayScheduleExceptionRepository.findAllByDate(to.toLocalDate());

        // in case a day schedule exception exists for that date use it instead of the active weekschedule one
        DaySchedule fromDaySchedule = fromDayScheduleExceptions != null && !fromDayScheduleExceptions.isEmpty() ?
                DaySchedule.fromDayScheduleException(fromDayScheduleExceptions.get(0)) : settings.getLoungeSchedule().getDaySchedule(from.getDayOfWeek());
        DaySchedule toDaySchedule = toDayScheduleExceptions != null && !toDayScheduleExceptions.isEmpty() ?
                DaySchedule.fromDayScheduleException(toDayScheduleExceptions.get(0)) : settings.getLoungeSchedule().getDaySchedule(to.getDayOfWeek());

        // data validations with settings
        if (!fromDaySchedule.isValidDateTime(from, to) && !toDaySchedule.isValidDateTime(from, to)) {
            throw new RuntimeException("Invalid from and to date times!");
        }

        if (ChronoUnit.MINUTES.between(from, to) > settings.getMaxBookingTimeLength()) {
            throw new RuntimeException("Booking time length exceeds the maximum!");
        }

        if (reservation.getReservee() == null) {
            reservation.setReservee(currentPrincipal);
        }

        List<? extends Reservation> conflictingReservations;
        if (reservation.instanceOf(DeviceReservation.class)) {
            DeviceReservation deviceReservation = (DeviceReservation) reservation.getThis();

             if (deviceReservation.getDevices() == null || deviceReservation.getDevices().isEmpty()) {
                 throw new RuntimeException("These is an empty mandatory field!");
             }

            if (deviceReservation.getDevices().stream().anyMatch(Device::isDisabled)) {
                throw new RuntimeException("The device you are trying to reserve is disabled!");
            }

            if (deviceReservation.getDevices().size() > settings.getMaxDevicesPerReservation()) {
                throw new RuntimeException("You exceed the maximum devices per reservation!");
            }

            conflictingReservations = reservationRepository.findAll(
                    new ReservationBusyCheckSpecification(
                            from,
                            to,
                            DeviceReservation.class,
                            deviceReservation.getDevices().stream().map(Device::getId).collect(Collectors.toList())
                    ));

            if (!conflictingReservations.isEmpty()) {
                throw new RuntimeException("There are conflicting reservations with the specified timeslot!");
            }

            if (settings.isAutoAcceptanceOfDeviceReservations()) deviceReservation.setStatus(Reservation.ReservationStatus.APPROVED);

            return reservationRepository.save(deviceReservation);
        } else if (reservation.instanceOf(FacilityReservation.class)) {
            FacilityReservation facilityReservation = (FacilityReservation) reservation;

            if (facilityReservation.getFacility() == null) {
                throw new RuntimeException("These is an empty mandatory field!");
            }

            if (facilityReservation.getFacility().isDisabled()) {
                throw new RuntimeException("The facility you are trying to reserve is disabled!");
            }

            conflictingReservations = reservationRepository.findAll(
                    new ReservationBusyCheckSpecification(
                            from,
                            to,
                            FacilityReservation.class,
                            facilityReservation.getFacility().getId()
                    ));
            if (!conflictingReservations.isEmpty()) {
                throw new RuntimeException("There are conflicting reservations with the specified timeslot!");
            }

            if (settings.isAutoAcceptanceOfFacilityReservations()) facilityReservation.setStatus(Reservation.ReservationStatus.APPROVED);

            return reservationRepository.save(facilityReservation);
        }
        throw new RuntimeException("Unknown type of reservation type!");
    }

    @Override
    public void delete(Long id) {
        EttUser currentPrincipal = EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository);
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            throw new RuntimeException("Non-existent reservation!");
        }
        if (!currentPrincipal.isAdmin() && !reservation.getReservee().getId().equals(currentPrincipal.getId())) {
            throw new RuntimeException("Cannot delete this reservation!");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.REJECTED || reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cannot delete reservations other than pending or approved!");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
    }

    /**
     * Changes the status of a reservation and sends an email notification
     * @param id - the id of the reservation to be changed
     * @param status - the state we want to change to
     * @return The changed reservation
     */
    public Reservation updateReservationStatus(Long id, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);

        if (reservation == null) {
            throw new RuntimeException("No such reservation exists");
        }

        // status flow check
        if (status == Reservation.ReservationStatus.APPROVED && reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new RuntimeException("Cannot update this reservation to this status!");
        } else if (status == Reservation.ReservationStatus.REJECTED && reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new RuntimeException("Cannot update this reservation to this status!");
        } else if (status == Reservation.ReservationStatus.CANCELLED && (reservation.getStatus() != Reservation.ReservationStatus.APPROVED && reservation.getStatus() != Reservation.ReservationStatus.PENDING)) {
            throw new RuntimeException("Cannot update this reservation to this status!");
        } else if (status == Reservation.ReservationStatus.PENDING) {
            throw new RuntimeException("Cannot update this reservation to this status!");
        }

        reservation.setStatus(status);

        MailEventListener.MailEvent mailEvent = null;
        if (status == Reservation.ReservationStatus.APPROVED) {
            mailEvent = MailEventListener.MailEvent.of(
                    reservation.getReservee().getEmail(),
                    "Approved reservation",
                    "Approved reservation test"
            );
        } else if (status == Reservation.ReservationStatus.REJECTED) {
            mailEvent = MailEventListener.MailEvent.of(
                    reservation.getReservee().getEmail(),
                    "Rejected reservation",
                    "Rejected reservation test"
            );
        } else if (status == Reservation.ReservationStatus.CANCELLED) {
            mailEvent = MailEventListener.MailEvent.of(
                    reservation.getReservee().getEmail(),
                    "Cancelled reservation",
                    "Cancelled reservation test"
            );
        }

        if (mailEvent != null) applicationEventPublisher.publishEvent(mailEvent);

        return reservation;
    }
}
