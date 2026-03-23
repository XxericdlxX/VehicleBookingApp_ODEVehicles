/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.exceptions;

/**
 * Excepció que s'utilitza quan les dates d'una reserva no són vàlides.
 *
 * Aquesta excepció es llança en situacions com: - la data d'inici és posterior
 * a la data de finalització - la reserva es vol fer en una data passada - les
 * dates introduïdes no compleixen les regles del sistema
 *
 * S'utilitza principalment dins de la lògica de negoci del servei
 * {@link cat.copernic.backendProjecte3.business.ReservaService}.
 *
 * @author manel
 */
public class ReservaDatesNoValidsException extends Exception {

    /**
     * Constructor de l'excepció amb missatge descriptiu.
     *
     * @param msg missatge que descriu el motiu de l'error
     */
    public ReservaDatesNoValidsException(String msg) {
        super(msg);
    }

}
