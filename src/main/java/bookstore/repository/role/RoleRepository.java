package bookstore.repository.role;

import bookstore.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role getRolesByName(Role.RoleName name);
}
