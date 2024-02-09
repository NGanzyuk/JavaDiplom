package projects.javadiplom.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projects.javadiplom.entity.User;
import projects.javadiplom.model.UserModel;
import projects.javadiplom.repository.UsersRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {


    private final FilesServiceImplementation filesService;

    private final UsersRepository userDao;

    private final PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с таким логином не найден: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                new ArrayList<>());
    }

    public User save(UserModel user) {
        User newUser = new User();
        newUser.setLogin(user.getLogin());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        filesService.createDir(user.getLogin());
        return userDao.save(newUser);

    }
}
