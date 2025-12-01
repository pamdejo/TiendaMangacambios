document.addEventListener("DOMContentLoaded", () => {

  const menuToggle = document.getElementById("menuToggle");
  const navMenu = document.getElementById("navMenu");
  const body = document.body;

  if (menuToggle && navMenu) {
    // Abrir / cerrar menú
    menuToggle.addEventListener("click", () => {
      menuToggle.classList.toggle("active"); // 3 líneas ↔ X
      navMenu.classList.toggle("open");      // muestra / oculta menú
      body.classList.toggle("no-scroll");    // bloquea scroll del fondo
    });

    // Cerrar al hacer click en un enlace de navegación
    document.querySelectorAll(".nav a").forEach((link) => {
      link.addEventListener("click", () => {
        navMenu.classList.remove("open");
        menuToggle.classList.remove("active");
        body.classList.remove("no-scroll");
      });
    });
  }


  const grid = document.querySelector(".productos .grid");
  if (grid) {
    let productos = [];

    // 1) Intentar leer desde Android (Room vía bridge)
    if (typeof AndroidProduct !== "undefined" && AndroidProduct.getProductsJson) {
      try {
        const json = AndroidProduct.getProductsJson();
        productos = JSON.parse(json);
      } catch (e) {
        console.error("Error al leer productos desde AndroidProduct:", e);
      }
    }

    // 2) Si no hay AndroidProduct o falló, usar localStorage como antes
    if (!productos || productos.length === 0) {
      try {
        productos = JSON.parse(localStorage.getItem("productos")) || [];
      } catch (e) {
        console.error("Error al leer productos desde localStorage:", e);
        productos = [];
      }
    }

    const destacados = [];
    const usados = new Set();

    productos.forEach((p) => {
      // Soportar tanto estructura del JSON original como la de Room
      const idUnico = p.id || p.nombre?.toLowerCase() || p.name?.toLowerCase();

      // nombre / precio / imagen con fallback
      const nombre = p.nombre || p.name || "Producto sin nombre";
      const precio = p.precio != null ? p.precio : p.price;
      const imagen = p.imagen || p.imageUrl || "assets/img/placeholder.png";
      const categoria = p.categoria || "";


      const esDestacado =
        typeof p.destacado !== "undefined" ? !!p.destacado : true;

      if (esDestacado && p.stock && !usados.has(idUnico)) {
        usados.add(idUnico);
        destacados.push({
          id: p.id,
          nombre,
          precio,
          imagen,
          categoria,
        });
      }
    });

    // 🔹 Si no hay productos destacados
    if (destacados.length === 0) {
      grid.innerHTML =
        `<p style="color:#ccc; text-align:center;">No hay lanzamientos destacados por ahora.</p>`;
    } else {
      grid.innerHTML = destacados
        .map(
          (p) => `
        <div class="producto-card">
          <div class="producto-img">
            <img src="${p.imagen}" alt="${p.nombre}">
          </div>
          <div class="producto-info">
            <h3>${p.nombre}</h3>
            <p class="categoria">${p.categoria}</p>
            <p class="precio">$${parseInt(p.precio).toLocaleString("es-CL")}</p>
            <a href="tienda.html" class="btn-ver">Ver producto</a>
          </div>
        </div>
      `
        )
        .join("");
    }
  }


  const banner = document.querySelector(".banner-evento");
  if (banner) {
    const observer = new IntersectionObserver(
      (entries, obs) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            banner.classList.add("visible");
            obs.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.3 }
    );
    observer.observe(banner);
  }
});