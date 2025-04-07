package kartingRM.Repositorios;

import kartingRM.Entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepositorios extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreoCliente(String correoCliente);
}
