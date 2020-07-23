package com.discoveritech.sensorsetupprogram.Activities;

import android.os.Build;
import android.os.Bundle;
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.discoveritech.sensorsetupprogram.GeneralClasses.IconTreeItem;
import com.discoveritech.sensorsetupprogram.GeneralClasses.MyChildHolder;
import com.discoveritech.sensorsetupprogram.GeneralClasses.MyHolder;
import com.discoveritech.sensorsetupprogram.R;
import com.discoveritech.sensorsetupprogram.UDP.UDPBroadcast;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.Validator;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

;import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.TemporalAmount;


@RequiresApi(api = Build.VERSION_CODES.O)
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_all, btn_resume, btn_pause, btn_upadte, btn_shutdown, btn_clear, btn_send;
    EditText ssid, pass;
    String txt_ssid = "";
    String txt_pass = "";
    RelativeLayout v;
    UDPBroadcast udpBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
    }

    public void init() {
        v = findViewById(R.id.view);
        btn_all = (Button) findViewById(R.id.btn_allReports);
        btn_all.setOnClickListener(this);
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_resume.setOnClickListener(this);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_upadte = (Button) findViewById(R.id.btn_update);
        btn_upadte.setOnClickListener(this);
        btn_shutdown = (Button) findViewById(R.id.btn_shutdown);
        btn_shutdown.setOnClickListener(this);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        ssid = (EditText) findViewById(R.id.network_ssd);
        pass = (EditText) findViewById(R.id.network_pass);
        udpBroadcast = new UDPBroadcast();
        TreeNode root = TreeNode.root();

        IconTreeItem nodeItem1 = new IconTreeItem("Sensor 1", "16-12-2020 15:00:00");
        IconTreeItem nodeItem2 = new IconTreeItem("Sensor 2", "16-12-2020 15:00:00");
        IconTreeItem nodeItem3 = new IconTreeItem("Sensor 3", "16-12-2020 15:00:00");

        TreeNode child0 = new TreeNode(nodeItem1).setViewHolder(new MyHolder(DashboardActivity.this));
        TreeNode child1 = new TreeNode(nodeItem2).setViewHolder(new MyHolder(DashboardActivity.this));
        TreeNode child2 = new TreeNode(nodeItem3).setViewHolder(new MyHolder(DashboardActivity.this));

        child0.addChildren(new TreeNode(new IconTreeItem("ip", "192.168.0.162:5555")).setViewHolder(new MyChildHolder(DashboardActivity.this)));
        child1.addChildren(new TreeNode(new IconTreeItem("ip", "192.168.0.162:5555")).setViewHolder(new MyChildHolder(DashboardActivity.this)));
        child2.addChildren(new TreeNode(new IconTreeItem("ip", "192.168.0.162:5555")).setViewHolder(new MyChildHolder(DashboardActivity.this)));

        root.addChildren(child0, child1, child2);
        AndroidTreeView tView = new AndroidTreeView(DashboardActivity.this, root);
        v.addView(tView.getView());
//      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//       StrictMode.setThreadPolicy(policy);
        //   udpBroadcast.receiveBroadcst();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                udpBroadcast.receiveBroadcst();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        switch (v.getId()) {
            case R.id.btn_allReports:
                try {
                    jsonObject.put("command", "Report");
                    jsonObject.put("params", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put("['All'], " + jsonObject);
                udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Report', 'params': []}]"), DashboardActivity.this);
                break;
            case R.id.btn_resume:
                udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Resume_Posting', 'params': []}]"), DashboardActivity.this);
                break;
            case R.id.btn_pause:
                udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Pause_Posting', 'params': []}] "), DashboardActivity.this);
                break;
            case R.id.btn_update:
                udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Update_Check', 'params': []}] "), DashboardActivity.this);
                break;
            case R.id.btn_shutdown:
                udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Shutdown', 'params': []}]"), DashboardActivity.this);
                break;
            case R.id.btn_clear:
                Toast.makeText(DashboardActivity.this, "Will be handle later", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_send:
                if (validate()) {
                    udpBroadcast.sendBroadcast(encryptContent("[['All'], {'command': 'Set_Network', 'params': ['" + txt_ssid + "', '" + txt_pass + "']}"), DashboardActivity.this);
                }
                break;
        }

    }

    public boolean validate() {
        txt_ssid = ssid.getText().toString();
        txt_pass = pass.getText().toString();
        boolean valid = true;
        if (txt_ssid.isEmpty()) {
            Toast.makeText(DashboardActivity.this, R.string.ssid_message, Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (txt_pass.isEmpty()) {
            Toast.makeText(DashboardActivity.this, R.string.pass_message, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    public byte[] encryptContent(String Command) {
        final Key key = new Key("3t55GSk5qDRUif_v4MNQGLrkzaWv-TFOSJpqQWj9KKg=");
        final Token token = Token.generate(key, Command);

        final Validator<String> validator = new StringValidator() {
            public TemporalAmount getTimeToLive() {
                return Duration.ofHours(1);
            }
        };
        String payload = token.validateAndDecrypt(key, validator);
        byte[] encodedBytes = key.encrypt(token.toString().getBytes(),token.getInitializationVector());

//        String aString = new String(decodedBytesArray);
//
//       final Token newtoken = Token.fromString("gAAAAABfGGfaWnOrPF32a1aNVXUzFqdX-z0sDm8mexUdffIbHKJZOysHU19T__R5cWc_voMGxecxEXnXv6Rp_A3mtK3xAO9uiT5e32y_ahe9CaSWFFNYM1A=");
////
////        Log.d("Decrypted Token", "" + newtoken);
//       newtoken.validateAndDecrypt(key, validator);
//      Log.d("DecryptedCommand", "" +  newtoken.validateAndDecrypt(key, validator));

        return token.serialise().getBytes();
    }
}