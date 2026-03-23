/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.api.dto.reserva;

/**
 *
 * @author orjon
 */
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO utilitzat per representar la resposta bàsica d'una reserva.
 *
 * Aquesta classe conté la informació principal d'una reserva que el backend
 * retorna al client després de crear-la o quan es llisten reserves.
 *
 * Informació inclosa: - identificador de la reserva - correu electrònic del
 * client - matrícula del vehicle reservat - dates d'inici i fi de la reserva -
 * import total de la reserva - fiança associada al vehicle
 *
 * Aquest objecte s'utilitza principalment als endpoints del
 * {@link cat.copernic.backendProjecte3.api.controller.ReservaController}.
 *
 * @author orjon
 */
public class ReservaResponse {

    private Long idReserva;
    private String clientEmail;
    private String vehicleMatricula;
    private LocalDate dataInici;
    private LocalDate dataFi;
    private BigDecimal importTotal;
    private BigDecimal fianca;

    /**
     * Retorna l'identificador de la reserva.
     *
     * @return id de la reserva
     */
    public Long getIdReserva() {
        return idReserva;
    }

    /**
     * Defineix l'identificador de la reserva.
     *
     * @param idReserva identificador de la reserva
     */
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    /**
     * Retorna el correu electrònic del client que ha fet la reserva.
     *
     * @return email del client
     */
    public String getClientEmail() {
        return clientEmail;
    }

    /**
     * Defineix el correu electrònic del client.
     *
     * @param clientEmail correu electrònic del client
     */
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    /**
     * Retorna la matrícula del vehicle reservat.
     *
     * @return matrícula del vehicle
     */
    public String getVehicleMatricula() {
        return vehicleMatricula;
    }

    /**
     * Defineix la matrícula del vehicle reservat.
     *
     * @param vehicleMatricula matrícula del vehicle
     */
    public void setVehicleMatricula(String vehicleMatricula) {
        this.vehicleMatricula = vehicleMatricula;
    }

    /**
     * Retorna la data d'inici de la reserva.
     *
     * @return data d'inici
     */
    public LocalDate getDataInici() {
        return dataInici;
    }

    /**
     * Defineix la data d'inici de la reserva.
     *
     * @param dataInici data d'inici de la reserva
     */
    public void setDataInici(LocalDate dataInici) {
        this.dataInici = dataInici;
    }

    /**
     * Retorna la data de finalització de la reserva.
     *
     * @return data de finalització
     */
    public LocalDate getDataFi() {
        return dataFi;
    }

    /**
     * Defineix la data de finalització de la reserva.
     *
     * @param dataFi data de finalització de la reserva
     */
    public void setDataFi(LocalDate dataFi) {
        this.dataFi = dataFi;
    }

    /**
     * Retorna l'import total de la reserva.
     *
     * @return import total
     */
    public BigDecimal getImportTotal() {
        return importTotal;
    }

    /**
     * Defineix l'import total de la reserva.
     *
     * @param importTotal import total calculat de la reserva
     */
    public void setImportTotal(BigDecimal importTotal) {
        this.importTotal = importTotal;
    }

    /**
     * Retorna la fiança associada a la reserva.
     *
     * @return fiança de la reserva
     */
    public BigDecimal getFianca() {
        return fianca;
    }

    /**
     * Defineix la fiança associada a la reserva.
     *
     * @param fianca import de la fiança
     */
    public void setFianca(BigDecimal fianca) {
        this.fianca = fianca;
    }
}
