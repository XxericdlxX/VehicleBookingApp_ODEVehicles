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
 * Entitat que representa un token d'autenticació (sessió) d'un usuari.
 *
 * S'utilitza per validar peticions autenticades a l'API. Un token deixa de ser
 * vàlid quan caduca (expiresAt) o quan ha estat revocat (revoked), per exemple
 * en fer logout.
 *
 * La columna email identifica l'usuari propietari del token. La propietat
 * usuari existeix només per definir la clau forana cap a Usuari sense canviar
 * la lògica existent basada en email (insertable=false, updatable=false).
 */
@Entity
@Table(
        name = "auth_token",
        indexes = {
            @Index(name = "idx_auth_token_email", columnList = "email")
        }
)
public class AuthToken {

    /**
     * Valor del token (identificador únic del registre).
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
            foreignKey = @ForeignKey(name = "fk_auth_token_usuari")
    )
    private Usuari usuari;

    /**
     * Data i hora de caducitat del token.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Indica si el token ha estat revocat (per exemple, en fer logout).
     */
    @Column(nullable = false)
    private boolean revoked;

    /**
     * Constructor buit requerit per JPA.
     */
    public AuthToken() {
    }

    /**
     * Crea un nou token d'autenticació per a un usuari. Per defecte, el token
     * es crea com a no revocat.
     *
     * @param token valor del token
     * @param email correu de l'usuari
     * @param expiresAt data i hora de caducitat
     */
    public AuthToken(String token, String email, LocalDateTime expiresAt) {
        this.token = token;
        this.email = email;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    /**
     * Retorna el valor del token d'autenticació.
     *
     * @return token de sessió
     */
    public String getToken() {
        return token;
    }

    /**
     * Retorna el correu de l'usuari propietari del token.
     *
     * @return correu electrònic de l'usuari
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna la data i hora de caducitat del token.
     *
     * @return data i hora de caducitat
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Indica si el token està revocat.
     *
     * @return {@code true} si el token ja no és vàlid per revocació
     */
    public boolean isRevoked() {
        return revoked;
    }

    /**
     * Actualitza l'estat de revocació del token.
     *
     * @param revoked nou estat de revocació
     */
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    /**
     * Retorna l'usuari propietari (carrega lazy). Getter inclòs perquè no surti
     * el warning de camp no utilitzat.
     */
    public Usuari getUsuari() {
        return usuari;
    }
}
