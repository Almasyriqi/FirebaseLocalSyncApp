package com.example.loginfirebaseee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInput;
    private DatabaseReference databaseInputData;
    private ListView listViewData;
    private List<InputData> listData;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    AppDatabase databaseLocal;
    DataDao dataDao;

    List<InputData> temporaryLocal;

    double latitude, longitude;
    FirebaseAuth auth;
    Button button, buttonProfil, buttonReset;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set property export excel
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        // find view by id
        listViewData = findViewById(R.id.listView_Data);
        editTextInput = findViewById(R.id.editTextInput);
        button = findViewById(R.id.logout);
        buttonProfil = findViewById(R.id.buttonProfil);
        buttonReset = findViewById(R.id.buttonReset);
        textView = findViewById(R.id.user_details);

        // instansiasi object
        listData = new ArrayList<>();
        temporaryLocal = new ArrayList<>();

        // instansiasi database lokal room
        databaseLocal = AppDatabase.getInstance(this);
        dataDao = databaseLocal.dataDao();

        //memanggil instansi dari firebase dan dimasukkan ke variabel Data
        databaseInputData = FirebaseDatabase.getInstance().getReference("Data");

        // get current user firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //periksa ada akun yang login atau tidak, jika ada tetap pada halaman mainactivity atau jika tidak ada kembali ke halaman login
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Welcome " + user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Konfirmasi Logout");
                builder.setMessage("Apakah Anda yakin ingin logout?");

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isInternetConnected()==true){
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(MainActivity.this, "Berhasil logout", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Gagal logout, tidak ada internet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Tutup dialog jika pengguna memilih "Batal"
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        buttonProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profil = new Intent(getApplicationContext(), Profil.class);
                startActivity(profil);
//                finish();
            }
        });

        // setup untuk mendapatkan data lokasi
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // Lakukan sesuatu dengan data latitude dan longitude di sini
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Meminta izin lokasi pada runtime jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Meminta pembaruan lokasi dari layanan GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Atau meminta pembaruan lokasi dari layanan jaringan (NETWORK_PROVIDER)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hentikan pembaruan lokasi saat aplikasi di-pause
        locationManager.removeUpdates(locationListener);
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<InputData>> {
        @Override
        protected List<InputData> doInBackground(Void... voids) {
            return dataDao.getAllData();
        }

        @Override
        protected void onPostExecute(List<InputData> inputData) {
            temporaryLocal.clear();
            temporaryLocal = inputData;
            setDataListView(temporaryLocal);
        }
    }

    private class InsertDataAsyncTask extends AsyncTask<InputData, Void, Void> {
        @Override
        protected Void doInBackground(InputData... inputData) {
            dataDao.insert(inputData[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Setelah data berhasil diinsert, kita bisa melakukan operasi lainnya
            editTextInput.setText("");
            if (isInternetConnected() == false){
                updateListView();
            }

        }
    }

    public void buttonAdd(View view) {
        String data = editTextInput.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getEmail();

        if (!TextUtils.isEmpty(data)){
            String id = UUID.randomUUID().toString();
            InputData inputData = new InputData(id,data,userId,latitude,longitude);

            new InsertDataAsyncTask().execute(inputData);

            databaseInputData.child(id).setValue(inputData)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            editTextInput.setText("");
                            Toast.makeText(MainActivity.this,"Data berhasil ditambahkan",Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            Toast.makeText(this,"Data wajib diisi",Toast.LENGTH_SHORT).show();
        }
    }

    public void resetDatabase(View view) {
        if (isInternetConnected() == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Reset Data");
            builder.setMessage("Apakah Anda yakin ingin mereset data? Semua data akan dihapus.");

            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Jalankan aksi reset data di sini
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Operasi database di sini
                            dataDao.deleteTable();
                        }
                    });
                    databaseInputData.removeValue();
                    Toast.makeText(MainActivity.this, "Data berhasil direset", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Tutup dialog jika pengguna memilih "Batal"
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // alasan tidak dapat reset data tanpa internet karena akan menyebabkan ketidaksinkronan data firebase dengan local
            Toast.makeText(MainActivity.this, "Tidak dapat reset data tanpa internet", Toast.LENGTH_LONG).show();
        }
    }

    // fungsi untuk sinkronasi data firebase dan lokal
    public void buttonSync(View view) {
        if (isInternetConnected() == true) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    // Operasi database di sini
                    temporaryLocal.clear();
                    temporaryLocal = dataDao.getAllData();
                    Log.i("TOTAL", String.valueOf(temporaryLocal.size()));
                    // sinkronasi firebase dengan room (room menjadi patokan)
                    for (InputData local : temporaryLocal) {
                        boolean found = false;
                        for (InputData fb : listData) {
                            if (local.getData_Id().equals(fb.getData_Id())) {
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            Log.i("SYNC", "ID : " + local.getData_Id() + " tidak ada pada firebase");
                            databaseInputData.child(local.getData_Id()).setValue(local);
                        }
                    }
                    // sinkronasi room dengan firebase (firebase menjadi patokan)
                    for(InputData fb : listData){
                        boolean found = false;
                        for(InputData local : temporaryLocal){
                            if(fb.getData_Id().equals(local.getData_Id())){
                                found = true;
                                break;
                            }
                        }
                        if(found == false){
                            Log.i("SYNC", "ID : " + fb.getData_Id() + " tidak ada pada room");
                            dataDao.insert(fb);
                        }
                    }
                }
            });
            Toast.makeText(MainActivity.this, "Sinkronasi data berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Sinkronasi data gagal dikarenakan tidak terdapat koneksi internet", Toast.LENGTH_LONG).show();
        }
    }

    // fungsi untuk export data ke excel
    public void buttonExport(View view){
        if(isInternetConnected() == true){
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 1);
                } else {
                    createXlFile();
                }
            } else {
                createXlFile();
            }
            Toast.makeText(MainActivity.this, "Berhasil export data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Tidak dapat export data tanpa internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void createXlFile() {
        if (listData.size() > 0){
            Workbook wb = new HSSFWorkbook();
            Cell cell = null;
            Sheet sheet = null;
            sheet = wb.createSheet("Data");
            Row row = sheet.createRow(0);

            cell = row.createCell(0);
            cell.setCellValue("ID");

            cell = row.createCell(1);
            cell.setCellValue("Panjang (m)");

            cell = row.createCell(2);
            cell.setCellValue("User");

            cell = row.createCell(3);
            cell.setCellValue("Latitude");

            cell = row.createCell(4);
            cell.setCellValue("Longitude");

            cell = row.createCell(5);
            cell.setCellValue("Timestamp");

            //column width
            sheet.setColumnWidth(0, (50 * 200));
            sheet.setColumnWidth(1, (20 * 200));
            sheet.setColumnWidth(2, (30 * 200));
            sheet.setColumnWidth(3, (30 * 200));
            sheet.setColumnWidth(4, (30 * 200));
            sheet.setColumnWidth(5, (30 * 200));


            for (int i = 0; i < listData.size(); i++) {
                Row row1 = sheet.createRow(i + 1);

                cell = row1.createCell(0);
                cell.setCellValue(listData.get(i).getData_Id());

                cell = row1.createCell(1);
                cell.setCellValue((listData.get(i).getData_Data()));

                cell = row1.createCell(2);
                cell.setCellValue((listData.get(i).getUserId()));

                cell = row1.createCell(3);
                cell.setCellValue((listData.get(i).getLatitude()));

                cell = row1.createCell(4);
                cell.setCellValue((listData.get(i).getLongitude()));

                cell = row1.createCell(5);
                cell.setCellValue(listData.get(i).getFormattedTimestamp());

                sheet.setColumnWidth(0, (50 * 200));
                sheet.setColumnWidth(1, (20 * 200));
                sheet.setColumnWidth(2, (30 * 200));
                sheet.setColumnWidth(3, (30 * 200));
                sheet.setColumnWidth(4, (30 * 200));
                sheet.setColumnWidth(5, (30 * 200));

            }
            String fileName = String.valueOf(System.currentTimeMillis()) + "-export-data.xlsx";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String path = new File(downloadsDir, fileName).getAbsolutePath();

            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(path);
                wb.write(outputStream);
                // ShareViaEmail(file.getParentFile().getName(),file.getName());
                Toast.makeText(getApplicationContext(), "Excel Created in " + path, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Not OK", Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }
        } else {
            Toast.makeText(MainActivity.this, "Tidak dapat export data dikarenakan data kosong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (isInternetConnected() == true) {
            databaseInputData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listData.clear();
                    //perulangan untuk menampilkan semua data dari firebase
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        InputData inputData = postSnapshot.getValue(InputData.class);
                        listData.add(inputData);
                    }
                    //menampilkan list yang berisi data dari firebase ke dalam listview
                    setDataListView(listData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("DB_ERR", error.getMessage());
                }
            });
        } else {
            new LoadDataAsyncTask().execute();
        }
    }

    public void setDataListView(List<InputData> listData){
        Collections.sort(listData, new InputDataTimestampComparator());
        listview_inputdata inputDataList_Adapter = new listview_inputdata(MainActivity.this, listData);
        listViewData.setAdapter(inputDataList_Adapter);
    }

    public void updateListView() {
        new LoadDataAsyncTask().execute();
    }

    // Fungsi untuk memeriksa koneksi internet
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}