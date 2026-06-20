package com.example.mobiluygulamafinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView rvProfileList;
    private IcerikAdapter adapter;
    private List<IcerikModel> kayitliListem;
    private List<IcerikModel> anaListeYedek;

    private TextView tvStatTotal, tvStatCompleted, tvStatProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        rvProfileList = view.findViewById(R.id.rvProfileList);
        tvStatTotal = view.findViewById(R.id.tvStatTotal);
        tvStatCompleted = view.findViewById(R.id.tvStatCompleted);
        tvStatProgress = view.findViewById(R.id.tvStatProgress);

        com.google.android.material.chip.Chip chipFilterBitenler = view.findViewById(R.id.chipFilterBitenler);
        com.google.android.material.chip.Chip chipSortPuan = view.findViewById(R.id.chipSortPuan);

        rvProfileList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        kayitliListem = new ArrayList<>();
        anaListeYedek = new ArrayList<>();

        adapter = new IcerikAdapter(getContext(), kayitliListem, true);
        rvProfileList.setAdapter(adapter);

        chipFilterBitenler.setOnCheckedChangeListener((buttonView, isChecked) -> {
            kayitliListem.clear();
            if (isChecked) {
                for (IcerikModel model : anaListeYedek) {
                    if (model.getDurum() != null && model.getDurum().equals("Bitti")) {
                        kayitliListem.add(model);
                    }
                }
            } else {
                kayitliListem.addAll(anaListeYedek);
            }
            adapter.notifyDataSetChanged();
        });

        // --- 2. BUTON: PUANA GÖRE SIRALA FİLTRESİ ---
        chipSortPuan.setOnClickListener(v -> {
            java.util.Collections.sort(kayitliListem, new java.util.Comparator<IcerikModel>() {
                @Override
                public int compare(IcerikModel m1, IcerikModel m2) {
                    float puan1 = 0f;
                    float puan2 = 0f;

                    if (m1.getKullaniciPuani() != null && !m1.getKullaniciPuani().trim().isEmpty()) {
                        try { puan1 = Float.parseFloat(m1.getKullaniciPuani()); } catch (NumberFormatException ignored) {}
                    }
                    if (m2.getKullaniciPuani() != null && !m2.getKullaniciPuani().trim().isEmpty()) {
                        try { puan2 = Float.parseFloat(m2.getKullaniciPuani()); } catch (NumberFormatException ignored) {}
                    }

                    return Float.compare(puan2, puan1); // Yüksekten düşüğe
                }
            });
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Puanına göre sıralandı!", Toast.LENGTH_SHORT).show();
        });

        verileriGetir();

        return view;
    }

    private void verileriGetir() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(uid).child("Listem");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kayitliListem.clear();
                anaListeYedek.clear();

                int toplamIcerikSayisi = 0;
                int bitirilenSayisi = 0;
                int toplamIlerleme = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    IcerikModel model = data.getValue(IcerikModel.class);
                    if (model != null) {
                        model.setKey(data.getKey());

                        kayitliListem.add(model);
                        anaListeYedek.add(model);

                        toplamIcerikSayisi++;

                        if (model.getDurum() != null && model.getDurum().equals("Bitti")) {
                            bitirilenSayisi++;
                        }

                        toplamIlerleme += model.getGuncelBolum();
                    }
                }

                if (tvStatTotal != null) tvStatTotal.setText(String.valueOf(toplamIcerikSayisi));
                if (tvStatCompleted != null) tvStatCompleted.setText(String.valueOf(bitirilenSayisi));
                if (tvStatProgress != null) tvStatProgress.setText(String.valueOf(toplamIlerleme));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Hata: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}