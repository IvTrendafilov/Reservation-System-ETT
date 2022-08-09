package group18.eet.reservationsystem.reservable.device;

import com.vladmihalcea.hibernate.type.json.JsonType;
import group18.eet.reservationsystem.reservable.Position;
import group18.eet.reservationsystem.utils.Thisable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.Instant;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device implements Thisable<Device> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String code;
    private boolean disabled;
    private boolean reserved;

    @ManyToOne
    private DeviceType type;

    @Type(type = "json")
    private Position position;

    private Instant deletedOn;

}
