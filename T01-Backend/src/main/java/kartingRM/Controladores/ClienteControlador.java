package kartingRM.Controladores;

import kartingRM.Entidades.Cliente;
import kartingRM.Servicios.ClienteServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "*")
public class ClienteControlador {
    @Autowired
    private ClienteServicios clienteServicio;

    @PostMapping("/registrarCliente")
    public Cliente addCliente(@RequestBody Cliente newCliente) {
        return clienteServicio.registrarCliente(
                newCliente.getNombreCliente(),
                newCliente.getTelefonoCliente(),
                newCliente.getCorreoCliente(),
                newCliente.getVisitasMensual(),
                newCliente.getFechaNacimiento()
        );
    }
}
