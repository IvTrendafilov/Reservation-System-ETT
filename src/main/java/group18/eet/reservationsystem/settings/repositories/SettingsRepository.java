package group18.eet.reservationsystem.settings.repositories;

import group18.eet.reservationsystem.settings.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    boolean existsByLoungeScheduleId(Long id);
}
