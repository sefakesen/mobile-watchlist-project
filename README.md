# 🌟 MUF - Multi-Media & Universe Tracker

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-007396?style=flat-square&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Database-Firebase_Realtime-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Volley](https://img.shields.io/badge/Networking-Google_Volley-4285F4?style=flat-square)

**MUF Tracker**, animeleri, filmleri, video oyunlarını ve kitapları tek bir asenkron bulut tabanında toplayan; gelişmiş algoritmalar, canlı API desteği ve gerçek zamanlı kütüphane istatistikleri sunan kişisel bir arşiv ve takip uygulamasıdır.


---

## ✨ Öne Çıkan Özellikler

### 🌍 1. Dört Büyük Evren Tek Vitrinde (Multi-API Engine)
Uygulama, farklı veri tiplerini asenkron olarak parse edip tek bir ortak `IcerikModel` kalıbında birleştirir:
* **Anime:** [Jikan v4 API](https://jikan.moe/) (MyAnimeList resmi olmayan köprüsü)
* **Film & Dizi:** [TMDB API](https://www.themoviedb.org/) (Trending & Multi-search)
* **Oyunlar:** [RAWG.io API](https://rawg.io/) (Dünyanın en büyük video oyunu veritabanı)
* **Kitaplar:** **Google Books API** (Relevance & PrintType filtreli)

### 🚀 2. Yüksek Performanslı UX & Algoritmalar
* **Sonsuz Kaydırma (Infinite Scroll):** Verileri tek seferde indirip belleği şişirmek yerine, kullanıcı listenin altına yaklaştığında (`!canScrollVertically(1)`) otomatik sayfa artırımı yaparak yeni verileri kuyruğa ekler.
* **Canlı Arama Motoru:** Kullanıcı yazmayı bıraktığı an tetiklenen, boşlukları `%20` ve `+` formatına sanitize eden dinamik sorgu yapısı.
* **Detaylı Jikan Filtreleri:** Animeleri anlık olarak *Türlere* (Shounen, Isekai, Mecha vb.) ve *Sezonlara* (2026 İlkbahar, Yakında Çıkacaklar) göre ayrıştırır.

### ☁️ 3. Gerçek Zamanlı Bulut Arşivi (Firebase CRUD)
* **Kişiselleştirilmiş Formlar:** Kategoriye duyarlı akıllı Custom Dialog ekranları (Oyunlar için *Saat*, Kitaplar için *Sayfa*, Animeler için *Bölüm* takibi).
* **Anlık Senkronizasyon:** `addValueEventListener` kullanılarak, kullanıcının kütüphanesinde yaptığı bir durum değişikliği (Örn: *İzleniyor* -> *Bitti*) anında arayüze yansır.

### 📊 4. İzleme Karnesi (Dashboard) & Bellek İçi Filtreleme
* **Matematiksel Motor:** Arka planda kullanıcının tükettiği toplam eseri, bitirdiği içerikleri ve okuduğu/izlediği devasa bölüm havuzunu hesaplayan akıllı sayaç.
* **Offline Sıralama:** Sunucuyu yormamak adına verileri bellekte (`anaListeYedek`) cache'ler; **"Sadece Bitenler"** ve **"Puana Göre Sırala (Descending)"** işlemlerini Java `Collections.sort()` ve özel `Comparator` sınıflarıyla sıfır gecikmeyle telefon işlemcisinde yapar.

---

## 🛠️ Mimari & Teknoloji Yığını

* **Mimarisi:** `Single-Activity Architecture` (Gereksiz Activity şişmelerini önleyen, `MainActivity` gövdeli Fragment geçiş sistemi).
* **Ağ Katmanı:** `Google Volley` (RESTful JSON işleme, RequestQueue yönetimi).
* **Hafıza Optimizasyonu:** `RecyclerView` + `GridLayoutManager` (Sadece ekranda görünen kartları RAM'de tutan View-Recycling mantığı).
* **Veri Kalıcılığı:** `SharedPreferences` (Güvenli oturum denetimi ve "Beni Hatırla" mekanizması).
* **Güvenlik:** Korumalı Try-Catch blokları ve Null-Safe UI Binding (`TextUtils.isEmpty` denetimleri).

---

## ⚙️ Kurulum (Geliştiriciler İçin)

Projeyi kendi bilgisayarınızda derlemek için şu adımları izleyin:

1. Projeyi klonlayın:
   ```bash
   git clone [https://github.com/sefakesen/mobile-watchlist-project.git](https://github.com/sefakesen/mobile-watchlist-project.git)
