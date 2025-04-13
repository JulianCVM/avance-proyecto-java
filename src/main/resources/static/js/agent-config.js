/**
 * JavaScript para la página de configuración de agentes
 */
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('agentForm');
    const allowedTopicsInput = document.getElementById('allowedTopics');
    const restrictedTopicsInput = document.getElementById('restrictedTopics');
    
    // Preparar los datos antes de enviar el formulario
    if (form) {
        form.addEventListener('submit', function(e) {
            // Evitar que se envíe si hay campos requeridos vacíos
            const requiredFields = form.querySelectorAll('[required]');
            let hasEmptyField = false;
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    hasEmptyField = true;
                    field.classList.add('is-invalid');
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            
            if (hasEmptyField) {
                e.preventDefault();
                showAlert('Por favor, completa todos los campos requeridos.', 'danger');
                return;
            }
            
            // Si todo está bien, mostrar un indicador de carga
            showLoadingIndicator();
        });
    }
    
    // Validar campos al perder el foco
    document.querySelectorAll('.form-control, .form-select').forEach(element => {
        element.addEventListener('blur', function() {
            if (this.hasAttribute('required') && !this.value.trim()) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
    });
    
    // Actualizar vista previa del prompt del sistema al cambiar configuración
    document.querySelectorAll('#name, #purpose, #tone, #domainContext, #allowedTopics, #restrictedTopics').forEach(field => {
        field.addEventListener('input', updateSystemPromptPreview);
    });
    
    // Inicializar la vista previa
    updateSystemPromptPreview();
    
    /**
     * Actualiza la vista previa del prompt del sistema en base a la configuración
     */
    function updateSystemPromptPreview() {
        const previewElement = document.getElementById('systemPromptPreview');
        if (!previewElement) return;
        
        const name = document.getElementById('name').value || '[Nombre del Agente]';
        const purpose = document.getElementById('purpose').value || '[Propósito]';
        const tone = document.getElementById('tone').value || 'neutral';
        const domainContext = document.getElementById('domainContext').value || '';
        const allowedTopics = allowedTopicsInput.value || '';
        const restrictedTopics = restrictedTopicsInput.value || '';
        
        let preview = `Eres ${name}, un asistente IA especializado.\n\n`;
        preview += `Propósito: ${purpose}\n\n`;
        preview += `Tono de comunicación: ${tone}\n\n`;
        
        if (domainContext) {
            preview += `Contexto de dominio: ${domainContext}\n\n`;
        }
        
        if (allowedTopics) {
            preview += `Temas permitidos: ${allowedTopics}\n\n`;
        }
        
        if (restrictedTopics) {
            preview += `Debes evitar hablar sobre: ${restrictedTopics}\n\n`;
        }
        
        previewElement.textContent = preview;
    }
    
    /**
     * Muestra una alerta en la página
     */
    function showAlert(message, type = 'info') {
        const alertsContainer = document.getElementById('alertsContainer');
        if (!alertsContainer) return;
        
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        alertsContainer.appendChild(alert);
        
        // Auto-cerrar después de 5 segundos
        setTimeout(() => {
            alert.classList.remove('show');
            setTimeout(() => alert.remove(), 150);
        }, 5000);
    }
    
    /**
     * Muestra un indicador de carga durante el envío del formulario
     */
    function showLoadingIndicator() {
        const submitButton = form.querySelector('button[type="submit"]');
        if (!submitButton) return;
        
        const originalText = submitButton.innerHTML;
        submitButton.disabled = true;
        submitButton.innerHTML = `
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            Guardando...
        `;
        
        // Restaurar el botón después de 10 segundos (por si hay algún problema con la respuesta)
        setTimeout(() => {
            submitButton.disabled = false;
            submitButton.innerHTML = originalText;
        }, 10000);
    }
}); 