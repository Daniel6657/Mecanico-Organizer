package com.example.mecanicoorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = null;
    private ProgressDialog progressDialog = null;
    private EditText email = null;
    private EditText password = null;
    private TextView signUp = null;
    private Button loginBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        signUp = findViewById(R.id.signup_textView);
        loginBtn = findViewById(R.id.login_button);

        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainPanel.class));
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailValue = email.getText().toString().trim().toLowerCase();
                String passwordValue = password.getText().toString().trim();

                if(TextUtils.isEmpty(emailValue)) {
                    email.setError("Uzupełnij to pole. Adres email jest niezbędny do zalogowania !");
                    return;
                }

                if(TextUtils.isEmpty(passwordValue)) {
                    password.setError("Uzupełnij to pole. Hasło jest niezbędne do zalogowania !");
                    return;
                }

                progressDialog.setMessage("Logowanie rozpoczęte. Przygotowujemy wszytsko czego potrzebujesz.");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainPanel.class));
                            Toast.makeText(getApplicationContext(), "Udało się! Koniec z formalnościami, teraz możesz przejść do zarządzania swoim garażem.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Nasi najlepsi Mecanicos donoszą, że podany email i hasło nie pasują do twojego garażu",
                                    Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });
    }
}