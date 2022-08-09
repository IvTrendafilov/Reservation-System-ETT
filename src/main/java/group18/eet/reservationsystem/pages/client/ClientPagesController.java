package group18.eet.reservationsystem.pages.client;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class ClientPagesController {

    @GetMapping("/")
    public String index() {
        return "/client/makeabooking/makeabooking.html";
    }

    @GetMapping("/mybookings")
    public String clientBookings() {
        return "/client/mybookings/mybookings.html";
    }

    @GetMapping("/settings")
    public String clientSettings() {
        return "/client/settings/settings.html";
    }

    @GetMapping("/notifications")
    public String clientNotifications() {
        return "/common/notifications/notifications.html";
    }

    @GetMapping("/booking/device")
    public String device() {
        return "/client/devicebooking/devicebooking.html";
    }

    @GetMapping("/booking/tournament/facility")
    public String trainingFacility() {
        return "/client/facilitybooking/facilitybooking.html";
    }

    @GetMapping("/booking/broadcast/facility")
    public String broadcastFacility() {
        return "/client/facilitybooking/facilitybooking.html";
    }


    @GetMapping("/booking/lounge")
    public String lounge() {
        return "/client/loungebooking/loungebooking.html";
    }



}
