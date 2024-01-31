package projects.javadiplom.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import projects.javadiplom.entity.User;

@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {

    User findByLogin(String login);

}