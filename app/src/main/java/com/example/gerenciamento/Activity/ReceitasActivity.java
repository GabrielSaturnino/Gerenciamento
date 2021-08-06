package com.example.gerenciamento.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gerenciamento.R;
import com.example.gerenciamento.config.ConfiguracaoFirebase;
import com.example.gerenciamento.helper.Base64Custom;
import com.example.gerenciamento.helper.DataCustom;
import com.example.gerenciamento.model.Movimentacao;
import com.example.gerenciamento.model.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double saldoTotal;
    private Spinner categoria, spinnerCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.campoDataRe);
        campoCategoria = findViewById(R.id.campoCategoriaRe);
        campoDescricao = findViewById(R.id.campoDescricaoRe);
        campoValor = findViewById(R.id.editValorRe);


        // Preenche o campo Data com a data atual
        campoData.setText( DataCustom.dataAtual() );

        // Criando Mascaras
        SimpleMaskFormatter telefone = new SimpleMaskFormatter("NNN.NNN.NNN.NNN.NN");
        MaskTextWatcher mtw = new MaskTextWatcher(campoValor, telefone);
        campoValor.addTextChangedListener(mtw);


        recuperarReceitaTotal();

    }
    
    public void salvarReceita(View view){

        if( validarDadosReceita() ){

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor( valorRecuperado );
            movimentacao.setCategoria( campoCategoria.getText().toString() );
            movimentacao.setDescricao( campoDescricao.getText().toString() );
            movimentacao.setData( data );
            movimentacao.setTipo( "r");


            Double receitaAtualizada = saldoTotal + valorRecuperado;
            atualizarReceita( receitaAtualizada );
            movimentacao.salvar( data );
            finish();
        }
    }

    public  Boolean validarDadosReceita(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        String textoCat = categoria.getSelectedItem().toString();

        if( !textoValor.isEmpty() ){
            if( !textoData.isEmpty() ){
                if( !textoCategoria.isEmpty() ){
                    if( !textoDescricao.isEmpty() ){
                        if( !textoCat.isEmpty() ){
                            return true;
                        }else{
                            Toast.makeText(ReceitasActivity.this, "Selecione uma  categoria!", Toast.LENGTH_LONG).show();
                            return  false;
                        }
                    }else{
                        Toast.makeText(ReceitasActivity.this, "A descrição não foi preenchida!", Toast.LENGTH_LONG).show();
                        return  false;
                    }
                }else{
                    Toast.makeText(ReceitasActivity.this, "A categoria não foi preenchida!", Toast.LENGTH_LONG).show();
                    return  false;
                }
            }else{
                Toast.makeText(ReceitasActivity.this, "A data não foi preenchida!", Toast.LENGTH_LONG).show();
                return  false;
            }
        }else{
            Toast.makeText(ReceitasActivity.this, "Valor não foi preenchido!", Toast.LENGTH_LONG).show();
            return  false;
        }
    }

    public  void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                saldoTotal = usuario.getSaldoTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public  void atualizarReceita(Double receita){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child( "usuarios" ).child(idUsuario);
        usuarioRef.child("saldoTotal").setValue( receita );
    }
}