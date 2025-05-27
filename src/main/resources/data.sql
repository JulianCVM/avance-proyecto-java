-- Datos iniciales para el agente por defecto (Chatbot informativo)
-- Solo se ejecutará si la tabla está vacía
INSERT INTO default_agent (name, description, system_prompt, api_token, model_provider, active, created_at, updated_at)
SELECT 'Asistente AVANCE', 
       'Chatbot informativo del sistema AVANCE',
       'Eres el asistente de información del sistema AVANCE, una plataforma para crear agentes de IA personalizados. Tu tarea es proporcionar información sobre cómo usar el sistema, sus características y ayudar a resolver problemas comunes. Debes ser amigable, informativo y útil, centrándote exclusivamente en información sobre la plataforma AVANCE. No debes proporcionar información que no esté relacionada con AVANCE o sus funcionalidades. Si te preguntan sobre otros temas, debes amablemente redirigir la conversación hacia cómo AVANCE puede ser utilizado para esos propósitos.',
       'AIzaSyAoN96NskMz9hj1g4hksIcawywSHTgYG48',
       'gemini',
       1,
       NOW(),
       NOW()
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM default_agent WHERE active = 1);

-- Ejemplo de usuario administrador (la contraseña es "admin" encriptada con BCrypt)
-- Solo se ejecutará si la tabla está vacía
INSERT INTO users (username, email, password, first_name, last_name, active, created_at)
SELECT 'admin', 
       'admin@avance.com', 
       '$2a$10$rAfu6lzEGJskRHkSCYJl0.K1NxKDojIxnqL4KjUMukcBH3fikFVa2', 
       'Admin', 
       'Usuario', 
       1, 
       NOW()
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Agregar rol de administrador
-- Solo se ejecutará si no existe ya el rol para el usuario
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN'
FROM users
WHERE username = 'admin'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin') AND role = 'ROLE_ADMIN'); 