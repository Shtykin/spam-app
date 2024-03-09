package ru.shtykin.antispam.presentation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

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
                } else {
                    Toast.makeText(MainActivity.this, "Разрешение уже предоставлено", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.buttonRequestDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDefaultPermissions()) {
                    Toast.makeText(MainActivity.this, "Приложение уже установлено по умолчанию", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.buttonRequestContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkContactsPermissions()) {
                    Toast.makeText(MainActivity.this, "Доступ уже предоставлен", Toast.LENGTH_LONG).show();
                }
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

    private void requestContactsPermissions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Доступ к контактам, звонкам и журналу вызовов");
        builder.setMessage("Предоставьте E-Defender все необходимые разрешения в настройках");
        builder.setPositiveButton("Открыть настройки", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
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

    boolean checkDefaultPermissions() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) return true;

        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, RC_DEFAULT_APP);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_MULTIPLE_PERMISSIONS) {
            boolean badResult = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    badResult = true;
                    break;
                }
            }
            if (badResult) requestContactsPermissions();
        }

    }

    private  boolean checkContactsPermissions() {
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        int permissionReadCallLog = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);
        int permissionReadContacts = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionReadCallLog != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),RC_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private static final int RC_DEFAULT_APP = 1001;
    private static final int RC_MULTIPLE_PERMISSIONS = 1002;
}
