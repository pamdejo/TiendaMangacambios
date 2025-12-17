document.addEventListener("DOMContentLoaded", () => {
  const API_URL = "http://10.0.2.2/tienda_api/products.php";

  const contenedor = document.getElementById("contenedor-productos");
  const inputBusqueda = document.getElementById("busqueda");

  let productos = [];

  function normalizarProducto(p) {
    const precioRaw = p.precio ?? p.price ?? 0;
    return {
      id: Number(p.id ?? 0),
      nombre: p.nombre || p.name || "Producto sin nombre",
      precio: Number(precioRaw) || 0,
      imagen: p.imagen || p.imageUrl || "img/placeholder.png",
      categoria: p.categoria || p.category || "Sin categoría",
      stock: Number(p.stock ?? p.quantity ?? 0),
      destacado: !!(p.destacado === 1 || p.destacado === true)
    };
  }

  async function apiGet() {
    const res = await fetch(API_URL);
    if (!res.ok) throw new Error("HTTP " + res.status);
    const lista = await res.json();
    return (lista || []).map(normalizarProducto);
  }

  async function apiDelete(id) {
    const res = await fetch(`${API_URL}?id=${id}`, { method: "DELETE" });
    if (!res.ok && res.status !== 204) throw new Error("DELETE " + res.status);
  }

  async function apiPut(id, body) {
    const res = await fetch(`${API_URL}?id=${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error("PUT " + res.status);
    return await res.json();
  }

  async function refresh() {
    try {
      productos = await apiGet();
      mostrarProductos(productos);
    } catch (e) {
      contenedor.innerHTML = `<p style="color:#ccc;">No se pudo cargar productos (¿XAMPP apagado?).</p>`;
      console.warn(e);
    }
  }

  function mostrarProductos(lista) {
    contenedor.innerHTML = "";

    if (!lista || lista.length === 0) {
      contenedor.innerHTML = `<p style="color:#ccc;">No hay productos disponibles.</p>`;
      return;
    }

    lista.forEach((p) => {
      const card = document.createElement("div");
      card.className = "producto";
      card.dataset.id = p.id;

      card.innerHTML = `
        <img src="${p.imagen}" alt="${p.nombre}">
        <h3>${p.nombre}</h3>
        <p class="precio">$${parseInt(p.precio).toLocaleString("es-CL")}</p>
        <p class="categoria">${p.categoria}</p>

        <button class="btn-cart" style="width:100%; margin-top:10px;">
            <i class="fas fa-cart-plus"></i> Agregar al carrito
          </button>

        <div style="display:flex; gap:8px; margin-top:10px;">
          <button class="btn-edit" style="flex:1;">Editar</button>
          <button class="btn-del" style="flex:1;">Eliminar</button>
        </div>
      `;

      // Editar
      card.querySelector(".btn-edit").addEventListener("click", async (ev) => {
        ev.stopPropagation();

      card.querySelector(".btn-cart").addEventListener("click", (ev) => {
        ev.stopPropagation();
        agregarAlCarrito(p);
      });

        const nuevoNombre = prompt("Nombre:", p.nombre);
        if (nuevoNombre === null) return;

        const nuevoPrecioStr = prompt("Precio:", String(p.precio));
        if (nuevoPrecioStr === null) return;

        const nuevoStockStr = prompt("Stock:", String(p.stock));
        if (nuevoStockStr === null) return;

        const nuevaCategoria = prompt("Categoría:", p.categoria);
        if (nuevaCategoria === null) return;

        const nuevaImagen = prompt("URL/Imagen:", p.imagen);
        if (nuevaImagen === null) return;


        const body = {
          nombre: nuevoNombre.trim(),
          precio: Number(nuevoPrecioStr),
          stock: Number(nuevoStockStr),
          categoria: nuevaCategoria.trim(),
          imagen: nuevaImagen.trim(),
          destacado: p.destacado
        };

        try {
          await apiPut(p.id, body);
          await refresh();
          alert("✅ Producto actualizado");
        } catch (e) {
          alert("❌ Error al editar: " + (e?.message || e));
        }
      });

      // Eliminar
      card.querySelector(".btn-del").addEventListener("click", async (ev) => {
        ev.stopPropagation();

        if (!confirm(`¿Eliminar "${p.nombre}"?`)) return;

        try {
          await apiDelete(p.id);
          await refresh();
          alert("🗑️ Eliminado");
        } catch (e) {
          alert("❌ Error al eliminar: " + (e?.message || e));
        }
      });

      contenedor.appendChild(card);
    });
  }

  // Buscador
  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", () => {
      const texto = inputBusqueda.value.toLowerCase();
      const filtrados = productos.filter(
        (p) =>
          p.nombre.toLowerCase().includes(texto) ||
          p.categoria.toLowerCase().includes(texto)
      );
      mostrarProductos(filtrados);
    });

    function agregarAlCarrito(producto) {
      let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

      // si quieres evitar duplicados, descomenta esto:
      // if (carrito.some(p => p.id === producto.id)) return alert("Ya está en el carrito");

      carrito.push(producto);
      localStorage.setItem("carrito", JSON.stringify(carrito));
      actualizarContadorCarrito();
      alert(`🛍️ "${producto.nombre}" se agregó al carrito.`);
    }

    function actualizarContadorCarrito() {
      const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
      const contador = document.getElementById("cart-count");
      if (contador) contador.textContent = carrito.length;
    }
  }

    actualizarContadorCarrito();
    refresh();
});