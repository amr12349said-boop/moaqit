const CACHE = 'moaqit-v7';
const ASSETS = [
  './', './index.html', './world.js', './quran-data.js', './manifest.webmanifest',
  './icon-192.png', './icon-512.png', './icon-maskable-512.png',
  './icon.svg', './favicon-32.png', './apple-touch-icon.png',
  './azan_nasser.mp3'
];
self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE).then((c) => Promise.allSettled(ASSETS.map((a) => c.add(a)))).then(() => self.skipWaiting())
  );
});
self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys().then((ks) => Promise.all(ks.filter((k) => k !== CACHE).map((k) => caches.delete(k)))).then(() => self.clients.claim())
  );
});
self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = req.url.split('?')[0];
  /* ملفات التحميل الكبيرة: تسيب المتصفح يتعامل معاها مباشرة
     عشان يدعم إكمال التحميل (Range) ولا يتقطع */
  if (/\.(apk|zip|7z|rar)$/i.test(url)) return;
  const accept = req.headers.get('accept') || '';
  const isDoc = req.mode === 'navigate' || accept.indexOf('text/html') !== -1;
  if (isDoc){
    e.respondWith(
      fetch(req).then((r) => { caches.open(CACHE).then((c) => c.put('./index.html', r.clone())); return r; })
        .catch(() => caches.match('./index.html'))
    );
    return;
  }
  e.respondWith(
    caches.match(req).then((hit) => {
      if (hit) return hit;
      return fetch(req).then((r) => {
        if (r && (r.ok || r.type === 'opaque')) caches.open(CACHE).then((c) => c.put(req, r.clone()));
        return r;
      }).catch(() => hit);
    })
  );
});
