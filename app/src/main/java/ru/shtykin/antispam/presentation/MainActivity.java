package ru.shtykin.antispam.presentation;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;

import androidx.navigation.ui.AppBarConfiguration;

import ru.shtykin.antispam.FloatingWindowApp;
import ru.shtykin.antispam.R;
import ru.shtykin.antispam.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Context context = this;
        if (isServiceRunning()) {
            stopService(new Intent(context, FloatingWindowApp.class));
        }

        binding.buttonPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    checkPermissions();
                }
                if (checkOverlayPermission()) {

                    Toast.makeText(MainActivity.this, "Все разрешения предоставлены", Toast.LENGTH_LONG).show();
                } else {
                    requestFloatingWindowPermission();
                }
            }
        });

//        setSupportActionBar(binding.toolbar);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    checkPermissions();
//                }
//            }
//        });
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE) ) {
            if (FloatingWindowApp.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestFloatingWindowPermission() {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setCancelable(true);
         builder.setTitle("Screen Overlay Permission Needed");
         builder.setMessage("Enable from settings");
         builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 Intent intent = new Intent(
                         Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                         Uri.parse("package:" + getPackageName())
                 );
                 startActivityForResult(intent, 1234);
             }
         });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted: " + 1001, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission NOT granted: " + 1001, Toast.LENGTH_SHORT).show();
            }

            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                Log.e("DEBUG1", "OK");
            } else {
                Log.e("DEBUG1", "Failed");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void checkPermissions() {

        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, REQUEST_ID);


    }
    private static final int REQUEST_ID = 1;
}
