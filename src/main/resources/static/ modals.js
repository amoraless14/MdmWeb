async function cargarModales() {

    try {

        const respuesta = await fetch("/modals/historial-cargador.html");

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar el modal: " + respuesta.status);
        }

        const html = await respuesta.text();

        document.body.insertAdjacentHTML("beforeend", html);

    } catch (error) {

        console.error("Error cargando el modal:", error);

    }

}