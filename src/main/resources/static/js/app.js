document.addEventListener('DOMContentLoaded', function() {
    // Lógica para la navegación activa
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.list-group-item');
    
    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        
        // Manejo especial para la página de inicio (/)
        if (currentPath === linkPath) {
            link.classList.add('active');
        } else if (currentPath.startsWith(linkPath) && linkPath !== '/') {
            link.classList.add('active');
        }
    });

    // Lógica para el formulario de cerrar sesión
    const logoutForm = document.getElementById('logout-form');
    if (logoutForm) {
        const logoutButton = logoutForm.querySelector('button');
        logoutButton.addEventListener('click', function(event) {
            event.preventDefault(); 
            logoutForm.submit();
        });
    }

    // Lógica para el modal de confirmación (si lo mueves a este archivo)
    const confirmModal = document.getElementById('confirmDeleteModal');
    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            const itemId = button.getAttribute('data-id');
            const actionUrl = button.getAttribute('data-action');
            
            const form = confirmModal.querySelector('#confirmForm');

            if (form && itemId && actionUrl) {
                form.action = actionUrl;
                const hiddenInput = form.querySelector('input[name="id"]');
                if (hiddenInput) {
                    hiddenInput.value = itemId;
                }
            }
        });
    }
});