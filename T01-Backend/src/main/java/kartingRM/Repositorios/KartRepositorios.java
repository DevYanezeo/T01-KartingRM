package kartingRM.Repositorios;

import kartingRM.Entidades.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KartRepositorios extends JpaRepository<Kart, Long> {
    Optional<Kart> findByCodigo(String codigo);
}
