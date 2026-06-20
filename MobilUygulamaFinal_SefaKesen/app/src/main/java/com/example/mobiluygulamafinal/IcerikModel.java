package com.example.mobiluygulamafinal;

public class IcerikModel {
    private String key;
    private String baslik;
    private String posterUrl;
    private String puan;
    private String kategori;
    private String kullaniciPuani;
    private String durum;
    private int guncelBolum;
    private int toplamBolum;
    private int harcananVakit;

    public IcerikModel() {
    }

    public IcerikModel(String baslik, String posterUrl, String puan, String kategori, int toplamBolum) {
        this.baslik = baslik;
        this.posterUrl = posterUrl;
        this.puan = puan;
        this.kategori = kategori;
        this.toplamBolum = toplamBolum;

        this.kullaniciPuani = "";
        this.durum = "Planlanıyor";
        this.guncelBolum = 0;
        this.harcananVakit = 0;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) { this.baslik = baslik; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getPuan() { return puan; }
    public void setPuan(String puan) { this.puan = puan; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getKullaniciPuani() { return kullaniciPuani; }
    public void setKullaniciPuani(String kullaniciPuani) { this.kullaniciPuani = kullaniciPuani; }

    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }

    public int getGuncelBolum() { return guncelBolum; }
    public void setGuncelBolum(int guncelBolum) { this.guncelBolum = guncelBolum; }

    public int getToplamBolum() { return toplamBolum; }
    public void setToplamBolum(int toplamBolum) { this.toplamBolum = toplamBolum; }

    public int getHarcananVakit() { return harcananVakit; }
    public void setHarcananVakit(int harcananVakit) { this.harcananVakit = harcananVakit; }
}