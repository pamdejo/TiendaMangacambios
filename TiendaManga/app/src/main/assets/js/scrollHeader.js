document.addEventListener("DOMContentLoaded", () => {
document.addEventListener("DOMContentLoaded", () => {
  const header = document.querySelector(".header");
  if (!header) return;

  let lastY = window.pageYOffset || 0;
  let ticking = false;

  const onScroll = () => {
    const y = window.pageYOffset || 0;

    // añade sombra cuando hay desplazamiento
    header.classList.toggle("scrolled", y > 10);

    // si baja y pasó 100px -> ocultar. si sube -> mostrar.
    if (y > lastY && y > 100) {
      header.classList.add("hidden");
    } else {
      header.classList.remove("hidden");
    }

    lastY = y <= 0 ? 0 : y; // evita negativos (iOS)
    ticking = false;
  };

  window.addEventListener(
    "scroll",
    () => {
      if (!ticking) {
        window.requestAnimationFrame(onScroll);
        ticking = true;
      }
    },
    { passive: true }
  );
});});