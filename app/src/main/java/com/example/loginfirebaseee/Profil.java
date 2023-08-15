package com.example.loginfirebaseee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profil extends AppCompatActivity {

    private TextInputEditText editTextEmailProfil, editTextPasswordProfil;
    private Button buttonUpdateProfilEmail,buttonUpdateProfilPassword, buttonDeleteProfil;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        editTextEmailProfil = findViewById(R.id.editTextEmailProfil);
        editTextPasswordProfil = findViewById(R.id.edittextPasswordProfil);
        buttonUpdateProfilEmail = findViewById(R.id.buttonUpdateEmail);
        buttonUpdateProfilPassword = findViewById(R.id.buttonUpdatePassword);
        buttonDeleteProfil = findViewById(R.id.buttonDeleteProfil);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        editTextEmailProfil.setText(firebaseUser.getEmail());

        buttonUpdateProfilEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update email
                String newEmail = String.valueOf(editTextEmailProfil.getText());
                if(newEmail != null && newEmail != ""){
                    firebaseUser.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"Berhasil update email",Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "email updated successfully.");
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Gagal update email",Toast.LENGTH_SHORT).show();
                                        Log.w("TAG", "email update failed.", task.getException());
                                    }
                                }
                            });
                } else{
                    Toast.makeText(getApplicationContext(),"Email harus diisi",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUpdateProfilPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update email
                String newPassword = String.valueOf(editTextPasswordProfil.getText());
                if(newPassword != null && newPassword != ""){
                    firebaseUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"Berhasil update password",Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "password updated successfully.");
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Gagal update password",Toast.LENGTH_SHORT).show();
                                        Log.w("TAG", "password update failed.", task.getException());
                                    }
                                }
                            });
                } else{
                    Toast.makeText(getApplicationContext(),"Password harus diisi",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDeleteProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
            private void deleteUserAccount() {
                if (firebaseUser != null) {
                    firebaseUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Pengguna berhasil dihapus. Lakukan tindakan setelah penghapusan.
                                        Toast.makeText(getApplicationContext(), "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "User account deleted successfully.");
                                        Intent hapus = new Intent(Profil.this, Login.class);
                                        hapus.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(hapus);
                                        finish();
                                    } else {
                                        // Gagal menghapus pengguna.
                                        Toast.makeText(getApplicationContext(), "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                                        Log.w("TAG", "Failed to delete user account.", task.getException());
                                    }
                                }
                            });
                } else {
                    // Pengguna tidak terautentikasi saat ini.
                    Toast.makeText(getApplicationContext(), "Tidak ada pengguna yang terautentikasi", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}