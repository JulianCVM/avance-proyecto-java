/**
 * Script para el chatbot flotante que aparece en la esquina inferior derecha
 */
document.addEventListener('DOMContentLoaded', function() {
    // Crear elementos del chatbot
    createChatbotElements();
    
    // Configurar eventos
    setupEventHandlers();
    
    // Verificar si hay una API key configurada
    checkApiKeyStatus();
});

/**
 * Crea los elementos HTML del chatbot flotante
 */
function createChatbotElements() {
    const chatbotHTML = `
        <div id="floating-chatbot" class="floating-chatbot-container">
            <div class="chatbot-button" id="chatbot-toggle">
                <i class="bi bi-chat-dots-fill"></i>
                <span class="chatbot-badge">1</span>
            </div>
            <div class="chatbot-panel" id="chatbot-panel">
                <div class="chatbot-header">
                    <div class="d-flex align-items-center">
                        <div class="chatbot-avatar me-2">
                            <i class="bi bi-robot"></i>
                        </div>
                        <h5 class="mb-0">Asistente Avance AI</h5>
                    </div>
                    <div>
                        <button class="btn-minimize me-2" id="chatbot-minimize">
                            <i class="bi bi-dash-lg"></i>
                        </button>
                        <button class="btn-close btn-close-white" id="chatbot-close"></button>
                    </div>
                </div>
                <div class="chatbot-body" id="chatbot-messages">
                    <div class="message bot-message">
                        <div class="message-content">
                            <p>¡Hola! Soy el asistente virtual de Avance AI. ¿En qué puedo ayudarte hoy?</p>
                        </div>
                        <div class="message-time">Ahora</div>
                    </div>
                </div>
                <div id="api-key-required" class="p-3 bg-light rounded mb-2 d-none">
                    <p class="small text-muted mb-2">Para utilizar el asistente, necesitas configurar una API key.</p>
                    <a href="/api-keys" class="btn btn-sm btn-primary w-100">Configurar API Key</a>
                </div>
                <div class="chatbot-footer">
                    <form id="chatbot-form">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="Escribe tu mensaje..." id="chatbot-input">
                            <button class="btn btn-primary" type="submit">
                                <i class="bi bi-send-fill"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    
    // Añadir a la página
    const chatbotContainer = document.createElement('div');
    chatbotContainer.innerHTML = chatbotHTML;
    document.body.appendChild(chatbotContainer.firstElementChild);
    
    // Añadir estilos CSS
    const styleElement = document.createElement('style');
    styleElement.textContent = `
        .floating-chatbot-container {
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 1000;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .chatbot-button {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: linear-gradient(135deg, #6610f2 0%, #5a0ddd 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(102, 16, 242, 0.3);
            transition: all 0.3s ease;
            position: relative;
        }
        .chatbot-button i {
            font-size: 24px;
        }
        .chatbot-button:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px rgba(102, 16, 242, 0.4);
        }
        .chatbot-button.active {
            transform: scale(0);
            opacity: 0;
        }
        .chatbot-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background-color: #dc3545;
            color: white;
            border-radius: 50%;
            width: 22px;
            height: 22px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: bold;
        }
        .chatbot-panel {
            position: absolute;
            bottom: -20px;
            right: -20px;
            width: 350px;
            height: 500px;
            background-color: white;
            border-radius: 16px;
            box-shadow: 0 5px 25px rgba(0, 0, 0, 0.15);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            transform: scale(0);
            transform-origin: bottom right;
            opacity: 0;
            transition: all 0.3s ease;
        }
        .chatbot-panel.active {
            transform: scale(1);
            opacity: 1;
            bottom: 80px;
            right: 0;
        }
        .chatbot-header {
            background: linear-gradient(135deg, #6610f2 0%, #5a0ddd 100%);
            color: white;
            padding: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .chatbot-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background-color: rgba(255, 255, 255, 0.2);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .chatbot-avatar i {
            font-size: 18px;
        }
        .btn-minimize {
            background: transparent;
            border: none;
            color: white;
            cursor: pointer;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 24px;
            height: 24px;
        }
        .chatbot-body {
            padding: 15px;
            flex: 1;
            overflow-y: auto;
            background-color: #f8f9fa;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%23d1d1d1' fill-opacity='0.1'%3E%3Cpath opacity='.5' d='M96 95h4v1h-4v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9zm-1 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm9-10v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm9-10v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9z'/%3E%3Cpath d='M6 5V0H5v5H0v1h5v94h1V6h94V5H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }
        .message {
            margin-bottom: 15px;
            max-width: 85%;
            animation: fadeIn 0.3s ease;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .user-message {
            margin-left: auto;
        }
        .bot-message {
            margin-right: auto;
        }
        .message-content {
            padding: 12px 15px;
            border-radius: 18px;
            position: relative;
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
        }
        .user-message .message-content {
            background-color: #6610f2;
            color: white;
            border-bottom-right-radius: 4px;
        }
        .bot-message .message-content {
            background-color: white;
            border-bottom-left-radius: 4px;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
        }
        .message-time {
            font-size: 0.7rem;
            color: #adb5bd;
            margin-top: 5px;
            text-align: right;
        }
        .user-message .message-time {
            color: #e0e0e0;
        }
        .typing-indicator {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            max-width: 85%;
            margin-right: auto;
        }
        .typing-dots {
            display: flex;
            align-items: center;
            gap: 4px;
        }
        .typing-dots span {
            height: 8px;
            width: 8px;
            border-radius: 50%;
            background-color: #6c757d;
            display: inline-block;
            animation: typingBubble 1s infinite;
        }
        .typing-dots span:nth-child(1) { animation-delay: 0s; }
        .typing-dots span:nth-child(2) { animation-delay: 0.2s; }
        .typing-dots span:nth-child(3) { animation-delay: 0.4s; }
        @keyframes typingBubble {
            0% { transform: translateY(0); }
            50% { transform: translateY(-5px); }
            100% { transform: translateY(0); }
        }
        .chatbot-footer {
            padding: 15px;
            background-color: white;
            border-top: 1px solid #e9ecef;
        }
        .chatbot-footer .form-control {
            border-radius: 20px;
            padding-left: 15px;
            border: 1px solid #dee2e6;
            transition: all 0.3s ease;
        }
        .chatbot-footer .form-control:focus {
            border-color: #6610f2;
            box-shadow: 0 0 0 0.2rem rgba(102, 16, 242, 0.25);
        }
        .chatbot-footer .btn {
            border-radius: 50%;
            width: 38px;
            height: 38px;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
        }
        .chatbot-footer .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(102, 16, 242, 0.3);
        }
        .chatbot-footer .input-group {
            position: relative;
        }
    `;
    document.head.appendChild(styleElement);
}

/**
 * Configura los manejadores de eventos
 */
function setupEventHandlers() {
    // Botón para abrir/cerrar el chat
    const toggleButton = document.getElementById('chatbot-toggle');
    const closeButton = document.getElementById('chatbot-close');
    const minimizeButton = document.getElementById('chatbot-minimize');
    const chatPanel = document.getElementById('chatbot-panel');
    const chatForm = document.getElementById('chatbot-form');
    const chatInput = document.getElementById('chatbot-input');
    
    // Abrir chatbot
    toggleButton.addEventListener('click', function() {
        chatPanel.classList.toggle('active');
        toggleButton.classList.toggle('active');
        
        // Remover indicador de nuevo mensaje
        const badge = toggleButton.querySelector('.chatbot-badge');
        if (badge) {
            badge.style.display = 'none';
        }
        
        // Enfocar el input
        setTimeout(() => {
            chatInput.focus();
        }, 300);
    });
    
    // Cerrar chatbot
    closeButton.addEventListener('click', function() {
        chatPanel.classList.remove('active');
        toggleButton.classList.remove('active');
    });
    
    // Minimizar chatbot
    minimizeButton.addEventListener('click', function() {
        chatPanel.classList.remove('active');
        toggleButton.classList.remove('active');
    });
    
    // Enviar mensaje
    chatForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const message = chatInput.value.trim();
        if (message) {
            sendMessage(message);
            chatInput.value = '';
            chatInput.focus();
        }
    });
    
    // Auto-resize del textarea
    chatInput.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });
}

/**
 * Envía un mensaje al chatbot
 */
function sendMessage(message) {
    // Añadir mensaje del usuario a la interfaz
    addMessage(message, 'user');
    
    // Mostrar indicador de "escribiendo..."
    showTypingIndicator();
    
    // Enviar mensaje al servidor
    fetch('/chatbot/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message: message })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor');
        }
        return response.json();
    })
    .then(data => {
        // Ocultar indicador de escritura
        hideTypingIndicator();
        
        // Mostrar respuesta
        addMessage(data.response, 'bot');
    })
    .catch(error => {
        console.error('Error:', error);
        hideTypingIndicator();
        
        // Mostrar mensaje de error
        if (error.message.includes('API key')) {
            showApiKeyRequired();
        } else {
            addMessage('Lo siento, ha ocurrido un error. Por favor, intenta nuevamente.', 'bot error');
        }
    });
}

/**
 * Añade un mensaje al panel de chat
 */
function addMessage(text, type) {
    const messagesContainer = document.getElementById('chatbot-messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}-message`;
    
    // Obtener la hora actual formateada
    const now = new Date();
    const timeStr = now.getHours().toString().padStart(2, '0') + ':' + 
                   now.getMinutes().toString().padStart(2, '0');
    
    messageDiv.innerHTML = `
        <div class="message-content">
            <p>${text}</p>
        </div>
        <div class="message-time">${timeStr}</div>
    `;
    
    messagesContainer.appendChild(messageDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

/**
 * Muestra el indicador de "escribiendo..."
 */
function showTypingIndicator() {
    const messagesContainer = document.getElementById('chatbot-messages');
    const typingDiv = document.createElement('div');
    typingDiv.className = 'typing-indicator';
    typingDiv.id = 'typing-indicator';
    
    typingDiv.innerHTML = `
        <div class="message-content">
            <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
    `;
    
    messagesContainer.appendChild(typingDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

/**
 * Oculta el indicador de "escribiendo..."
 */
function hideTypingIndicator() {
    const typingIndicator = document.getElementById('typing-indicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}

/**
 * Verifica si hay una API key configurada para Gemini
 */
function checkApiKeyStatus() {
    fetch('/api-keys/status/gemini')
    .then(response => response.json())
    .then(data => {
        if (!data.configured) {
            showApiKeyRequired();
        }
    })
    .catch(error => {
        console.error('Error al verificar estado de API key:', error);
    });
}

/**
 * Muestra la notificación de que se requiere una API key
 */
function showApiKeyRequired() {
    const apiKeyMessage = document.getElementById('api-key-required');
    const chatInput = document.getElementById('chatbot-input');
    const sendButton = chatInput.nextElementSibling;
    
    apiKeyMessage.classList.remove('d-none');
    chatInput.disabled = true;
    sendButton.disabled = true;
} 