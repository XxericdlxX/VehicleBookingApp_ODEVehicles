 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori JPA per a la gestió d'usuaris del sistema.
 */
@Repository
public interface UsuariRepository extends JpaRepository<Usuari, String> {

    /**
     * Cerca un usuari pel seu correu electrònic.
     *
     * @param email correu electrònic de l'usuari
     * @return usuari trobat, si existeix
     */
    Optional<Usuari> findByEmail(String email);
}
