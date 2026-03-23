/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author manel
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar reservas de un cliente
    List<Reserva> findByClient_Email(String email);

    // Buscar reservas de un vehículo
    List<Reserva> findByVehicle_Matricula(String matricula);

    /**
     * *
     * Verifica si un vehicle ja està reservat per unes determinades dates
     *
     * @param matricula
     * @param iniciSolicitado
     * @param fiSolicitado
     * @return
     */
    @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.dataInici <= :fiSolicitado AND r.dataFi >= :iniciSolicitado")
    List<Reserva> findReservasSolapadas(@Param("matricula") String matricula, @Param("iniciSolicitado") LocalDate iniciSolicitado, @Param("fiSolicitado") LocalDate fiSolicitado);

    boolean existsByVehicleAndDataFiAfter(Vehicle vehicle, LocalDate data);

    List<Reserva> findByClientEmailOrderByDataIniciAsc(String email);

    List<Reserva> findByClientEmailOrderByDataIniciDesc(String email);

    Optional<Reserva> findByIdReserva(Long idReserva);

    Optional<Reserva> findByIdReservaAndClientEmail(Long idReserva, String email);

    // Opcional (millor) per carregar vehicle+client d'una tirada (evita Lazy issues)
    @Query("""
        select r
        from Reserva r
        join fetch r.client
        join fetch r.vehicle
        where r.idReserva = :id
    """)
    Optional<Reserva> findDetallById(@Param("id") Long id);
}
