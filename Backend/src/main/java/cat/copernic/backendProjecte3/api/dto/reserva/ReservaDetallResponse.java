/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.api.dto.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO utilitzat per retornar el detall complet d'una reserva.
 *
 * Aquesta classe representa la informació que el backend envia al client quan
 * es consulta el detall d'una reserva concreta. Inclou tant les dades de la
 * reserva com la informació del vehicle associat.
 *
 * Informació inclosa: - Dades de la reserva (id, client, dates, import total i
 * fiança) - Informació del vehicle reservat (matrícula, tipus, motor, potència,
 * color i preu per hora) - Ruta o identificador de la fotografia del vehicle
 *
 * Aquest objecte és utilitzat principalment per l'endpoint de detall de reserva
 * del {@link cat.copernic.backendProjecte3.api.controller.ReservaController}.
 *
 * @author orjon
 */
public class ReservaDetallResponse {

    private Long idReserva;
    private String clientEmail;

    private LocalDate dataInici;
    private LocalDate dataFi;

    private BigDecimal importTotal;
    private BigDecimal fianca;

    // Vehicle (dades + “foto”)
    private String vehicleMatricula;
    private String vehicleTipus;
    private String vehicleMotor;
    private String vehiclePotencia;
    private String vehicleColor;
    private BigDecimal vehiclePreuHora;

    private String vehicleFoto;

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
     * @param clientEmail email del client
     */
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
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
     * @param dataInici data d'inici
     */
    public void setDataInici(LocalDate dataInici) {
        this.dataInici = dataInici;
    }

    public LocalDate getDataFi() {
        return dataFi;
    }

    public void setDataFi(LocalDate dataFi) {
        this.dataFi = dataFi;
    }

    public BigDecimal getImportTotal() {
        return importTotal;
    }

    public void setImportTotal(BigDecimal importTotal) {
        this.importTotal = importTotal;
    }

    public BigDecimal getFianca() {
        return fianca;
    }

    public void setFianca(BigDecimal fianca) {
        this.fianca = fianca;
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

    public String getVehicleTipus() {
        return vehicleTipus;
    }

    public void setVehicleTipus(String vehicleTipus) {
        this.vehicleTipus = vehicleTipus;
    }

    public String getVehicleMotor() {
        return vehicleMotor;
    }

    public void setVehicleMotor(String vehicleMotor) {
        this.vehicleMotor = vehicleMotor;
    }

    public String getVehiclePotencia() {
        return vehiclePotencia;
    }

    public void setVehiclePotencia(String vehiclePotencia) {
        this.vehiclePotencia = vehiclePotencia;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public BigDecimal getVehiclePreuHora() {
        return vehiclePreuHora;
    }

    public void setVehiclePreuHora(BigDecimal vehiclePreuHora) {
        this.vehiclePreuHora = vehiclePreuHora;
    }

    /**
     * Retorna la ruta o identificador de la fotografia del vehicle.
     *
     * @return ruta de la fotografia
     */
    public String getVehicleFoto() {
        return vehicleFoto;
    }

    /**
     * Defineix la ruta o identificador de la fotografia del vehicle.
     *
     * @param vehicleFoto ruta de la fotografia
     */
    public void setVehicleFoto(String vehicleFoto) {
        this.vehicleFoto = vehicleFoto;
    }
}
