/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori JPA per a la persistència de clients.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}
