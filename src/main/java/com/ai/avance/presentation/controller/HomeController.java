package com.ai.avance.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador para páginas de test y diagnóstico.
 */
@Controller
public class HomeController {

    /**
     * Página de test para verificar el correcto funcionamiento.
     */
    @GetMapping("/test")
    @ResponseBody
    public String home() {
        return "<h1>Bienvenido a Avance AI</h1>" +
               "<p>La aplicación está funcionando correctamente.</p>" +
               "<p>Si estás usando H2, puedes acceder a la consola en: <a href='/h2-console'>H2 Console</a></p>";
    }

    /**
     * Prueba simple para verificar que la API funciona.
     */
    @GetMapping("/api/test")
    @ResponseBody
    public String apiTest() {
        return "{\"message\": \"API funcionando correctamente\"}";
    }
} 