/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(length = 20)
    private String matricula;

    @Enumerated(EnumType.STRING)
    private TipusVehicle tipusVehicle;
    
    @Enumerated(EnumType.STRING)
    private EstatVehicle estatVehicle;

    private String motor;

    private String potencia;

    private String color;

    private Integer limitQuilometratge;

    @Column(precision = 10, scale = 2)
    private BigDecimal preuHora;

    @Column(precision = 10, scale = 2)
    private BigDecimal fiancaEstandard;

    private Integer minDiesLloguer;

    private Integer maxDiesLloguer;

    @Column(columnDefinition = "TEXT")
    private String comentarisPrivats;

    private String rutaDocumentacioPrivada;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle")
    private List<Reserva> reservas = new ArrayList<>();

    public Vehicle() {
        this.estatVehicle = EstatVehicle.ALTA;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public TipusVehicle getTipusVehicle() {
        return tipusVehicle;
    }

    public void setTipusVehicle(TipusVehicle tipusVehicle) {
        this.tipusVehicle = tipusVehicle;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getPotencia() {
        return potencia;
    }

    public void setPotencia(String potencia) {
        this.potencia = potencia;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getLimitQuilometratge() {
        return limitQuilometratge;
    }

    public void setLimitQuilometratge(Integer limitQuilometratge) {
        this.limitQuilometratge = limitQuilometratge;
    }

    public BigDecimal getPreuHora() {
        return preuHora;
    }

    public void setPreuHora(BigDecimal preuHora) {
        this.preuHora = preuHora;
    }

    public BigDecimal getFiancaEstandard() {
        return fiancaEstandard;
    }

    public void setFiancaEstandard(BigDecimal fiancaEstandard) {
        this.fiancaEstandard = fiancaEstandard;
    }

    public Integer getMinDiesLloguer() {
        return minDiesLloguer;
    }

    public void setMinDiesLloguer(Integer minDiesLloguer) {
        this.minDiesLloguer = minDiesLloguer;
    }

    public Integer getMaxDiesLloguer() {
        return maxDiesLloguer;
    }

    public void setMaxDiesLloguer(Integer maxDiesLloguer) {
        this.maxDiesLloguer = maxDiesLloguer;
    }

    public String getComentarisPrivats() {
        return comentarisPrivats;
    }

    public void setComentarisPrivats(String comentarisPrivats) {
        this.comentarisPrivats = comentarisPrivats;
    }

    public String getRutaDocumentacioPrivada() {
        return rutaDocumentacioPrivada;
    }

    public void setRutaDocumentacioPrivada(String rutaDocumentacioPrivada) {
        this.rutaDocumentacioPrivada = rutaDocumentacioPrivada;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public EstatVehicle getEstatVehicle() {
        return estatVehicle;
    }

    public void setEstatVehicle(EstatVehicle estatVehicle) {
        this.estatVehicle = estatVehicle;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.matricula);
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
        final Vehicle other = (Vehicle) obj;
        return Objects.equals(this.matricula, other.matricula);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vehicle{");
        sb.append("matricula=").append(matricula);
        sb.append(", tipusVehicle=").append(tipusVehicle);
        sb.append(", motor=").append(motor);
        sb.append(", potencia=").append(potencia);
        sb.append(", color=").append(color);
        sb.append(", limitQuilometratge=").append(limitQuilometratge);
        sb.append(", preuHora=").append(preuHora);
        sb.append(", fiancaEstandard=").append(fiancaEstandard);
        sb.append(", minDiesLloguer=").append(minDiesLloguer);
        sb.append(", maxDiesLloguer=").append(maxDiesLloguer);
        sb.append(", comentarisPrivats=").append(comentarisPrivats);
        sb.append(", rutaDocumentacioPrivada=").append(rutaDocumentacioPrivada);
        sb.append(", num reservas=").append(reservas.size());
        sb.append('}');
        return sb.toString();
    }
    
    
   
}
