package esteticaapp.co.kaxan.UM.Menu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import esteticaapp.co.kaxan.R;

public class perfil extends Fragment {




    public static perfil newInstance(){
        return new perfil();
    }

    View view;
    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private ImageView imagenPerfil;
    private EditText nombrePerfil;
    private EditText edadPerfil;
    //private EditText contraPerfil;
    private Button guardarPerfil;

    public static TextView mTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_perfil, container, false);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZxdtUxxfUoRrTw9dxoHA6XLAHqJ2").child("um").child(firebaseAuth.getUid()).child("datos");


        imagenPerfil = (ImageView) view.findViewById(R.id.imgPUM);
        nombrePerfil = (EditText) view.findViewById(R.id.nombrePUM);
        edadPerfil = (EditText) view.findViewById(R.id.edadPUM);
        mTextView = (TextView) view.findViewById(R.id.contraPUM);
        guardarPerfil = (Button) view.findViewById(R.id.botonPUM);


        return view;
    }

}
