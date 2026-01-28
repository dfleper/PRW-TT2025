(function () {
  // ====== Availability "live check" ======
  // En el HTML, añade:
  // <div id="availabilityBox" class="alert d-none" data-availability-url="/api/availability"></div>
  // y usa #serviceId y #startDateTime (y opcional #vehicleId)
  const box = document.getElementById("availabilityBox");
  if (!box) return;

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
        // si el endpoint no existe o falla, no rompemos nada
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
})();
