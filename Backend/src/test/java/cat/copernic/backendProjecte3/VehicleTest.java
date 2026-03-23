/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import cat.copernic.backendProjecte3.business.VehicleService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
public class VehicleTest {

    @Autowired private VehicleService vehicleService;
    
    // Repositoris necessaris per preparar l'escenari
    @Autowired private VehicleRepository vehicleRepo;
    @Autowired private ReservaRepository reservaRepo;
    @Autowired private ClientRepository clientRepo;
    

    // Dades globals per als tests
    private Vehicle cotxeTest;

    @BeforeAll
    public void cleanDb() {
        // Neteja en ordre per evitar violacions de claus foranes
        reservaRepo.deleteAll();
        vehicleRepo.deleteAll();
        clientRepo.deleteAll();
    }

    @BeforeEach
    public void setupEscenari() {
        // Creem un vehicle disponible 
        cotxeTest = new Vehicle();
        cotxeTest.setMatricula("1234-BCN");
        cotxeTest.setTipusVehicle(TipusVehicle.COTXE);
        cotxeTest.setEstatVehicle(EstatVehicle.ALTA);
        cotxeTest.setPreuHora(new BigDecimal("10.00"));
        vehicleRepo.save(cotxeTest);
    }

    /**
     * TEST CAS D'ÚS: Verifica que el repositori filtra correctament
     */
    @Test
    public void testCercarVehiclesDisponibles() {
        

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setMatricula("9999-MAD");
        vehicle1.setTipusVehicle(TipusVehicle.MOTO);
        vehicle1.setEstatVehicle(EstatVehicle.ALTA);
        vehicleRepo.save(vehicle1);

        // Execució: Cerquem cotxes a Barcelona
        List<Vehicle> resultats = vehicleService.cercarVehiclesDisponibles(
                LocalDate.now(), 
                LocalDate.now().plusDays(1), 
                TipusVehicle.COTXE, 
                "08001"
        );

        // Verificació
        assertFalse(resultats.isEmpty());
        assertEquals(1, resultats.size());
        assertEquals("1234-BCN", resultats.get(0).getMatricula());
    }

    /**
     * Verifica que si no hi ha reserves, podem donar un vehicle de baixa
     */
    @Test
    public void testDonarDeBaixaVehicle_SenseReserves() {
        
        vehicleService.donarDeBaixaVehicle("1234-BCN");
        Vehicle vActualitzat = vehicleRepo.findById("1234-BCN").orElseThrow();        
        assertEquals(EstatVehicle.BAIXA, vActualitzat.getEstatVehicle(), 
                "El vehicle hauria d'estar en estat BAIXA després de donar-lo de baixa");
    }

    /***
     * * Verifica que si hi ha reserves, NO podem donar un vehicle de baixa
     */
    @Test
    public void testDonarDeBaixaVehicle_AmbReservesFutures_Error() {
        
        Client client = new Client();
        client.setEmail("test@client.com");
        client.setDni("12345678A");
        client.setPassword("pass");
        client.setRol(UserRole.CLIENT);
        clientRepo.save(client);

        
        Reserva reservaFutura = new Reserva();
        reservaFutura.setVehicle(cotxeTest);
        reservaFutura.setClient(client);
        reservaFutura.setDataInici(LocalDate.now().plusDays(5));
        reservaFutura.setDataFi(LocalDate.now().plusDays(10));
        reservaFutura.setImportTotal(BigDecimal.TEN);
        reservaRepo.save(reservaFutura);

        
        assertThrows(IllegalStateException.class, () -> {vehicleService.donarDeBaixaVehicle("1234-BCN");});
        
        Vehicle vehicleTest = vehicleRepo.findById("1234-BCN").get();
        assertEquals(EstatVehicle.ALTA, vehicleTest.getEstatVehicle());
    }

    /***
     * Un vehicle de baixa SEMPRE es pot donar d'alta
     */
    @Test
    public void testDonarDeAltaVehicle() {
     
        cotxeTest.setEstatVehicle(EstatVehicle.BAIXA);
        vehicleRepo.save(cotxeTest);

     
        vehicleService.donarDeAltaVehicle("1234-BCN", "Reparació finalitzada");

     
        Vehicle vActualitzat = vehicleRepo.findById("1234-BCN").orElseThrow();
        
     
        assertTrue(vActualitzat.getEstatVehicle() == EstatVehicle.ALTA);
    }
    
    /***
     * Un vehicle sempre es pot eliminar
     */
    @Test
    public void testEliminarVehicle() {
        vehicleService.eliminarVehicle("1234-BCN");
        assertFalse(vehicleRepo.existsById("1234-BCN"));
    }
}
