document.addEventListener("DOMContentLoaded", () => {
  /* =========================================================
     📱 MENÚ MÓVIL FULL SCREEN + ANIMACIÓN X
  ========================================================= */
  const menuToggle = document.getElementById("menuToggle");
  const navMenu = document.getElementById("navMenu");
  const body = document.body;

  if (menuToggle && navMenu) {
    menuToggle.addEventListener("click", () => {
      menuToggle.classList.toggle("active"); // 🔹 cambia entre 3 líneas ↔ X
      navMenu.classList.toggle("open");      // 🔹 muestra / oculta el menú
      body.classList.toggle("no-scroll");    // 🔹 bloquea scroll de fondo
    });

    // 🔹 Cierra el menú al hacer clic en un enlace
    document.querySelectorAll(".nav a").forEach((link) => {
      link.addEventListener("click", () => {
        navMenu.classList.document.addEventListener("DOMContentLoaded", () => {

  /* =========================================================
     🌟 PRODUCTOS DESTACADOS (sin duplicados)
  ========================================================= */
  const grid = document.querySelector(".productos .grid");
  if (grid) {
    const productos = JSON.parse(localStorage.getItem("productos")) || [];
    const destacados = [];
    const usados = new Set();

    productos.forEach((p) => {
      const idUnico = p.id || p.nombre?.toLowerCase();
      if (p.destacado && p.stock && !usados.has(idUnico)) {
        usados.add(idUnico);
        destacados.push(p);
      }
    });

    // 🔹 Si no hay productos destacados
    if (destacados.length === 0) {
      grid.innerHTML = `<p style="color:#ccc; text-align:center;">No hay lanzamientos destacados por ahora.</p>`;
    } else {
      grid.innerHTML = destacados.map((p) => `
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
      `).join("");
    }
  }

  /* =========================================================
     🎬 ANIMACIÓN DEL BANNER
  ========================================================= */
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
});remove("open");
        menuToggle.classList.remove("active");
        body.classList.remove("no-scroll");
      });
    });
  }

  /* =========================================================
     🌟 PRODUCTOS DESTACADOS (sin duplicados)
  ========================================================= */
  const grid = document.querySelector(".productos .grid");
  if (grid) {
    const productos = JSON.parse(localStorage.getItem("productos")) || [];
    const destacados = [];
    const usados = new Set();

    productos.forEach((p) => {
      const idUnico = p.id || p.nombre?.toLowerCase();
      if (p.destacado && p.stock && !usados.has(idUnico)) {
        usados.add(idUnico);
        destacados.push(p);
      }
    });

    // 🔹 Si no hay productos destacados
    if (destacados.length === 0) {
      grid.innerHTML = `<p style="color:#ccc; text-align:center;">No hay lanzamientos destacados por ahora.</p>`;
    } else {
      grid.innerHTML = destacados.map((p) => `
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
      `).join("");
    }
  }

  /* =========================================================
     🎬 ANIMACIÓN DEL BANNER
  ========================================================= */
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