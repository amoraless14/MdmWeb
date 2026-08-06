let modalUsuario;
let usuarioEditando = null;

function iniciarUsuarios() {

    const modal = document.getElementById("modalUsuario");

    if (!modal) {

        console.error("No existe #modalUsuario");

        return;

    }

    modalUsuario = new bootstrap.Modal(modal);

    document.getElementById("btnGuardarUsuario")
        .addEventListener("click", guardarUsuario);

    cargarUsuarios();

}

function abrirModalUsuario() {

    usuarioEditando = null;

    limpiarFormularioUsuario();

    document.getElementById("txtUsuarioSistema").disabled = false;

    document.getElementById("tituloModalUsuario").textContent =
        "Nuevo Usuario";

    modalUsuario.show();

}

function cerrarModalUsuario() {

    modalUsuario.hide();

}

function limpiarFormularioUsuario() {

    document.getElementById("txtUsuarioSistema").value = "";
    document.getElementById("txtNombreUsuario").value = "";
    document.getElementById("txtPasswordUsuario").value = "";
    document.getElementById("cmbRolUsuario").value = "ADMIN";
    document.getElementById("chkUsuarioActivo").checked = true;

}

function formatearFecha(fecha) {

    if (!fecha) {

        return "-";

    }

    return new Date(fecha).toLocaleString("es-GT", {

        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"

    });

}

async function cargarUsuarios() {

    const tbody = document.getElementById("tablaUsuarios");

    tbody.innerHTML = `
        <tr>
            <td colspan="5" class="text-center py-5 text-muted">
                Cargando usuarios...
            </td>
        </tr>
    `;

    try {

        const res = await fetch("/usuarios");

        const usuarios = await res.json();

        if (usuarios.length === 0) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center py-5 text-muted">
                        No existen usuarios registrados.
                    </td>
                </tr>
            `;

            return;

        }

        tbody.innerHTML = "";

        usuarios.forEach(u => {

            tbody.innerHTML += `
                <tr>

                    <td>${u.usuario}</td>

                    <td>${u.nombre}</td>

                    <td>
                        <span class="badge bg-primary">
                            ${u.rol}
                        </span>
                    </td>

                   <td>
    ${u.activo
                    ? '<span class="badge bg-success">Activo</span>'
                    : '<span class="badge bg-danger">Inactivo</span>'
                }
</td>

<td>

    ${formatearFecha(u.fechaCreacion)}

</td>

<td>

    ${formatearFecha(u.fechaModificacion)}

</td>

<td class="text-end">

                        <button class="btn btn-sm btn-outline-primary me-1"
                                onclick="editarUsuario(${u.id})">

                            <i class="bi bi-pencil"></i>

                        </button>

                        <button class="btn btn-sm btn-outline-danger"
                                onclick="eliminarUsuario(${u.id})">

                            <i class="bi bi-trash"></i>

                        </button>

                    </td>

                </tr>
            `;

        });

    } catch (e) {

        console.error(e);

        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-danger py-5">
                    Error al cargar usuarios.
                </td>
            </tr>
        `;

    }

}

async function eliminarUsuario(id) {

    const ok = await Swal.fire({

        title: "¿Eliminar usuario?",

        html: `
            <div class="mt-2">
                Esta acción eliminará permanentemente el usuario.
                <br><br>
                <b>Esta acción no se puede deshacer.</b>
            </div>
        `,

        icon: "warning",

        showCancelButton: true,

        confirmButtonColor: "#dc3545",

        cancelButtonColor: "#6c757d",

        confirmButtonText: '<i class="bi bi-trash"></i> Eliminar',

        cancelButtonText: "Cancelar",

        reverseButtons: true,

        focusCancel: true

    });

    if (!ok.isConfirmed) {
        return;
    }

    try {

        const res = await fetch(`/usuarios/${id}`, {

            method: "DELETE"

        });

        if (!res.ok) {

            throw new Error();

        }

        await Swal.fire({

            icon: "success",

            title: "Usuario eliminado",

            text: "El usuario fue eliminado correctamente.",

            timer: 1500,

            showConfirmButton: false

        });

        cargarUsuarios();

    } catch (e) {

        Swal.fire({

            icon: "error",

            title: "Error",

            text: "No fue posible eliminar el usuario."

        });

    }

}

async function editarUsuario(id) {

    usuarioEditando = id;

    const res = await fetch(`/usuarios/${id}`);

    const u = await res.json();

    document.getElementById("tituloModalUsuario").textContent =
        "Editar Usuario";

    document.getElementById("usuarioId").value = u.id;

    document.getElementById("txtUsuarioSistema").value = u.usuario;

    document.getElementById("txtUsuarioSistema").disabled = true;

    document.getElementById("txtNombreUsuario").value = u.nombre;

    document.getElementById("txtPasswordUsuario").value = "";

    document.getElementById("cmbRolUsuario").value = u.rol;

    document.getElementById("chkUsuarioActivo").checked = u.activo;

    modalUsuario.show();

}

async function guardarUsuario() {

    const dto = {

        usuario: document.getElementById("txtUsuarioSistema").value.trim(),

        nombre: document.getElementById("txtNombreUsuario").value.trim(),

        password: document.getElementById("txtPasswordUsuario").value,

        rol: document.getElementById("cmbRolUsuario").value,

        activo: document.getElementById("chkUsuarioActivo").checked

    };

    if (dto.usuario === "") {

        Swal.fire({
            icon: "warning",
            title: "Usuario requerido",
            text: "Debe ingresar el usuario."
        });

        return;

    }

    if (dto.nombre === "") {

        Swal.fire({
            icon: "warning",
            title: "Nombre requerido",
            text: "Debe ingresar el nombre."
        });

        return;

    }

    if (usuarioEditando == null && dto.password === "") {

        Swal.fire({
            icon: "warning",
            title: "Contraseña requerida",
            text: "Debe ingresar una contraseña."
        });

        return;

    }

    try {

        let res;

        if (usuarioEditando == null) {

            res = await fetch("/usuarios", {

                method: "POST",

                headers: {

                    "Content-Type": "application/json"

                },

                body: JSON.stringify(dto)

            });

        } else {

            res = await fetch(`/usuarios/${usuarioEditando}`, {

                method: "PUT",

                headers: {

                    "Content-Type": "application/json"

                },

                body: JSON.stringify(dto)

            });

        }

        if (!res.ok) {

            const mensaje = await res.text();

            throw new Error(mensaje);

        }

        modalUsuario.hide();

        await Swal.fire({

            icon: "success",

            title: usuarioEditando == null
                ? "Usuario creado"
                : "Usuario actualizado",

            timer: 1500,

            showConfirmButton: false

        });

        usuarioEditando = null;

        cargarUsuarios();

    } catch (e) {

        Swal.fire({

            icon: "error",

            title: "Error",

            text: e.message || "No fue posible guardar el usuario."

        });

    }

}