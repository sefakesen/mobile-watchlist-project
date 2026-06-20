package com.example.mobiluygulamafinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvDiscover;
    private IcerikAdapter adapter;
    private List<IcerikModel> icerikListesi;
    private RequestQueue requestQueue;
    private String aktifKategori = "Anime";
    private int guncelSayfa = 1;
    private boolean yukleniyorMu = false;
    private String sonAranan = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvDiscover = view.findViewById(R.id.rvDiscover);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        EditText etSearch = view.findViewById(R.id.etSearch);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);

        rvDiscover.setLayoutManager(new GridLayoutManager(getContext(), 2));
        icerikListesi = new ArrayList<>();
        adapter = new IcerikAdapter(getContext(), icerikListesi, false);
        rvDiscover.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(requireContext());

        tabLayout.addTab(tabLayout.newTab().setText("Anime"));
        tabLayout.addTab(tabLayout.newTab().setText("Film & Dizi"));
        tabLayout.addTab(tabLayout.newTab().setText("Oyun"));
        tabLayout.addTab(tabLayout.newTab().setText("Kitap"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                aktifKategori = tab.getText().toString();
                etSearch.setText("");
                sonAranan = "";
                ilkSayfayiGetir();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnSearch.setOnClickListener(v -> {
            sonAranan = etSearch.getText().toString().trim();
            ilkSayfayiGetir();
        });

        rvDiscover.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !yukleniyorMu) {
                    guncelSayfa++;
                    verileriGetir();
                }
            }
        });

        ilkSayfayiGetir();

        return view;
    }

    private void ilkSayfayiGetir() {
        guncelSayfa = 1;
        icerikListesi.clear();
        adapter.notifyDataSetChanged();
        verileriGetir();
    }

    private void verileriGetir() {
        yukleniyorMu = true;
        switch (aktifKategori) {
            case "Anime": animeleriGetir(); break;
            case "Film & Dizi": filmleriGetir(); break;
            case "Oyun": oyunlariGetir(); break;
            case "Kitap": kitaplariGetir(); break;
        }
    }

    private void animeleriGetir() {
        String aramaMetni = sonAranan.replace(" ", "%20");
        String url = sonAranan.isEmpty()
                ? "https://api.jikan.moe/v4/top/anime?page=" + guncelSayfa
                : "https://api.jikan.moe/v4/anime?q=" + aramaMetni + "&page=" + guncelSayfa;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray dataArray = response.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String baslik = obj.getString("title");
                    String puan = obj.has("score") && !obj.isNull("score") ? String.valueOf(obj.getDouble("score")) : "N/A";
                    String posterUrl = obj.getJSONObject("images").getJSONObject("jpg").getString("image_url");
                    int bolumSayisi = obj.has("episodes") && !obj.isNull("episodes") ? obj.getInt("episodes") : 0;
                    icerikListesi.add(new IcerikModel(baslik, posterUrl, puan, "Anime", bolumSayisi));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) { e.printStackTrace(); }
            yukleniyorMu = false; // Kilit açıldı
        }, error -> yukleniyorMu = false);
        requestQueue.add(request);
    }

    private void oyunlariGetir() {
        String apiKey = "cba9da8dbd9540f28eff3ca797e51ab7";
        String aramaMetni = sonAranan.replace(" ", "%20");
        String url = sonAranan.isEmpty()
                ? "https://api.rawg.io/api/games?key=" + apiKey + "&ordering=-rating&page_size=20&page=" + guncelSayfa
                : "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + aramaMetni + "&page=" + guncelSayfa;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray dataArray = response.getJSONArray("results");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String baslik = obj.getString("name");
                    String puan = String.valueOf(obj.getDouble("rating"));
                    String posterUrl = obj.has("background_image") && !obj.isNull("background_image") ? obj.getString("background_image") : "";
                    icerikListesi.add(new IcerikModel(baslik, posterUrl, puan, "Oyun", 1));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) { e.printStackTrace(); }
            yukleniyorMu = false;
        }, error -> yukleniyorMu = false);
        requestQueue.add(request);
    }

    private void filmleriGetir() {
        String apiKey = "8f875ba8139f259193b758bf666ded77";
        String aramaMetni = sonAranan.replace(" ", "%20");
        String url = sonAranan.isEmpty()
                ? "https://api.themoviedb.org/3/trending/all/day?api_key=" + apiKey + "&language=tr-TR&page=" + guncelSayfa
                : "https://api.themoviedb.org/3/search/multi?api_key=" + apiKey + "&language=tr-TR&query=" + aramaMetni + "&page=" + guncelSayfa;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray dataArray = response.getJSONArray("results");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String baslik = obj.has("title") ? obj.getString("title") : obj.getString("name");
                    String puan = obj.has("vote_average") ? String.format("%.1f", obj.getDouble("vote_average")) : "N/A";
                    String posterUrl = obj.has("poster_path") && !obj.isNull("poster_path") ? "https://image.tmdb.org/t/p/w500" + obj.getString("poster_path") : "";
                    icerikListesi.add(new IcerikModel(baslik, posterUrl, puan, "Film/Dizi", 1));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) { e.printStackTrace(); }
            yukleniyorMu = false;
        }, error -> yukleniyorMu = false);
        requestQueue.add(request);
    }

    private void kitaplariGetir() {
        int startIndex = (guncelSayfa - 1) * 20;

        String aramaMetni = sonAranan.replace(" ", "+");

        String url;
        if (sonAranan.isEmpty()) {
            url = "https://www.googleapis.com/books/v1/volumes?q=a&orderBy=relevance&printType=books&maxResults=20&startIndex=" + startIndex;
        } else {
            url = "https://www.googleapis.com/books/v1/volumes?q=" + aramaMetni + "&printType=books&maxResults=20&startIndex=" + startIndex;
        }

        Log.d("KITAP_API", "İstek atılıyor: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (response.has("items")) {
                    JSONArray dataArray = response.getJSONArray("items");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        JSONObject obj = item.getJSONObject("volumeInfo");

                        String baslik = obj.has("title") ? obj.getString("title") : "Bilinmeyen Kitap";
                        String puan = obj.has("averageRating") ? String.valueOf(obj.getDouble("averageRating")) : "N/A";

                        String posterUrl = "";
                        if (obj.has("imageLinks")) {
                            posterUrl = obj.getJSONObject("imageLinks").getString("thumbnail").replace("http:", "https:");
                        }

                        int sayfaSayisi = obj.has("pageCount") ? obj.getInt("pageCount") : 0;

                        icerikListesi.add(new IcerikModel(baslik, posterUrl, puan, "Kitap", sayfaSayisi));
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("KITAP_API", "Veriler başarıyla eklendi. Liste boyutu: " + icerikListesi.size());
                } else {
                    Log.d("KITAP_API", "API cevap verdi ama 'items' dizisi boş!");
                    if (guncelSayfa == 1) {
                        Toast.makeText(getContext(), "Kitap bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("KITAP_API", "JSON Ayrıştırma Hatası: " + e.getMessage());
                e.printStackTrace();
            }
            yukleniyorMu = false;
        }, error -> {
            Log.e("KITAP_API", "Volley Hatası: " + error.toString());
            yukleniyorMu = false;
            if (getContext() != null) {
                Toast.makeText(getContext(), "Bağlantı hatası!", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }
}