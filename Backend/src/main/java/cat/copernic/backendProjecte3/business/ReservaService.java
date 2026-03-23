/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.api.dto.reserva.ReservaCancelResponse;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.exceptions.ReservaNoAnulableException;
import cat.copernic.backendProjecte3.exceptions.ReservaNoTrobadaException;
import cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.EmailService;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Servei encarregat de gestionar tota la lògica de negoci relacionada amb les
 * reserves de vehicles.
 *
 * Aquesta classe implementa les funcionalitats principals del sistema de
 * reserves: - Creació de reserves - Consulta de reserves - Consulta de reserves
 * d'un client - Anul·lació de reserves - Càlcul del cost d'una reserva
 *
 * També s'encarrega de: - Validar dates de reserva - Comprovar disponibilitat
 * dels vehicles - Controlar permisos segons el rol de l'usuari - Enviar
 * notificacions per correu electrònic
 *
 * Utilitza diferents repositoris per accedir a la base de dades i altres
 * serveis auxiliars per gestionar funcionalitats addicionals.
 *
 * @author manel
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private UserLogic userLogic;
    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    /**
     * Retorna totes les reserves registrades al sistema.
     *
     * @return llista de totes les reserves
     */
    public List<Reserva> obtenirTotes() {
        return reservaRepo.findAll();
    }

    /**
     * Obté una reserva a partir del seu identificador.
     *
     * @param id identificador de la reserva
     * @return reserva corresponent a l'identificador indicat
     * @throws ReservaNoTrobadaException si la reserva no existeix
     */
    public Reserva obtenirPerId(Long id) throws ReservaNoTrobadaException {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new ReservaNoTrobadaException("Reserva no trobada: " + id));
    }

    /**
     * Retorna totes les reserves associades a un client concret.
     *
     * @param email correu electrònic del client
     * @return llista de reserves del client
     */
    public List<Reserva> obtenirPerClient(String email) {
        return reservaRepo.findByClient_Email(email);
    }

    /**
     * Elimina una reserva del sistema.
     *
     * @param id identificador de la reserva que es vol eliminar
     */
    @Transactional
    public void eliminarReserva(Long id) {
        reservaRepo.deleteById(id);
    }

    /**
     * Crea una nova reserva per a un vehicle en un període de dates determinat.
     *
     * Aquest mètode valida: - el rol de l'usuari - la validesa de les dates -
     * la disponibilitat del vehicle - l'existència del client i del vehicle
     *
     * També calcula l'import total de la reserva i la fiança associada. Un cop
     * creada la reserva, s'envia un correu electrònic de confirmació al client.
     *
     * @param emailClient correu electrònic del client que fa la reserva
     * @param matricula matrícula del vehicle reservat
     * @param inici data d'inici de la reserva
     * @param fi data de finalització de la reserva
     * @param userName usuari que realitza l'operació
     *
     * @return reserva creada i guardada a la base de dades
     *
     * @throws ReservaDatesNoValidsException si les dates de la reserva no són
     * vàlides
     * @throws VehicleNoDisponibleException si el vehicle no està disponible
     * @throws AccesDenegatException si l'usuari no té permisos
     * @throws DadesNoTrobadesException si el client o el vehicle no existeixen
     */
    @Transactional
    public Reserva crearReserva(String emailClient, String matricula, LocalDate inici, LocalDate fi, String userName) throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {

        //control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow();
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        //Validacio dates
        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        // Validació disponibilitat
        List<Reserva> reserves = reservaRepo.findReservasSolapadas(matricula, inici, fi);
        if (!reserves.isEmpty()) {
            throw new ReservaDatesNoValidsException("Vehicle no disponible en aquestes dates");
        }

        // client i vehicle
        Client client = clientRepo.findById(emailClient).orElseThrow(() -> new DadesNoTrobadesException("Client no trobat"));
        Vehicle vehicle = vehicleRepo.findById(matricula).orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

        EstatVehicle estat = vehicle.getEstatVehicle();
        if (estat == null) {
            throw new VehicleNoDisponibleException("El vehicle no té estat assignat");
        }
        if (EstatVehicle.BAIXA.equals(vehicle.getEstatVehicle())) {
            throw new VehicleNoDisponibleException("El vehicle està fora de servei");
        }

        // creem reserva
        Reserva reserva = new Reserva();
        reserva.setClient(client);
        reserva.setVehicle(vehicle);
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        // calculem dies entre dates
        long dies = ChronoUnit.DAYS.between(inici, fi);
        // menys d'un dia és un dia sencer
        dies = (dies == 0 ? 1 : dies);

        // el preu hora per 24 hores i pels dies de llogier
        BigDecimal importTotal = vehicle.getPreuHora().multiply(new BigDecimal(24)).multiply(new BigDecimal(dies));

        reserva.setImportTotal(importTotal);
        reserva.setFianca(vehicle.getFiancaEstandard());

        Reserva guardada = reservaRepo.save(reserva);

        logger.info("Reserva creada correctament. ID={} Client={} Vehicle={} Inici={} Fi={} Import={} Fianca={}",
                guardada.getIdReserva(),
                client.getEmail(),
                vehicle.getMatricula(),
                inici,
                fi,
                guardada.getImportTotal(),
                guardada.getFianca());
        try {
            String subject = "Confirmació de reserva - ODE COTXES";

            String body = """
    Hola %s,

    La teva reserva s'ha creat correctament.

    Codi reserva: %d
    Vehicle: %s
    Data inici: %s
    Data fi: %s
    Import total: %s €
    Fiança: %s €

    Gràcies per confiar en nosaltres.

    ODE COTXES
    """.formatted(
                    client.getEmail(),
                    guardada.getIdReserva(),
                    vehicle.getMatricula(),
                    inici,
                    fi,
                    guardada.getImportTotal(),
                    guardada.getFianca()
            );

            emailServiceImpl.sendSimpleMessage(
                    client.getEmail(),
                    subject,
                    body
            );

        } catch (Exception e) {
            logger.error("Error enviant email de reserva", e);
        }

        return guardada;
    }

    /**
     * Elimina una reserva existent del sistema.
     *
     * Només es permet l'operació si l'usuari té un rol autoritzat.
     *
     * @param idReserva identificador de la reserva
     * @param userName usuari que realitza l'operació
     *
     * @throws ReservaNoTrobadaException si la reserva no existeix
     * @throws AccesDenegatException si l'usuari no té permisos
     */
    @Transactional
    public void anularReserva(Long idReserva, String userName) throws ReservaNoTrobadaException, AccesDenegatException {

        //control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow();
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        Reserva reserva = obtenirPerId(idReserva);

        reservaRepo.delete(reserva);
    }

    /**
     * Retorna les reserves associades a un client autenticat.
     *
     * Permet ordenar les reserves per data d'inici en ordre ascendent o
     * descendent.
     *
     * @param userEmail correu electrònic del client
     * @param order tipus d'ordenació (asc o desc)
     *
     * @return llista de reserves del client
     *
     * @throws AccesDenegatException si l'usuari no té permisos
     * @throws DadesNoTrobadesException si no es troben reserves
     */
    public List<Reserva> veureReservesPropies(String userEmail, String order)
            throws AccesDenegatException, DadesNoTrobadesException {

        UserRole rol = userLogic.getRole(userEmail).orElseThrow();
        if (rol != UserRole.CLIENT) {
            throw new AccesDenegatException("Només CLIENT pot veure les seves reserves");
        }

        if (order.equalsIgnoreCase("asc")) {
            return reservaRepo.findByClientEmailOrderByDataIniciAsc(userEmail);
        } else {
            return reservaRepo.findByClientEmailOrderByDataIniciDesc(userEmail);
        }
    }

    /**
     * Obté el detall complet d'una reserva.
     *
     * També valida que l'usuari tingui permisos per accedir a la reserva.
     *
     * @param idReserva identificador de la reserva
     * @param userEmail correu electrònic de l'usuari autenticat
     *
     * @return reserva amb tota la informació associada
     *
     * @throws DadesNoTrobadesException si la reserva no existeix
     * @throws AccesDenegatException si l'usuari no té permisos
     */
    public Reserva veureDetallReserva(Long idReserva, String userEmail)
            throws DadesNoTrobadesException, AccesDenegatException {

        UserRole rol = userLogic.getRole(userEmail)
                .orElseThrow(() -> new AccesDenegatException("Usuari sense rol"));

        // Carreguem la reserva (si tens findDetallById, millor)
        Reserva reserva = reservaRepo.findDetallById(idReserva)
                .orElseThrow(() -> new DadesNoTrobadesException("Reserva no trobada"));

        // CONTROL ACCÉS
        if (rol == UserRole.CLIENT) {
            String emailReserva = reserva.getClient().getEmail();
            if (!userEmail.equals(emailReserva)) {
                throw new AccesDenegatException("No pots veure reserves d'un altre client");
            }
        } else if (rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no autoritzat");
        }

        return reserva;
    }
    /**
     * Anul·la una reserva existent i calcula l'import que s'ha de retornar al
     * client segons els dies d'antelació amb què es realitza la cancel·lació.
     *
     * Si la cancel·lació es fa amb suficient antelació, es retorna l'import
     * complet de la reserva.
     *
     * @param idReserva identificador de la reserva
     * @param userEmail correu electrònic de l'usuari
     *
     * @return objecte amb la informació de la cancel·lació
     *
     * @throws DadesNoTrobadesException si la reserva no existeix
     * @throws AccesDenegatException si l'usuari no té permisos
     * @throws ReservaNoAnulableException si la reserva no es pot anul·lar
     */
    @Value("${reserves.cancelacio.dies-retorn:3}")
    private int diesRetorn;

    public ReservaCancelResponse anulLarReserva(Long idReserva, String userEmail)
            throws DadesNoTrobadesException, AccesDenegatException, ReservaNoAnulableException {

        UserRole rol = userLogic.getRole(userEmail).orElseThrow();
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no autoritzat");
        }

        // Carreguem reserva (si tens findDetallById amb fetch, millor)
        Reserva r = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new DadesNoTrobadesException("Reserva no trobada"));

        // CLIENT només pot anul·lar la seva
        if (rol == UserRole.CLIENT) {
            if (!userEmail.equals(r.getClient().getEmail()) && !userEmail.equals(r.getClient().getUsername())) {
                throw new AccesDenegatException("No pots anul·lar reserves d'un altre client");
            }
        }

        LocalDate avui = LocalDate.now();

        // No es pot anul·lar si ja ha començat o ja ha acabat
        // (si data_inici <= avui -> iniciada o en curs; si data_fi < avui -> finalitzada)
        if (!r.getDataInici().isAfter(avui)) {
            throw new ReservaNoAnulableException("No es pot anul·lar una reserva iniciada o finalitzada");
        }

        // Càlcul retorn
        long diesAntelacio = ChronoUnit.DAYS.between(avui, r.getDataInici());
        BigDecimal retorn = BigDecimal.ZERO;
        if (diesAntelacio >= diesRetorn) {
            retorn = r.getImportTotal() != null ? r.getImportTotal() : BigDecimal.ZERO;
        }

        String to = r.getClient().getEmail();
        String subject = "Reserva anul·lada #" + r.getIdReserva();

        String text = "La teva reserva amb codi " + r.getIdReserva() + " ha estat anul·lada.\n"
                + "Import retornat: " + retorn + " €\n"
                + "Gràcies.";

        emailServiceImpl.sendSimpleMessage(to, subject, text);

        // Anul·lar = esborrar
        reservaRepo.delete(r);

        ReservaCancelResponse resp = new ReservaCancelResponse();
        resp.setIdReserva(idReserva);
        resp.setAnulada(true);
        resp.setImportRetornat(retorn);
        resp.setMissatge("Reserva anul·lada correctament");
        return resp;
    }

    /**
     * Calcula una previsualització d'una reserva sense guardar-la al sistema.
     *
     * Permet conèixer l'import total i la fiança abans de confirmar la reserva.
     *
     * @param matricula matrícula del vehicle
     * @param inici data d'inici de la reserva
     * @param fi data de finalització de la reserva
     *
     * @return reserva amb els imports calculats però sense guardar
     *
     * @throws ReservaDatesNoValidsException si les dates no són vàlides
     * @throws VehicleNoDisponibleException si el vehicle no està disponible
     * @throws DadesNoTrobadesException si el vehicle no existeix
     */

    @Transactional
    public Reserva previewReservaPublic(
            String matricula,
            LocalDate inici,
            LocalDate fi
    ) throws ReservaDatesNoValidsException, VehicleNoDisponibleException, DadesNoTrobadesException {

        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        List<Reserva> reserves = reservaRepo.findReservasSolapadas(matricula, inici, fi);
        if (!reserves.isEmpty()) {
            throw new ReservaDatesNoValidsException("Vehicle no disponible en aquestes dates");
        }

        Vehicle vehicle = vehicleRepo.findById(matricula)
                .orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

        EstatVehicle estat = vehicle.getEstatVehicle();
        if (estat == null) {
            throw new VehicleNoDisponibleException("El vehicle no té estat assignat");
        }
        if (EstatVehicle.BAIXA.equals(estat)) {
            throw new VehicleNoDisponibleException("El vehicle està fora de servei");
        }

        Reserva r = new Reserva();
        r.setVehicle(vehicle);
        r.setDataInici(inici);
        r.setDataFi(fi);

        long dies = java.time.temporal.ChronoUnit.DAYS.between(inici, fi);
        dies = (dies == 0 ? 1 : dies);

        BigDecimal importTotal = vehicle.getPreuHora()
                .multiply(new BigDecimal(24))
                .multiply(new BigDecimal(dies));

        r.setImportTotal(importTotal);
        r.setFianca(vehicle.getFiancaEstandard());

        return r;
    }
}
