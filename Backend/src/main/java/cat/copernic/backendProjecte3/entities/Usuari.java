/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entitat base que representa un usuari autenticable del sistema.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuari")
public class Usuari {

    @Id
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol = UserRole.NONE;

    /**
     * Crea un usuari amb el correu indicat.
     *
     * @param email correu electrònic identificador de l'usuari
     */
    public Usuari(String email) {
        this.email = email;
    }

    /**
     * Constructor buit requerit per JPA.
     */
    public Usuari() {
    }

    /**
     * Actualitza el correu electrònic de l'usuari.
     *
     * @param email nou correu electrònic
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Actualitza la contrasenya de l'usuari.
     *
     * @param password nova contrasenya codificada o en procés de codificació
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Calcula el codi hash de l'usuari a partir del correu.
     *
     * @return codi hash de l'usuari
     */
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * Retorna el correu electrònic de l'usuari.
     *
     * @return correu electrònic
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna la llista de rols associats a l'usuari.
     *
     * @return col·lecció amb el rol assignat a l'usuari
     */
    public List<UserRole> getAuthorities() {

        List<UserRole> roles = new ArrayList<>();

        roles.add(this.getRol());

        return roles;
    }

    /**
     * Retorna la contrasenya de l'usuari.
     *
     * @return contrasenya emmagatzemada
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Retorna el nom d'usuari utilitzat a l'aplicació.
     *
     * @return correu electrònic de l'usuari
     */
    public String getUsername() {
        return this.email;
    }

    /**
     * Retorna el rol de l'usuari.
     *
     * @return rol assignat
     */
    public UserRole getRol() {
        return rol;
    }

    /**
     * Assigna un rol a l'usuari.
     *
     * @param rol nou rol de l'usuari
     */
    public void setRol(UserRole rol) {
        this.rol = rol;
    }

    /**
     * Compara dos usuaris a partir del seu correu electrònic.
     *
     * @param o objecte a comparar
     * @return {@code true} si representen el mateix usuari
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Usuari usuari = (Usuari) o;
        return Objects.equals(email, usuari.email);
    }

    /**
     * Genera una representació textual de l'usuari.
     *
     * @return cadena amb les dades principals de l'usuari
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuari{");
        sb.append("email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", rol=").append(rol);
        sb.append('}');
        return sb.toString();
    }
}
