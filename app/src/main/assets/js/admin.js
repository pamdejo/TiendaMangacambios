document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-producto");
  const mensaje = document.getElementById("mensaje");
  const tablaUsuarios = document.querySelector("#tablaUsuarios tbody");
  const tablaPedidos = document.querySelector("#tablaPedidos tbody");
  const btnCerrar = document.getElementById("cerrarSesion");
  const links = document.querySelectorAll(".nav-link");
  const secciones = document.querySelectorAll(".seccion");

  /* =========================================================
     🔐 Protección del panel admin
  ========================================================= */
  const usuarioActivo = JSON.parse(localStorage.getItem("usuarioActivo"));

  if (!usuarioActivo) {
    alert("⚠️ Debes iniciar sesión para acceder al panel.");
    window.location.href = "login.html";
    return;
  }

  if (usuarioActivo.rol !== "admin") {
    alert("🚫 No tienes permisos para acceder al panel de administración.");
    window.location.href = "tienda.html";
    return;
  }

  /* =========================================================
     🚪 Botón Cerrar Sesión
  ========================================================= */
  if (btnCerrar) {
    btnCerrar.addEventListener("click", (e) => {
      e.preventDefault();
      localStorage.removeItem("usuarioActivo");
      alert("👋 Sesión cerrada correctamente.");
      window.location.href = "index.html";
    });
  }

  /* =========================================================
     🧭 Navegación entre secciones
  ========================================================= */
  links.forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      links.forEach((l) => l.classList.remove("active"));
      link.classList.add("active");
      secciones.forEach((sec) => sec.classList.remove("visible"));
      const id = link.getAttribute("data-section");
      document.getElementById(id).classList.add("visible");
    });
  });

  /* =========================================================
     🛍️ Agregar y gestionar productos
  ========================================================= */
  if (form) {
    const tablaProductos = document.querySelector("#tablaProductos tbody");

    // 🧩 Normaliza productos antiguos
    function normalizarProductos(productos) {
      return productos.map((p, i) => ({
        id: p.id || Date.now() + i,
        nombre: p.nombre || "Sin nombre",
        precio: p.precio || "0",
        imagen: p.imagen || "",
        categoria: p.categoria || "General",
        stock: p.stock !== undefined ? p.stock : true,
        destacado: p.destacado || false,
      }));
    }

    // ➕ Guardar producto nuevo
    form.addEventListener("submit", (e) => {
      e.preventDefault();

      const producto = {
        id: Date.now(),
        nombre: document.getElementById("nombre").value.trim(),
        precio: document.getElementById("precio").value.trim(),
        imagen: document.getElementById("imagen").value.trim(),
        categoria: document.getElementById("categoria").value.trim(),
        stock: true,
        destacado: false,
      };

      if (!producto.nombre || !producto.precio || !producto.imagen || !producto.categoria) {
        mensaje.textContent = "⚠️ Completa todos los campos.";
        return;
      }

      let productos = JSON.parse(localStorage.getItem("productos")) || [];
      productos = normalizarProductos(productos);
      productos.push(producto);
      localStorage.setItem("productos", JSON.stringify(productos));

      mensaje.textContent = `✅ "${producto.nombre}" agregado correctamente.`;
      form.reset();
      renderProductos();
    });

    // 🧾 Renderizar tabla de productos (con data-label)
    function renderProductos() {
      let productos = JSON.parse(localStorage.getItem("productos")) || [];
      productos = normalizarProductos(productos);
      localStorage.setItem("productos", JSON.stringify(productos));

      tablaProductos.innerHTML = "";

      if (productos.length === 0) {
        tablaProductos.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#ccc;">No hay productos registrados.</td></tr>`;
        return;
      }

      productos.forEach((p) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
          <td data-label="Nombre">${p.nombre}</td>
          <td data-label="Precio">$${parseInt(p.precio).toLocaleString("es-CL")}</td>
          <td data-label="Categoría">${p.categoria}</td>
          <td data-label="Estado">${p.stock ? "✅ En stock" : "❌ Sin stock"}</td>
          <td data-label="Destacado">${p.destacado ? "🌟 Sí" : "—"}</td>
          <td data-label="Acciones">
            <button class="btn-stock" data-id="${p.id}" title="Cambiar stock">
              <i class="fas fa-sync-alt"></i>
            </button>
            <button class="btn-destacar ${p.destacado ? "destacado" : ""}" data-id="${p.id}" title="Marcar como destacado">
              <i class="fas fa-star"></i>
            </button>
            <button class="btn-eliminar" data-id="${p.id}" title="Eliminar producto">
              <i class="fas fa-trash-alt"></i>
            </button>
          </td>
        `;
        tablaProductos.appendChild(fila);
      });

      // ⚙️ Botones de acción
      document.querySelectorAll(".btn-stock").forEach((btn) => {
        btn.addEventListener("click", (e) => toggleStock(parseInt(e.currentTarget.dataset.id)));
      });

      document.querySelectorAll(".btn-destacar").forEach((btn) => {
        btn.addEventListener("click", (e) => toggleDestacado(parseInt(e.currentTarget.dataset.id)));
      });

      document.querySelectorAll(".btn-eliminar").forEach((btn) => {
        btn.addEventListener("click", (e) => eliminarProducto(parseInt(e.currentTarget.dataset.id)));
      });
    }

    // 🔄 Cambiar estado de stock
    function toggleStock(id) {
      let productos = JSON.parse(localStorage.getItem("productos")) || [];
      const index = productos.findIndex((p) => p.id === id);
      if (index !== -1) {
        productos[index].stock = !productos[index].stock;
        localStorage.setItem("productos", JSON.stringify(productos));
        renderProductos();
      }
    }

    // 🌟 Marcar o desmarcar como destacado
    function toggleDestacado(id) {
      let productos = JSON.parse(localStorage.getItem("productos")) || [];
      const index = productos.findIndex((p) => p.id === id);
      if (index !== -1) {
        productos[index].destacado = !productos[index].destacado;
        localStorage.setItem("productos", JSON.stringify(productos));

        const boton = document.querySelector(`.btn-destacar[data-id="${id}"] i`);
        if (boton) {
          boton.style.transform = "scale(1.4)";
          boton.style.transition = "transform 0.2s ease";
          setTimeout(() => (boton.style.transform = "scale(1)"), 200);
        }

        renderProductos();
      }
    }

    // 🗑️ Eliminar producto
    function eliminarProducto(id) {
      if (!confirm("¿Seguro que deseas eliminar este producto?")) return;
      let productos = JSON.parse(localStorage.getItem("productos")) || [];
      productos = productos.filter((p) => p.id !== id);
      localStorage.setItem("productos", JSON.stringify(productos));
      renderProductos();
    }

    renderProductos();
  }

  /* =========================================================
     👥 Mostrar usuarios registrados
  ========================================================= */
  if (tablaUsuarios) {
    const usuarios = JSON.parse(localStorage.getItem("usuariosRegistrados")) || [];

    if (usuarios.length === 0) {
      tablaUsuarios.innerHTML = `<tr><td colspan="5" style="text-align:center; color:#ccc;">No hay usuarios registrados.</td></tr>`;
    } else {
      usuarios.forEach((u) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
          <td data-label="Nombre">${u.nombre || "-"}</td>
          <td data-label="Correo">${u.correo || "-"}</td>
          <td data-label="Usuario">${u.usuario || "-"}</td>
          <td data-label="Fecha Nac.">${u.nacimiento || "-"}</td>
          <td data-label="Registro">${u.fechaRegistro || "-"}</td>
        `;
        tablaUsuarios.appendChild(fila);
      });
    }
  }

  /* =========================================================
     📦 Mostrar pedidos recibidos
  ========================================================= */
  if (tablaPedidos) {
    const pedidos = JSON.parse(localStorage.getItem("pedidos")) || [];

    if (pedidos.length === 0) {
      tablaPedidos.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#ccc;">No hay pedidos todavía.</td></tr>`;
    } else {
      pedidos.forEach((p) => {
        const fila = document.createElement("tr");
        const productosLista = p.productos
          .map((prod) => `${prod.nombre} ($${parseInt(prod.precio).toLocaleString("es-CL")})`)
          .join("<br>");

        fila.innerHTML = `
          <td data-label="ID">${p.id}</td>
          <td data-label="Usuario">${p.usuario.nombre}</td>
          <td data-label="Correo">${p.usuario.correo}</td>
          <td data-label="Total">$${p.total.toLocaleString("es-CL")}</td>
          <td data-label="Fecha">${p.fecha}</td>
          <td data-label="Productos">${productosLista}</td>
        `;
        tablaPedidos.appendChild(fila);
      });
    }
  }
});