package group18.eet.reservationsystem.pages.admin;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.security.services.UserService;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPagesController {

    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping()
    public String adminIndex() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/allbookings/allbookings.html";
    }

    @GetMapping("/pendingbookings")
    public String adminPendingBookings() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/pendingbookings/pendingbookings.html";
    }

    // Settings

    @GetMapping("/settings")
    public String adminSettings() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/settings/settings.html";
    }

    @GetMapping("/manual")
    public String adminManual() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/manual/manual.html";
    }

    @GetMapping("/interactivemap")
    public String adminInteractiveMap() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/interactivemap/interactivemap.html";
    }

    @GetMapping("/device/create")
    public String createDevice() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/device/createeditdevice/createeditdevice.html";
    }

    @GetMapping("/device/edit/{id}")
    public String editDevice() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/device/createeditdevice/createeditdevice.html";
    }

    @GetMapping("/device")
    public String device() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/device/device.html";
    }

    @GetMapping("/dayscheduleexception/create")
    public String createDayscheduleException() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/dayscheduleexception/createeditdayscheduleexception/createeditdayscheduleexception.html";
    }

    @GetMapping("/dayscheduleexception/edit/{id}")
    public String editDayscheduleException() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/dayscheduleexception/createeditdayscheduleexception/createeditdayscheduleexception.html";
    }

    @GetMapping("/dayscheduleexception")
    public String dayscheduleException() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/dayscheduleexception/dayscheduleexception.html";
    }

    @GetMapping("/devicetypes/create")
    public String createDeviceType() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/devicetypes/createeditdevicetypes/createeditdevicetypes.html";
    }

    @GetMapping("/devicetypes/edit/{id}")
    public String editDeviceTypes() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/devicetypes/createeditdevicetypes/createeditdevicetypes.html";
    }

    @GetMapping("/devicetypes")
    public String deviceTypes() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/devicetypes/devicetypes.html";
    }

    @GetMapping("/dayschedule")
    public String dayschedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/dayschedule/dayschedule.html";
    }

    @GetMapping("/dayschedule/create")
    public String createDaySchedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/dayschedule/createdayschedule/createdayschedule.html";
    }

    @GetMapping("/dayschedule/edit/{id}")
    public String editDaySchedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/dayschedule/createdayschedule/createdayschedule.html";
    }

    @GetMapping("/facility/create")
    public String createFacilityPage() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/facility/createeditfacility/createeditfacility.html";
    }

    @GetMapping("/facility/edit/{id}")
    public String editFacilityPage() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/facility/createeditfacility/createeditfacility.html";
    }

    @GetMapping("/facility")
    public String facility() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/facility/facility.html";
    }

    @GetMapping("/weekschedules")
    public String weekSchedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/weekschedules.html";
    }

    @GetMapping("/weekschedules/create")
    public String createWeekSchedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/createweekschedule/createweekschedule.html";
    }

    @GetMapping("/weekschedules/edit/{id}")
    public String editWeekSchedule() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/schedules/createweekschedule/createweekschedule.html";
    }
    @GetMapping("makeabooking")
    public String makeAbookingAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/makeabooking/makeabooking.html";
    }

    @GetMapping("/mybookings")
    public String myBookingsAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/mybookings/mybookings.html";
    }

    @GetMapping("/booking/device")
    public String deviceBookingAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/devicebooking/devicebooking.html";
    }

    @GetMapping("/bookingfacility/tournament")
    public String trainingFacilityAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/facilitybooking/facilitybooking.html";
    }

    @GetMapping("/bookingfacility/broadcast")
    public String broadcastFacilityAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/facilitybooking/facilitybooking.html";
    }

    @GetMapping("/admin/create")
    public String createAdmin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/admins/createeditadmin/createeditadmin.html";
    }

    @GetMapping("/booking/lounge")
    public String lounge() {
        return "/admin/loungebooking/loungebooking.html";
    }

    @GetMapping("/admin")
    public String admin() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return "/admin/admins/admin.html";
    }
}
