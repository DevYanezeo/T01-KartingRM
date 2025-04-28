package kartingRM.Repositories;

import kartingRM.Entities.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

    List<BusinessHour> findByDayOfWeek(DayOfWeek dayOfWeek);
}
