package com.cinema.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour le hashing et la vérification de mots de passe
 * Utilise BCrypt avec un facteur de coût de 10
 */
public class PasswordUtil {
    
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Hash un mot de passe en clair avec BCrypt
     * 
     * @param plainPassword Mot de passe en clair
     * @return Hash BCrypt du mot de passe
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Vérifie qu'un mot de passe correspond à son hash
     * 
     * @param plainPassword Mot de passe en clair
     * @param hashedPassword Hash stocké en base
     * @return true si le mot de passe correspond
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash invalide
            return false;
        }
    }
}
