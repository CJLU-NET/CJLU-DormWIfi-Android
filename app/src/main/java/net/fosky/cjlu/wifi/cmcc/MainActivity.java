package net.fosky.cjlu.wifi.cmcc;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.fosky.cjlu.wifi.cmcc.utils.CMCC;
import net.fosky.cjlu.wifi.cmcc.utils.Intranet;
import net.fosky.cjlu.wifi.cmcc.utils.Log;
import net.fosky.cjlu.wifi.cmcc.utils.Wifi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        askForPermissions();

        TextView net_user = findViewById(R.id.net_account);
        TextView net_pass = findViewById(R.id.net_password);
        TextView cmcc_user = findViewById(R.id.wlan_phone);
        TextView cmcc_pass = findViewById(R.id.wlan_password);

        Log.create(findViewById(R.id.debug_text));

        Button button = findViewById(R.id.btn_login);

        SharedPreferences mContextSp = this.getSharedPreferences( "config", Context.MODE_PRIVATE );
        net_user.setText(mContextSp.getString("net_user", ""));
        net_pass.setText(mContextSp.getString("net_pass", ""));
        cmcc_user.setText(mContextSp.getString("cmcc_user", ""));
        cmcc_pass.setText(mContextSp.getString("cmcc_pass", ""));

        button.setOnClickListener(view -> {

            button.setEnabled(false);
            button.setText("登录中");

            String n_user = net_user.getText().toString();
            String n_pass = net_pass.getText().toString();
            String c_user = cmcc_user.getText().toString();
            String c_pass = cmcc_pass.getText().toString();

            SharedPreferences.Editor editor = mContextSp.edit();
            editor.putString( "net_user", n_user);
            editor.putString( "net_pass", n_pass);
            editor.putString( "cmcc_user", c_user);
            editor.putString( "cmcc_pass", c_pass);
            editor.apply();

            // Toast.makeText(MainActivity.this, Wifi.testPage(), Toast.LENGTH_SHORT).show();

            if (!Wifi.isConnected(context)) {
                button.setEnabled(true);
                button.setText("登录");
                Toast.makeText(MainActivity.this,"未连接至WIFI（CMCC-EDU）", Toast.LENGTH_SHORT).show();
            } else if (!Wifi.isPortal()) {
                button.setEnabled(true);
                button.setText("登录");
                Toast.makeText(MainActivity.this,"当前Wifi无需认证", Toast.LENGTH_SHORT).show();
            } else if (!Intranet.Login(n_user, n_pass)) {
                Toast.makeText(MainActivity.this,"登录失败", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
                button.setText("登录");
            } else if (!CMCC.Login(c_user, c_pass)) {
                // Toast.makeText(MainActivity.this, Wifi.getPortalPage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,"内网登录成功，但移动WLAN尝试登录失败，请等待一会后重试登录", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
                button.setText("登录");
            } else {
                Toast.makeText(MainActivity.this,"登录成功，APP稍后自动退出", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    finish();
                    System.exit(0);
                }, 3000);
            }
        });
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //多个权限一起申请
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);

        if (requestCode == 1) {
            if (grantResult.length > 0){
                if (!(grantResult[0] == PackageManager.PERMISSION_GRANTED)) Toast.makeText(getApplicationContext(), "你拒绝了权限的授权，无法正常使用APP", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "你拒绝了权限的授权，无法正常使用APP", Toast.LENGTH_SHORT).show();
            }
        }
    }

}