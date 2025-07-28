package disdik.getak.id;

import static disdik.getak.id.MainActivity.urlKoneksi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import disdik.getak.id.Library.ModuleNetwork;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText email, password;
    private MaterialButton btnLogin;
    private TextView btnRegister, btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        layoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#455A64")));

        mainLogic();
    }

    private void initialization(){
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.tv_register);
        btnForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void mainLogic(){
        btnLogin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText())) {
                JSONObject postData = new JSONObject();

                try {
                    postData.put("email", email.getText());
                    postData.put("password", password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ModuleNetwork.postObject(this, urlKoneksi + "api/login-with-sanctum", postData,
                        response -> {
                            String token;

                            try {
                                if(response.getString("status").equals("success")){
                                    token = response.getString("token");

                                    SharedPreferences sharedPreferences = this.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token", token);
                                    editor.putString("uid", response.getString("uid"));
                                    editor.putBoolean("status", true);
                                    editor.apply();

                                    startActivity(new Intent(this, DashboardActivity.class));
                                    finish();
                                } else if (response.getString("status").equals("failed")) {
                                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> Toast.makeText(this, "Silahkan coba lagi nanti", Toast.LENGTH_SHORT).show()
                );
            }else{
                Toast.makeText(this, "Silahkan isi semua kolom yang dibutuhkan!", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        btnForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));
    }
}