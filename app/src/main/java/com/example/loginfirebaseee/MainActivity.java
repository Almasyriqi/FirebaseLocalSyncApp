package com.example.loginfirebaseee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;
import java.util.Collections;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInput;
    private DatabaseReference databaseInputData;
    private ListView listViewData;
    private List<InputData> listData;
    FirebaseAuth auth;
    Button button, buttonProfil;
    TextView textView;
    FirebaseUser user;
    AppDatabase databaseLocal;
    DataDao dataDao;
    List<InputData> temporaryLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instansiasi object dan find view by id
        listViewData = findViewById(R.id.listView_Data);
        listData = new ArrayList<>();
        temporaryLocal = new ArrayList<>();
        editTextInput = findViewById(R.id.editTextInput);
        button = findViewById(R.id.logout);
        buttonProfil = findViewById(R.id.buttonProfil);
        textView = findViewById(R.id.user_details);

        //memanggil instansi dari firebase dan dimasukkan ke variabel Data
        databaseInputData = FirebaseDatabase.getInstance().getReference("Data");

        // database lokal room
        databaseLocal = AppDatabase.getInstance(this);
        dataDao = databaseLocal.dataDao();

        // mendapatkan data user yang login
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //periksa ada akun yang login atau tidak, jika ada tetap pada halaman mainactivity atau jika tidak ada kembali ke halaman login
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        // button untuk logout
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // button untuk ke halaman profil
        buttonProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profil = new Intent(getApplicationContext(), Profil.class);
                startActivity(profil);
            }
        });
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<InputData>> {
        @Override
        protected List<InputData> doInBackground(Void... voids) {
            return dataDao.getAllData();
        }

        @Override
        protected void onPostExecute(List<InputData> inputData) {
            listData.clear();
            listData = inputData;
            setDataListView();
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
            updateListView();
        }
    }

    // fungsi untuk menambah data
    public void buttonAdd(View view) {
        String data = editTextInput.getText().toString();

        if (!TextUtils.isEmpty(data)) {
            String id = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();
            InputData inputData = new InputData(id, data);

            // Menjalankan operasi database local di thread terpisah
            new InsertDataAsyncTask().execute(inputData);

            // memasukkan data ke firebase
            databaseInputData.child(id).setValue(inputData)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            editTextInput.setText("");
                            Toast.makeText(MainActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String error_message = e.getMessage();
                            Log.i("Error_data", error_message);
                            Toast.makeText(MainActivity.this, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Data wajib diisi", Toast.LENGTH_SHORT).show();
        }
    }

    // fungsi untuk reset data
    public void buttonReset(View view) {
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
            // alasan tidak dapat reset data tanpa internet karena akan menimbulkan selip pada data dan menyebabkan ketidaksinkronan data firebase dengan local
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
                }
            });
            Toast.makeText(MainActivity.this, "Sinkronasi data berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Sinkronasi data gagal dikarenakan tidak terdapat koneksi internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
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
                    setDataListView();
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

    public void setDataListView(){
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