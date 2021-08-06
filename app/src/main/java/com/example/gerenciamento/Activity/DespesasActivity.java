package com.example.gerenciamento.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        // Preenche o campo Data com a data atual
        campoData.setText( DataCustom.dataAtual() );

        // Criando Mascaras
        SimpleMaskFormatter telefone = new SimpleMaskFormatter("NNN.NNN.NNN.NNN.NN");
        MaskTextWatcher mtw = new MaskTextWatcher(campoValor, telefone);
        campoValor.addTextChangedListener(mtw);

        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view){

        if( validarDadosDespesa() ){

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor( valorRecuperado );
            movimentacao.setCategoria( campoCategoria.getText().toString() );
            movimentacao.setDescricao( campoDescricao.getText().toString() );
            movimentacao.setData( data );
            movimentacao.setTipo( "d" );

            Double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa( despesaAtualizada );
            movimentacao.salvar( data );
            finish();
        }
    }

    public  Boolean validarDadosDespesa(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if( !textoValor.isEmpty() ){
            if( !textoData.isEmpty() ){
                if( !textoCategoria.isEmpty() ){
                    if( !textoDescricao.isEmpty() ){
                        return true;
                    }else{
                        Toast.makeText(DespesasActivity.this, "A descrição não foi preenchida!", Toast.LENGTH_LONG).show();
                        return  false;
                    }
                }else{
                    Toast.makeText(DespesasActivity.this, "A categoria não foi preenchida!", Toast.LENGTH_LONG).show();
                    return  false;
                }
            }else{
                Toast.makeText(DespesasActivity.this, "A data não foi preenchida!", Toast.LENGTH_LONG).show();
                return  false;
            }
        }else{
            Toast.makeText(DespesasActivity.this, "Valor não foi preenchido!", Toast.LENGTH_LONG).show();
            return  false;
        }
    }

    public  void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public  void atualizarDespesa(Double despesa){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child( "usuarios" ).child(idUsuario);

        usuarioRef.child("despesaTotal").setValue( despesa );
    }
}