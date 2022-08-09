package group18.eet.reservationsystem.reservable.facility.repository;

import group18.eet.reservationsystem.reservable.facility.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long>, JpaSpecificationExecutor<Facility> {
    List<Facility> findAllByDisabled(boolean disabled);
}
