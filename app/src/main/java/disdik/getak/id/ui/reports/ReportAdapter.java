package disdik.getak.id.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import disdik.getak.id.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final ArrayList<ReportItem> dummyList;

    public ReportAdapter() {
        dummyList = new ArrayList<>();

        // Dummy data seperti di XML kamu
        dummyList.add(new ReportItem("Item 0", "Judul A", "Fisik", "Andi", "Budi", "TPPKSP 1", "Konseling"));
        dummyList.add(new ReportItem("Item 1", "Judul B", "Verbal", "Sari", "Joko", "TPPKSP 2", "Pendampingan"));
        dummyList.add(new ReportItem("Item 2", "Judul C", "Psikis", "Nina", "Doni", "TPPKSP 3", "Mediasi"));
        dummyList.add(new ReportItem("Item 3", "Judul D", "Online", "Tina", "Faisal", "TPPKSP 4", "Pemulihan"));
        dummyList.add(new ReportItem("Item 4", "Judul E", "Finansial", "Rani", "Bayu", "TPPKSP 5", "Dampingi Psikolog"));
        dummyList.add(new ReportItem("Item 5", "Judul F", "Sosial", "Ika", "Rio", "TPPKSP 6", "Rujukan Hukum"));
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reports, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = dummyList.get(position);
        holder.id.setText(item.id);
        holder.judul.setText(item.judul);
        holder.bentuk.setText(item.bentuk);
        holder.korban.setText(item.korban);
        holder.terlapor.setText(item.terlapor);
        holder.rekomendasi.setText(item.rekomendasi);
    }

    @Override
    public int getItemCount() {
        return dummyList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView id, judul, bentuk, korban, terlapor, tppksp, rekomendasi;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tvIdLaporan);
            judul = itemView.findViewById(R.id.tvJudul);
            bentuk = itemView.findViewById(R.id.tvKekerasan);
            korban = itemView.findViewById(R.id.tvKorban);
            terlapor = itemView.findViewById(R.id.tvTerlapor);
            rekomendasi = itemView.findViewById(R.id.tvRekomendasi);
        }
    }

    // Model lokal di dalam adapter
    public static class ReportItem {
        String id, judul, bentuk, korban, terlapor, rekomendasi;

        public ReportItem(String id, String judul, String bentuk, String korban,
                          String terlapor, String tppksp, String rekomendasi) {
            this.id = id;
            this.judul = judul;
            this.bentuk = bentuk;
            this.korban = korban;
            this.terlapor = terlapor;
            this.rekomendasi = rekomendasi;
        }
    }
}
