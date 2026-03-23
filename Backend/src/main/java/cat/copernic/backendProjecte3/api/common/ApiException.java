package cat.copernic.backendProjecte3.api.common;

/**
 * Excepció controlada de l'API.
 * <p>
 * Permet llançar un error amb codi HTTP i un missatge entenedor des de
 * controladors i serveis.
 * </p>
 */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
