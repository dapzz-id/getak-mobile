package disdik.getak.id.ui.reports;

import static disdik.getak.id.MainActivity.urlKoneksi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import disdik.getak.id.AddReportsActivity;
import disdik.getak.id.R;
import disdik.getak.id.databinding.FragmentReportsBinding;

public class ReportFragment extends Fragment {

    private FragmentReportsBinding binding;
    private ListView listview1;
    private ArrayList<HashMap<String, String>> listData;
    private SimpleAdapter adapter;
    private String url = urlKoneksi + "/api/laporan"; // Ganti sesuai endpoint kamu

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        binding.btnTambah.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddReportsActivity.class);
            startActivity(intent);
        });
        View root = binding.getRoot();

        listview1 = binding.listView;
        listData = new ArrayList<>();

        adapter = new SimpleAdapter(
                getContext(),
                listData,
                R.layout.custom_data,
                new String[]{"id", "judul", "bentuk_kekerasan", "nama_korban", "nama_terlapor",  "rekomendasi"},
                new int[]{R.id.tvIdLaporan, R.id.tvJudul, R.id.tvKekerasan, R.id.tvKorban, R.id.tvTerlapor, R.id.tvRekomendasi}
        );

        listview1.setAdapter(adapter);
        loadDataFromApi();

        return root;
    }

    private void loadDataFromApi() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", obj.getString("id_laporan"));
                            map.put("judul", obj.getString("judul"));
                            map.put("bentuk", obj.getString("bentuk"));
                            map.put("korban", obj.getString("korban"));
                            map.put("terlapor", obj.getString("terlapor"));
                            map.put("rekomendasi", obj.getString("rekomendasi"));

                            listData.add(map);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
