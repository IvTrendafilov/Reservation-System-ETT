package group18.eet.reservationsystem;

import group18.eet.reservationsystem.reservable.device.Device;
import group18.eet.reservationsystem.reservable.device.DeviceType;
import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.reservation.entities.DeviceReservation;
import group18.eet.reservationsystem.reservation.entities.FacilityReservation;
import group18.eet.reservationsystem.reservation.entities.Reservation;
import group18.eet.reservationsystem.reservation.repository.ReservationRepository;
import group18.eet.reservationsystem.reservation.services.ReservationService;
import group18.eet.reservationsystem.schedule.entities.DaySchedule;
import group18.eet.reservationsystem.schedule.entities.WeekSchedule;
import group18.eet.reservationsystem.schedule.repositories.DayScheduleExceptionRepository;
import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import group18.eet.reservationsystem.settings.entities.Settings;
import group18.eet.reservationsystem.settings.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationTests {

    @InjectMocks private ReservationService reservationService;
    @Mock private EttUserDetailsService ettUserDetailsService;
    @Mock private EttUserDetailsRepository ettUserDetailsRepository;
    @Mock private DayScheduleExceptionRepository dayScheduleExceptionRepository;
    @Mock private SettingsService settingsService;
    @Mock private ReservationRepository reservationRepository;


    @BeforeEach
    public void init() {
        EttUser user = createUser();
        DaySchedule daySchedule = createDaySchedule();
        Settings settings = createSettings(daySchedule, 180, 3, false, false);
        DefaultOidcUser defaultOidcUser = createCurrentPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(defaultOidcUser, defaultOidcUser));

        when(EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository)).thenReturn(user);
        when(ettUserDetailsRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(settingsService.findSettings()).thenReturn(settings);
        when(dayScheduleExceptionRepository.findAllByDate(LocalDate.of(1, 1, 1))).thenReturn(Collections.emptyList());
        when(dayScheduleExceptionRepository.findAllByDate(LocalDate.of(1, 1, 2))).thenReturn(Collections.emptyList());
        when(reservationRepository.findAll(Mockito.any(Specification.class))).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).then(returnsFirstArg());

    }

    /**
     * Test for normal reservations without any errors in it
     */
    @Test
    public void testNormalDeviceReservation() {
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(new Device(1L, "test", false, false, new DeviceType(1L, "test", "test"), null, null)));

        Reservation r = reservationService.update(reservation);
        assert r != null;
    }

    @Test
    public void testNormalFacilityReservation() {
        FacilityReservation reservation = new FacilityReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setFacility(new Facility(1L, "test", List.of("BROADCAST"), 1, false, false, null));

        Reservation r = reservationService.update(reservation);
        assert r != null;
    }

    /**
     * Test for reservations which interfere with another reservation
     */
    @Test
    public void testInterferingHoursDeviceReservation() {
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(new Device(1L, "test", false, false, new DeviceType(1L, "test", "test"), null, null)));

        when(reservationRepository.findAll(Mockito.any(Specification.class))).thenReturn(List.of(reservation));

        DeviceReservation interferingReservation = new DeviceReservation();
        interferingReservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        interferingReservation.setTo(LocalDateTime.of(2022, 2, 2, 11, 0));
        interferingReservation.setStatus(Reservation.ReservationStatus.PENDING);
        interferingReservation.setDevices(List.of(new Device(1L, "test", false, false, new DeviceType(1L, "test", "test"), null, null)));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(interferingReservation));
        assertEquals("There are conflicting reservations with the specified timeslot!", exception.getMessage());
    }

    @Test
    public void testInterferingHoursFacilityReservation() {
        FacilityReservation reservation = new FacilityReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setFacility(new Facility(1L, "test", List.of("BROADCAST"), 1, false, false, null));

        when(reservationRepository.findAll(Mockito.any(Specification.class))).thenReturn(List.of(reservation));

        FacilityReservation interferingReservation = new FacilityReservation();
        interferingReservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        interferingReservation.setTo(LocalDateTime.of(2022, 2, 2, 11, 0));
        interferingReservation.setStatus(Reservation.ReservationStatus.PENDING);
        interferingReservation.setFacility(new Facility(1L, "test", List.of("BROADCAST"), 1, false, false, null));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(interferingReservation));
        assertEquals("There are conflicting reservations with the specified timeslot!", exception.getMessage());
    }

    @Test
    public void testFromAfterToReservation() {
        Reservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 11, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("Cannot have a negative or equal timeslot as a reservation!", exception.getMessage());
    }

    @Test
    public void testTimeslotLargerThanMaxLength() {
        when(settingsService.findSettings()).thenReturn(createSettings(createDaySchedule(), 120, 2, false, false));

        Reservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 11, 31));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("Booking time length exceeds the maximum!", exception.getMessage());
    }

    @Test
    public void testInterferingDayScheduleReservation() {
        when(settingsService.findSettings()).thenReturn(createSettings(createDaySchedule(), 120, 2, false, false));

        // the lounge is closed from 13:00 to 14:00
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 13, 0));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 15, 0));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(new Device(1L, "test", false, false, new DeviceType(1L, "test", "test"), null, null)));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("Invalid from and to date times!", exception.getMessage());
    }

    @Test
    public void testDisabledDeviceReservation() {
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(new Device(1L, "test", true, false, new DeviceType(1L, "test", "test"), null, null)));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("The device you are trying to reserve is disabled!", exception.getMessage());
    }

    @Test
    public void testDisabledFacilityReservation() {
        FacilityReservation reservation = new FacilityReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setFacility(new Facility(1L, "test", List.of("BROADCAST"), 1, true, false, null));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("The facility you are trying to reserve is disabled!", exception.getMessage());
    }

    @Test
    public void testMoreThanAllowedDeviceNumber() {
        when(settingsService.findSettings()).thenReturn(createSettings(createDaySchedule(), 120, 1, false, false));

        // the lounge is closed from 13:00 to 14:00
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 11, 0));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 12, 0));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(
                new Device(1L, "test1", false, false, new DeviceType(1L, "test", "test"), null, null),
                new Device(2L, "test2", false, false, new DeviceType(1L, "test", "test"), null, null)
        ));

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.update(reservation));
        assertEquals("You exceed the maximum devices per reservation!", exception.getMessage());
    }

    @Test
    public void testDeviceReservationAutoAcceptance() {
        when(settingsService.findSettings()).thenReturn(createSettings(createDaySchedule(), 120, 2, true, false));

        // the lounge is closed from 13:00 to 14:00
        DeviceReservation reservation = new DeviceReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 11, 0));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 12, 0));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setDevices(List.of(
                new Device(1L, "test1", false, false, new DeviceType(1L, "test", "test"), null, null),
                new Device(2L, "test2", false, false, new DeviceType(1L, "test", "test"), null, null)
        ));

        Reservation r = reservationService.update(reservation);
        assertEquals(r.getStatus(), Reservation.ReservationStatus.APPROVED);
    }

    @Test
    public void testFacilityReservationAutoAcceptance() {
        when(settingsService.findSettings()).thenReturn(createSettings(createDaySchedule(), 120, 1, false, true));

        FacilityReservation reservation = new FacilityReservation();
        reservation.setFrom(LocalDateTime.of(2022, 2, 2, 9, 30));
        reservation.setTo(LocalDateTime.of(2022, 2, 2, 10, 30));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setFacility(new Facility(1L, "test", List.of("BROADCAST"), 1, false, false, null));

        Reservation r = reservationService.update(reservation);
        assertEquals(r.getStatus(), Reservation.ReservationStatus.APPROVED);
    }

    public EttUser createUser() {
        return new EttUser("test@gmail.com", "Test Name", List.of("USER", "ADMIN"));
    }

    public DefaultOidcUser createCurrentPrincipal(EttUser user) {
        return new DefaultOidcUser(null, new OidcIdToken("test", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new HashMap<>(){{ put("email",  user.getEmail()); }}), new OidcUserInfo(new HashMap<>(){{ put("email",  user.getEmail()); put("sub",  user.getEmail()); }}));
    }

    public Settings createSettings(DaySchedule daySchedule, Integer maxBookingTimeLength, Integer maxDevicesPerReservation, boolean deviceAutoAccept, boolean facilityAutoAccept) {
        return new Settings(1L, deviceAutoAccept, facilityAutoAccept, maxDevicesPerReservation, maxBookingTimeLength, new WeekSchedule(1L, "test", daySchedule, daySchedule, daySchedule, daySchedule, daySchedule, daySchedule, daySchedule));
    }

    public DaySchedule createDaySchedule() {
        return new DaySchedule(1L, "test", false, List.of(LocalTime.of(9, 0), LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(18, 0)));
    }
}
