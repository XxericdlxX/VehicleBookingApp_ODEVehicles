/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.exceptions;

/**
 * Excepció que s'utilitza quan una reserva no es troba al sistema.
 *
 * Aquesta excepció es llança quan es busca una reserva a la base de dades i no
 * existeix cap registre amb l'identificador indicat.
 *
 * S'utilitza principalment en operacions com: - consulta d'una reserva -
 * anul·lació d'una reserva - obtenció del detall d'una reserva
 *
 * Aquesta excepció és utilitzada habitualment dins del servei
 * {@link cat.copernic.backendProjecte3.business.ReservaService}.
 *
 * @author manel
 */
public class ReservaNoTrobadaException extends Exception {

    /**
     * Constructor de l'excepció amb un missatge descriptiu.
     *
     * @param msg missatge que descriu el motiu pel qual la reserva no s'ha
     * pogut trobar
     */
    public ReservaNoTrobadaException(String msg) {
        super(msg);
    }

}
