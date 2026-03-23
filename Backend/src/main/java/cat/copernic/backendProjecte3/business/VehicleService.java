/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author manel
 */
@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    public List<Vehicle> obtenirTots() {
        return vehicleRepo.findAll();
    }

    public Vehicle obtenirPerId(String matricula) {
        return vehicleRepo.findById(matricula)
                .orElseThrow(() -> new RuntimeException("Vehicle no trobat: " + matricula));
    }

    @Transactional
    public Vehicle guardarVehicle(Vehicle vehicle) {
        return vehicleRepo.save(vehicle);
    }

    @Transactional
    public void eliminarVehicle(String matricula) {
        vehicleRepo.deleteById(matricula);
    }

    public List<Vehicle> cercarVehiclesDisponibles(LocalDate inici, LocalDate fi, TipusVehicle tipus, String codiPostal) {
        // Assumeix una @Query al repositori que filtra per dates i lloc
        return vehicleRepo.findDisponibles(inici, fi, tipus, codiPostal);
    }

    @Transactional
    public void donarDeBaixaVehicle(String matricula) {
        Vehicle v = obtenirPerId(matricula);

        // Validació de negoci: No es pot donar de baixa si té reserves futures
        boolean teReservesFutures = reservaRepo.existsByVehicleAndDataFiAfter(v, LocalDate.now());
        if (teReservesFutures) {
            throw new IllegalStateException("El vehicle té reserves compromeses i no es pot desactivar.");
        }

        v.setEstatVehicle(EstatVehicle.BAIXA);
        vehicleRepo.save(v);
    }

    @Transactional
    public void donarDeAltaVehicle(String matricula, String motiuManteniment) {
        Vehicle v = obtenirPerId(matricula);

        v.setEstatVehicle(EstatVehicle.ALTA);
        vehicleRepo.save(v);
    }

    public List<Vehicle> obtenirDisponibles(LocalDate inici, LocalDate fi) throws ReservaDatesNoValidsException {

        if (inici == null || fi == null) {
            throw new ReservaDatesNoValidsException("Dates obligatòries");
        }
        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        // Opción rápida o pro (elige una)
        return vehicleRepo.findVehiclesDisponibles(inici, fi);
    }
}
