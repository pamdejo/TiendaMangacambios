document.addEventListener("DOMContentLoaded", () => {
  const contenedor = document.getElementById("contenedor-productos");
  const productos = JSON.parse(localStorage.getItem("productos")) || [];

  function mostrarProductos(lista) {
    contenedor.innerHTML = "";
    if (lista.length === 0) {
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
        agregarAlCarrito(productos[index]);
      });
    });
  }

  mostrarProductos(productos);

  const inputBusqueda = document.getElementById("busqueda");
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

  function agregarAlCarrito(producto) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
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

  actualizarContadorCarrito();
});