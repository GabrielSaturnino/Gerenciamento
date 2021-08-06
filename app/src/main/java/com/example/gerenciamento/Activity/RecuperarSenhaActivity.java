package com.example.gerenciamento.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gerenciamento.R;
import com.example.gerenciamento.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private Button recuperarSenha;
    private EditText recuperarEmail;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        recuperarSenha = findViewById(R.id.buttonRecuperarSenha);
        recuperarEmail = findViewById(R.id.editEmailRecuperarSenha);

        recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = recuperarEmail.getText().toString();
                if( email.isEmpty() ){  // verifica email
                    Toast.makeText(RecuperarSenhaActivity.this, "Email inválido ou inexistente!", Toast.LENGTH_LONG).show();
                }else{
                    autenticacao.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), "Uma mensagem foi enviada para seu e-mail com um link para redefinir sua senha.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getBaseContext(), "Erro ao ao enviar o e-mail",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}