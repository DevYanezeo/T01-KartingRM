package kartingRM.Servicios;


import kartingRM.Repositorios.KartRepositorios;
import kartingRM.Entidades.Kart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KartServicios {
    @Autowired
    private KartRepositorios kartRepositorio;

    public Kart registrarKart(String codigo, String modelo, String estado){
        Optional<Kart> kartExistente = kartRepositorio.findByCodigo(codigo);

        if(kartExistente.isPresent()) {
            throw new IllegalArgumentException("Duplicado");
        }
        Kart kart = new Kart(codigo, modelo, estado);
        return kartRepositorio.save(kart);
    }
}
