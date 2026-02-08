(function () {

  // =========================================================
  // A) Cliente/Servicios: habilitar "Reservar cita"
  // Requiere:
  // - radios con clase .service-radio
  // - botón con id #reserveBtn
  //
  // Nota: como ya usamos UN solo form, NO hace falta copiar serviceId
  // a ningún hidden. El radio ya se envía en el POST.
  // =========================================================
  (function setupServiceReserveCTA() {

    const reserveBtn = document.getElementById("reserveBtn");
    if (!reserveBtn) return; // no estamos en la página de servicios cliente

    const radios = document.querySelectorAll(".service-radio");
    if (!radios.length) return;

    function updateButtonState() {
      const checked = document.querySelector(".service-radio:checked");
      reserveBtn.disabled = !checked;
    }

    // Estado inicial (por si viene preseleccionado desde sesión)
    updateButtonState();

    // Al cambiar, habilitar
    radios.forEach(r => r.addEventListener("change", updateButtonState));
  })();


  // =========================================================
  // B) Cliente/Citas: Availability "live check"
  // Requiere en el HTML:
  // <div id="availabilityBox" class="alert d-none" data-availability-url="/api/availability"></div>
  // y los campos #serviceId y #startDateTime
  // =========================================================
  (function setupAvailabilityLiveCheck() {

    const box = document.getElementById("availabilityBox");
    if (!box) return; // no estamos en el form de cita

    const serviceEl = document.getElementById("serviceId");
    const startEl = document.getElementById("startDateTime");

    const baseUrl = box.getAttribute("data-availability-url") || "/api/availability";

    let timer = null;

    function show(kind, msg) {
      box.classList.remove("d-none", "alert-success", "alert-warning", "alert-danger");
      box.classList.add("alert-" + kind);
      box.textContent = msg;
    }

    function hide() {
      box.classList.add("d-none");
      box.textContent = "";
    }

    async function check() {
      const serviceId = serviceEl?.value;
      const start = startEl?.value; // datetime-local => "YYYY-MM-DDTHH:mm"

      if (!serviceId || !start) {
        hide();
        return;
      }

      show("warning", "Comprobando disponibilidad…");

      try {
        // Spring suele aceptar sin segundos, pero así vamos a lo seguro
        const startDateTime = (start.length === 16) ? (start + ":00") : start;

        const url = `${baseUrl}?startDateTime=${encodeURIComponent(startDateTime)}&serviceId=${encodeURIComponent(serviceId)}`;
        const res = await fetch(url, { headers: { "Accept": "application/json" } });

        if (!res.ok) {
          // Si el endpoint no existe o falla, no rompemos nada
          hide();
          return;
        }

        const data = await res.json();
        if (data.available) {
          show("success", "Disponible ✅ Puedes reservar.");
        } else {
          show("danger", "No disponible ❌");
        }
      } catch (e) {
        hide();
      }
    }

    function schedule() {
      clearTimeout(timer);
      timer = setTimeout(check, 250);
    }

    serviceEl?.addEventListener("change", schedule);
    startEl?.addEventListener("change", schedule);
    startEl?.addEventListener("input", schedule);

    // chequeo inicial si ya hay valores
    schedule();
  })();

})();
