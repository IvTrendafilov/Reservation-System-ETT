package group18.eet.reservationsystem.reservable.facility.dtos;

import group18.eet.reservationsystem.reservable.facility.Facility;
import group18.eet.reservationsystem.reservable.facility.services.FacilityService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    private Long id;
    private String name;
    private List<String> facilityType;
    private Integer roomId;
    private boolean disabled;
    private boolean reserved;

    public FacilityDto(Facility facility) {
        if (facility == null) throw new RuntimeException("this entity does not exist");
        this.id = facility.getId();
        this.name = facility.getName();
        this.facilityType = facility.getFacilityType().stream().map(Facility.FacilityType::getText).collect(Collectors.toList());
        this.disabled = facility.isDisabled();
        this.reserved = facility.isReserved();
        this.roomId = facility.getRoomId();
    }

   public Facility toEntity(FacilityService facilityService) {
        Facility facility = new Facility();
        if (this.id != null) facility = facilityService.findOrNull(id);
        // TODO: If id is non existent there will be null pointer !!
       facility.setName(this.name);
       facility.setFacilityType(this.facilityType.stream().map(Facility.FacilityType::fromText).collect(Collectors.toList()));
       facility.setDisabled(this.disabled);
       facility.setReserved(this.reserved);
       facility.setRoomId(this.roomId);
       return facility;
   }
}
