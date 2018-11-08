package mx.edu.ittepic.tpdm_u3_practica2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    Button insertar,eliminar,buscar,actualizar,cerrar;
    EditText nombre,apellido,numerocontrol;
    String datos="";
    FirebaseAuth fba;
    FirebaseAuth.AuthStateListener asl;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        insertar = findViewById(R.id.insertar);
        eliminar = findViewById(R.id.eliminar);
        actualizar = findViewById(R.id.actualizar);
        cerrar = findViewById(R.id.cerrar);
        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        numerocontrol = findViewById(R.id.numerocontrol);
        buscar = findViewById(R.id.buscar);




        db = FirebaseFirestore.getInstance();

        fba = FirebaseAuth.getInstance();
        asl = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if(usuario==null || !usuario.isEmailVerified()){
                    //NO ESTA AUTENTICADO
                    startActivity(new Intent(Main2Activity.this,MainActivity.class));
                    finish();
                }
            }
        };

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numerocontrol.getText().toString().isEmpty() || nombre.getText().toString().isEmpty() || apellido.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this,"Todos los campos son obligatorios",Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String, Object> data = new HashMap<>();
                data.put("nombre",nombre.getText().toString());
                data.put("apellido", apellido.getText().toString());

                db.collection("persona").document(numerocontrol.getText().toString()).set(data);
                Toast.makeText( Main2Activity.this,"SE INSERTO CON EXITO",Toast.LENGTH_LONG).show();
                limpiardatos();
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numerocontrol.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this,"Campo Num Control vacio",Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentReference ref = db.collection("persona").document(numerocontrol.getText().toString());
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(Main2Activity.this,"NumControl: "+numerocontrol.getText().toString()+"\n"+document.getData(),Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Main2Activity.this,"No existe este alumno",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Main2Activity.this,"Error en la busqueda",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numerocontrol.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this,"Campo Num Control vacio",Toast.LENGTH_LONG).show();
                    return;
                }

                final DocumentReference ref = db.collection("persona").document(numerocontrol.getText().toString());

                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ref.delete();
                                Toast.makeText(Main2Activity.this,"Se elimino el alumno",Toast.LENGTH_LONG).show();
                                limpiardatos();
                            } else {
                                Toast.makeText(Main2Activity.this,"No existe este alumno",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Main2Activity.this,"Error en la busqueda",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numerocontrol.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this,"Campo Num Control vacio",Toast.LENGTH_LONG).show();
                    return;
                }

                final DocumentReference ref = db.collection("persona").document(numerocontrol.getText().toString());

                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("nombre",nombre.getText().toString());
                                data.put("apellido", apellido.getText().toString());

                                db.collection("persona").document(numerocontrol.getText().toString()).set(data);
                                Toast.makeText( Main2Activity.this,"Se actualizo el dato",Toast.LENGTH_LONG).show();
                                limpiardatos();
                            } else {
                                Toast.makeText(Main2Activity.this,"No existe este alumno",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Main2Activity.this,"Error en la busqueda",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fba.signOut();
            }
        });

    }

    private void limpiardatos(){
        numerocontrol.setText("");
        nombre.setText("");
        apellido.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        fba.addAuthStateListener(asl);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fba.removeAuthStateListener(asl);
    }

}
