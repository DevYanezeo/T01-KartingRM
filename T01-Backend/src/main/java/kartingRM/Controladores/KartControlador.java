package kartingRM.Controladores;
import kartingRM.Entidades.Kart;
import kartingRM.Servicios.KartServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/kart")
@CrossOrigin(origins = "*")
public class KartControlador {
    @Autowired
    private KartServicios kartServicios;

    @PostMapping("/registrarKart")
    public Kart addKart(@RequestBody Kart newkart) {
        return kartServicios.registrarKart(
            newkart.getCodigo(),
            newkart.getModelo(),
            newkart.getEstado()
        );
    }
}
