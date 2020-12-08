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

public class Registration extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = null;
    private ProgressDialog progressDialog = null;
    private EditText email = null;
    private EditText password = null;
    private TextView signIn = null;
    private Button registerBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        signIn = findViewById(R.id.signin_textView);
        registerBtn = findViewById(R.id.register_button);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailValue = email.getText().toString().trim().toLowerCase();
                String passwordValue = password.getText().toString().trim();

                if(TextUtils.isEmpty(emailValue)) {
                    email.setError("Uzupełnij to pole. Adres email jest niezbędny do rejestracji !");
                    return;
                }

                if(TextUtils.isEmpty(passwordValue)) {
                    password.setError("Uzupełnij to pole. Hasło jest niezbędne do rejestracji !");
                    return;
                }

                progressDialog.setMessage("Rejestracja rozpoczęta. Teraz wszystko w naszych rękach.");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainPanel.class));
                            Toast.makeText(getApplicationContext(), "Udało się! Teraz masz dostęp do mecanico organizera.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Checmy chronić twoje dane tak dobrze jak Ty chronisz swój garaż, więc hasło do twojego konta musi zawierać conajmniej 8 znaków, " +
                                            "a email zawierać poprawny format, tak by go nie zapomnieć",
                                    Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}