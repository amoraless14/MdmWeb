document.addEventListener("DOMContentLoaded", () => {

    const txtUsuario = document.getElementById("txtUsuario");
    const txtPassword = document.getElementById("txtPassword");
    const btnLogin = document.getElementById("btnLogin");
    const mensajeError = document.getElementById("mensajeError");

    txtPassword.addEventListener("keypress", e => {

        if (e.key === "Enter") {

            iniciarSesion();

        }

    });

    btnLogin.addEventListener("click", iniciarSesion);

    async function iniciarSesion() {

        mensajeError.style.display = "none";

        const usuario = txtUsuario.value.trim();
        const password = txtPassword.value.trim();

        if (usuario === "" || password === "") {

            mostrarError("Ingrese usuario y contraseña.");

            return;

        }

        btnLogin.disabled = true;
        btnLogin.innerHTML = "Ingresando...";

        try {

            const res = await fetch(`/usuarios/login?usuario=${encodeURIComponent(usuario)}&password=${encodeURIComponent(password)}`, {

                method: "POST"

            });

            if (!res.ok) {

                throw new Error();

            }

            const datos = await res.json();

            sessionStorage.setItem("id", datos.id);
            sessionStorage.setItem("usuario", datos.usuario);
            sessionStorage.setItem("nombre", datos.nombre);
            sessionStorage.setItem("rol", datos.rol);

            window.location.replace("index.html");

        } catch (e) {

            mostrarError("Usuario o contraseña incorrectos.");

        }

        btnLogin.disabled = false;
        btnLogin.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i> Iniciar Sesión';

    }

    function mostrarError(texto) {

        mensajeError.innerText = texto;
        mensajeError.style.display = "block";

    }

});