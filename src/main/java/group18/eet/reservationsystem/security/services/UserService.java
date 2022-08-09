package group18.eet.reservationsystem.security.services;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import group18.eet.reservationsystem.utils.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class UserService extends BaseService<EttUser, EttUserDetailsRepository> {

    private final EttUserDetailsRepository userRepository;

    @Autowired
    public UserService(EttUserDetailsRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }
}
