package sg.edu.rp.c346.quiz_p09;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    EditText et;
    TextView tv;
    Button btnSave, btnRead, btnShow;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.et);
        btnSave = findViewById(R.id.btnSave);
        tv = findViewById(R.id.tv);
        btnRead = findViewById(R.id.btnRead);
        btnShow = findViewById(R.id.btnShow);

        FusedLocationProviderClient client;
        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        checkPermission();
        checkPermission1();

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(MainActivity.this, location -> {
            if (location != null) {
                et.setText(location.getLatitude() + ", " + location.getLongitude());
            }
        });

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/quiz";
        File folder = new File(folderLocation);
        if (folder.exists() == false){
            Log.d("FileRead/Write", "Don't Exist");
            folder.mkdir();
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("FileRead/Write", "Folder created");
            }
        }

        btnSave.setOnClickListener((v)->{
            try {
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/quiz";
                File targetFile = new File(folderLocation , "quiz.txt");
                FileWriter writer = new FileWriter(targetFile , false);
                writer.write(et.getText().toString());
                writer.flush();
                writer.close();
                Toast.makeText(MainActivity. this , "Added!", Toast. LENGTH_LONG ).show();
            } catch (Exception e){
                Toast.makeText(MainActivity. this , "Failed to write!", Toast. LENGTH_LONG ).show();
                e.printStackTrace();
            }
        });

        btnRead.setOnClickListener((v)->{
            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/quiz";
            File targetFile = new File(folderLocation , "quiz.txt");
            if(targetFile.exists() == true){
                String data = "";
                try{
                    FileReader reader = new FileReader(targetFile);
                    BufferedReader br = new BufferedReader(reader);
                    String line = br.readLine();
                    while(line!=null){
                        data += line + "\n";
                        line = br.readLine();
                    }
                    tv.setText(data);
                    br.close();
                    reader.close();
                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this , "Failed to read!", Toast. LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d("Content", data);
            }
        });

        btnShow.setOnClickListener((v)->{
            Intent i = new Intent(MainActivity.this, SecondActivity.class);
            i.putExtra("Point", et.getText().toString());
            startActivity(i);
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
    }

    private boolean checkPermission1(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return false;
        }
    }

}
