package com.example.gerenciamento.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gerenciamento.R;
import com.example.gerenciamento.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class InicialActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_inicial);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Esses são os Slides, eu utilizo fragments e é isso.

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_5)
                .canGoForward(false)
                .build());
    }

    public void comecar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    // Metodo onStart Vai verificar se tem usuaro logado
    // Se um usuario logado ela pula pra tela principal

    @Override
    protected  void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if( autenticacao.getCurrentUser() != null ){
            abrirTelaPrincipal();
        }
    }
}

