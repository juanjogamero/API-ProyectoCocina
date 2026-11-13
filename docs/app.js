/* Proyecto Cocina | demostración autónoma para GitHub Pages.
   No realiza llamadas a la API Spring Boot ni tramita reservas reales. */
(() => {
  "use strict";
  const STORAGE_KEY = "proyecto-cocina-portfolio-v1";
  const chefs = Object.freeze([
    {
      id: 1, name: "Alba Moreno", category: "Mediterránea", specialty: "Cocina mediterránea de temporada",
      price: 95, rating: "4,9", reviews: 38,
      image: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=900&q=85",
      description: "Menús inspirados en los productos de temporada, ingredientes frescos y recetas que reúnen a todos alrededor de la mesa.",
      styles: ["Producto local", "Menús personalizados", "Cena en casa"]
    },
    {
      id: 2, name: "Carlos Ruiz", category: "Creativa", specialty: "Cocina de autor contemporánea",
      price: 145, rating: "4,8", reviews: 26,
      image: "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=85",
      description: "Una propuesta contemporánea que combina técnica, creatividad y una cuidada presentación de cada plato.",
      styles: ["Alta cocina", "Degustación", "Experiencias"]
    },
    {
      id: 3, name: "Lucía Benítez", category: "Vegana", specialty: "Vegetal, creativa y sostenible",
      price: 80, rating: "4,9", reviews: 31,
      image: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=900&q=85",
      description: "Sabores vegetales llenos de personalidad, con ingredientes de proximidad y opciones adaptadas a cada ocasión.",
      styles: ["Vegetal", "Opciones sin gluten", "Producto fresco"]
    },
    {
      id: 4, name: "Mateo Serrano", category: "Tradicional", specialty: "Recetas clásicas con un toque personal",
      price: 90, rating: "4,8", reviews: 43,
      image: "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=900&q=85",
      description: "Una cocina cercana que recupera las recetas de siempre, ideal para reuniones familiares y celebraciones.",
      styles: ["Cocina casera", "Celebraciones", "Menús de grupo"]
    }
  ]);
  const $ = selector => document.querySelector(selector);
  const $$ = selector => Array.from(document.querySelectorAll(selector));
  const icon = name => '<svg class="icon" aria-hidden="true"><use href="#i-' + name + '"></use></svg>';
  const esc = input => String(input ?? "").replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[char]);
  const formatMoney = amount => new Intl.NumberFormat("es-ES", {
    style: "currency", currency: "EUR", maximumFractionDigits: 0
  }).format(amount);
  const formatDate = value => {
    const [year, month, day] = String(value).split("-").map(Number);
    const date = new Date(year, month - 1, day);
    return Number.isNaN(date.getTime()) ? "Fecha no disponible" :
      new Intl.DateTimeFormat("es-ES",{day:"numeric",month:"long",year:"numeric"}).format(date);
  };
  const localDate = offset => {
    const date = new Date();
    date.setHours(12, 0, 0, 0);
    date.setDate(date.getDate() + offset);
    const pad = n => String(n).padStart(2, "0");
    return date.getFullYear() + "-" + pad(date.getMonth()+1) + "-" + pad(date.getDate());
  };
  const nextDay = () => localDate(1);
  const allChefs = () => chefs.map(c => ({...c}));
  const defaultData = () => ({reservations:[], favorites:[]});
  const parseData = raw => {
    try {
      const data = typeof raw === "string" ? JSON.parse(raw) : raw;
      if (!data || typeof data !== "object") return defaultData();
      const reservations = Array.isArray(data.reservations) ? data.reservations.filter(r =>
        r && typeof r === "object" &&
        Number.isSafeInteger(r.id) && Number.isSafeInteger(r.chefId) &&
        chefs.some(c => c.id === r.chefId) &&
        typeof r.date === "string" && /^\d{4}-\d{2}-\d{2}$/.test(r.date) &&
        ["pendiente","confirmada","cancelada"].includes(r.status)
      ).slice(-200) : [];
      const favorites = Array.isArray(data.favorites) ?
        [...new Set(data.favorites.map(Number).filter(id => chefs.some(c => c.id === id)))] : [];
      return {reservations, favorites};
    } catch (_) { return defaultData(); }
  };
  let data = defaultData();
  try { data = parseData(localStorage.getItem(STORAGE_KEY)); } catch (_) { /* navegación privada */ }
  const persist = () => {
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); return true; }
    catch (_) { return false; }
  };
  const findChef = id => chefs.find(c => c.id === Number(id)) || null;
  const isFavorite = id => data.favorites.includes(Number(id));
  const conflicting = (chefId, date) => data.reservations.some(r =>
    r.chefId === Number(chefId) && r.date === date && r.status !== "cancelada");
  const validateReservation = item => {
    if (!findChef(item.chefId)) return "No hemos encontrado ese chef.";
    if (typeof item.date !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(item.date) ||
        item.date < nextDay()) return "Selecciona una fecha futura.";
    if (!Number.isInteger(Number(item.guests)) || Number(item.guests) < 1 || Number(item.guests) > 20)
      return "Indica entre 1 y 20 comensales.";
    if (String(item.name ?? "").trim().length < 2 || String(item.name).trim().length > 70)
      return "Escribe un nombre de 2 a 70 caracteres.";
    if (String(item.note ?? "").length > 280) return "El mensaje no puede superar 280 caracteres.";
    if (conflicting(item.chefId, item.date)) return "Ese chef ya tiene una solicitud de ejemplo en esa fecha.";
    return null;
  };
  const reserve = item => {
    const error = validateReservation(item);
    if (error) return {ok:false,error};
    const reservation = {
      id: Math.max(0,...data.reservations.map(r => r.id)) + 1,
      chefId: Number(item.chefId), date: item.date, guests: Number(item.guests),
      name: String(item.name).trim(), note: String(item.note ?? "").trim(),
      status: "pendiente", createdAt: new Date().toISOString()
    };
    data.reservations.push(reservation);
    persist();
    return {ok:true,reservation:{...reservation}};
  };
  const changeStatus = (id, status) => {
    if (!["confirmada","cancelada"].includes(status)) return false;
    const item = data.reservations.find(r => r.id === Number(id));
    if (!item || item.status !== "pendiente") return false;
    item.status = status;
    persist();
    return true;
  };
  const toggleFavorite = id => {
    if (!findChef(id)) return false;
    const key = Number(id);
    if (isFavorite(key)) data.favorites = data.favorites.filter(x => x !== key);
    else data.favorites.push(key);
    persist();
    return isFavorite(key);
  };
  const reset = () => { data = defaultData(); persist(); };
  let filter = "Todas", search = "", toastTimer = null;
  const imageMarkup = (chef, cls = "") =>
    '<img src="' + esc(chef.image) + '" alt="Fotografía de una elaboración culinaria de ejemplo" loading="lazy" ' +
    'data-fallback="food-placeholder.svg" class="' + cls + '">';
  const chefCard = chef =>
    '<article class="chef-card">' +
      '<div class="chef-visual">' + imageMarkup(chef) +
        '<span class="chef-tag">' + esc(chef.category) + '</span>' +
        '<button type="button" class="card-fav ' + (isFavorite(chef.id) ? "is-favorite" : "") + '" ' +
          'data-favorite="' + chef.id + '" aria-pressed="' + isFavorite(chef.id) + '" aria-label="' +
          (isFavorite(chef.id) ? "Quitar favorito: " : "Guardar favorito: ") + esc(chef.name) +
          '" title="Guardar como favorito">' + icon("bookmark") + '</button>' +
      '</div>' +
      '<div class="chef-card-body">' +
        '<div class="chef-card-head"><div><h3>' + esc(chef.name) + '</h3>' +
          '<p class="chef-card-specialty">' + esc(chef.specialty) + '</p></div>' +
          '<span class="rating">' + icon("star") + esc(chef.rating) + '</span></div>' +
        '<p class="chef-card-text">' + esc(chef.description) + '</p>' +
        '<div class="chef-card-footer"><div class="chef-price">' + formatMoney(chef.price) +
          '<small>tarifa orientativa / servicio</small></div>' +
          '<button type="button" class="card-link" data-open="' + chef.id + '">Ver perfil ↗</button></div>' +
      '</div>' +
    '</article>';
  function renderChefs() {
    const query = search.normalize("NFD").replace(/[\u0300-\u036f]/g,"").toLowerCase().trim();
    const list = chefs.filter(chef => {
      const category = filter === "Todas" || filter === chef.category;
      const full = (chef.name+" "+chef.specialty+" "+chef.category+" "+chef.description)
        .normalize("NFD").replace(/[\u0300-\u036f]/g,"").toLowerCase();
      return category && full.includes(query);
    });
    const grid = $("#chef-grid");
    if (grid) grid.innerHTML = list.length ? list.map(chefCard).join("") :
      '<div class="empty-result"><strong>No encontramos chefs con esos filtros</strong>' +
      '<p>Prueba con otra especialidad o modifica tu búsqueda.</p>' +
      '<button class="card-link" data-clear="true" type="button">Restablecer filtros</button></div>';
    const count = $("#chef-count");
    if (count) count.textContent = list.length + " " + (list.length===1 ? "chef":"chefs");
    $$("[data-filter]").forEach(btn => {
      btn.classList.toggle("is-active",btn.dataset.filter === filter);
      btn.setAttribute("aria-pressed",String(btn.dataset.filter === filter));
    });
  }
  function bookingCard(item) {
    const chef = findChef(item.chefId);
    if (!chef) return "";
    const statusText = {pendiente:"Pendiente",confirmada:"Confirmada (simulada)",cancelada:"Cancelada"};
    return '<article class="booking-card">' +
      imageMarkup(chef) +
      '<div><h3>' + esc(chef.name) + '</h3><p>' + esc(formatDate(item.date)) + ' · ' +
      esc(item.guests) + ' ' + (item.guests===1?"comensal":"comensales") +
      ' · A nombre de ' + esc(item.name || "Sin nombre") + '</p>' +
      (item.note ? '<p>' + esc(item.note) + '</p>' : '') + '</div>' +
      '<div class="booking-right"><span class="booking-status ' + esc(item.status) + '">' +
      statusText[item.status] + '</span>' +
      (item.status === "pendiente" ? '<div class="booking-card-actions">' +
        '<button type="button" data-confirm="' + item.id + '">Simular confirmación</button>' +
        '<button type="button" data-cancel="' + item.id + '">Cancelar</button></div>' : '') +
      '</div></article>';
  }
  function renderReservations() {
    const count = data.reservations.filter(r => r.status !== "cancelada").length;
    const nav = $("#nav-reservas-count"); if(nav) nav.textContent = String(count);
    const container = $("#bookings-container"); if(!container) return;
    if (!data.reservations.length) {
      container.innerHTML = '<div class="bookings-empty">' +
        '<span>' + icon("calendar") + '</span><h3>Tu agenda empieza aquí</h3>' +
        '<p>Todavía no tienes reservas de ejemplo. Descubre un chef, elige una fecha y crea tu primera solicitud.</p>' +
        '<a href="#chefs" class="button button-dark">Descubrir chefs ' + icon("arrow") + '</a>' +
        '</div>';
      return;
    }
    container.innerHTML = '<div class="booking-list">' + [...data.reservations].reverse().map(bookingCard).join("") + '</div>';
  }
  function render() { renderChefs(); renderReservations(); }
  function notify(message) {
    const el = $("#toast"); if (!el) return;
    el.textContent = message;
    el.classList.add("is-visible");
    if (toastTimer) clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.classList.remove("is-visible"), 3500);
  }
  function showChef(id) {
    const chef = findChef(id); if(!chef) return;
    const dialog = $("#chef-dialog");
    const content = $("#chef-dialog-content");
    if (!dialog || !content) return;
    content.innerHTML =
      '<div class="dialog-hero">' + imageMarkup(chef) +
        '<div class="dialog-head"><span class="section-kicker">/ CONOCE A TU CHEF</span>' +
          '<h2 id="dialog-title">' + esc(chef.name) + '</h2><p>' + esc(chef.specialty) + '</p>' +
          '<p class="rating">' + icon("star") + esc(chef.rating) + ' · ' +
          esc(chef.reviews) + ' valoraciones ficticias</p></div></div>' +
      '<div class="dialog-content"><p>' + esc(chef.description) + '</p>' +
        '<div class="dialog-tags">' + chef.styles.map(style => '<span>' + esc(style) + '</span>').join("") + '</div>' +
        '<form id="booking-form" class="booking-form" novalidate>' +
          '<h3>Crear solicitud de ejemplo</h3>' +
          '<input type="hidden" name="chefId" value="' + chef.id + '">' +
          '<div class="form-field"><label for="booking-date">¿Qué día te gustaría?</label>' +
            '<input type="date" id="booking-date" name="date" required min="' + nextDay() + '"></div>' +
          '<div class="form-field"><label for="booking-guests">Número de comensales</label>' +
            '<input type="number" id="booking-guests" name="guests" required min="1" max="20" value="2"></div>' +
          '<div class="form-field full"><label for="booking-name">Nombre para identificar la reserva</label>' +
            '<input type="text" id="booking-name" name="name" required minlength="2" maxlength="70" ' +
            'placeholder="Ej. Ana García" autocomplete="name"></div>' +
          '<div class="form-field full"><label for="booking-note">Notas para tu solicitud (opcional)</label>' +
            '<textarea id="booking-note" name="note" maxlength="280" placeholder="Una cena de cumpleaños, preferencias culinarias…"></textarea></div>' +
          '<p class="form-message" id="form-message" role="alert"></p>' +
          '<div class="form-actions"><small>Simulación local: no hay pagos, disponibilidad real ni contacto con profesionales.</small>' +
            '<button class="button button-dark" type="submit">Añadir a mis reservas ' + icon("arrow") + '</button></div>' +
        '</form></div>';
    if (typeof dialog.showModal === "function") dialog.showModal();
    else dialog.setAttribute("open","");
  }
  function closeDialog() {
    const dialog = $("#chef-dialog"); if(!dialog) return;
    if(typeof dialog.close === "function") dialog.close();
    else dialog.removeAttribute("open");
  }
  const navActive = () => {
    const hash = location.hash.replace(/^#/,"") || "inicio";
    $$("[data-nav]").forEach(a => a.classList.toggle("is-active",a.dataset.nav === hash));
    const nav = $("#main-nav"); if(nav) nav.classList.remove("is-open");
    const mobile = $("#mobile-menu-button");
    if(mobile) mobile.setAttribute("aria-expanded","false");
  };
  document.addEventListener("click",event => {
    const target = event.target.closest?.("[data-open],[data-favorite],[data-clear],[data-confirm],[data-cancel]");
    if (!target) return;
    if (target.dataset.open) showChef(target.dataset.open);
    if (target.dataset.favorite) {
      const saved = toggleFavorite(target.dataset.favorite);
      renderChefs();
      notify(saved ? "Chef guardado en favoritos." : "Chef eliminado de favoritos.");
    }
    if (target.dataset.clear) {
      filter = "Todas"; search = "";
      const input=$("#chef-search"); if(input) input.value="";
      renderChefs();
    }
    if (target.dataset.confirm && changeStatus(target.dataset.confirm,"confirmada")) {
      renderReservations(); notify("Confirmación simulada. No es una reserva real.");
    }
    if (target.dataset.cancel && changeStatus(target.dataset.cancel,"cancelada")) {
      renderReservations(); notify("Solicitud de ejemplo cancelada.");
    }
  });
  document.addEventListener("submit",event => {
    if(event.target?.id !== "booking-form") return;
    event.preventDefault();
    const form = event.target;
    const find = key => form.elements.namedItem(key)?.value ?? "";
    const result = reserve({
      chefId:Number(find("chefId")),date:String(find("date")),
      guests:Number(find("guests")),name:String(find("name")),note:String(find("note"))
    });
    if (!result.ok) {const message=$("#form-message");if(message) message.textContent=result.error;return;}
    closeDialog();renderReservations();location.hash="#reservas";
    notify("Solicitud de ejemplo creada en este navegador.");
  });
  document.addEventListener("error",event => {
    const img = event.target;
    if (img?.tagName === "IMG" && img.dataset.fallback && !img.dataset.fallbackUsed) {
      img.dataset.fallbackUsed = "true";
      img.src = img.dataset.fallback;
    }
  },true);
  $("#chef-search")?.addEventListener("input",event => {
    search=String(event.target.value || "");renderChefs();
  });
  $("#chef-filters")?.addEventListener("click",event => {
    const btn = event.target.closest?.("[data-filter]");
    if(btn) { filter=btn.dataset.filter; renderChefs(); }
  });
  $("#dialog-close")?.addEventListener("click",closeDialog);
  $("#chef-dialog")?.addEventListener("click",event => {
    if(event.target === $("#chef-dialog")) closeDialog();
  });
  $("#reset-reservations")?.addEventListener("click",() => {
    if (data.reservations.length || data.favorites.length) {
      if (window.confirm && !window.confirm("¿Borrar todas las reservas de ejemplo y favoritos guardados en este navegador?")) return;
      reset();render();notify("Datos locales restablecidos.");
    } else notify("No hay datos que restablecer.");
  });
  $("#mobile-menu-button")?.addEventListener("click",() => {
    const nav=$("#main-nav"),btn=$("#mobile-menu-button");if(!nav||!btn)return;
    const open=nav.classList.toggle("is-open");
    btn.setAttribute("aria-expanded",String(open));
  });
  window.addEventListener("hashchange",navActive);
  $("#year") && ($("#year").textContent = String(new Date().getFullYear()));
  render();navActive();

  // API puramente local, expuesta para comprobar las reglas desde Node.
  // No interactúa con el backend ni contiene datos reales de clientes.
  window.ProyectoCocinaDemo = Object.freeze({
    listChefs: allChefs,
    getReservations: () => data.reservations.map(r => ({...r})),
    validateReservation, reserve, changeStatus, toggleFavorite, isFavorite, reset
  });
})();