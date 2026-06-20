package com.example.mobiluygulamafinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class IcerikAdapter extends RecyclerView.Adapter<IcerikAdapter.IcerikViewHolder> {

    private Context context;
    private List<IcerikModel> icerikList;
    private boolean isProfileMode;

    public IcerikAdapter(Context context, List<IcerikModel> icerikList, boolean isProfileMode) {
        this.context = context;
        this.icerikList = icerikList;
        this.isProfileMode = isProfileMode;
    }

    public void filtrele(List<IcerikModel> filtrelenmisListe) {
        this.icerikList = filtrelenmisListe;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IcerikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new IcerikViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IcerikViewHolder holder, int position) {
        IcerikModel currentItem = icerikList.get(position);

        holder.tvTitle.setText(currentItem.getBaslik());

        if (isProfileMode) {
            holder.tvRating.setText("⭐ " + currentItem.getKullaniciPuani() + " (Puanın)");

            holder.tvDurum.setVisibility(View.VISIBLE);
            holder.tvIlerleme.setVisibility(View.VISIBLE);

            holder.tvDurum.setText("Durum: " + currentItem.getDurum());

            String kategori = currentItem.getKategori();
            if (kategori != null) {
                if (kategori.equals("Kitap")) {
                    holder.tvIlerleme.setText("İlerleme: " + currentItem.getGuncelBolum() + " Sayfa");
                } else if (kategori.equals("Oyun")) {
                    holder.tvIlerleme.setText("İlerleme: " + currentItem.getGuncelBolum() + " Saat");
                } else {
                    holder.tvIlerleme.setText("İlerleme: " + currentItem.getGuncelBolum() + " / " + currentItem.getToplamBolum() + " Bölüm");
                }
            }
        } else {
            holder.tvRating.setText("⭐ " + currentItem.getPuan());

            holder.tvDurum.setVisibility(View.GONE);
            holder.tvIlerleme.setVisibility(View.GONE);
        }

        Glide.with(context).load(currentItem.getPosterUrl()).placeholder(R.mipmap.ic_launcher).into(holder.ivPoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProfileMode) {
                    profilIslemDialog(currentItem);
                } else {
                    kaydetDialog(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return icerikList.size();
    }

    private void kaydetDialog(IcerikModel secilenIcerik) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_kaydet, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        android.widget.Spinner spinnerDurum = dialogView.findViewById(R.id.spinnerDurum);
        EditText etPuan = dialogView.findViewById(R.id.etDialogPuan);
        EditText etIlerleme = dialogView.findViewById(R.id.etDialogIlerleme);
        TextView tvIlerlemeBaslik = dialogView.findViewById(R.id.tvIlerlemeBaslik);

        tvTitle.setText(secilenIcerik.getBaslik());

        if(secilenIcerik.getKategori().equals("Kitap")) tvIlerlemeBaslik.setText("Kaçıncı Sayfadasın?");
        else if (secilenIcerik.getKategori().equals("Oyun")) tvIlerlemeBaslik.setText("Kaçıncı Saattesin/Seviyedesin?");
        else tvIlerlemeBaslik.setText("Kaçıncı Bölümdesin? (Toplam: " + secilenIcerik.getToplamBolum() + ")");

        String[] durumlar = {"Planlanıyor", "Devam Ediyor", "Bitti", "Yarıda Bırakıldı"};
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, durumlar);
        spinnerDurum.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String girilenPuan = etPuan.getText().toString().trim();
            String girilenIlerleme = etIlerleme.getText().toString().trim();
            String secilenDurum = spinnerDurum.getSelectedItem().toString();

            if (!girilenPuan.isEmpty()) {
                secilenIcerik.setKullaniciPuani(girilenPuan);
            }
            if (!girilenIlerleme.isEmpty()) {
                secilenIcerik.setGuncelBolum(Integer.parseInt(girilenIlerleme));
            }
            secilenIcerik.setDurum(secilenDurum);

            firebaseKaydet(secilenIcerik);
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void profilIslemDialog(IcerikModel secilenIcerik) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("İşlem Seçin");
        builder.setMessage(secilenIcerik.getBaslik() + " için ne yapmak istersin?");

        builder.setPositiveButton("Puan Değiştir", (dialog, which) -> {
            puanGuncelleDialog(secilenIcerik);
        });

        builder.setNegativeButton("Listeden Sil", (dialog, which) -> {
            firebaseSil(secilenIcerik.getKey());
        });

        builder.setNeutralButton("İptal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void puanGuncelleDialog(IcerikModel secilenIcerik) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Puanı Güncelle");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(secilenIcerik.getKullaniciPuani());
        builder.setView(input);

        builder.setPositiveButton("Güncelle", (dialog, which) -> {
            String yeniPuan = input.getText().toString().trim();
            firebaseGuncelle(secilenIcerik.getKey(), yeniPuan);
        });
        builder.show();
    }

    private DatabaseReference getDbRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseDatabase.getInstance().getReference("Kullanicilar").child(uid).child("Listem");
    }

    private void firebaseKaydet(IcerikModel secilenIcerik) {
        DatabaseReference dbRef = getDbRef();
        String id = dbRef.push().getKey();
        dbRef.child(id).setValue(secilenIcerik).addOnCompleteListener(task -> {
            if (task.isSuccessful()) Toast.makeText(context, "Listene eklendi!", Toast.LENGTH_SHORT).show();
        });
    }

    private void firebaseSil(String key) {
        if (key != null) {
            getDbRef().child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) Toast.makeText(context, "İçerik Silindi!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void firebaseGuncelle(String key, String yeniPuan) {
        if (key != null) {
            getDbRef().child(key).child("kullaniciPuani").setValue(yeniPuan).addOnCompleteListener(task -> {
                if (task.isSuccessful()) Toast.makeText(context, "Puan Güncellendi!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public class IcerikViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvRating, tvDurum, tvIlerleme;

        public IcerikViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDurum = itemView.findViewById(R.id.tvDurum);
            tvIlerleme = itemView.findViewById(R.id.tvIlerleme);
        }
    }
}