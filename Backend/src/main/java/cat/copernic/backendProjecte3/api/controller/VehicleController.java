package cat.copernic.backendProjecte3.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.business.VehicleService;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private static final Logger log = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    // RF90: Llistar tots els vehicles
    @GetMapping
    public List<Vehicle> llistarTots() {
        log.info("Llistant tots els vehicles");
        return vehicleService.obtenirTots();
    }

    // RF91: Veure el detall d'un vehicle per matrícula
    @GetMapping("/{matricula}")
    public Vehicle obtenirDetall(@PathVariable("matricula") String matricula) {
        log.info("Obtenint detall del vehicle amb matricula: {}", matricula);
        return vehicleService.obtenirPerId(matricula);
    }

    @PostMapping
    public Vehicle crearVehicle(@RequestBody Vehicle vehicle) {
        log.info("Creant vehicle amb matricula: {}", vehicle.getMatricula());
        return vehicleService.guardarVehicle(vehicle);
    }

    @DeleteMapping("/eliminarVehicle/{matricula}")
    public void eliminarVehicle(@PathVariable String matricula) {
        log.warn("Eliminant vehicle amb matricula: {}", matricula);
        vehicleService.eliminarVehicle(matricula);
    }

    @PatchMapping("/{matricula}/baixa")
    public void donarDeBaixa(@PathVariable String matricula) {
        log.warn("Donant de baixa vehicle: {}", matricula);
        vehicleService.donarDeBaixaVehicle(matricula);
    }

    @PatchMapping("/{matricula}/alta")
    public void donarDeAlta(@PathVariable String matricula, @RequestParam String motiu) {
        log.info("Donant d'alta vehicle: {}. Motiu: {}", matricula, motiu);
        vehicleService.donarDeAltaVehicle(matricula, motiu);
    }

    @GetMapping("/disponibles")
    public List<Vehicle> llistarDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInici,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFi
    ) throws ReservaDatesNoValidsException {

        log.info("Consultant vehicles disponibles entre {} i {}", dataInici, dataFi);
        return vehicleService.obtenirDisponibles(dataInici, dataFi);
    }
}