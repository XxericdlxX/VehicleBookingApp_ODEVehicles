/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.api.controller;

import cat.copernic.backendProjecte3.api.dto.reserva.ReservaCancelResponse;
import cat.copernic.backendProjecte3.api.dto.reserva.ReservaCreateRequest;
import cat.copernic.backendProjecte3.api.dto.reserva.ReservaDetallResponse;
import cat.copernic.backendProjecte3.api.dto.reserva.ReservaResponse;
import cat.copernic.backendProjecte3.business.ReservaService;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.exceptions.ReservaNoAnulableException;
import cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encarregat de gestionar les operacions relacionades amb les
 * reserves de vehicles.
 *
 * Aquest controlador exposa diferents endpoints per: - Crear una reserva -
 * Llistar les reserves de l’usuari autenticat - Veure el detall d’una reserva -
 * Anul·lar una reserva - Obtenir una previsualització del cost d’una reserva
 *
 * Utilitza el servei {@link ReservaService} per aplicar la lògica de negoci i
 * el repositori {@link VehicleRepository} per obtenir informació dels vehicles.
 *
 * Totes les operacions utilitzen la capçalera HTTP "X-User" per identificar
 * l’usuari que realitza la petició.
 *
 * @author orjon
 */
@RestController
@RequestMapping("/api/reserves")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private VehicleRepository vehicleRepo;

    /**
     * Crea una nova reserva per a un vehicle.
     *
     * L’usuari autenticat es rep mitjançant la capçalera HTTP "X-User". Es
     * valida que les dades de la petició siguin correctes abans de delegar la
     * creació de la reserva al servei corresponent.
     *
     * @param userEmail correu electrònic de l’usuari autenticat.
     * @param req objecte que conté les dades necessàries per crear la reserva
     * (matrícula del vehicle i dates d’inici i fi).
     *
     * @return un {@link ResponseEntity} amb l’objecte {@link ReservaResponse}
     * que representa la reserva creada.
     *
     * @throws ReservaDatesNoValidsException si les dates de la reserva no són
     * vàlides.
     * @throws VehicleNoDisponibleException si el vehicle no està disponible en
     * el període indicat.
     * @throws AccesDenegatException si l’usuari no té permisos.
     * @throws DadesNoTrobadesException si no es troba alguna dada necessària.
     */
    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(
            @RequestHeader("X-User") String userEmail,
            @RequestBody ReservaCreateRequest req
    ) throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {

        // Validación mínima de request
        if (req == null || req.getMatricula() == null || req.getDataInici() == null || req.getDataFi() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Para esta fase: el cliente crea su propia reserva (emailClient = userEmail)
        Reserva reserva = reservaService.crearReserva(
                userEmail,
                req.getMatricula(),
                req.getDataInici(),
                req.getDataFi(),
                userEmail
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(reserva));
    }

    /**
     * Converteix una entitat {@link Reserva} en un DTO {@link ReservaResponse}.
     *
     * Aquest mètode transforma les dades internes de la reserva en una
     * estructura adequada per ser retornada al client de l’API.
     *
     * @param r reserva que es vol convertir.
     * @return objecte {@link ReservaResponse} amb la informació de la reserva.
     */
    private ReservaResponse toResponse(Reserva r) {
        ReservaResponse dto = new ReservaResponse();
        dto.setIdReserva(r.getIdReserva());
        dto.setClientEmail(r.getClient().getEmail());
        dto.setVehicleMatricula(r.getVehicle().getMatricula());
        dto.setDataInici(r.getDataInici());
        dto.setDataFi(r.getDataFi());
        dto.setImportTotal(r.getImportTotal());
        dto.setFianca(r.getFianca());
        return dto;
    }

    /**
     * Retorna la llista de reserves associades a l’usuari autenticat.
     *
     * Permet ordenar les reserves per data en ordre ascendent o descendent.
     *
     * @param userEmail correu electrònic de l’usuari autenticat.
     * @param order tipus d’ordenació de les reserves (asc o desc).
     *
     * @return una llista de reserves en format {@link ReservaResponse}.
     *
     * @throws AccesDenegatException si l’usuari no té permisos.
     * @throws DadesNoTrobadesException si no es troben reserves.
     */

    @GetMapping("/me")
    public ResponseEntity<List<ReservaResponse>> llistarMeves(
            @RequestHeader("X-User") String userEmail,
            @RequestParam(defaultValue = "desc") String order
    ) throws AccesDenegatException, DadesNoTrobadesException {

        List<Reserva> reserves = reservaService.veureReservesPropies(userEmail, order);

        List<ReservaResponse> dtos = reserves.stream()
                .map(r -> {
                    ReservaResponse dto = new ReservaResponse();
                    dto.setIdReserva(r.getIdReserva());
                    dto.setClientEmail(r.getClient().getEmail());
                    dto.setVehicleMatricula(r.getVehicle().getMatricula());
                    dto.setDataInici(r.getDataInici());
                    dto.setDataFi(r.getDataFi());
                    dto.setImportTotal(r.getImportTotal());
                    dto.setFianca(r.getFianca());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Obté el detall complet d’una reserva concreta.
     *
     * Inclou també informació del vehicle associat a la reserva.
     *
     * @param idReserva identificador de la reserva.
     * @param userEmail correu electrònic de l’usuari autenticat.
     *
     * @return un {@link ReservaDetallResponse} amb tota la informació de la
     * reserva.
     *
     * @throws DadesNoTrobadesException si la reserva no existeix.
     * @throws AccesDenegatException si l’usuari no té permisos per veure-la.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDetallResponse> veureDetall(
            @PathVariable("id") Long idReserva,
            @RequestHeader("X-User") String userEmail
    ) throws DadesNoTrobadesException, AccesDenegatException {

        Reserva r = reservaService.veureDetallReserva(idReserva, userEmail);

        ReservaDetallResponse dto = new ReservaDetallResponse();
        dto.setIdReserva(r.getIdReserva());
        dto.setClientEmail(r.getClient().getEmail());
        dto.setDataInici(r.getDataInici());
        dto.setDataFi(r.getDataFi());
        dto.setImportTotal(r.getImportTotal());
        dto.setFianca(r.getFianca());

        Vehicle v = r.getVehicle();
        dto.setVehicleMatricula(v.getMatricula());
        dto.setVehicleTipus(v.getTipusVehicle() != null ? v.getTipusVehicle().name() : null);
        dto.setVehicleMotor(v.getMotor());
        dto.setVehiclePotencia(v.getPotencia());
        dto.setVehicleColor(v.getColor());
        dto.setVehiclePreuHora(v.getPreuHora());
        dto.setVehicleFoto(v.getRutaDocumentacioPrivada()); // “foto”

        return ResponseEntity.ok(dto);
    }

    /**
     * Anul·la una reserva existent.
     *
     * Només es permet anul·lar una reserva si aquesta encara no ha començat.
     *
     * @param idReserva identificador de la reserva a anul·lar.
     * @param userEmail correu electrònic de l’usuari autenticat.
     *
     * @return un {@link ReservaCancelResponse} amb la informació de la
     * cancel·lació.
     *
     * @throws DadesNoTrobadesException si la reserva no existeix.
     * @throws AccesDenegatException si l’usuari no té permisos.
     * @throws ReservaNoAnulableException si la reserva no es pot anul·lar.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservaCancelResponse> anulLar(
            @PathVariable("id") Long idReserva,
            @RequestHeader("X-User") String userEmail
    ) throws DadesNoTrobadesException, AccesDenegatException, ReservaNoAnulableException {

        ReservaCancelResponse resp = reservaService.anulLarReserva(idReserva, userEmail);
        return ResponseEntity.ok(resp);
    }

    /**
     * Calcula una previsualització del cost d’una reserva sense crear-la
     * realment.
     *
     * Aquest endpoint permet mostrar a l’usuari l’import total i la fiança
     * abans de confirmar la reserva.
     *
     * @param req dades de la reserva que es vol calcular.
     *
     * @return un {@link ReservaResponse} amb el càlcul del cost de la reserva.
     *
     * @throws ReservaDatesNoValidsException si les dates indicades no són
     * vàlides.
     * @throws DadesNoTrobadesException si el vehicle no existeix.
     */
    @PostMapping("/preview")
    public ResponseEntity<ReservaResponse> preview(@RequestBody ReservaCreateRequest req)
            throws ReservaDatesNoValidsException, DadesNoTrobadesException {

        if (req == null || req.getMatricula() == null || req.getDataInici() == null || req.getDataFi() == null) {
            return ResponseEntity.badRequest().build();
        }

        Vehicle v = vehicleRepo.findById(req.getMatricula())
                .orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

        LocalDate inici = req.getDataInici();
        LocalDate fi = req.getDataFi();

        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici abans que data final!!");
        }

        long dies = ChronoUnit.DAYS.between(inici, fi);
        dies = (dies == 0 ? 1 : dies);

        BigDecimal importTotal = v.getPreuHora()
                .multiply(BigDecimal.valueOf(24))
                .multiply(BigDecimal.valueOf(dies));

        ReservaResponse resp = new ReservaResponse();
        resp.setVehicleMatricula(v.getMatricula());
        resp.setDataInici(inici);
        resp.setDataFi(fi);
        resp.setImportTotal(importTotal);

        resp.setFianca(v.getFiancaEstandard());

        // idReserva y clientEmail pueden quedar null en preview
        return ResponseEntity.ok(resp);
    }
}
