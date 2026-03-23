/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.business.UserLogic;
import jakarta.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class LoginTest {

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ClientRepository clientRepo;
    
    
    private final String EMAIL_TEST = "test@login.com";
    private final String PASSWORD_RAW = "password123";
    private Client clientTest;

    @BeforeAll
    public void setupDb() {
        // Neteja prèvia
        clientRepo.deleteAll();
    }

    @BeforeEach
    public void setupUser() {
        // insert d'usuari a la BBDD
        clientTest = new Client();
        clientTest.setEmail(EMAIL_TEST);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); 
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setDni("12345678Z");
        
        clientRepo.save(clientTest);
    }

    /**
     * CAS 1: Login Correcte
     * L'usuari existeix i el password coincideix.
     */
    @Test
    public void testLoginCorrecte() {
        
        assertDoesNotThrow(() -> {
             userLogic.login(clientTest.getUsername(), PASSWORD_RAW);
        });
        
    }

    /**
     * CAS 2: Usuari No Trobat
     * L'email no existeix a la base de dades.
     */
    @Test
    public void testLoginUsuariNoTrobat() {
        String emailInexistent = "pepeMilks@gmail.com";
        
        assertThrows(AccesDenegatException.class, () -> {
            userLogic.login(emailInexistent, clientTest.getPassword());
        });
    }

    /**
     * CAS 3: Password Incorrecte
     * L'usuari existeix però el password no és el bo.
     */
    @Test
    public void testLoginPasswordIncorrecte() {
        String passwordDolent = "123Password";

        assertThrows(AccesDenegatException.class, () -> {
            userLogic.login(EMAIL_TEST, passwordDolent);
        });
    }

}
