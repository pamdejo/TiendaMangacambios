document.addEventListener("DOMContentLoaded", () => {
  const userIcon = document.getElementById("userIcon");
  if (!userIcon) return;

  const usuarioActivo = JSON.parse(localStorage.getItem("usuarioActivo"));

  // Sin sesión
  if (!usuarioActivo) {
    userIcon.innerHTML = `<i class="fas fa-user"></i>`;
    userIcon.href = "login.html";
    return;
  }

  // Admin
  if (usuarioActivo.rol === "admin") {
    userIcon.innerHTML = `<i class="fas fa-crown"></i>`;
    userIcon.title = "Panel de administración";
    userIcon.href = "admin.html";
    return;
  }

  // Usuario normal
  userIcon.innerHTML = `<i class="fas fa-sign-out-alt"></i>`;
  userIcon.title = "Cerrar sesión";
  userIcon.addEventListener("click", (e) => {
    e.preventDefault();
    const confirmLogout = confirm("¿Seguro que quieres cerrar sesión?");
    if (confirmLogout) {
      localStorage.removeItem("usuarioActivo");
      alert("👋 Sesión cerrada correctamente.");
      window.location.href = "index.html";
    }
  });
});