package group18.eet.reservationsystem.security.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EttUserDetailsRepository extends JpaRepository<EttUser, Long> {

    EttUser findByEmail(String email);

    @Query(value = "select * from ett_user where jsonb_exists(authorities, 'ADMIN')", nativeQuery = true)
    List<EttUser> findAllAdmins();

}
