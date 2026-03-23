/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import java.math.BigDecimal;

/**
 *
 * @author orjon
 */
public interface EmailService {

    void enviarAnulacioReserva(String email, Long idReserva, BigDecimal importRetornat);
}
