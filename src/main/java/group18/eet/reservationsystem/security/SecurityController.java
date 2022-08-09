package group18.eet.reservationsystem.security;

import group18.eet.reservationsystem.exceptions.UnauthorizedException;
import group18.eet.reservationsystem.security.dtos.EttUserDto;
import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/userdetails")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SecurityController {

    private final EttUserDetailsRepository ettUserDetailsRepository;

    @GetMapping
    public EttUser getMyDetails() {
        return EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository);
    }

    @GetMapping("/admin")
    public List<EttUserDto> getAllAdmins() {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isAdmin()) throw new UnauthorizedException();
        return ettUserDetailsRepository.findAllAdmins().stream().map(EttUserDto::new).collect(Collectors.toList());
    }
    @DeleteMapping("/admin/{id}")
    public void deleteAdminById(@PathVariable("id") Long id) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isGlobalAdmin()) throw new UnauthorizedException();
        ettUserDetailsRepository.deleteById(id);
    }

    @PostMapping("/admin")
    public EttUserDto createAdmin(@RequestBody EttUserDto userDto) {
        if (!EttUserDetailsService.getCurrentPrincipal(ettUserDetailsRepository).isGlobalAdmin()) throw new UnauthorizedException();
        EttUser ettUser = ettUserDetailsRepository.save(new EttUser(
                userDto.getEmail(),
                userDto.getName(),
                List.of("USER", "ADMIN")
        ));
        return new EttUserDto(ettUser);
    }
}
