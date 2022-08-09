package group18.eet.reservationsystem.security.repositories;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<EttUser, Long> {
}
