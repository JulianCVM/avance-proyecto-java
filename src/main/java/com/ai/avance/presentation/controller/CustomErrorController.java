package com.ai.avance.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar errores de la aplicación.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Maneja los errores de la aplicación, incluyendo 404, 500, etc.
     * En caso de error, redirige a la página principal.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Obtener el código de error
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        
        if (statusCode != null) {
            if (statusCode == 404) {
                // Página no encontrada
                model.addAttribute("errorMessage", "La página que estás buscando no existe.");
            } else if (statusCode == 403) {
                // Acceso prohibido
                model.addAttribute("errorMessage", "No tienes permisos para acceder a esta página.");
            } else if (statusCode == 500) {
                // Error interno del servidor
                model.addAttribute("errorMessage", "Ha ocurrido un error en el servidor. Por favor, inténtalo más tarde.");
            } else {
                // Otro tipo de error
                model.addAttribute("errorMessage", "Ha ocurrido un error. Por favor, inténtalo más tarde.");
            }
            
            model.addAttribute("statusCode", statusCode);
        }
        
        // Para SPA, redirigir a la página principal
        return "redirect:/";
    }
} 