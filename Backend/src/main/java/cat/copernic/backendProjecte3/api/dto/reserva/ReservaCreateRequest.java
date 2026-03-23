/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.api.dto.reserva;

import java.time.LocalDate;

/**
 * DTO utilitzat per representar la petició de creació d'una nova reserva.
 *
 * Aquesta classe conté les dades necessàries que el client envia al backend per
 * crear una reserva d'un vehicle. Inclou la matrícula del vehicle i el rang de
 * dates en què es vol realitzar la reserva.
 *
 * Camps: - matricula: identificador del vehicle que es vol reservar -
 * dataInici: data d'inici de la reserva - dataFi: data de finalització de la
 * reserva
 *
 * Aquest objecte és utilitzat principalment pels endpoints del
 * {@link cat.copernic.backendProjecte3.api.controller.ReservaController}.
 *
 * @author orjon
 */
public class ReservaCreateRequest {

    private String matricula;
    private LocalDate dataInici;
    private LocalDate dataFi;

    /**
     * Retorna la matrícula del vehicle que es vol reservar.
     *
     * @return matrícula del vehicle
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Defineix la matrícula del vehicle que es vol reservar.
     *
     * @param matricula matrícula del vehicle
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
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
}
