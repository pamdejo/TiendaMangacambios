document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-producto");
  const mensaje = document.getElementById("mensaje");
  const tablaBody = document.querySelector("#tablaProductos tbody");


  function obtenerProductos() {
    let productos = [];


    if (typeof AndroidProduct !== "undefined" && AndroidProduct.getProductsJson) {
      try {
        const json = AndroidProduct.getProductsJson();
        productos = JSON.parse(json) || [];
      } catch (e) {
        console.error("Error leyendo productos desde AndroidProduct:", e);
      }
    }


    if (!productos || productos.length === 0) {
      try {
        productos = JSON.parse(localStorage.getItem("productos")) || [];
      } catch (e) {
        productos = [];
      }
    }

    return productos;
  }


  function guardarProductosEnLocal(productos) {
    try {
      localStorage.setItem("productos", JSON.stringify(productos));
    } catch (e) {
      console.error("Error guardando en localStorage:", e);
    }
  }


  function renderProductos() {
    const productos = obtenerProductos();
    tablaBody.innerHTML = "";

    if (!productos || productos.length === 0) {
      const tr = document.createElement("tr");
      const td = document.createElement("td");
      td.colSpan = 5;
      td.textContent = "No hay productos registrados.";
      td.style.textAlign = "center";
      tablaBody.appendChild(tr);
      tr.appendChild(td);
      return;
    }

    productos.forEach((p) => {
      const tr = document.createElement("tr");

      const nombre = p.nombre || p.name || "Sin nombre";
      const precio = p.precio != null ? p.precio : p.price;
      const categoria = p.categoria || "-";
      const stock = p.stock != null ? p.stock : 0;
      const estado = stock > 0 ? "Disponible" : "Sin stock";

      // Nombre
      const tdNombre = document.createElement("td");
      tdNombre.textContent = nombre;

      // Precio
      const tdPrecio = document.createElement("td");
      tdPrecio.textContent = `$${parseInt(precio).toLocaleString("es-CL")}`;

      // Categoría
      const tdCategoria = document.createElement("td");
      tdCategoria.textContent = categoria;

      // Estado
      const tdEstado = document.createElement("td");
      tdEstado.textContent = estado;

      // Acciones
      const tdAcciones = document.createElement("td");
      const btnEditar = document.createElement("button");
      btnEditar.textContent = "Editar";
      btnEditar.classList.add("btn-accion");

      const btnEliminar = document.createElement("button");
      btnEliminar.textContent = "Eliminar";
      btnEliminar.classList.add("btn-accion", "btn-danger");

      tdAcciones.appendChild(btnEditar);
      tdAcciones.appendChild(btnEliminar);

      tr.appendChild(tdNombre);
      tr.appendChild(tdPrecio);
      tr.appendChild(tdCategoria);
      tr.appendChild(tdEstado);
      tr.appendChild(tdAcciones);

      tablaBody.appendChild(tr);

      // EDITAR
      btnEditar.addEventListener("click", () => {
        editarProductoAdmin(p);
      });

      // ELIMINAR
      btnEliminar.addEventListener("click", () => {
        eliminarProductoAdmin(p, nombre);
      });
    });
  }


  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nombre = document.getElementById("nombre").value.trim();
    const precio = parseFloat(document.getElementById("precio").value.trim());
    const imagen = document.getElementById("imagen").value.trim();
    const categoria = document.getElementById("categoria").value.trim();

    if (!nombre || isNaN(precio) || !imagen || !categoria) {
      mensaje.textContent = "Por favor completa todos los campos correctamente.";
      mensaje.style.color = "tomato";
      return;
    }


    const stockInicial = 10;


    if (typeof AndroidProduct !== "undefined" && AndroidProduct.createProduct) {
      AndroidProduct.createProduct(nombre, precio, stockInicial, imagen);
      mensaje.textContent = "Producto creado en la app (SQLite).";
      mensaje.style.color = "lightgreen";
    } else {

      const productos = obtenerProductos();
      const nuevo = {
        id: Date.now(),
        nombre,
        precio,
        imagen,
        categoria,
        stock: stockInicial,
        destacado: false,
      };
      productos.push(nuevo);
      guardarProductosEnLocal(productos);
      mensaje.textContent = "Producto guardado en localStorage (modo navegador).";
      mensaje.style.color = "lightgreen";
    }

    form.reset();
    renderProductos();
  });


  function editarProductoAdmin(p) {
    const nombreActual = p.nombre || p.name || "";
    const precioActual = p.precio != null ? p.precio : p.price || 0;
    const stockActual = p.stock != null ? p.stock : 0;
    const imagenActual = p.imagen || p.imageUrl || "";

    const nuevoNombre = prompt("Nuevo nombre:", nombreActual);
    if (!nuevoNombre) return;

    const nuevoPrecio = parseFloat(prompt("Nuevo precio:", precioActual));
    if (isNaN(nuevoPrecio)) return;

    const nuevoStock = parseInt(prompt("Nuevo stock:", stockActual));
    if (isNaN(nuevoStock)) return;

    const nuevaImagen = prompt("Nueva URL de imagen:", imagenActual) || "";


    if (typeof AndroidProduct !== "undefined" && AndroidProduct.updateProduct) {
      if (p.id == null) {
        alert("No se puede actualizar: el producto no tiene id.");
        return;
      }
      AndroidProduct.updateProduct(
        p.id,
        nuevoNombre,
        nuevoPrecio,
        nuevoStock,
        nuevaImagen || null
      );
      alert("Producto actualizado en SQLite.");
    } else {

      const productos = obtenerProductos();
      const idx = productos.findIndex((x) => x.id === p.id);
      if (idx >= 0) {
        productos[idx].nombre = nuevoNombre;
        productos[idx].precio = nuevoPrecio;
        productos[idx].stock = nuevoStock;
        productos[idx].imagen = nuevaImagen;
        guardarProductosEnLocal(productos);
      }
    }

    renderProductos();
  }


  function eliminarProductoAdmin(p, nombreMostrar) {
    const nombre = nombreMostrar || p.nombre || p.name || "este producto";
    if (!confirm(`¿Eliminar "${nombre}"?`)) return;

    if (typeof AndroidProduct !== "undefined" && AndroidProduct.deleteProduct) {
      if (p.id == null) {
        alert("No se puede eliminar: el producto no tiene id.");
        return;
      }
      AndroidProduct.deleteProduct(p.id);
      alert("Producto eliminado de SQLite.");
    } else {
      // Fallback localStorage
      let productos = obtenerProductos();
      productos = productos.filter((x) => x.id !== p.id);
      guardarProductosEnLocal(productos);
    }

    renderProductos();
  }


  const btnCerrarSesion = document.getElementById("cerrarSesion");
  if (btnCerrarSesion) {
    btnCerrarSesion.addEventListener("click", (e) => {
      e.preventDefault();

      alert("Sesión cerrada (puedes conectar esto con tu LoginActivity).");
    });
  }

  // Inicializar tabla
  renderProductos();
});
