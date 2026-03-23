package cat.copernic.backendProjecte3.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.copernic.backendProjecte3.api.client.dto.ClientProfileResponse;
import cat.copernic.backendProjecte3.api.client.dto.ClientProfileUpdateRequest;
import cat.copernic.backendProjecte3.api.common.ApiException;
import cat.copernic.backendProjecte3.business.AuthTokenService;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

/**
 * Controlador del perfil del client.
 * <p>
 * Permet consultar i actualitzar les dades del perfil de l'usuari autenticat.
 * </p>
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final AuthTokenService tokenService;
    private final ClientRepository clientRepo;

    /**
     * Crea el controlador del perfil de client.
     *
     * @param tokenService servei encarregat de validar el token de sessió
     * @param clientRepo repositori amb les dades persistides dels clients
     */
    public ClientController(AuthTokenService tokenService, ClientRepository clientRepo) {
        this.tokenService = tokenService;
        this.clientRepo = clientRepo;
    }

    @GetMapping("/me")
    /**
     * Retorna el perfil del client autenticat.
     *
     * @param auth Capçalera Authorization.
     * @return Perfil del client.
     */
    public ClientProfileResponse me(@RequestHeader(name = "Authorization", required = false) String auth) {
        log.info("GET request received in ClientController");
        String email = tokenService.requireEmail(auth);
        Client c = clientRepo.findById(email).orElseThrow(() -> new ApiException(404, "Client no trobat"));
        return toResponse(c);
    }

    /**
     * Actualitza les dades del perfil del client autenticat.
     *
     * @param auth capçalera Authorization amb el token de sessió
     * @param req dades del perfil que es volen actualitzar
     * @return perfil actualitzat del client
     */
    @PutMapping("/me")
    public ClientProfileResponse updateMe(
            @RequestHeader(name = "Authorization", required = false) String auth,
            @RequestBody ClientProfileUpdateRequest req
    ) {
        log.info("PUT request received in ClientController");
        String email = tokenService.requireEmail(auth);
        Client c = clientRepo.findById(email).orElseThrow(() -> new ApiException(404, "Client no trobat"));

        if (req == null) {
            throw new ApiException(400, "Body requerit");
        }

        if (req.dni() != null) {
            c.setDni(req.dni());
        }
        if (req.nomComplet() != null) {
            c.setNomComplet(req.nomComplet());
        }
        if (req.nacionalitat() != null) {
            c.setNacionalitat(req.nacionalitat());
        }
        if (req.dataCaducitatDocument() != null) {
            c.setDataCaducitatDocument(req.dataCaducitatDocument());
        }
        if (req.adreca() != null) {
            c.setAdreca(req.adreca());
        }
        if (req.carnetConduir() != null) {
            c.setCarnetConduir(req.carnetConduir());
        }
        if (req.dataCaducitatCarnetConduir() != null) {
            c.setDataCaducitatCarnetConduir(req.dataCaducitatCarnetConduir());
        }
        if (req.numeroTargetaCredit() != null) {
            c.setNumeroTargetaCredit(req.numeroTargetaCredit());
        }

        if (req.fotoPerfilBase64() != null) {
            c.setFotoPerfil(decodeB64(req.fotoPerfilBase64()));
        }
        if (req.docIdentitatBase64() != null) {
            c.setDocIdentitat(decodeB64(req.docIdentitatBase64()));
        }
        if (req.docCarnetBase64() != null) {
            c.setDocCarnet(decodeB64(req.docCarnetBase64()));
        }

        clientRepo.save(c);
        return toResponse(c);
    }

    /**
     * Construeix la resposta de perfil a partir de l'entitat de client.
     *
     * @param c entitat del client
     * @return resposta preparada per enviar a l'API
     */
    private ClientProfileResponse toResponse(Client c) {
        return new ClientProfileResponse(
                c.getUsername(),
                c.getNomComplet(),
                c.getDni(),
                c.getDataCaducitatDocument(),
                c.getAdreca(),
                c.getNacionalitat(),
                c.getCarnetConduir(),
                c.getDataCaducitatCarnetConduir(),
                c.getNumeroTargetaCredit(),
                encodeB64(c.getFotoPerfil()),
                encodeB64(c.getDocIdentitat()),
                encodeB64(c.getDocCarnet())
        );
    }

    private byte[] decodeB64(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return Base64.getDecoder().decode(s);
    }

    private String encodeB64(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(b);
    }
}
