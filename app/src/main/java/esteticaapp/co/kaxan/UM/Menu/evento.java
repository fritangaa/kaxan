package esteticaapp.co.kaxan.UM.Menu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import esteticaapp.co.kaxan.R;
import esteticaapp.co.kaxan.UM.menu;


public class evento extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    DatabaseReference databaseReference;

    private RadioGroup opcionesC;
    private DatePicker calendario;
    private TimePicker relojInicio, relojFin;
    private RelativeLayout nombreEvento;
    private RelativeLayout lugarEvento;
    private RelativeLayout diaEvento;
    private RelativeLayout horaEvento;
    private RelativeLayout confirmarEvento;

    private String fecha;
    private String horaIni, horaFinal;

    String[] mes = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    private View view;

    private GoogleMap mMap;
    private MapView mMapView;
    private static final int LOCATION_REQUEST_CODE = 1;


    //Widgets
    EditText textoNombre, textoLugar;
    TextView conNombre, conLugar, conDia, conHora;

    private String direccion;
    private List<Address> address;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    private String ubi;


    ImageButton agregarEvento, btnLugar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference= FirebaseDatabase.getInstance().getReference("/ZxdtUxxfUoRrTw9dxoHA6XLAHqJ2/um");

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();


        opcionesC = (RadioGroup) findViewById(R.id.radioOp);//Grupo de opciones para armar cita
        final RadioButton opcionI1 = (RadioButton) opcionesC.getChildAt(0);
        final RadioButton opcionI2 = (RadioButton) opcionesC.getChildAt(1);
        final RadioButton opcionI3 = (RadioButton) opcionesC.getChildAt(2);
        final RadioButton opcionI4 = (RadioButton) opcionesC.getChildAt(3);
        final RadioButton opcionI5 = (RadioButton) opcionesC.getChildAt(4);

        calendario = (DatePicker) findViewById(R.id.diaEvento);
        relojInicio = (TimePicker) findViewById(R.id.horainiEvento);//Reloj para el tiempo
        relojFin = (TimePicker) findViewById(R.id.horafinEvento);//Reloj para el tiempo

        nombreEvento = (RelativeLayout) findViewById(R.id.relativeNombre);
        lugarEvento = (RelativeLayout) findViewById(R.id.relativeLugar);
        diaEvento = (RelativeLayout) findViewById(R.id.relativeDia);
        horaEvento = (RelativeLayout) findViewById(R.id.relativeHora);
        confirmarEvento = (RelativeLayout) findViewById(R.id.relativeConfirmacion);

        //nombre
        textoNombre = findViewById(R.id.textoNombreEvento);
        //lugar
        btnLugar = findViewById(R.id.boton_lugar);
        textoLugar = findViewById(R.id.textoLugar);
        //dia
        fecha = (""+calendario.getYear()+"/"+calendario.getMonth()+"/"+calendario.getDayOfMonth());
        //hora
        relojFin.setIs24HourView(false);
        relojInicio.setIs24HourView(false);
        horaIni = (relojInicio.getHour()+":"+relojInicio.getMinute());
        horaFinal = (relojFin.getHour()+":"+relojFin.getMinute());
        //confirmacion
        conNombre = findViewById(R.id.textView85);
        conLugar =  findViewById(R.id.textView84);
        conDia =  findViewById(R.id.textView83);
        conHora =  findViewById(R.id.textView82);
        agregarEvento = findViewById(R.id.boton_aceptar);
        opcionI1.setChecked(true);



        textoNombre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Mostrar mensaje
                    opcionI2.setChecked(true);
                    nombreEvento.setVisibility(View.INVISIBLE);
                    lugarEvento.setVisibility(View.VISIBLE);
                    diaEvento.setVisibility(View.INVISIBLE);
                    horaEvento.setVisibility(View.INVISIBLE);
                    confirmarEvento.setVisibility(View.INVISIBLE);

                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    procesado = true;
                }
                return procesado;
            }
        });

        textoLugar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;
                direccion = textoLugar.getText().toString();

                if(direccion.equals("")){
                    toast("No hay dirección para buscar : (");
                }else{
                    toast("Buscando \""+direccion+"\"");
                    Geocoder geo = new Geocoder(getApplicationContext());
                    int maxResultados = 1;
                    List<Address> adress = null;
                    try {
                        adress = geo.getFromLocationName(direccion, maxResultados);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LatLng buscar = new LatLng(adress.get(0).getLatitude(), adress.get(0).getLongitude());
                    ubi="";
                    ubi=(""+adress.get(0).getLatitude()+","+adress.get(0).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(buscar).title("Marker UPV"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(buscar));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(buscar)      // Sets the center of the map to Mountain View
                            .zoom(17)
                            .bearing(90)// Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return procesado;
            }
        });

        btnLugar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                direccion = textoLugar.getText().toString();

                if(direccion.equals("")){
                    toast("No hay dirección para buscar : (");
                }else{
                    toast("Buscando \""+direccion+"\"");
                    Geocoder geo = new Geocoder(getApplicationContext());
                    int maxResultados = 1;
                    List<Address> adress = null;
                    try {
                        adress = geo.getFromLocationName(direccion, maxResultados);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LatLng buscar = new LatLng(adress.get(0).getLatitude(), adress.get(0).getLongitude());
                    ubi="";
                    ubi=(""+adress.get(0).getLatitude()+","+adress.get(0).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(buscar).title("Marker UPV"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(buscar));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(buscar)      // Sets the center of the map to Mountain View
                            .zoom(17)
                            .bearing(90)// Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
                // Ocultar el teclado
            }

        });

        agregarEvento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (!conNombre.getText().toString().isEmpty() && !fecha.isEmpty() && !horaIni.toString().isEmpty() && !horaFinal.toString().isEmpty() && !textoLugar.getText().toString().isEmpty()) {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(evento.this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setIcon(R.drawable.ic_alerta_notificacion);
                    dialogo1.setMessage("¿Quieres agregar el evento?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                            String nom = conNombre.getText().toString();
                            String dia = fecha;
                            String horaIn = horaIni;
                            String horaFin = horaFinal;
                            String lugar = textoLugar.getText().toString();
                            String idusu = databaseReference.push().getKey();

                            objEvento nuevoEvento=new objEvento(nom,dia,horaIn,horaFin,lugar,false,ubi);

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            databaseReference.child(firebaseAuth.getUid()).child("evento").child(idusu).setValue(nuevoEvento);

                            Toast.makeText(evento.this, "Evento registrado", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(evento.this, menu.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo1.show();

                } else {
                    Toast.makeText(evento.this, "Aun no completas el evento", Toast.LENGTH_SHORT).show();
                }



            }

        });


        mMapView = findViewById(R.id.mapaUbicacionEvento);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        opcionI1.setOnClickListener(this);
        opcionI2.setOnClickListener(this);
        opcionI3.setOnClickListener(this);
        opcionI4.setOnClickListener(this);
        opcionI5.setOnClickListener(this);




    }



    public void toast(String mensaje){
        Toast.makeText(evento.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            evento.this, R.raw.mapstyle));

            if (!success) {
                Log.e("ubicacion", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("ubicacion", "Can't find style. Error: ", e);
        }

        // Controles UI
        if (ContextCompat.checkSelfPermission(evento.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(evento.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        evento.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);




    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.rb_nombre:
                nombreEvento.setVisibility(View.VISIBLE);
                lugarEvento.setVisibility(View.INVISIBLE);
                diaEvento.setVisibility(View.INVISIBLE);
                horaEvento.setVisibility(View.INVISIBLE);
                confirmarEvento.setVisibility(View.INVISIBLE);
                break;

            case R.id.rb_lugar:
                nombreEvento.setVisibility(View.INVISIBLE);
                lugarEvento.setVisibility(View.VISIBLE);
                diaEvento.setVisibility(View.INVISIBLE);
                horaEvento.setVisibility(View.INVISIBLE);
                confirmarEvento.setVisibility(View.INVISIBLE);
                break;

            case R.id.rb_dia:
                nombreEvento.setVisibility(View.INVISIBLE);
                lugarEvento.setVisibility(View.INVISIBLE);
                diaEvento.setVisibility(View.VISIBLE);
                horaEvento.setVisibility(View.INVISIBLE);
                confirmarEvento.setVisibility(View.INVISIBLE);
                break;

            case R.id.rb_hora:
                nombreEvento.setVisibility(View.INVISIBLE);
                lugarEvento.setVisibility(View.INVISIBLE);
                diaEvento.setVisibility(View.INVISIBLE);
                horaEvento.setVisibility(View.VISIBLE);
                confirmarEvento.setVisibility(View.INVISIBLE);
                break;

            case R.id.rb_confirma:
                nombreEvento.setVisibility(View.INVISIBLE);
                lugarEvento.setVisibility(View.INVISIBLE);
                diaEvento.setVisibility(View.INVISIBLE);
                horaEvento.setVisibility(View.INVISIBLE);
                confirmarEvento.setVisibility(View.VISIBLE);

                conNombre.setText(textoNombre.getText().toString());
                conLugar.setText(textoLugar.getText().toString());
                conDia.setText(calendario.getDayOfMonth()+" de "+mes[calendario.getMonth()]);
                conHora.setText("De las "+ relojInicio.getCurrentHour()+":"+relojInicio.getCurrentMinute()+" a las "+ relojFin.getCurrentHour()+":"+relojFin.getCurrentMinute());

                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
