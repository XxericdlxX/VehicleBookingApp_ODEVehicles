/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.config;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author manel
 */
public class PasswordHasher {
    
    /**
     * Genera un encode seguro a partir de una contraseña.
     * @param password Contraseña en texto plano.
     * @return El encode completo (incluye algoritmo, costo, sal y encode).
     */
    public static String encode(String password) {
        
        return BCrypt.hashpw(password, BCrypt.gensalt(6));
    }

    /**
     * Verifica si una contraseña coincide con un encode previo.
     * @param password Contraseña en texto plano a verificar.
     * @param storedHash El encode almacenado previamente.
     * @return true si la contraseña es correcta.
     */
    public static boolean check(String password, String storedHash) {
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (Exception e) {
            // Maneja posibles formatos de encode inválidos
            return false;
        }
    }
    
}
