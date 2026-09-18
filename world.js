'use strict';
/* ==================== توقيتات دول العالم ==================== */
/* [IANA timezone, المدينة, الدولة, القارة] — القارات: asia, europe, africa, amer, oce */
const WORLD = [
  /* آسيا والشرق الأوسط */
  ['Asia/Riyadh','مكة المكرمة','السعودية','asia'],['Asia/Riyadh','الرياض','السعودية','asia'],['Asia/Riyadh','جدة','السعودية','asia'],
  ['Asia/Dubai','دبي','الإمارات','asia'],['Asia/Dubai','أبو ظبي','الإمارات','asia'],
  ['Asia/Qatar','الدوحة','قطر','asia'],['Asia/Bahrain','المنامة','البحرين','asia'],['Asia/Kuwait','الكويت','الكويت','asia'],
  ['Asia/Muscat','مسقط','عُمان','asia'],['Asia/Aden','صنعاء','اليمن','asia'],['Asia/Amman','عمّان','الأردن','asia'],
  ['Asia/Hebron','القدس','فلسطين','asia'],['Asia/Beirut','بيروت','لبنان','asia'],['Asia/Damascus','دمشق','سوريا','asia'],
  ['Asia/Baghdad','بغداد','العراق','asia'],['Asia/Tehran','طهران','إيران','asia'],
  ['Europe/Istanbul','إسطنبول','تركيا','asia'],['Europe/Istanbul','أنقرة','تركيا','asia'],['Asia/Jerusalem','تل أبيب','إسرائيل','asia'],
  ['Asia/Kabul','كابول','أفغانستان','asia'],['Asia/Karachi','كراتشي','باكستان','asia'],['Asia/Karachi','إسلام آباد','باكستان','asia'],
  ['Asia/Kolkata','نيودلهي','الهند','asia'],['Asia/Kolkata','مومباي','الهند','asia'],['Asia/Dhaka','دكا','بنغلاديش','asia'],
  ['Asia/Kathmandu','كاتماندو','نيبال','asia'],['Asia/Thimphu','تيمفو','بوتان','asia'],['Asia/Colombo','كولومبو','سريلانكا','asia'],
  ['Indian/Maldives','ماليه','المالديف','asia'],['Asia/Yangon','يانغون','ميانمار','asia'],['Asia/Bangkok','بانكوك','تايلاند','asia'],
  ['Asia/Vientiane','فيينتيان','لاوس','asia'],['Asia/Ho_Chi_Minh','هو تشي منه','فيتنام','asia'],['Asia/Ho_Chi_Minh','هانوي','فيتنام','asia'],
  ['Asia/Phnom_Penh','بنوم بنه','كمبوديا','asia'],['Asia/Kuala_Lumpur','كوالالمبور','ماليزيا','asia'],
  ['Asia/Singapore','سنغافورة','سنغافورة','asia'],['Asia/Jakarta','جاكرتا','إندونيسيا','asia'],
  ['Asia/Makassar','دينباسار (بالي)','إندونيسيا','asia'],['Asia/Manila','مانيلا','الفلبين','asia'],
  ['Asia/Brunei','بندر سري بقاوان','بروناي','asia'],['Asia/Shanghai','بكين','الصين','asia'],['Asia/Shanghai','شنغهاي','الصين','asia'],
  ['Asia/Hong_Kong','هونغ كونغ','هونغ كونغ','asia'],['Asia/Taipei','تايبيه','تايوان','asia'],
  ['Asia/Seoul','سيول','كوريا الجنوبية','asia'],['Asia/Pyongyang','بيونغ يانغ','كوريا الشمالية','asia'],
  ['Asia/Tokyo','طوكيو','اليابان','asia'],['Asia/Ulaanbaatar','أولان باتور','منغوليا','asia'],
  ['Asia/Almaty','أستانا','كازاخستان','asia'],['Asia/Almaty','ألماتي','كازاخستان','asia'],['Asia/Tashkent','طشقند','أوزبكستان','asia'],
  ['Asia/Ashgabat','عشق آباد','تركمانستان','asia'],['Asia/Bishkek','بيشكك','قيرغيزستان','asia'],['Asia/Dushanbe','دوشنبه','طاجيكستان','asia'],
  ['Asia/Baku','باكو','أذربيجان','asia'],['Asia/Yerevan','يريفان','أرمينيا','asia'],['Asia/Tbilisi','تبليسي','جورجيا','asia'],
  ['Asia/Nicosia','نيقوسيا','قبرص','asia'],
  /* أوروبا */
  ['Europe/London','لندن','المملكة المتحدة','europe'],['Europe/Dublin','دبلن','أيرلندا','europe'],['Europe/Lisbon','لشبونة','البرتغال','europe'],
  ['Europe/Madrid','مدريد','إسبانيا','europe'],['Europe/Paris','باريس','فرنسا','europe'],['Europe/Amsterdam','أمستردام','هولندا','europe'],
  ['Europe/Brussels','بروكسل','بلجيكا','europe'],['Europe/Luxembourg','لوكسمبورغ','لوكسمبورغ','europe'],
  ['Europe/Berlin','برلين','ألمانيا','europe'],['Europe/Zurich','زيورخ','سويسرا','europe'],['Europe/Vienna','فيينا','النمسا','europe'],
  ['Europe/Rome','روما','إيطاليا','europe'],['Europe/Athens','أثينا','اليونان','europe'],['Europe/Copenhagen','كوبنهاغن','الدنمارك','europe'],
  ['Europe/Stockholm','ستوكهولم','السويد','europe'],['Europe/Oslo','أوسلو','النرويج','europe'],['Europe/Helsinki','هلسنكي','فنلندا','europe'],
  ['Atlantic/Reykjavik','ريكيافيك','آيسلندا','europe'],['Europe/Warsaw','وارسو','بولندا','europe'],['Europe/Prague','براغ','التشيك','europe'],
  ['Europe/Bratislava','براتيسلافا','سلوفاكيا','europe'],['Europe/Budapest','بودابست','المجر','europe'],
  ['Europe/Bucharest','بوخارست','رومانيا','europe'],['Europe/Sofia','صوفيا','بلغاريا','europe'],
  ['Europe/Belgrade','بلغراد','صربيا','europe'],['Europe/Zagreb','زغرب','كرواتيا','europe'],['Europe/Ljubljana','ليوبليانا','سلوفينيا','europe'],
  ['Europe/Sarajevo','سراييفو','البوسنة والهرسك','europe'],['Europe/Skopje','سكوبيه','مقدونيا الشمالية','europe'],
  ['Europe/Podgorica','بودغوريتسا','الجبل الأسود','europe'],['Europe/Tirane','تيرانا','ألبانيا','europe'],
  ['Europe/Kyiv','كييف','أوكرانيا','europe'],['Europe/Minsk','مينسك','بيلاروسيا','europe'],
  ['Europe/Moscow','موسكو','روسيا','europe'],['Asia/Yekaterinburg','يكاترينبورغ','روسيا','europe'],
  ['Asia/Novosibirsk','نوفوسيبيرسك','روسيا','europe'],['Asia/Vladivostok','فلاديفوستوك','روسيا','europe'],
  ['Europe/Tallinn','تالين','إستونيا','europe'],['Europe/Riga','ريغا','لاتفيا','europe'],['Europe/Vilnius','فيلنيوس','ليتوانيا','europe'],
  ['Europe/Chisinau','كيشيناو','مولدوفا','europe'],['Europe/Malta','فاليتا','مالطا','europe'],['Europe/Monaco','موناكو','موناكو','europe'],
  ['Europe/Andorra','أندورا','أندورا','europe'],['Europe/San_Marino','سان مارينو','سان مارينو','europe'],
  ['Europe/Vatican','الفاتيكان','الفاتيكان','europe'],['Europe/Vaduz','فادوز','ليختنشتاين','europe'],
  /* أفريقيا */
  ['Africa/Cairo','القاهرة','مصر','africa'],['Africa/Cairo','الإسكندرية','مصر','africa'],
  ['Africa/Algiers','الجزائر','الجزائر','africa'],['Africa/Casablanca','الدار البيضاء','المغرب','africa'],
  ['Africa/Tunis','تونس','تونس','africa'],['Africa/Tripoli','طرابلس','ليبيا','africa'],['Africa/Khartoum','الخرطوم','السودان','africa'],
  ['Africa/Juba','جوبا','جنوب السودان','africa'],['Africa/Mogadishu','مقديشو','الصومال','africa'],
  ['Africa/Djibouti','جيبوتي','جيبوتي','africa'],['Africa/Nouakchott','نواكشوط','موريتانيا','africa'],
  ['Africa/Dakar','داكار','السنغال','africa'],['Africa/Bamako','باماكو','مالي','africa'],['Africa/Niamey','نيامي','النيجر','africa'],
  ['Africa/Ndjamena','نجامينا','تشاد','africa'],['Africa/Lagos','لاغوس','نيجيريا','africa'],['Africa/Lagos','أبوجا','نيجيريا','africa'],
  ['Africa/Douala','ياوندي','الكاميرون','africa'],['Africa/Libreville','ليبرفيل','الغابون','africa'],
  ['Africa/Brazzaville','برازافيل','الكونغو','africa'],['Africa/Kinshasa','كينشاسا','الكونغو الديمقراطية','africa'],
  ['Africa/Luanda','لواندا','أنغولا','africa'],['Africa/Accra','أكرا','غانا','africa'],['Africa/Abidjan','أبيدجان','ساحل العاج','africa'],
  ['Africa/Ouagadougou','واغادوغو','بوركينا فاسو','africa'],['Africa/Conakry','كوناكري','غينيا','africa'],
  ['Africa/Bissau','بيساو','غينيا بيساو','africa'],['Africa/Freetown','فريتاون','سيراليون','africa'],
  ['Africa/Monrovia','مونروفيا','ليبيريا','africa'],['Africa/Lome','لومي','توغو','africa'],
  ['Africa/Porto-Novo','بورتو نوفو','بنين','africa'],['Africa/Addis_Ababa','أديس أبابا','إثيوبيا','africa'],
  ['Africa/Asmara','أسمرة','إريتريا','africa'],['Africa/Nairobi','نيروبي','كينيا','africa'],
  ['Africa/Dar_es_Salaam','دار السلام','تنزانيا','africa'],['Africa/Kampala','كمبالا','أوغندا','africa'],
  ['Africa/Kigali','كيغالي','رواندا','africa'],['Africa/Bujumbura','بوجمبورا','بوروندي','africa'],
  ['Africa/Lusaka','لوساكا','زامبيا','africa'],['Africa/Harare','هراري','زيمبابوي','africa'],
  ['Africa/Blantyre','ليلونغوي','مالاوي','africa'],['Africa/Maputo','مابوتو','موزمبيق','africa'],
  ['Indian/Antananarivo','أنتاناناريفو','مدغشقر','africa'],['Africa/Johannesburg','جوهانسبرغ','جنوب أفريقيا','africa'],
  ['Africa/Johannesburg','كيب تاون','جنوب أفريقيا','africa'],['Africa/Gaborone','غابورون','بوتسوانا','africa'],
  ['Africa/Windhoek','ويندهوك','ناميبيا','africa'],['Africa/Maseru','ماسيرو','ليسوتو','africa'],
  ['Africa/Mbabane','مباباني','إسواتيني','africa'],['Indian/Mahe','فيكتوريا','سيشل','africa'],
  ['Indian/Mauritius','بورت لويس','موريشيوس','africa'],['Indian/Comoro','موروني','جزر القمر','africa'],
  ['Atlantic/Cape_Verde','برايا','الرأس الأخضر','africa'],['Africa/Sao_Tome','ساو تومي','ساو تومي وبرينسيبي','africa'],
  ['Africa/Banjul','بانجول','غامبيا','africa'],['Africa/Malabo','مالابو','غينيا الاستوائية','africa'],
  ['Africa/Bangui','بانغي','أفريقيا الوسطى','africa'],
  /* الأمريكتان */
  ['America/New_York','نيويورك','الولايات المتحدة','amer'],['America/New_York','واشنطن','الولايات المتحدة','amer'],
  ['America/Chicago','شيكاغو','الولايات المتحدة','amer'],['America/Denver','دنفر','الولايات المتحدة','amer'],
  ['America/Los_Angeles','لوس أنجلوس','الولايات المتحدة','amer'],['America/Anchorage','أنكوريج','الولايات المتحدة','amer'],
  ['Pacific/Honolulu','هونولولو','الولايات المتحدة','amer'],
  ['America/Toronto','تورونتو','كندا','amer'],['America/Toronto','أوتاوا','كندا','amer'],
  ['America/Vancouver','فانكوفر','كندا','amer'],['America/Edmonton','إدمونتون','كندا','amer'],
  ['America/Mexico_City','مكسيكو سيتي','المكسيك','amer'],['America/Tijuana','تيخوانا','المكسيك','amer'],
  ['America/Cancun','كانكون','المكسيك','amer'],['America/Havana','هافانا','كوبا','amer'],
  ['America/Jamaica','كينغستون','جامايكا','amer'],['America/Port-au-Prince','بورت أو برنس','هايتي','amer'],
  ['America/Santo_Domingo','سانتو دومينغو','الدومينيكان','amer'],['America/Puerto_Rico','سان خوان','بورتوريكو','amer'],
  ['America/Port_of_Spain','بورت أوف سبين','ترينيداد وتوباغو','amer'],['America/Guatemala','غواتيمالا','غواتيمالا','amer'],
  ['America/Tegucigalpa','تيغوسيغالبا','هندوراس','amer'],['America/El_Salvador','سان سلفادور','السلفادور','amer'],
  ['America/Managua','ماناغوا','نيكاراغوا','amer'],['America/Costa_Rica','سان خوسيه','كوستاريكا','amer'],
  ['America/Panama','بنما','بنما','amer'],['America/Belize','بلموبان','بليز','amer'],
  ['America/Sao_Paulo','ساو باولو','البرازيل','amer'],['America/Sao_Paulo','ريو دي جانيرو','البرازيل','amer'],
  ['America/Manaus','ماناوس','البرازيل','amer'],['America/Argentina/Buenos_Aires','بوينس آيرس','الأرجنتين','amer'],
  ['America/Santiago','سانتياغو','تشيلي','amer'],['America/Lima','ليما','بيرو','amer'],['America/Bogota','بوغوتا','كولومبيا','amer'],
  ['America/Caracas','كاراكاس','فنزويلا','amer'],['America/Guayaquil','كيتو','الإكوادور','amer'],
  ['America/La_Paz','لاباز','بوليفيا','amer'],['America/Asuncion','أسونسيون','باراغواي','amer'],
  ['America/Montevideo','مونتيفيديو','أوروغواي','amer'],['America/Guyana','جورج تاون','غيانا','amer'],
  ['America/Paramaribo','باراماريبو','سورينام','amer'],['America/Nuuk','نوك','غرينلاند','amer'],
  /* أوقيانوسيا */
  ['Australia/Sydney','سيدني','أستراليا','oce'],['Australia/Melbourne','ملبورن','أستراليا','oce'],
  ['Australia/Brisbane','بريزبن','أستراليا','oce'],['Australia/Perth','بيرث','أستراليا','oce'],
  ['Australia/Adelaide','أديلايد','أستراليا','oce'],['Pacific/Auckland','أوكلاند','نيوزيلندا','oce'],
  ['Pacific/Port_Moresby','بورت مورسبي','بابوا غينيا الجديدة','oce'],['Pacific/Fiji','سوفا','فيجي','oce'],
  ['Pacific/Guadalcanal','هونيارا','جزر سليمان','oce'],['Pacific/Efate','بورت فيلا','فانواتو','oce'],
  ['Pacific/Apia','أبيا','ساموا','oce'],['Pacific/Tongatapu','نوكو ألوفا','تونغا','oce'],
  ['Pacific/Tarawa','تاراوا','كيريباتي','oce'],['Pacific/Chuuk','ونو','ميكرونيزيا','oce'],
  ['Pacific/Palau','نغيرولمود','بالاو','oce'],['Pacific/Majuro','ماجورو','جزر مارشال','oce'],
  ['Pacific/Nauru','يارين','ناورو','oce'],['Pacific/Funafuti','فونافوتي','توفالو','oce']
].map(function(e, i){ return {tz:e[0], city:e[1], country:e[2], reg:e[3], id:i}; });

let worldPins = [], lastWorldSec = -1, worldReg = 'all';

function loadPins(){
  try { worldPins = JSON.parse(localStorage.getItem('moaqit_pins') || '[]'); } catch(e){ worldPins = []; }
  if (!Array.isArray(worldPins)) worldPins = [];
  if (!worldPins.length) worldPins = [0];
}
function savePins(){ localStorage.setItem('moaqit_pins', JSON.stringify(worldPins)); }

function tzParts(tz){
  const now = new Date();
  let p = [];
  try {
    p = new Intl.DateTimeFormat('en-GB', {timeZone:tz, hour:'2-digit', minute:'2-digit', second:'2-digit', hour12:false}).formatToParts(now);
  } catch(e){}
  const get = t => { const x = p.find(f => f.type === t); return x ? +x.value : 0; };
  const h = get('hour') % 24, m = get('minute'), s = get('second');
  return {h12: h % 12 || 12, m, s, ampm: h < 12 ? 'ص' : 'م'};
}

function tzOffsetMinutes(tz){
  const now = new Date();
  const toMin = s => { const q = s.split(':'); return +q[0]*60 + +q[1]; };
  let utcS = '', tzS = '';
  try {
    utcS = new Intl.DateTimeFormat('en-GB', {timeZone:'UTC', hour:'2-digit', minute:'2-digit', hour12:false}).format(now);
    tzS = new Intl.DateTimeFormat('en-GB', {timeZone:tz, hour:'2-digit', minute:'2-digit', hour12:false}).format(now);
  } catch(e){ return 0; }
  let diff = toMin(tzS) - toMin(utcS);
  if (diff < -720) diff += 1440;
  if (diff > 720) diff -= 1440;
  return diff;
}
function offsetStr(min){
  const sign = min < 0 ? '-' : '+';
  const a = Math.abs(min);
  return 'UTC' + sign + Math.floor(a/60) + (a%60 ? ':' + app.fmt(a%60) : '');
}

function cardHTML(it, pinned){
  return '<div class="world-card" data-id="' + it.id + '" title="' + it.tz + '">' +
    '<button class="pin-btn' + (pinned ? ' pinned' : '') + '" data-id="' + it.id + '" title="تثبيت / إلغاء التثبيت">' +
      '<svg width="12" height="12" viewBox="0 0 24 24" fill="' + (pinned ? '#171310' : 'none') + '" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 17v5"/><path d="M5 3h14v3l-3 5v3H8v-3L5 6z"/></svg>' +
    '</button>' +
    '<div class="wcity">' + it.city + '</div>' +
    '<div class="wcountry">' + it.country + '</div>' +
    '<div class="wtime">--:--</div>' +
    '<div><span class="woffset"></span></div>' +
    '<div class="wdate"></div>' +
  '</div>';
}

function renderWorld(){
  const grid = document.getElementById('world-grid');
  const pinnedGrid = document.getElementById('pinned-grid');
  grid.innerHTML = '';
  pinnedGrid.innerHTML = '';
  WORLD.forEach(it => {
    const pinned = worldPins.indexOf(it.id) > -1;
    (pinned ? pinnedGrid : grid).insertAdjacentHTML('beforeend', cardHTML(it, pinned));
  });
  updateWorldTimes(true);
}

function updateWorldTimes(force){
  const now = new Date();
  const sec = now.getSeconds();
  if (!force && sec === lastWorldSec) return;
  lastWorldSec = sec;
  const cards = document.querySelectorAll('.world-card');
  if (!cards.length) return;
  const locale = 'ar-EG-u-nu-' + (app.numSystem ? app.numSystem() : (app.isLatin() ? 'latn' : 'arab'));
  for (let i = 0; i < cards.length; i++){
    const card = cards[i];
    const it = WORLD[+card.dataset.id];
    if (!it) continue;
    const p = tzParts(it.tz);
    const wt = card.querySelector('.wtime');
    wt.textContent = app.fmt(p.h12) + ':' + app.fmt(p.m) + ' ' + p.ampm;
    if (!card.dataset.off){
      card.dataset.off = tzOffsetMinutes(it.tz);
      card.querySelector('.woffset').textContent = offsetStr(+card.dataset.off);
    }
    if (force || sec === 0 || !card.dataset.d){
      card.dataset.d = '1';
      card.querySelector('.wdate').textContent = new Intl.DateTimeFormat(locale, {timeZone:it.tz, weekday:'short', day:'numeric', month:'short'}).format(now);
    }
  }
}

function worldRefresh(){
  document.querySelectorAll('.world-card').forEach(c => { delete c.dataset.off; delete c.dataset.d; });
  updateWorldTimes(true);
}

function applyWorldFilter(){
  const q = document.getElementById('world-search').value.trim();
  let visible = 0;
  document.querySelectorAll('#world-grid .world-card').forEach(card => {
    const it = WORLD[+card.dataset.id];
    const okReg = worldReg === 'all' || it.reg === worldReg;
    const okQ = !q || it.city.indexOf(q) > -1 || it.country.indexOf(q) > -1;
    const show = okReg && okQ;
    card.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  let emptyEl = document.getElementById('world-empty');
  if (!emptyEl){
    emptyEl = document.createElement('div');
    emptyEl.id = 'world-empty';
    emptyEl.className = 'world-empty';
    emptyEl.textContent = 'لا توجد نتائج مطابقة';
    document.getElementById('world-grid').appendChild(emptyEl);
  }
  emptyEl.style.display = visible ? 'none' : 'block';
}

window.WORLD_LIST = WORLD;
window.tzOffset = tzOffsetMinutes;
window.offsetStrFmt = offsetStr;

function initWorldClock(){
  loadPins();
  renderWorld();
  document.getElementById('world-search').addEventListener('input', applyWorldFilter);
  document.getElementById('region-chips').addEventListener('click', e => {
    const chip = e.target.closest('.chip');
    if (!chip) return;
    worldReg = chip.dataset.reg;
    document.querySelectorAll('#region-chips .chip').forEach(c => c.classList.toggle('sel', c === chip));
    applyWorldFilter();
  });
  document.addEventListener('click', e => {
    const pb = e.target.closest('.pin-btn');
    if (!pb) return;
    const id = +pb.dataset.id;
    const idx = worldPins.indexOf(id);
    if (idx > -1) worldPins.splice(idx, 1); else worldPins.push(id);
    savePins();
    renderWorld();
    applyWorldFilter();
  });
  updateWorldTimes(true);
}