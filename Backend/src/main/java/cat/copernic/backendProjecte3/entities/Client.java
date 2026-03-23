/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.Reputacio;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entitat que representa el perfil d'un client dins del sistema.
 */
@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "client_email")
public class Client extends Usuari {

    @Column(nullable = false, length = 20)
    private String dni;

    private String nomComplet;

    private String nacionalitat;

    /**
     * Data de caducitat del document (DNI/Passaport) en format YYYY-MM-DD.
     */
    private String dataCaducitatDocument;

    /**
     * Data de caducitat del carnet de conduir en format YYYY-MM-DD.
     */
    private String dataCaducitatCarnetConduir;

    private String adreca;

    private String carnetConduir;

    private String numeroTargetaCredit;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fotoPerfil;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] docIdentitat;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] docCarnet;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    private Reputacio reputacio;

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL
    )
    private List<Reserva> reservas = new ArrayList<>();

    /**
     * Constructor buit requerit per JPA.
     */
    public Client() {
        super();
    }

    /**
     * Retorna el DNI del client.
     *
     * @return document identificatiu del client
     */
    public String getDni() {
        return dni;
    }

    /**
     * Actualitza el DNI del client.
     *
     * @param dni nou document identificatiu
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Retorna el nom complet del client.
     *
     * @return nom complet del client
     */
    public String getNomComplet() {
        return nomComplet;
    }

    /**
     * Actualitza el nom complet del client.
     *
     * @param nomComplet nou nom complet
     */
    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    /**
     * Retorna la nacionalitat del client.
     *
     * @return nacionalitat del client
     */
    public String getNacionalitat() {
        return nacionalitat;
    }

    /**
     * Actualitza la nacionalitat del client.
     *
     * @param nacionalitat nova nacionalitat
     */
    public void setNacionalitat(String nacionalitat) {
        this.nacionalitat = nacionalitat;
    }

    /**
     * Retorna la data de caducitat del document d'identitat.
     *
     * @return data de caducitat del document
     */
    public String getDataCaducitatDocument() {
        return dataCaducitatDocument;
    }

    /**
     * Actualitza la data de caducitat del document d'identitat.
     *
     * @param dataCaducitatDocument nova data de caducitat
     */
    public void setDataCaducitatDocument(String dataCaducitatDocument) {
        this.dataCaducitatDocument = dataCaducitatDocument;
    }

    /**
     * Retorna la data de caducitat del carnet de conduir.
     *
     * @return data de caducitat del carnet
     */
    public String getDataCaducitatCarnetConduir() {
        return dataCaducitatCarnetConduir;
    }

    /**
     * Actualitza la data de caducitat del carnet de conduir.
     *
     * @param dataCaducitatCarnetConduir nova data de caducitat del carnet
     */
    public void setDataCaducitatCarnetConduir(String dataCaducitatCarnetConduir) {
        this.dataCaducitatCarnetConduir = dataCaducitatCarnetConduir;
    }

    /**
     * Retorna l'adreça del client.
     *
     * @return adreça postal del client
     */
    public String getAdreca() {
        return adreca;
    }

    /**
     * Actualitza l'adreça del client.
     *
     * @param adreca nova adreça postal
     */
    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    /**
     * Retorna el carnet de conduir del client.
     *
     * @return identificador del carnet de conduir
     */
    public String getCarnetConduir() {
        return carnetConduir;
    }

    /**
     * Actualitza el carnet de conduir del client.
     *
     * @param carnetConduir nou identificador del carnet
     */
    public void setCarnetConduir(String carnetConduir) {
        this.carnetConduir = carnetConduir;
    }

    /**
     * Retorna el número de targeta de crèdit del client.
     *
     * @return número de targeta de crèdit
     */
    public String getNumeroTargetaCredit() {
        return numeroTargetaCredit;
    }

    /**
     * Actualitza el número de targeta de crèdit del client.
     *
     * @param numeroTargetaCredit nou número de targeta
     */
    public void setNumeroTargetaCredit(String numeroTargetaCredit) {
        this.numeroTargetaCredit = numeroTargetaCredit;
    }

    /**
     * Retorna la reputació del client.
     *
     * @return reputació actual del client
     */
    public Reputacio getReputacio() {
        return reputacio;
    }

    /**
     * Actualitza la reputació del client.
     *
     * @param reputacio nova reputació
     */
    public void setReputacio(Reputacio reputacio) {
        this.reputacio = reputacio;
    }

    /**
     * Retorna la llista de reserves del client.
     *
     * @return col·lecció de reserves associades
     */
    public List<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Actualitza la llista de reserves del client.
     *
     * @param reservas nova col·lecció de reserves
     */
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    // ---- Getters/Setters reals dels BLOBs ----
    /**
     * Retorna la fotografia de perfil del client.
     *
     * @return contingut binari de la fotografia
     */
    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    /**
     * Actualitza la fotografia de perfil del client.
     *
     * @param fotoPerfil nova imatge de perfil en binari
     */
    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    /**
     * Retorna el document d'identitat del client.
     *
     * @return contingut binari del document
     */
    public byte[] getDocIdentitat() {
        return docIdentitat;
    }

    /**
     * Actualitza el document d'identitat del client.
     *
     * @param docIdentitat nou document d'identitat en binari
     */
    public void setDocIdentitat(byte[] docIdentitat) {
        this.docIdentitat = docIdentitat;
    }

    /**
     * Retorna la imatge del carnet de conduir del client.
     *
     * @return contingut binari del carnet
     */
    public byte[] getDocCarnet() {
        return docCarnet;
    }

    /**
     * Actualitza la imatge del carnet de conduir del client.
     *
     * @param docCarnet nova imatge del carnet en binari
     */
    public void setDocCarnet(byte[] docCarnet) {
        this.docCarnet = docCarnet;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.dni);
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
        final Client other = (Client) obj;
        return Objects.equals(this.dni, other.dni);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Client{");
        sb.append("dni=").append(dni);
        sb.append(", adreca=").append(adreca);
        sb.append(", carnetConduir=").append(carnetConduir);
        sb.append(", numeroTargetaCredit=").append(numeroTargetaCredit);
        sb.append(", reputacio=").append(reputacio);
        sb.append(", fotoPerfil=").append(fotoPerfil == null ? "null" : (fotoPerfil.length + " bytes"));
        sb.append(", docIdentitat=").append(docIdentitat == null ? "null" : (docIdentitat.length + " bytes"));
        sb.append(", docCarnet=").append(docCarnet == null ? "null" : (docCarnet.length + " bytes"));
        sb.append(", reservas=").append(reservas);
        sb.append('}');
        return sb.toString();
    }
}
