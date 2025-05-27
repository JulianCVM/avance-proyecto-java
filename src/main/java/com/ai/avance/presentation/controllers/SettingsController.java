package com.ai.avance.presentation.controllers;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para manejar las solicitudes de configuración de usuario.
 */
@Slf4j
@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserService userService;

    /**
     * Procesa cambios en el perfil del usuario.
     */
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada, por favor inicia sesión nuevamente");
                return "redirect:/login";
            }
            
            // Actualizar datos del usuario
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            
            userService.save(user);
            
            // Actualizar la sesión con los nuevos datos
            session.setAttribute("user", user);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar el perfil: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/settings";
    }

    /**
     * Procesa cambios en las preferencias de la cuenta.
     */
    @PostMapping("/account")
    public String updateAccount(
            @RequestParam String language,
            @RequestParam String timezone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Aquí se implementaría la lógica para guardar las preferencias de cuenta
            // Por ahora, simplemente devolvemos un mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Preferencias de cuenta actualizadas correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar las preferencias de cuenta: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar las preferencias: " + e.getMessage());
        }
        
        return "redirect:/settings";
    }

    /**
     * Procesa cambios en las contraseñas.
     */
    @PostMapping("/security/password")
    public String updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada, por favor inicia sesión nuevamente");
                return "redirect:/login";
            }
            
            // Verificar que la contraseña actual sea correcta
            // Esta es una simulación, en un sistema real se verificaría con un encoder
            if (!user.getPassword().equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/settings";
            }
            
            // Verificar que las nuevas contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
                return "redirect:/settings";
            }
            
            // Actualizar la contraseña
            user.setPassword(newPassword);
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar la contraseña: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/settings";
    }

    /**
     * Procesa cambios en las preferencias de IA.
     */
    @PostMapping("/preferences")
    public String updatePreferences(
            @RequestParam String defaultModel,
            @RequestParam(required = false) boolean saveConversations,
            @RequestParam(required = false) boolean darkMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Aquí se implementaría la lógica para guardar las preferencias de IA
            // Por ahora, simplemente devolvemos un mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Preferencias de IA actualizadas correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar las preferencias de IA: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar las preferencias: " + e.getMessage());
        }
        
        return "redirect:/settings";
    }

    /**
     * Procesa la eliminación de la cuenta.
     */
    @PostMapping("/account/delete")
    public String deleteAccount(
            @RequestParam String deleteConfirmation,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar que la confirmación sea correcta
            if (!"ELIMINAR".equals(deleteConfirmation)) {
                redirectAttributes.addFlashAttribute("error", "La confirmación de eliminación es incorrecta");
                return "redirect:/settings";
            }
            
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada, por favor inicia sesión nuevamente");
                return "redirect:/login";
            }
            
            // Eliminar la cuenta
            userService.deleteById(user.getId());
            
            // Invalidar la sesión
            session.invalidate();
            
            redirectAttributes.addFlashAttribute("success", "Tu cuenta ha sido eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar la cuenta: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cuenta: " + e.getMessage());
        }
        
        return "redirect:/";
    }
} 