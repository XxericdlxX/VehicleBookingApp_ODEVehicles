package cat.copernic.backendProjecte3.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entitat que representa un token temporal per restablir la contrasenya d'un
 * usuari.
 *
 * S'utilitza en el flux de "forgot password". El token és vàlid fins a
 * expiresAt i només es pot utilitzar una vegada; quan s'usa es marca used =
 * true.
 *
 * La columna email identifica l'usuari que ha sol·licitat el restabliment. La
 * propietat usuari existeix només per definir la clau forana cap a Usuari sense
 * canviar la lògica existent basada en email (insertable=false,
 * updatable=false).
 */
@Entity
@Table(
        name = "password_reset_token",
        indexes = {
            @Index(name = "idx_password_reset_token_email", columnList = "email")
        }
)
public class PasswordResetToken {

    /**
     * Valor del token de restabliment (identificador únic del registre).
     */
    @Id
    @Column(length = 64)
    private String token;

    /**
     * Correu electrònic de l'usuari propietari del token.
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Relació JPA amb Usuari per integritat referencial (FK). No s'escriu
     * directament perquè la columna real que es desa és email.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "email",
            referencedColumnName = "email",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_pwd_reset_token_usuari")
    )
    private Usuari usuari;

    /**
     * Data i hora de caducitat del token.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Indica si el token ja s'ha utilitzat.
     */
    @Column(nullable = false)
    private boolean used;

    /**
     * Constructor buit requerit per JPA.
     */
    public PasswordResetToken() {
    }

    /**
     * Crea un token nou de restabliment de contrasenya. Per defecte, el token
     * es crea com a no utilitzat.
     *
     * @param token valor del token
     * @param email correu de l'usuari
     * @param expiresAt data i hora de caducitat
     */
    public PasswordResetToken(String token, String email, LocalDateTime expiresAt) {
        this.token = token;
        this.email = email;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    /**
     * Retorna el valor del token temporal de recuperació.
     *
     * @return token de recuperació
     */
    public String getToken() {
        return token;
    }

    /**
     * Retorna el correu de l'usuari associat al token.
     *
     * @return correu electrònic del propietari del token
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna la data i hora de caducitat del token.
     *
     * @return moment de caducitat del token
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Indica si el token ja s'ha utilitzat.
     *
     * @return {@code true} si el token ja s'ha consumit
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Marca el token com a utilitzat o no utilitzat.
     *
     * @param used nou estat d'ús del token
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Retorna l'usuari propietari (carrega lazy). Getter inclòs perquè no surti
     * el warning de camp no utilitzat.
     */
    public Usuari getUsuari() {
        return usuari;
    }
}
