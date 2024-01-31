package projects.javadiplom.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projects.javadiplom.entity.User;
import projects.javadiplom.model.userModel;
import projects.javadiplom.repository.UsersRepository;

import java.util.ArrayList;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    FilesServiceImplementation filesService;

    @Autowired
    private UsersRepository userDao;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с таким логином не найден: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                new ArrayList<>());
    }

    public User save(userModel user) {
        User newUser = new User();
        newUser.setLogin(user.getLogin());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        filesService.createDir(user.getLogin());
        return userDao.save(newUser);

    }
}
