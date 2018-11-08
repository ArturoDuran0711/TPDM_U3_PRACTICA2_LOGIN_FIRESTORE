package mx.edu.ittepic.tpdm_u3_practica2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button entrar,inscribir;
    EditText id,pass;
    FirebaseAuth fba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entrar = findViewById(R.id.entrar);
        inscribir = findViewById(R.id.inscribirte);
        id = findViewById(R.id.correo);
        pass = findViewById(R.id.contraseña);

        fba= FirebaseAuth.getInstance();

        inscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().isEmpty() && pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"No hay datos en los campos",Toast.LENGTH_LONG).show();
                    return;
                }

                fba.createUserWithEmailAndPassword(id.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"EXITO! SE CREO EL USUARIO", Toast.LENGTH_LONG).show();
                            //se esta verificando el correo
                            fba.getCurrentUser().sendEmailVerification();
                        } else {
                            Toast.makeText(MainActivity.this,"ERROR! NO SE CREO EL USUARIO",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().isEmpty() && pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"No hay datos en los campos",Toast.LENGTH_LONG).show();
                    return;
                }
                fba.signInWithEmailAndPassword(id.getText().toString(),pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //SI SE AUTENTICO
                                    if(fba.getCurrentUser().isEmailVerified()){
                                        startActivity(new Intent(MainActivity.this,Main2Activity.class));
                                    }else {
                                        mensajeVerificar(fba.getCurrentUser());
                                    }
                                }else {
                                    Toast.makeText(MainActivity.this,"ERROR! VERIFIQUE USUARIO Y/O CONTRASEÑA",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
    private void mensajeVerificar(final FirebaseUser usuarioLogueado){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ERROR!")
                .setMessage("AL PARACER NO HAS VERIFICADO TU USUARIO\nDeseas que envie el correo de verificacion?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        usuarioLogueado.sendEmailVerification();
                        Toast.makeText(MainActivity.this,"SE REENVIO CORREO DE VERIFICACION",Toast.LENGTH_LONG).show();
                        fba.signOut();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fba.signOut();
                    }
                })
                .show();
    }
}
