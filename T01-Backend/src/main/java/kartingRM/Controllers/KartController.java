package kartingRM.Controllers;

import kartingRM.Entities.Kart;
import kartingRM.Services.KartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kart")
@CrossOrigin(origins = "*")
public class KartController {
    @Autowired
    private KartServices kartServices;

    @PostMapping("/registerKart")
    public Kart addKart(@RequestBody Kart newKart) {
        return kartServices.registerKart(
                newKart.getKartCode(),
                newKart.getModel(),
                newKart.getStatus()
        );
    }
}
