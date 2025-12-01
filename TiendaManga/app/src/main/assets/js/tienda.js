document.addEventListener("DOMContentLoaded", () => {
  const contenedor = document.getElementById("contenedor-productos");
  const inputBusqueda = document.getElementById("busqueda");

  let productos = []; // productos normalizados

  // Normalizar producto: convierte lo que venga (Room / localStorage) al formato estándar
  function normalizarProducto(p) {
    const precioRaw = p.precio ?? p.price ?? 0;

    return {
      // nombre: "nombre" (Room usa name)
      nombre: p.nombre || p.name || "Producto sin nombre",
      // precio: número
      precio: Number(precioRaw) || 0,
      // imagen: "imagen" (Room usa imageUrl)
      imagen: p.imagen || p.imageUrl || "img/placeholder.png",
      // categoria: "categoria" (Room puede usar category)
      categoria: p.categoria || p.category || "Sin categoría",
      // stock por si lo quieres usar después
      stock: p.stock ?? p.quantity ?? 0,
    };
  }

  // 1) Intentar leer productos desde Android (Room) si existe el bridge
  function cargarDesdeAndroid() {
    if (typeof AndroidProduct !== "undefined" && AndroidProduct.getProductsJson) {
      try {
        const json = AndroidProduct.getProductsJson();
        const lista = JSON.parse(json) || [];
        return lista.map(normalizarProducto);
      } catch (e) {
        console.error("Error leyendo productos desde AndroidProduct:", e);
      }
    }
    return [];
  }

  // 2) Si no hay productos desde Android, usar localStorage como antes
  function cargarDesdeLocalStorage() {
    try {
      const raw = JSON.parse(localStorage.getItem("productos")) || [];
      return raw.map(normalizarProducto);
    } catch (e) {
      console.error("Error leyendo productos desde localStorage:", e);
      return [];
    }
  }

  function cargarProductosIniciales() {
    let lista = cargarDesdeAndroid();
    if (!lista || lista.length === 0) {
      lista = cargarDesdeLocalStorage();
    }
    productos = lista;
    mostrarProductos(productos);
  }

  function mostrarProductos(lista) {
    contenedor.innerHTML = "";
    if (!lista || lista.length === 0) {
      contenedor.innerHTML = `<p style="color:#ccc;">No hay productos disponibles.</p>`;
      return;
    }

    lista.forEach((p, i) => {
      const card = document.createElement("div");
      card.classList.add("producto");
      card.innerHTML = `
        <img src="${p.imagen}" alt="${p.nombre}">
        <h3>${p.nombre}</h3>
        <p class="precio">$${parseInt(p.precio).toLocaleString("es-CL")}</p>
        <p class="categoria">${p.categoria}</p>
        <button class="add-to-cart" data-index="${i}">
          <i class="fas fa-cart-plus"></i> Agregar
        </button>
      `;
      contenedor.appendChild(card);
    });


    document.querySelectorAll(".add-to-cart").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = e.currentTarget.dataset.index;
        const producto = lista[index];
        if (producto) {
          agregarAlCarrito(producto);
        }
      });
    });
  }

  //  Buscador
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
  }

  // Carrito
  function agregarAlCarrito(producto) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    carrito.push(producto); // ya viene normalizado con nombre, precio, imagen, categoria
    localStorage.setItem("carrito", JSON.stringify(carrito));
    actualizarContadorCarrito();
    alert(`🛍️ "${producto.nombre}" se agregó al carrito.`);
  }

  function actualizarContadorCarrito() {
    const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    const contador = document.getElementById("cart-count");
    if (contador) contador.textContent = carrito.length;
  }

  // Inicial
  cargarProductosIniciales();
  actualizarContadorCarrito();
});