package kartingRM.Repositories;

import kartingRM.Entities.AppliedDiscount;
import kartingRM.Entities.Client;
import kartingRM.Entities.Discount;
import kartingRM.Entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AppliedDiscountRepository extends JpaRepository<AppliedDiscount, Long> {
    List<AppliedDiscount> findByInvoice(Invoice invoice);
    List<AppliedDiscount> findByClient(Client client);
    List<AppliedDiscount> findByDiscount(Discount discount);
}