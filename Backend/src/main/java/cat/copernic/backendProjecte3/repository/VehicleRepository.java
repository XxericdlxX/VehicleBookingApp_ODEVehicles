/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author manel
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    /**
     * Cerca vehicles que compleixin: El tipus demanat. Estat 'ALTA'. NO tinguin
     * cap reserva que es solapi amb les dates d'inici i fi.
     */
    @Query("SELECT v FROM Vehicle v WHERE "
            + "v.tipusVehicle = :tipus "
            + "AND v.estatVehicle = 'ALTA' "
            + "AND NOT EXISTS ("
            + "   SELECT r FROM Reserva r "
            + "   WHERE r.vehicle = v "
            + "   AND r.dataInici <= :fi "
            + "   AND r.dataFi >= :inici"
            + ")")
    List<Vehicle> findDisponibles(
            @Param("inici") LocalDate inici,
            @Param("fi") LocalDate fi,
            @Param("tipus") TipusVehicle tipus,
            @Param("codiPostal") String codiPostal
    );

    @Query("""
        SELECT v
        FROM Vehicle v
        WHERE v.estatVehicle = cat.copernic.backendProjecte3.enums.EstatVehicle.ALTA
          AND v.matricula NOT IN (
              SELECT r.vehicle.matricula
              FROM Reserva r
              WHERE r.dataInici <= :fi
                AND r.dataFi >= :inici
          )
    """)
    List<Vehicle> findVehiclesDisponibles(@Param("inici") LocalDate inici, @Param("fi") LocalDate fi);

}
