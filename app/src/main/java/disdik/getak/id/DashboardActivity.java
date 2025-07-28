package disdik.getak.id;

import static disdik.getak.id.MainActivity.urlKoneksi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import disdik.getak.id.Library.ModuleNetwork;
import disdik.getak.id.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private TextView namaLengkap, email;
    private SharedPreferences sharedPreferences;
    private Button btnReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        btnReport = binding.getRoot().findViewById(R.id.btnTambah);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDashboard.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_dashboard);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
//                SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = loginPrefs.edit();
//                editor.clear();
//                editor.apply();

                startActivity(new Intent(this, AddReportsActivity.class));
                drawer.closeDrawer(GravityCompat.START);
//                finish();
                return true;
            } else {
                NavController navController1 = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController1);
                if (handled) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return handled;
            }
        });



        initialization();
        mainLogic();
    }

    private void initialization(){
        View headerView = binding.navView.getHeaderView(0);
        namaLengkap = headerView.findViewById(R.id.namaLengkap);
        email = headerView.findViewById(R.id.email);
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void mainLogic(){
        ModuleNetwork.getObjectWithToken(this, urlKoneksi + "api/getMyAccount/" + sharedPreferences.getString("uid", ""), sharedPreferences.getString("token", ""), response -> {
            try {
                if(response.getString("status").equals("success")){
                    namaLengkap.setText(response.getString("name"));
                    email.setText(response.getString("email"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, error -> Snackbar.make(findViewById(android.R.id.content), "Tidak ada tanggapan dari server. Silahkan coba lagi nanti...", Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> finishAffinity()).show());
    }
}