package kartingRM.Services;


import kartingRM.Repositories.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PricingServices {
    @Autowired
    private PricingRepository pricingRepository;
    

}
