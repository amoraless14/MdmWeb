
let modalDispositivos;
let tablets = [];
let modalTarea;



function iniciarTareas() {

    modalTarea = new bootstrap.Modal(
        document.getElementById("modalTarea")
    );

    modalDispositivos = new bootstrap.Modal(
        document.getElementById("modalDispositivos")
    );

    document.getElementById("btnNuevaTarea").onclick = abrirModalNuevaTarea;

    document.getElementById("progUnaVez")
        .addEventListener("change", cambiarTipoProgramacion);

    document.getElementById("progRepetir")
        .addEventListener("change", cambiarTipoProgramacion);

    document.getElementById("cmbFrecuencia")
        .addEventListener("change", cambiarFrecuencia);


}

async function sincronizarTareas() {

    await cargarTareas();

}

function abrirModalNuevaTarea() {

    tareaEditando = null;

    document.getElementById("txtNombreTarea").value = "";
    document.getElementById("txtDescripcionTarea").value = "";

    document.getElementById("cmbTipoTarea").value = "REINICIO";
    document.getElementById("cmbDestinoTarea").value = "TODAS";

    document.getElementById("txtFechaTarea").value = "";
    document.getElementById("txtHoraTarea").value = "";

    document.getElementById("txtHoraDiaria").value = "";
    document.getElementById("txtHoraSemanal").value = "";
    document.getElementById("txtHoraMensual").value = "";
    document.getElementById("txtFechaMensual").value = "";

    document.querySelectorAll(".dias-semana .btn-check")
        .forEach(x => x.checked = false);

    document.getElementById("progUnaVez").checked = true;
    document.getElementById("progRepetir").checked = false;

    document.getElementById("cmbFrecuencia").value = "DIARIA";

    cambiarDestino();
    cambiarTipoProgramacion();
    cambiarFrecuencia();

    document.querySelector("#modalTarea .modal-title").innerHTML = `
        <i class="bi bi-calendar2-check me-2"></i>
        Nueva Tarea
    `;

    modalTarea.show();

}

async function cargarTareas() {

    const res = await fetch("/tareas");

    const tareas = await res.json();

    const tbody = document.getElementById("tablaTareas");

    tbody.innerHTML = "";

    tareas.forEach(t => {

        let estadoTexto = "";
        let estadoBadge = "";

        switch (t.estado) {

            case "PENDIENTE":
                estadoTexto = "Pendiente";
                estadoBadge = "bg-secondary";
                break;

            case "EN_PROCESO":
                estadoTexto = "En progreso";
                estadoBadge = "bg-primary";
                break;

            case "COMPLETADA":
                estadoTexto = "Completada";
                estadoBadge = "bg-success";
                break;

            case "COMPLETADA_CON_ERRORES":
                estadoTexto = "Completada parcialmente";
                estadoBadge = "bg-warning text-dark";
                break;

            case "CANCELADA":
                estadoTexto = "Cancelada";
                estadoBadge = "bg-danger";
                break;

            default:
                estadoTexto = t.estado;
                estadoBadge = "secondary";
        }

        tbody.innerHTML += `
    <tr>

        <td>${t.nombre}</td>

        <td>${t.tipoTarea}</td>

        <td>${t.destinoTarea}</td>

        <td>${t.fechaProgramada}</td>

        <td>${t.horaProgramada}</td>

        <td style="width:220px;">
            <span class="badge ${estadoBadge}">
                ${estadoTexto}
            </span>
        </td>

        <td class="text-center fw-bold" style="width:90px;">
            ${t.completados}/${t.totalDispositivos}
        </td>

        <td class="text-center" style="width:130px;">

            <button class="btn btn-sm btn-info me-1"
                onclick="verDetalleTarea(${t.id})">

                <i class="bi bi-eye"></i>

            </button>

            <button class="btn btn-sm btn-warning me-1"
                onclick="editarTarea(${t.id})">

                <i class="bi bi-pencil"></i>

            </button>

            <button class="btn btn-sm btn-danger"
                onclick="eliminarTarea(${t.id})">

                <i class="bi bi-trash"></i>

            </button>

        </td>

    </tr>
`;

    });

}

async function guardarTarea() {

    const destino = document.getElementById("cmbDestinoTarea").value;

    let valorDestino = "";
    let dispositivos = [];

    if (destino === "PLANTA") {

        valorDestino = document.getElementById("cmbPlanta").value;

    } else if (destino === "CATEGORIA") {

        valorDestino = document.getElementById("cmbCategoria").value;

    } else if (destino === "DISPOSITIVOS") {

        dispositivos = dispositivosSeleccionados;

    }

    const tipoProgramacion = document.querySelector(
        'input[name="tipoProgramacion"]:checked'
    ).value;

    let fechaProgramada = "";
    let horaProgramada = "";
    let diasSemana = "";
    let diaMes = null;
    let tipo = "UNA_VEZ";

    if (tipoProgramacion === "UNA_VEZ") {

        tipo = "UNA_VEZ";

        fechaProgramada = document.getElementById("txtFechaTarea").value;
        horaProgramada = document.getElementById("txtHoraTarea").value;

    } else {

        tipo = document.getElementById("cmbFrecuencia").value;

        switch (tipo) {

            case "DIARIA":

                fechaProgramada = new Date().toISOString().substring(0, 10);

                horaProgramada = document.getElementById("txtHoraDiaria").value;

                break;

            case "SEMANAL":

                fechaProgramada = new Date().toISOString().substring(0, 10);

                diasSemana = [...document.querySelectorAll(".dias-semana .btn-check:checked")]
                    .map(x => x.value)
                    .join(",");

                horaProgramada = document.getElementById("txtHoraSemanal").value;

                break;

            case "MENSUAL":

                fechaProgramada = new Date().toISOString().substring(0, 10);

                const fecha = document.getElementById("txtFechaMensual").value;

                diaMes = fecha ? Number(fecha.split("-")[2]) : null;

                horaProgramada = document.getElementById("txtHoraMensual").value;

                break;

        }

    }

    const tarea = {

        nombre: document.getElementById("txtNombreTarea").value,

        descripcion: document.getElementById("txtDescripcionTarea").value,

        tipoTarea: document.getElementById("cmbTipoTarea").value,

        destinoTarea: destino,

        valorDestino: valorDestino,

        fechaProgramada: fechaProgramada,

        horaProgramada: horaProgramada,

        tipoProgramacion: tipo,

        diasSemana: diasSemana,

        diaMes: diaMes,

        parametros: "",

        dispositivos: dispositivos

    };

    const url = tareaEditando == null
        ? "/tareas"
        : "/tareas/" + tareaEditando;

    const metodo = tareaEditando == null
        ? "POST"
        : "PUT";

    const res = await fetch(url, {

        method: metodo,

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(tarea)

    });

    if (!res.ok) {

        alert("No fue posible guardar la tarea.");
        return;

    }

    modalTarea.hide();

    cargarTareas();

}

document.addEventListener("change", function (e) {

    if (e.target.id === "cmbDestinoTarea") {

        cambiarDestino();

    }

});




function cambiarDestino() {

    dispositivosSeleccionados = [];

    const destino = document.getElementById("cmbDestinoTarea").value;

    const div = document.getElementById("contenidoDestino");

    switch (destino) {

        case "TODAS":

            div.innerHTML = `
    <div class="alert alert-secondary mb-0">
        Esta tarea se ejecutará en todas las tablets.
    </div>
    `;
            break;

        case "PLANTA":

            div.innerHTML = `
    <select id="cmbPlanta" class="form-select"></select>
    `;

            llenarPlantas();

            break;

        case "CATEGORIA":

            div.innerHTML = `
    <select id="cmbCategoria" class="form-select"></select>
    `;

            llenarCategorias();

            break;

        case "DISPOSITIVOS":

            div.innerHTML = `
    <button class="btn btn-outline-primary w-100"
        onclick="abrirSelectorDispositivos()">

        Seleccionar dispositivos

    </button>

    <div id="resumenDispositivos"
        class="mt-2 text-muted">

        Ningún dispositivo seleccionado.

    </div>
    `;

            break;

    }

}

async function llenarPlantas() {

    const combo = document.getElementById("cmbPlanta");

    if (plantas.length === 0) {

        const res = await fetch("/devices/plantas");

        plantas = await res.json();
    }

    combo.innerHTML = "";

    combo.innerHTML += `<option value="">Seleccione una planta</option>`;

    plantas.forEach(planta => {

        combo.innerHTML += `
            <option value="${planta}">
                ${planta}
            </option>
        `;

    });
}

async function llenarCategorias() {

    const combo = document.getElementById("cmbCategoria");

    if (categorias.length === 0) {

        const res = await fetch("/devices/categorias");

        categorias = await res.json();
    }

    combo.innerHTML = "";

    combo.innerHTML += `<option value="">Seleccione una categoría</option>`;

    categorias.forEach(categoria => {

        combo.innerHTML += `
            <option value="${categoria}">
                ${categoria}
            </option>
        `;

    });
}

async function eliminarTarea(id) {

    const resultado = await Swal.fire({
        title: "¿Eliminar tarea?",
        text: "Esta acción no se puede deshacer.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#6c757d",
        reverseButtons: true
    });

    if (!resultado.isConfirmed) {
        return;
    }

    const res = await fetch("/tareas/" + id, {
        method: "DELETE"
    });

    if (!res.ok) {

        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No fue posible eliminar la tarea."
        });

        return;
    }

    Swal.fire({
        icon: "success",
        title: "Tarea eliminada",
        text: "La tarea se eliminó correctamente.",
        timer: 1500,
        showConfirmButton: false
    });

    cargarTareas();
}



async function editarTarea(id) {

    const res = await fetch("/tareas/" + id);

    const t = await res.json();

    tareaEditando = id;

    document.getElementById("txtNombreTarea").value = t.nombre;
    document.getElementById("txtDescripcionTarea").value = t.descripcion;
    document.getElementById("cmbTipoTarea").value = t.tipoTarea;
    document.getElementById("cmbDestinoTarea").value = t.destinoTarea;

    cambiarDestino();

    setTimeout(() => {

        if (t.destinoTarea === "PLANTA") {

            document.getElementById("cmbPlanta").value = t.valorDestino;

        } else if (t.destinoTarea === "CATEGORIA") {

            document.getElementById("cmbCategoria").value = t.valorDestino;

        }

    }, 200);

    if (t.tipoProgramacion === "UNA_VEZ") {

        document.getElementById("progUnaVez").checked = true;
        document.getElementById("progRepetir").checked = false;

        cambiarTipoProgramacion();

        document.getElementById("txtFechaTarea").value = t.fechaProgramada;
        document.getElementById("txtHoraTarea").value = t.horaProgramada;

    } else {

        document.getElementById("progUnaVez").checked = false;
        document.getElementById("progRepetir").checked = true;

        cambiarTipoProgramacion();

        document.getElementById("cmbFrecuencia").value = t.tipoProgramacion;

        cambiarFrecuencia();

        switch (t.tipoProgramacion) {

            case "DIARIA":

                document.getElementById("txtHoraDiaria").value =
                    t.horaProgramada;

                break;

            case "SEMANAL":

                document.getElementById("txtHoraSemanal").value =
                    t.horaProgramada;

                document.querySelectorAll(".dias-semana .btn-check")
                    .forEach(x => x.checked = false);

                if (t.diasSemana) {

                    t.diasSemana.split(",").forEach(d => {

                        switch (d) {

                            case "LUN":
                                document.getElementById("diaLun").checked = true;
                                break;

                            case "MAR":
                                document.getElementById("diaMar").checked = true;
                                break;

                            case "MIE":
                                document.getElementById("diaMie").checked = true;
                                break;

                            case "JUE":
                                document.getElementById("diaJue").checked = true;
                                break;

                            case "VIE":
                                document.getElementById("diaVie").checked = true;
                                break;

                            case "SAB":
                                document.getElementById("diaSab").checked = true;
                                break;

                            case "DOM":
                                document.getElementById("diaDom").checked = true;
                                break;

                        }

                    });

                }

                break;

            case "MENSUAL":

                if (t.diaMes) {

                    const hoy = new Date();

                    const mes = String(hoy.getMonth() + 1).padStart(2, "0");
                    const dia = String(t.diaMes).padStart(2, "0");

                    document.getElementById("txtFechaMensual").value =
                        `${hoy.getFullYear()}-${mes}-${dia}`;
                }

                document.getElementById("txtHoraMensual").value =
                    t.horaProgramada;

                break;

        }

    }

    document.querySelector("#modalTarea .modal-title").innerHTML = `
        <i class="bi bi-calendar2-check me-2"></i>
        Editar Tarea
    `;

    modalTarea.show();

}

let plantas = [];
let categorias = [];
let dispositivosSeleccionados = [];


async function abrirSelectorDispositivos() {

    if (tablets.length === 0) {

        const res = await fetch("/devices/lista");

        tablets = await res.json();
    }

    const tbody = document.getElementById("tablaSeleccionDispositivos");

    tbody.innerHTML = "";

    tablets.forEach(t => {

        const marcado = dispositivosSeleccionados.includes(t.id)
            ? "checked"
            : "";

        tbody.innerHTML += `
    <tr>

        <td>
            <input class="form-check-input dispositivo-check"
                type="checkbox"
                value="${t.id}"
                ${marcado}>
        </td>

        <td>${t.activo ?? ""}</td>

        <td>${t.deviceName ?? ""}</td>

        <td>${t.model ?? ""}</td>

        <td>${t.categoria ?? ""}</td>

    </tr>
    `;
    });

    modalDispositivos.show();
}

function confirmarDispositivos() {

    dispositivosSeleccionados = [];

    document.querySelectorAll(".dispositivo-check:checked")
        .forEach(x => {

            dispositivosSeleccionados.push(
                Number(x.value)
            );

        });

    document.getElementById("resumenDispositivos").innerHTML =
        dispositivosSeleccionados.length + " dispositivo(s) seleccionado(s)";

    modalDispositivos.hide();
}

async function verDetalleTarea(id) {

    tareaDetalleId = id;

    paginaDetalle = 0;

    const res = await fetch(`/tareas/${id}/detalle`);

    const detalle = await res.json();

    document.getElementById("detalleTotal").textContent =
        detalle.totalDispositivos;

    document.getElementById("detalleCompletados").textContent =
        detalle.completados;

    document.getElementById("detallePendientes").textContent =
        detalle.pendientes;

    document.getElementById("detalleErrores").textContent =
        detalle.errores;

    const porcentaje = detalle.totalDispositivos === 0
        ? 0
        : Math.round((detalle.completados * 100) / detalle.totalDispositivos);

    const barra = document.getElementById("barraDetalle");

    barra.style.width = porcentaje + "%";

    barra.textContent =
        `${detalle.completados}/${detalle.totalDispositivos}`;

    document.getElementById("txtBuscarDetalle").value = "";

    document.getElementById("cmbEstadoDetalle").value = "";

    document.getElementById("cmbSizeDetalle").value = registrosDetalle;

    await cargarDispositivosDetalle();

    new bootstrap.Modal(
        document.getElementById("modalDetalleTarea")
    ).show();

}


let tareaDetalleId = 0;

let paginaDetalle = 0;

let totalPaginasDetalle = 0;

let registrosDetalle = 25;

async function cargarDispositivosDetalle() {

    const buscar =
        document.getElementById("txtBuscarDetalle").value;

    const estado =
        document.getElementById("cmbEstadoDetalle").value;

    const res = await fetch(

        `/tareas/${tareaDetalleId}/dispositivos?page=${paginaDetalle}&size=${registrosDetalle}&buscar=${buscar}&estado=${estado}`

    );

    const pagina = await res.json();

    totalPaginasDetalle = pagina.totalPages;
    document.getElementById("lblTotalDetalle").textContent =
        pagina.totalElements;

    document.getElementById("lblPaginaDetalle").textContent =
        `Página ${pagina.number + 1} de ${pagina.totalPages}`;

    const tbody =
        document.getElementById("tablaDetalleTarea");

    tbody.innerHTML = "";

    pagina.content.forEach(d => {

        let estadoTexto = "";
        let clase = "";

        switch (d.estado) {

            case "CONFIRMADA":

                estadoTexto = "✅ Confirmada";
                clase = "text-success";
                break;

            case "SIN_CONEXION":

                estadoTexto = "❌ Sin conexión";
                clase = "text-danger";
                break;

            case "PENDIENTE":

                estadoTexto = "⏳ Pendiente";
                clase = "text-warning";
                break;

            case "ENVIADA":

                estadoTexto = "📤 Enviada";
                clase = "text-primary";
                break;

        }

        tbody.innerHTML += `

            <tr>

                <td>${d.activo}</td>

                <td>${d.equipo}</td>

                <td class="${clase} fw-bold">

                    ${estadoTexto}

                </td>

                <td>${d.fechaEjecucion ?? "-"}</td>

            </tr>

        `;

    });

}


function paginaAnteriorDetalle() {

    if (paginaDetalle > 0) {

        paginaDetalle--;

        cargarDispositivosDetalle();

    }

}

function paginaSiguienteDetalle() {

    if (paginaDetalle < totalPaginasDetalle - 1) {

        paginaDetalle++;

        cargarDispositivosDetalle();

    }

}

function cambiarTamanoDetalle() {

    registrosDetalle =

        Number(document.getElementById("cmbSizeDetalle").value);

    paginaDetalle = 0;

    cargarDispositivosDetalle();

}


function cambiarTipoProgramacion() {

    const unaVez = document.getElementById("progUnaVez").checked;

    document.getElementById("panelUnaVez").style.display =
        unaVez ? "block" : "none";

    document.getElementById("panelRepetir").style.display =
        unaVez ? "none" : "block";

    if (!unaVez) {

        cambiarFrecuencia();

    }

}
function cambiarFrecuencia() {

    const frecuencia = document.getElementById("cmbFrecuencia").value;

    document.getElementById("configDiaria").style.display = "none";
    document.getElementById("configSemanal").style.display = "none";
    document.getElementById("configMensual").style.display = "none";

    switch (frecuencia) {

        case "DIARIA":

            document.getElementById("configDiaria").style.display = "block";
            break;

        case "SEMANAL":

            document.getElementById("configSemanal").style.display = "block";
            break;

        case "MENSUAL":

            document.getElementById("configMensual").style.display = "block";
            break;

    }

}