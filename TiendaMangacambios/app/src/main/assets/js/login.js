document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  const formTitle = document.getElementById("formTitle");
  const goRegister = document.getElementById("goRegister");
  const goLogin = document.getElementById("goLogin");

  // 🔄 Cambiar entre login y registro
  goRegister.addEventListener("click", (e) => {
    e.preventDefault();
    loginForm.classList.remove("active");
    registerForm.classList.add("active");
    formTitle.textContent = "Crear Cuenta";
  });

  goLogin.addEventListener("click", (e) => {
    e.preventDefault();
    registerForm.classList.remove("active");
    loginForm.classList.add("active");
    formTitle.textContent = "Iniciar Sesión";
  });

  // 🟣 REGISTRO
  registerForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const nombre = registerForm.querySelector('input[placeholder="Nombre completo"]').value.trim();
    const correo = registerForm.querySelector('input[placeholder="Correo electrónico"]').value.trim();
    const contrasena = registerForm.querySelector('input[placeholder="Contraseña"]').value.trim();
    const nacimiento = registerForm.querySelector('input[type="date"]').value.trim();
    const usuario = registerForm.querySelector('input[placeholder="Nombre de usuario"]').value.trim();

    if (!nombre || !correo || !contrasena || !nacimiento || !usuario) {
      alert("Por favor, completa todos los campos.");
      return;
    }

    const usuarios = JSON.parse(localStorage.getItem("usuariosRegistrados")) || [];

    // Evitar correos duplicados
    if (usuarios.some((u) => u.correo === correo)) {
      alert("⚠️ Este correo ya está registrado.");
      return;
    }

    const nuevoUsuario = {
      nombre,
      correo,
      contrasena,
      nacimiento,
      usuario,
      fechaRegistro: new Date().toLocaleDateString(),
      rol: "usuario", // 🔹 todos los nuevos son usuarios normales
    };

    usuarios.push(nuevoUsuario);
    localStorage.setItem("usuariosRegistrados", JSON.stringify(usuarios));

    alert("🎉 Registro exitoso. Ahora puedes iniciar sesión.");
    registerForm.reset();
    goLogin.click();
  });

  // 🟣 INICIO DE SESIÓN
  loginForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const correo = loginForm.querySelector('input[placeholder="Correo electrónico"]').value.trim();
    const contrasena = loginForm.querySelector('input[placeholder="Contraseña"]').value.trim();

    const usuarios = JSON.parse(localStorage.getItem("usuariosRegistrados")) || [];

    // ⚙️ ADMIN por defecto
    const adminUser = {
      correo: "admin@mangastore.cl",
      contrasena: "admin123",
      nombre: "Administrador",
      rol: "admin",
    };

    // Añadir admin si aún no existe
    if (!usuarios.some((u) => u.correo === adminUser.correo)) {
      usuarios.push(adminUser);
      localStorage.setItem("usuariosRegistrados", JSON.stringify(usuarios));
    }

    // Buscar usuario válido
    const usuarioValido = usuarios.find(
      (u) => u.correo === correo && u.contrasena === contrasena
    );

    if (!usuarioValido) {
      alert("❌ Correo o contraseña incorrectos.");
      return;
    }

    // Guardar sesión activa
    localStorage.setItem("usuarioActivo", JSON.stringify(usuarioValido));

    // 🔹 Redirección según rol
    if (usuarioValido.rol === "admin") {
      alert("👑 Bienvenido al panel, administrador.");
      window.location.href = "admin.html";
    } else {
      alert(`✅ Bienvenido ${usuarioValido.nombre}!`);
      window.location.href = "tienda.html";
    }
  });
});