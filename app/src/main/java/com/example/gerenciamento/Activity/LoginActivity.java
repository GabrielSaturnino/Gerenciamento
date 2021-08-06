package com.example.gerenciamento.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gerenciamento.R;
import com.example.gerenciamento.config.ConfiguracaoFirebase;
import com.example.gerenciamento.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private Button botaoAcessar, recuperar;
    private EditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;
    private TextView aqui;
    private Usuario usuario;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Login");

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = (String) campoEmail.getText().toString();
                String senha = (String) campoSenha.getText().toString();
                if( !email.isEmpty() ){ // verifica email
                    if( !senha.isEmpty() ){ // verifica senha

                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        logarUsuario();

                    }else{
                        Toast.makeText(LoginActivity.this, "Digite uma senha!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_LONG).show();
                }
            }
        });

        aqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();
            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaRecuperarSenha();
            }
        });
    }

    public void logarUsuario(){

        dialog = new SpotsDialog.Builder().setContext( this ).setMessage("Carregando").setCancelable( false ).build();
        dialog.show();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    abrirTelaPrincipal();
                }else{
                    dialog.dismiss();
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ){
                        excecao = "Usuário não está cadastrado!";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "Email e senha não correspondem a um usuário cadastrado!";
                    }catch ( Exception e ){
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcessar);
        aqui = findViewById(R.id.textViewAqui);
        recuperar = findViewById(R.id.buttonRecuperar);
    }

    public void abrirTelaCadastro(){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( intent );
    }

    public void abrirTelaRecuperarSenha(){
        Intent intent = new Intent(LoginActivity.this, RecuperarSenhaActivity.class);
        startActivity( intent );
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}