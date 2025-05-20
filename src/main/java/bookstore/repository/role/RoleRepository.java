package bookstore.repository.role;

import bookstore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRolesByName(Role.RoleName name);
}
