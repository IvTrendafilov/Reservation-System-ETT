package group18.eet.reservationsystem.security.dtos;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EttUserDto {
    private Long id;
    private String email;
    private String name;

    public EttUserDto(EttUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }

   public EttUser toEntity(EttUserDetailsService ettUserDetailsService) {
        if (this.id == null) throw new RuntimeException("Non existant user");
        EttUser user = ettUserDetailsService.findById(this.id);
        if (user == null) throw new RuntimeException("Non existant user");

        return user;

    }
}
