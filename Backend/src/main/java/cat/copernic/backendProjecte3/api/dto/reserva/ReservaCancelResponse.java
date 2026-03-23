/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.api.dto.reserva;

import java.math.BigDecimal;

/**
 * DTO utilitzat com a resposta quan es realitza l’anul·lació d’una reserva.
 *
 * Aquesta classe conté la informació que es retorna al client després
 * d’intentar anul·lar una reserva, indicant si l’operació s’ha realitzat
 * correctament i quin import s’ha retornat.
 *
 * Camps principals:
 * - idReserva: identificador de la reserva anul·lada
 * - anulada: indica si la reserva s’ha anul·lat correctament
 * - importRetornat: import que es retorna al client després de l’anul·lació
 * - missatge: missatge informatiu sobre el resultat de l’operació
 */
public class ReservaCancelResponse {

    private Long idReserva;
    private boolean anulada;
    private BigDecimal importRetornat;
    private String missatge;

    /**
     * Constructor buit necessari per a la serialització/deserialització
     * dels objectes quan s’envien com a resposta de l’API.
     */
    public ReservaCancelResponse() {
    }

    /**
     * Retorna l'identificador de la reserva.
     *
     * @return id de la reserva anul·lada
     */
    public Long getIdReserva() {
        return idReserva;
    }

    /**
     * Estableix l'identificador de la reserva.
     *
     * @param idReserva identificador de la reserva
     */
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    /**
     * Indica si la reserva s’ha anul·lat correctament.
     *
     * @return true si la reserva s'ha anul·lat, false en cas contrari
     */
    public boolean isAnulada() {
        return anulada;
    }

    /**
     * Defineix si la reserva ha estat anul·lada.
     *
     * @param anulada estat de l’anul·lació de la reserva
     */
    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    /**
     * Retorna l'import retornat al client després de l’anul·lació.
     *
     * @return import retornat
     */
    public BigDecimal getImportRetornat() {
        return importRetornat;
    }

    /**
     * Estableix l'import que es retorna al client.
     *
     * @param importRetornat import retornat
     */
    public void setImportRetornat(BigDecimal importRetornat) {
        this.importRetornat = importRetornat;
    }

    /**
     * Retorna el missatge informatiu del resultat de l’anul·lació.
     *
     * @return missatge descriptiu
     */
    public String getMissatge() {
        return missatge;
    }

    /**
     * Defineix el missatge informatiu de la resposta.
     *
     * @param missatge missatge descriptiu
     */
    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }
}
