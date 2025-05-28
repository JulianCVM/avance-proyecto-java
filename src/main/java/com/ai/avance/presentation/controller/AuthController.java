package com.ai.avance.presentation.controller;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.presentation.dto.UserDTO;
import com.ai.avance.security.JwtTokenProvider;
import com.ai.avance.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * Controlador para gestionar la autenticación de usuarios.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Página de inicio de sesión.
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "login";
    }

    /**
     * Procesa el inicio de sesión.
     */
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") UserDTO userDTO, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userDTO.getUsername(),
                    userDTO.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            UserEntity user = userService.findByUsername(userDTO.getUsername());
            user.setLastLogin(LocalDateTime.now());
            userService.save(user);
            
            session.setAttribute("user", user);
            session.setAttribute("token", jwt);
            
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/login";
        }
    }

    /**
     * Página de registro.
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    /**
     * Procesa el registro de usuario.
     */
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") UserDTO userDTO,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(userDTO.getUsername()) != null) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso");
                return "redirect:/register";
            }
            
            if (userService.findByEmail(userDTO.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado");
                return "redirect:/register";
            }
            
            UserEntity newUser = new UserEntity();
            newUser.setUsername(userDTO.getUsername());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newUser.setFirstName(userDTO.getFirstName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setActive(true);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.getRoles().add("ROLE_USER");
            
            userService.save(newUser);
            
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor, inicia sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
} 