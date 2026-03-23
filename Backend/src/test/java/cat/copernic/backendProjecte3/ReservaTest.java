/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.api.dto.reserva.ReservaCancelResponse;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import cat.copernic.backendProjecte3.business.ReservaService;
import cat.copernic.backendProjecte3.business.UserLogic;
import cat.copernic.backendProjecte3.exceptions.ReservaNoAnulableException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author manel
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservaTest {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ReservaRepository reservaRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private VehicleRepository vehicleRepo;

    // Components per fer el Login REAL
    @Autowired
    private UserLogic userLogic;

    private Client clientTest;
    private Vehicle vehicleTest;
    private Reserva reservaTest;

    // Dades per fer escenari1
    private final String EMAIL_LOGIN = "client.real@test.com";
    private final String PASSWORD_RAW = "passwordSegur123";

    @BeforeAll
    public void setupGlobal() {
        reservaRepo.deleteAll();
        clientRepo.deleteAll();
        vehicleRepo.deleteAll();
    }

    /**
     * *
     * - Rol client - Vehicle disponible
     */
    public void escenari1() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol client - Vehicle de baixa
     */
    public void escenari2() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.BAIXA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol NONE - Vehicle disponible
     */
    public void escenari3() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.NONE);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol AGENT - Vehicle disponible - reserva OK
     */
    public void escenari4() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.AGENT);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * - Rol CLIENT - Vehicle disponible - reserva OK
     */
    public void escenari5() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok        
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * - Rol CLIENT - Vehicle disponible - reserva OK
     */
    public void escenari6() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    @Test
    public void crearReserva_Ok() {

        escenari1();

        assertDoesNotThrow(() -> {
            Reserva resultat = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );

            assertNotNull(resultat);
            assertEquals(EMAIL_LOGIN, resultat.getClient().getUsername());

        });

    }

    @Test
    public void crearReserva_VehicleBaixa() {

        escenari2();

        assertThrows(VehicleNoDisponibleException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    @Test
    public void crearReserva_RolNoautoritzat() {

        escenari3();

        assertThrows(AccesDenegatException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * Es eeserva un vehicle i llavors es reserva de nou en dates no compatibles
     */
    @Test
    public void crearReserva_VehicleJaReservat() {

        escenari6();

        assertThrows(ReservaDatesNoValidsException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    reservaTest.getDataFi().minusDays(1),
                    reservaTest.getDataFi().plusDays(10),
                    clientTest.getUsername()
            );
        });
    }

    @Test
    public void llistarReservesPropies_Ok() throws Exception {

        escenari5();

        Reserva r2 = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                clientTest.getUsername()
        );

        List<Reserva> llista = reservaService.veureReservesPropies(EMAIL_LOGIN, "des");

        assertNotNull(llista);
        assertEquals(2, llista.size());

        // Todas las reservas deben ser del mismo cliente (en tu modelo: username = email)
        assertTrue(llista.stream().allMatch(r -> EMAIL_LOGIN.equals(r.getClient().getUsername())));
    }

    @Test
    public void llistarReservesPropies_OrderAsc() throws Exception {

        // Escenario base: CLIENT + VEHICLE
        escenari1();

        // Creamos 2 reservas con fechas distintas
        Reserva r1 = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                clientTest.getUsername()
        );

        Reserva r2 = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                clientTest.getUsername()
        );

        // RF52: ordenar ASC (la más antigua primero)
        List<Reserva> llista = reservaService.veureReservesPropies(EMAIL_LOGIN, "asc");

        assertNotNull(llista);
        assertEquals(2, llista.size());

        // Comprobación orden: dataInici ASC
        assertTrue(!llista.get(0).getDataInici().isAfter(llista.get(1).getDataInici()));

        // Opcional: asegurar que son del mismo cliente
        assertTrue(llista.stream().allMatch(r -> EMAIL_LOGIN.equals(r.getClient().getEmail())));
    }

    @Test
    public void llistarReservesPropies_OrderDesc() throws Exception {

        // Escenario base: CLIENT + VEHICLE
        escenari1();

        // Creamos 2 reservas con fechas distintas
        Reserva r1 = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                clientTest.getUsername()
        );

        Reserva r2 = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                clientTest.getUsername()
        );

        // RF52: ordenar DESC (la más nueva primero)
        List<Reserva> llista = reservaService.veureReservesPropies(EMAIL_LOGIN, "desc");

        assertNotNull(llista);
        assertEquals(2, llista.size());

        // Comprobación orden: dataInici DESC
        assertTrue(!llista.get(0).getDataInici().isBefore(llista.get(1).getDataInici()));

        // Opcional: asegurar que son del mismo cliente
        assertTrue(llista.stream().allMatch(r -> EMAIL_LOGIN.equals(r.getClient().getEmail())));
    }

    @Test
    public void veureDetallReserva_Client_Ok() throws Exception {
        escenari5(); // client + vehicle + reservaTest creada

        Reserva r = reservaService.veureDetallReserva(reservaTest.getIdReserva(), EMAIL_LOGIN);

        assertNotNull(r);
        assertEquals(reservaTest.getIdReserva(), r.getIdReserva());
        assertEquals(EMAIL_LOGIN, r.getClient().getEmail());
        assertNotNull(r.getVehicle());
    }

    @Test
    public void veureDetallReserva_Client_Altres_NoPermes() throws Exception {
        escenari5(); // crea reserva del EMAIL_LOGIN

        // Creem un altre client diferent
        Client altre = new Client();
        altre.setEmail("altre@test.com");
        altre.setPassword(PasswordHasher.encode("x"));
        altre.setRol(UserRole.CLIENT);
        altre.setDni("99999999Z");
        clientRepo.save(altre);

        assertThrows(AccesDenegatException.class, () -> {
            reservaService.veureDetallReserva(reservaTest.getIdReserva(), "altre@test.com");
        });
    }

    @Test
    public void anulLarReserva_Ok_RetornaImport() throws Exception {
        escenari1();

        Reserva r = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                clientTest.getUsername()
        );

        ReservaCancelResponse resp = reservaService.anulLarReserva(r.getIdReserva(), EMAIL_LOGIN);

        assertNotNull(resp);
        assertTrue(resp.isAnulada());
        assertEquals(r.getIdReserva(), resp.getIdReserva());
        // si diesRetorn=3, aquí retorna tot perquè falten 10 dies
        assertEquals(r.getImportTotal(), resp.getImportRetornat());
        assertTrue(reservaRepo.findById(r.getIdReserva()).isEmpty());
    }

    @Test
    public void anulLarReserva_NoPermesa_Iniciada() throws Exception {
        escenari1();

        Reserva r = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(1), // demà
                LocalDate.now().plusDays(3),
                clientTest.getUsername()
        );

        // Simulem "avui"?? Si el teu codi usa LocalDate.now(), aquest test depèn del temps real.
        // Solució simple: crea una reserva que comenci avui (però el teu crearReserva ho prohibeix).
        // Llavors per testejar això bé, cal injectar Clock o permetre dataInici==avui per test.
        // Alternativa pràctica: crea la reserva directament via repo amb dataInici = avui
        r.setDataInici(LocalDate.now());
        reservaRepo.save(r);

        assertThrows(ReservaNoAnulableException.class, () -> {
            reservaService.anulLarReserva(r.getIdReserva(), EMAIL_LOGIN);
        });
    }

    @Test
    public void anulLarReserva_NoEsSeva() throws Exception {
        escenari1();

        Reserva r = reservaService.crearReserva(
                EMAIL_LOGIN,
                vehicleTest.getMatricula(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                clientTest.getUsername()
        );

        // altre client
        Client altre = new Client();
        altre.setEmail("altre@test.com");
        altre.setPassword(PasswordHasher.encode("x"));
        altre.setRol(UserRole.CLIENT);
        altre.setDni("99999999Z");
        clientRepo.save(altre);

        assertThrows(AccesDenegatException.class, () -> {
            reservaService.anulLarReserva(r.getIdReserva(), "altre@test.com");
        });
    }
}
