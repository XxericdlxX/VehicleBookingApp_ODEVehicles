package cat.copernic.backendProjecte3.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.copernic.backendProjecte3.api.dto.registre.RegistreRequest;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class RegistreController {

    private static final Logger log = LoggerFactory.getLogger(RegistreController.class);


    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/registre")
public ResponseEntity<String> registrar(@RequestBody RegistreRequest request) {
        log.info("POST request received in RegistreController");
    try {
        Client nouClient = new Client();

        // 1. Seteamos los campos de la clase Usuari (Padre)
        nouClient.setEmail(request.getEmail());
        nouClient.setPassword(PasswordHasher.encode(request.getPassword()));
        nouClient.setRol(UserRole.CLIENT);

        // 2. Seteamos los campos específicos de Client (Hijo)
        nouClient.setNomComplet(request.getNomComplet());
        nouClient.setDni(request.getDni());
        nouClient.setDataCaducitatDocument(request.getCaducitatDni());
        nouClient.setNacionalitat(request.getNacionalitat());
        nouClient.setAdreca(request.getAdreca());
        nouClient.setNumeroTargetaCredit(request.getNumTargeta());
        nouClient.setCarnetConduir(request.getTipusLlicencia());
        nouClient.setDataCaducitatCarnetConduir(request.getCaducitatLlicencia());

        // ✅ 3. Procesamos AMBOS documentos (aquí estaba el fallo)
        
        // Bloque para el DNI (Este es el que faltaba)
        if (request.getDocIdentitatBase64() != null && !request.getDocIdentitatBase64().isEmpty()) {
            byte[] decodedDni = Base64.getDecoder().decode(request.getDocIdentitatBase64());
            nouClient.setDocIdentitat(decodedDni);
        }

        // Bloque para el CARNET
        if (request.getDocCarnetBase64() != null && !request.getDocCarnetBase64().isEmpty()) {
            byte[] decodedCarnet = Base64.getDecoder().decode(request.getDocCarnetBase64());
            nouClient.setDocCarnet(decodedCarnet);
        }

        // 4. Guardamos el cliente
        clientRepository.save(nouClient);

            return ResponseEntity.ok("{\"status\": \"ok\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en el registro: " + e.getMessage());
        }
    }
}
