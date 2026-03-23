/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.Reputacio;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.business.ClientService;
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
public class ClientTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepo;

    @BeforeAll
    public void init() {
        clientRepo.deleteAll();
    }

    @Test
    public void testRegistrarNouClient_FluxCorrecte() {
        
        // Alta client
        Client client = new Client();
        client.setEmail("nou.client@test.com"); 
        client.setDni("12345678Z");
        client.setAdreca("Carrer de l'Exemple, 123"); // Sense relació amb Localització
        client.setCarnetConduir("B");
        client.setNumeroTargetaCredit("1111-2222-3333-4444");
        client.setReputacio(Reputacio.NORMAL);
        client.setRol(UserRole.CLIENT);

        String passwordEnClar = "secret123";
        assertDoesNotThrow(() -> {
             Client resultat = clientService.registrarNouClient(client, passwordEnClar);
             assertNotNull(resultat);
        });
        
        Client clientDesat = clientRepo.findById("nou.client@test.com").orElse(null);
        
        //verificacions
        assertNotNull(clientDesat);
        assertEquals("nou.client@test.com", clientDesat.getUsername());
        assertTrue(PasswordHasher.check("secret123", clientDesat.getPassword())); //password ha d'estar xifrat
        assertEquals("12345678Z", clientDesat.getDni());
        assertEquals("Carrer de l'Exemple, 123", clientDesat.getAdreca());
        assertEquals(Reputacio.NORMAL, clientDesat.getReputacio());
    }

    /**
     * Validació del Registre: Control de Duplicats
     */
    @Test
    public void testRegistrarClient_EmailDuplicat() {
        
        Client existent = new Client();
        existent.setEmail("ja.existeix@test.com");
        existent.setDni("99999999X");
        existent.setPassword(PasswordHasher.encode("1234"));
        existent.setRol(UserRole.CLIENT);
        clientRepo.save(existent);

        
        Client nouIntent = new Client();
        nouIntent.setEmail("ja.existeix@test.com"); // Email conflictiu
        nouIntent.setDni("88888888Y"); // DNI diferent

        
        assertThrows(ErrorAltaException.class, () -> {
            clientService.registrarNouClient(nouIntent, "novaPass");
        });
    }
}
