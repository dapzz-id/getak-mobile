package disdik.getak.id;

import static disdik.getak.id.MainActivity.urlKoneksi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import disdik.getak.id.Library.ModuleNetwork;

public class RegisterActivity extends AppCompatActivity {
    private TextView btnLogin;
    private TextInputLayout layoutEmail, layoutNIK, layoutSekolah, layoutNamaLengkap, layoutNoWhatsApp, layoutPassword;
    private TextInputEditText email, nik, namaLengkap, noWhatsApp, password;
    private AutoCompleteTextView sekolah;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(RegisterActivity.this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        ModuleNetwork.init(getApplicationContext());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        getWindow().setNavigationBarColor(Color.argb(220, 0, 0, 0));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        initialization();

        layoutEmail.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));
        layoutNIK.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));
        layoutSekolah.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));
        layoutNamaLengkap.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));
        layoutNoWhatsApp.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));
        layoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));

        if(ModuleNetwork.isInternetAvailable(getApplicationContext())){
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlKoneksi + "api/data-sekolah",
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                                List<String> sekolahList = new ArrayList<>();
                                sekolahList.add("Pilih Sekolah");

                                JSONArray dataArray = jsonResponse.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    sekolahList.add(dataArray.getJSONObject(i).getString("school_name"));
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sekolahList);
                                sekolah.setAdapter(adapter);
                            } else {
                                Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Terjadi kesalahan parsing.", Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Gagal mengambil data.", Toast.LENGTH_SHORT).show();
                    }
            );

            queue.add(stringRequest);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Tidak ada internet, silahkan coba lagi nanti!", Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> finishAffinity()).show();
        }

        mainLogic();
    }

    private void initialization(){
        btnLogin = findViewById(R.id.tv_login);
        btnRegister = findViewById(R.id.btn_register);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutNIK = findViewById(R.id.layoutNIK);
        layoutSekolah = findViewById(R.id.layoutSekolah);
        layoutNamaLengkap = findViewById(R.id.layoutNamaLengkap);
        layoutNoWhatsApp = findViewById(R.id.layoutNoWhatsApp);
        layoutPassword = findViewById(R.id.layoutPassword);
        email = findViewById(R.id.email);
        nik = findViewById(R.id.nik);
        namaLengkap = findViewById(R.id.namaLengkap);
        noWhatsApp = findViewById(R.id.noWhatsApp);
        password = findViewById(R.id.password);
        sekolah = findViewById(R.id.autoCompleteSekolah);
    }

    private void mainLogic(){
        btnRegister.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(namaLengkap.getText()) && !TextUtils.isEmpty(password.getText()) && !TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(nik.getText()) && !TextUtils.isEmpty(sekolah.getText()) && !sekolah.getText().toString().equals("Pilih Sekolah") && !TextUtils.isEmpty(noWhatsApp.getText())) {
                JSONObject postData = new JSONObject();

                try {
                    postData.put("nik", nik.getText().toString());
                    postData.put("email", email.getText().toString());
                    postData.put("name", namaLengkap.getText().toString());
                    postData.put("password", password.getText().toString());
                    postData.put("name_school", sekolah.getText().toString());
                    postData.put("phone_number", "+62" + (noWhatsApp.getText().toString().trim().startsWith("0") ? noWhatsApp.getText().toString().trim().substring(1) : noWhatsApp.getText().toString().trim()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ModuleNetwork.init(RegisterActivity.this);
                ModuleNetwork.postObject(RegisterActivity.this, urlKoneksi + "api/check-for-daftar-akun", postData,
                        response -> {
                            try {
                                if(response.getString("status").equals("success")){
                                    JSONObject includeObj = response.getJSONObject("include");
                                    JSONObject sendObj = new JSONObject();

                                    try {
                                        sendObj.put("nik", includeObj.getString("nik"));
                                        sendObj.put("email", includeObj.getString("email"));
                                        sendObj.put("name", includeObj.getString("name"));
                                        sendObj.put("type", includeObj.getString("type"));
                                        sendObj.put("school_id", includeObj.getString("school_id"));
                                        sendObj.put("phone_number", includeObj.getString("phone_number"));
                                        sendObj.put("password", includeObj.getString("password"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    ModuleNetwork.postObject(RegisterActivity.this, urlKoneksi + "api/register-account", sendObj,
                                            response1 -> {
                                                try {
                                                    if(response1.has("status")){
                                                        Toast.makeText(RegisterActivity.this, response1.getString("message"), Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                        finish();
                                                    }else if (response1.has("status") && response1.getString("status").equals("failed")){
                                                        Toast.makeText(RegisterActivity.this, response1.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            },
                                            error -> Toast.makeText(RegisterActivity.this, "Silahkan coba lagi nanti", Toast.LENGTH_SHORT).show()
                                    );
                                } else if (response.getString("status").equals("failed")) {
                                    Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> Toast.makeText(RegisterActivity.this, "Gagal kirim data: " + error.getMessage(), Toast.LENGTH_LONG).show()
                );
            }else{
                Toast.makeText(RegisterActivity.this, "Silahkan isi semua kolom yang dibutuhkan!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}