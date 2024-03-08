package ru.shtykin.antispam.presentation;

import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ru.shtykin.antispam.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRequestWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkOverlayPermission()) {
                    requestFloatingWindowPermission();
                }
            }
        });

        binding.buttonRequestDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });

    }

    private void requestFloatingWindowPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Получайте информацию о звонках");
        builder.setMessage("Разрешите E-Defender отображать всплывающие окна, чтобы видеть, кто вам звонит в момент вызова");
        builder.setPositiveButton("Открыть настройки", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestDefaultAppPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Приложение по умолчания для АОН и защиты от спама");
        builder.setMessage("Установите E-Defender приложением по умолчанию для АОН и защиты от спама");
        builder.setPositiveButton("Открыть настройки", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settingsIntent = new Intent();
                settingsIntent.setAction(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_DEFAULT_APP) {
            if (resultCode != android.app.Activity.RESULT_OK) {
                requestDefaultAppPermission();
            }
        }
    }

    void checkPermissions() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, RC_DEFAULT_APP);
    }

    private static final int RC_DEFAULT_APP = 1001;
}
