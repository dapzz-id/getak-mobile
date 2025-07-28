package disdik.getak.id;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import disdik.getak.id.Library.ModuleNetwork;

public class MainActivity extends AppCompatActivity {
    public static String urlKoneksi = "https://getak.syntx.id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        ImageView logoGetak = findViewById(R.id.logo_getak);

        android.view.animation.Animation animPop = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pop_scale);
        logoGetak.startAnimation(animPop);

        new android.os.Handler().postDelayed(() -> {
            SharedPreferences login = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = login.getBoolean("status", false);

            if (isLoggedIn && ModuleNetwork.isInternetAvailable(getApplicationContext())) {
                ModuleNetwork.getObjectWithToken(this, urlKoneksi + "api/validation-login", login.getString("token", ""),
                        response -> {
                            try {
                                if(response.has("status") && response.getString("status").equals("success")){
                                    startActivity(new Intent(this, DashboardActivity.class));
                                    finish();
                                } else if(response.has("status") && response.getString("status").equals("failed")){
                                    Snackbar.make(findViewById(android.R.id.content), "Akun Anda sedang digunakan di perangkat lain, silahkan masuk kembali...", Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> {
                                        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = loginPrefs.edit();
                                        editor.clear();
                                        editor.apply();

                                        startActivity(new Intent(this, LoginActivity.class));
                                        finish();
                                    }).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> Snackbar.make(findViewById(android.R.id.content), error.toString(), Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> finishAffinity()).show()
                );
            } else if(ModuleNetwork.isInternetAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Tidak ada internet, silahkan coba lagi nanti!", Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> finishAffinity()).show();
            }
        }, 3500);
    }
}