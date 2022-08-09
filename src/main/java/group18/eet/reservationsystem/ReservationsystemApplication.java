package group18.eet.reservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ReservationsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationsystemApplication.class, args);
    }

}
