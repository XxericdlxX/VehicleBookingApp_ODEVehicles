/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.exceptions;

/**
 * Excepció que s'utilitza quan una reserva no es pot anul·lar.
 *
 * Aquesta excepció es llança quan el sistema detecta que una reserva no
 * compleix les condicions necessàries per ser anul·lada, per exemple: - la
 * reserva ja ha començat - la reserva ja ha finalitzat - no es compleixen les
 * condicions de cancel·lació establertes
 *
 * Aquesta excepció s'utilitza principalment dins del servei
 * {@link cat.copernic.backendProjecte3.business.ReservaService}.
 *
 * @author orjon
 */
public class ReservaNoAnulableException extends Exception {

    /**
     * Constructor de l'excepció amb missatge descriptiu.
     *
     * @param msg missatge que descriu el motiu pel qual la reserva no es pot
     * anul·lar
     */
    public ReservaNoAnulableException(String msg) {
        super(msg);
    }
}
