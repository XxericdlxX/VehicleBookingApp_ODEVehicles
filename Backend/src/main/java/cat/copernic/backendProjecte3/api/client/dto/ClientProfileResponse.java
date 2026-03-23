package cat.copernic.backendProjecte3.api.client.dto;

/**
 * Resposta amb les dades del perfil del client.
 *
 * @param email correu electrònic del client
 * @param nomComplet nom complet del client
 * @param dni document identificatiu del client
 * @param dataCaducitatDocument data de caducitat del document d'identitat
 * @param adreca adreça postal del client
 * @param nacionalitat nacionalitat del client
 * @param carnetConduir tipus o número del carnet de conduir
 * @param dataCaducitatCarnetConduir data de caducitat del carnet de conduir
 * @param numeroTargetaCredit número de la targeta de crèdit
 * @param fotoPerfilBase64 fotografia de perfil codificada en Base64
 * @param docIdentitatBase64 imatge del document d'identitat codificada en Base64
 * @param docCarnetBase64 imatge del carnet de conduir codificada en Base64
 */
public record ClientProfileResponse(
        String email,
        String nomComplet,
        String dni,
        String dataCaducitatDocument,
        String adreca,
        String nacionalitat,
        String carnetConduir,
        String dataCaducitatCarnetConduir,
        String numeroTargetaCredit,
        String fotoPerfilBase64,
        String docIdentitatBase64,
        String docCarnetBase64
        ) {

}
