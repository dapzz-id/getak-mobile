package disdik.getak.id;

import static disdik.getak.id.MainActivity.urlKoneksi;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputLayout layoutEmail;
    private TextInputEditText email;
    private MaterialButton btnSend, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
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

        mainLogic();
    }

    private void initialization() {
        layoutEmail = findViewById(R.id.layoutEmail);
        email = findViewById(R.id.email);
        btnSend = findViewById(R.id.btn_reset);
        btnBack = findViewById(R.id.btn_back);
    }

    private void mainLogic(){
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(email.getText())){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlKoneksi + "api/forgot-password",
                        response -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                if (jsonResponse.getString("status").equals("success")) {
                                    email.setText(null);
                                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(this::finish, 3000);
                                } else {
                                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
//                            Log.e("API_ERROR", "Error: " + error.toString());
//                            if (error.networkResponse != null) {
//                                Log.e("API_ERROR", "Status Code: " + error.networkResponse.statusCode);
//                                Log.e("API_ERROR", "Response Data: " + new String(error.networkResponse.data));
//                            }
                            Toast.makeText(this, "Silahkan coba lagi nanti", Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email.getText().toString());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } else {
                layoutEmail.setError("Email tidak boleh kosong");
            }
        });
    }
}