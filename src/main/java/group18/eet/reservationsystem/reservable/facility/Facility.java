package group18.eet.reservationsystem.reservable.facility;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.vladmihalcea.hibernate.type.json.JsonType;
import group18.eet.reservationsystem.reservable.facility.dtos.FacilityDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @Type(type = "json")
    private List<String> facilityType;
    private Integer roomId;
    private boolean disabled;
    private boolean reserved;
    private Instant deletedOn;

    public void setRoomId(Integer id) {
        if (!Arrays.asList(ROOM_IDS).contains(id)) {
            throw new RuntimeException("Non existent room");
        }
        this.roomId = id;

    }

    public List<FacilityType> getFacilityType() {
        return facilityType.stream().map(FacilityType::fromText).collect(Collectors.toList());
    }

    public void setFacilityType(List<FacilityType> facilityType) {
        this.facilityType = facilityType.stream().map(FacilityType::getText).collect(Collectors.toList());;
    }

    public static final Integer[] ROOM_IDS = new Integer[] {
            1, 2, 3, 4, 7, 8, 9, 10, 12
    };

    public enum FacilityType {
        EVENT("EVENT"),
        BROADCAST("BROADCAST"),
        TOURNAMENT("TOURNAMENT");

        @Getter
        private final String text;

        FacilityType(String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return text;
        }

        public static FacilityType fromText(String text) {
            if(text == null) {
                throw new IllegalArgumentException();
            }
            for(FacilityType v : values()) {
                if(text.equals(v.getText())) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
