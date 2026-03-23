/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entitat que representa una reserva d'un vehicle dins del sistema.
 *
 * Cada reserva conté la informació del període de temps en què un client
 * reserva un vehicle determinat, així com les dades econòmiques associades
 * (import total i fiança).
 *
 * Relacions principals: - Un client pot tenir múltiples reserves. - Un vehicle
 * pot estar associat a múltiples reserves en diferents dates.
 *
 * Aquesta entitat està mapejada a la taula "reserva" de la base de dades
 * mitjançant JPA.
 *
 * Camps principals: - idReserva: identificador únic de la reserva - dataInici:
 * data d'inici de la reserva - dataFi: data de finalització de la reserva -
 * importTotal: import total calculat de la reserva - fianca: import de la
 * fiança associada - client: client que realitza la reserva - vehicle: vehicle
 * reservat
 */

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    // --- Fechas ---
    @Column(nullable = false)
    private LocalDate dataInici;

    @Column(nullable = false)
    private LocalDate dataFi;

    // --- Datos Económicos ---
    @Column(precision = 10, scale = 2)
    private BigDecimal importTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal fianca;

    // --- Relaciones ---
    @ManyToOne
    @JoinColumn(
            name = "client_email",
            nullable = false
    )
    private Client client;

    @ManyToOne
    @JoinColumn(
            name = "vehicle_matricula",
            nullable = false
    )
    private Vehicle vehicle;

    public Reserva() {
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDate getDataInici() {
        return dataInici;
    }

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva{");
        sb.append("idReserva=").append(idReserva);
        sb.append(", dataInici=").append(dataInici);
        sb.append(", dataFi=").append(dataFi);
        sb.append(", importTotal=").append(importTotal);
        sb.append(", fiancaPagada=").append(fianca);
        sb.append(", client=").append(client);
        sb.append(", vehicle=").append(vehicle);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.dataInici);
        hash = 73 * hash + Objects.hashCode(this.dataFi);
        hash = 73 * hash + Objects.hashCode(this.client);
        hash = 73 * hash + Objects.hashCode(this.vehicle);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reserva other = (Reserva) obj;
        if (!Objects.equals(this.dataInici, other.dataInici)) {
            return false;
        }
        if (!Objects.equals(this.dataFi, other.dataFi)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        return Objects.equals(this.vehicle, other.vehicle);
    }
}
