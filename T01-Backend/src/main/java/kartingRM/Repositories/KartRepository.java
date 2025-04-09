package kartingRM.Repositories;

import kartingRM.Entities.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KartRepository extends JpaRepository<Kart, String> {
    Optional<Kart> findByCode(String kartCode);
}
