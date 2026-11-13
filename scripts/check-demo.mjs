import { readFileSync } from "node:fs";
import { runInNewContext } from "node:vm";
import assert from "node:assert/strict";

const source = readFileSync(new URL("../docs/app.js", import.meta.url), "utf8");
const html = readFileSync(new URL("../docs/index.html", import.meta.url), "utf8");
const css = readFileSync(new URL("../docs/styles.css", import.meta.url), "utf8");
const readme = readFileSync(new URL("../README.md", import.meta.url), "utf8");

for (const id of ["chef-grid","chef-search","chef-filters","bookings-container","chef-dialog","toast","nav-reservas-count"]) {
  assert.match(html, new RegExp('id="' + id + '"'), 'Elemento de interfaz ausente: ' + id);
}
assert.match(html, /styles\.css/);
assert.match(html, /app\.js/);
assert.match(html, /Proyecto Cocina/);
assert.match(css, /@media\(max-width:560px\)/);
assert.match(readme, /demostración independiente/);
assert.match(readme, /Josemajr6\/GestionCocina-Spring/);
assert.match(readme, /https:\/\/juanjogamero\.github\.io\/API-ProyectoCocina\//);

const storage = new Map();
const docEvents = new Map();
const winEvents = new Map();
const nodes = new Map();
const fakeNode = key => {
  if (!nodes.has(key)) {
    nodes.set(key,{
      innerHTML:"",textContent:"",value:"",dataset:{},tagName:"DIV",
      classList:{add(){},remove(){},toggle(){return true;}},
      setAttribute(){},removeAttribute(){},
      addEventListener(name, fn){this.listeners[name] = fn;},
      listeners:{},showModal(){this.open=true;},close(){this.open=false;},open:false
    });
  }
  return nodes.get(key);
};
const document = {
  querySelector: fakeNode,
  querySelectorAll:()=>[],
  addEventListener(name,fn){docEvents.set(name,fn);}
};
const localStorage = {
  getItem(key){return storage.get(key) ?? null;},
  setItem(key,val){storage.set(key,String(val));},
  removeItem(key){storage.delete(key);}
};
const location = {hash:"#inicio"};
const window = {
  addEventListener(name,fn){winEvents.set(name,fn);},
  confirm(){return true;}
};
runInNewContext(source,{
  document,window,localStorage,location,Date,Intl,
  setTimeout:()=>1,clearTimeout:()=>{},
  console
});
const api=window.ProyectoCocinaDemo;
assert.ok(api,"No se inicializó la aplicación.");
assert.equal(api.listChefs().length,4);
assert.match(fakeNode("#chef-grid").innerHTML,/Alba Moreno/);
assert.match(fakeNode("#chef-grid").innerHTML,/Lucía Benítez/);
assert.match(fakeNode("#bookings-container").innerHTML,/Tu agenda empieza aquí/);

const futureDate = add => {
  const d = new Date();d.setHours(12,0,0,0);d.setDate(d.getDate()+add);
  return d.getFullYear()+"-"+String(d.getMonth()+1).padStart(2,"0")+"-"+String(d.getDate()).padStart(2,"0");
};
const example = {chefId:1,date:futureDate(3),guests:4,name:"María García",note:"Cena en familia"};
assert.equal(api.validateReservation({...example,date:"2020-01-01"}),"Selecciona una fecha futura.");
assert.match(api.validateReservation({...example,guests:21}),/1 y 20/);
assert.equal(api.reserve(example).ok,true);
assert.equal(api.getReservations().length,1);
assert.match(storage.get("proyecto-cocina-portfolio-v1"),/"status":"pendiente"/);
assert.equal(api.reserve(example).ok,false,"No permite una reserva duplicada en el mismo día.");
assert.equal(api.reserve({...example,chefId:2}).ok,true,"Diferentes chefs pueden tener solicitudes el mismo día.");
assert.equal(api.changeStatus(1,"confirmada"),true);
assert.equal(api.changeStatus(1,"cancelada"),false,"Una reserva confirmada no se modifica desde esta demo.");
assert.equal(api.changeStatus(2,"cancelada"),true);
assert.equal(api.changeStatus(2,"confirmada"),false);
assert.equal(api.toggleFavorite(3),true);
assert.equal(api.isFavorite(3),true);
assert.equal(api.toggleFavorite(3),false);

const click=docEvents.get("click");
click({target:{closest:selector=>selector==="[data-open],[data-favorite],[data-clear],[data-confirm],[data-cancel]"?
{dataset:{favorite:"2"}}:null}});
assert.equal(api.isFavorite(2),true);
const input=fakeNode("#chef-search");
input.value="vegana";
input.listeners.input({target:input});
assert.match(fakeNode("#chef-grid").innerHTML,/Lucía Benítez/);
assert.doesNotMatch(fakeNode("#chef-grid").innerHTML,/Alba Moreno/);

assert.equal(api.reserve({...example,date:futureDate(10),name:"<img src=x onerror=alert(1)>"}).ok,true);
const renderEvents=docEvents.get("click");
renderEvents({target:{closest:selector=>selector==="[data-open],[data-favorite],[data-clear],[data-confirm],[data-cancel]"?
{dataset:{favorite:"1"}}:null}});
// La API de pruebas modifica el estado; la vista se actualiza al activar una acción de UI.
renderEvents({target:{closest:selector=>selector==="[data-open],[data-favorite],[data-clear],[data-confirm],[data-cancel]"?
{dataset:{confirm:"3"}}:null}});
const bookings=fakeNode("#bookings-container").innerHTML;
assert.ok(bookings.includes("&lt;img src=x onerror=alert(1)&gt;"),"El texto del usuario debe escapar HTML");
assert.ok(!bookings.includes('name="<img'),"No debe inyectar HTML desde el formulario");
api.reset();
assert.equal(api.getReservations().length,0);
assert.equal(api.isFavorite(2),false);
assert.ok(winEvents.has("hashchange"));
console.log("Proyecto Cocina: navegación, catálogo, filtros, favoritos y reservas de ejemplo OK");
