package com.microservices.user.domain.model;

import java.util.regex.Pattern;

/**
 * 📧 EMAIL - Value Object
 * 
 * Representa un email válido.
 * 
 * Ventajas de usar un Value Object para Email:
 * 1. Validación centralizada: El email siempre es válido
 * 2. No puedes crear un Email inválido
 * 3. Expresividad: El código es más claro
 * 4. Reutilización: La validación está en un solo lugar
 * 
 * Ejemplo:
 * ❌ String email = "invalid-email"; // Se puede crear
 * ✅ Email email = new Email("invalid-email"); // Lanza excepción
 */
public record Email(String value) {
    
    // Patrón de validación de email (simplificado)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    /**
     * Constructor compacto con validación
     * Se ejecuta automáticamente al crear un Email
     */
    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        value = value.trim().toLowerCase(); // Normalizar
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email inválido: " + value);
        }
    }
    
    /**
     * Verifica si el email es de un dominio específico
     */
    public boolean isDomain(String domain) {
        return value.endsWith("@" + domain);
    }
    
    /**
     * Obtiene el dominio del email
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    /**
     * Obtiene la parte local del email (antes del @)
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
}
