package cat.copernic.backendProjecte3.api.common;

import java.time.Instant;

/**
 * Resposta estàndard d'error per a l'API.
 * <p>
 * Serveix per retornar un missatge clar i un codi HTTP quan hi ha un problema.
 * </p>
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
        ) {

}
