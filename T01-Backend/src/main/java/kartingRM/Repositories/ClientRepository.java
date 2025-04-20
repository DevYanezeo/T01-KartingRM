package kartingRM.Repositories;

import kartingRM.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    List<Client> findByMonthlyVisitsGreaterThanEqual(int visits);

    @Query("SELECT c FROM Client c WHERE MONTH(c.birthDate) = MONTH(CURRENT_DATE) AND DAY(c.birthDate) = DAY(CURRENT_DATE)")
    List<Client> findClientsWithBirthdayToday();

    @Transactional
    @Modifying
    @Query("UPDATE Client c SET c.monthlyVisits = c.monthlyVisits + 1 WHERE c.email = :email")
    void incrementMonthlyVisitsByEmail(String email);
}