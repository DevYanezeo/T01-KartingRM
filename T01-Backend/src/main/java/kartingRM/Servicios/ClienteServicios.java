package kartingRM.Servicios;

import kartingRM.Entidades.Cliente;
import kartingRM.Repositorios.ClienteRepositorios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ClienteServicios{
    @Autowired
    private ClienteRepositorios clienteRepositorio;

    public Cliente registrarCliente(String nombreCliente, String telefonoCliente, String correoCliente, int visitasMensual, Date fechaNacimiento){
        Optional<Cliente> clienteExistente = clienteRepositorio.findByCorreoCliente(correoCliente);

        if(clienteExistente.isPresent()){
            Cliente cliente = clienteExistente.get();
        }
        Cliente cliente = new Cliente(nombreCliente, telefonoCliente, correoCliente, visitasMensual, fechaNacimiento);
        return clienteRepositorio.save(cliente);

    }
}
