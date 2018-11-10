package esteticaapp.co.kaxan.UA;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import esteticaapp.co.kaxan.R;
import esteticaapp.co.kaxan.UA.recyclerMiembros.AdapterRecycler;
import esteticaapp.co.kaxan.UA.recyclerMiembros.UMViewHolder;
import esteticaapp.co.kaxan.UM.menu;
import esteticaapp.co.kaxan.objUbicacion;

public class MapaFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView listaUM;

    FirebaseRecyclerAdapter<UM,UMViewHolder.ViewHolder> adapter;
    private DatabaseReference databaseReference;


    GoogleMap mgoogleMap;
    MapView mapView;
    View mview;

    DatabaseReference ref;

    UM ubicacionUM, nombreUM;

    ArrayList<UM> monitoredUsers= new ArrayList<>();
    ArrayList<UM> monitoredUsers2= new ArrayList<>();
    ArrayList<String> ids= new ArrayList<>();

    ArrayList<String> nombres= new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdapterRecycler mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public Activity mActivity;

    UM[] usuariosMonitoreados;


    public MapaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapaFragment newInstance(String param1, String param2) {
        MapaFragment fragment = new MapaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//
//        time time = new time();
//        time.execute();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_mapa, container, false);
        return mview;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) mview.findViewById(R.id.mapaMiembros);
        if (mapView != null) {

            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }

        listaUM = view.findViewById(R.id.recyclerMiembros);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaUM.setLayoutManager(linearLayoutManager);

        //final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = databaseReference.child("ZxdtUxxfUoRrTw9dxoHA6XLAHqJ2").child("um");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                monitoredUsers.clear();
                monitoredUsers2.clear();
                ids.clear();
                usuariosMonitoreados = new UM[Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()))];

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ubicacionUM = snapshot.child("ubicacion").getValue(UM.class);
                    nombreUM = snapshot.child("datos").getValue(UM.class);

                    monitoredUsers.add(ubicacionUM);
                    monitoredUsers2.add(nombreUM);
                    ids.add(snapshot.getKey());

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });


        adapter=new FirebaseRecyclerAdapter<UM, UMViewHolder.ViewHolder>(
                UM.class,
                R.layout.recycler_view_item,
                UMViewHolder.ViewHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(final UMViewHolder.ViewHolder viewHolder,
                                              UM model, final int position) {
                viewHolder.nombre.setText(monitoredUsers2.get(position).getNombre());
                viewHolder.bateria.setText(monitoredUsers.get(position).getBateria());
                viewHolder.senial.setText("Intensa");
                viewHolder.direccion.setText(setLocation(Double.parseDouble(monitoredUsers.get(position).getLatitud()),Double.parseDouble(monitoredUsers.get(position).getLongitud())));
                viewHolder.imgFoto.setImageResource(R.drawable.ic_persona);
                viewHolder.imgBateria.setImageResource(R.drawable.batteryfull);
                viewHolder.imgSenial.setImageResource(R.drawable.signal4);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(getContext(), viewHolder.mview);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.opciones_miembros);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.ver_en_mapa:
                                        mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(monitoredUsers.get(position).getLatitud()),Double.parseDouble(monitoredUsers.get(position).getLongitud())))
                                                .title("NOWHERE").snippet("HOLA"));

                                        CameraPosition liberty = CameraPosition.builder().target(new LatLng(Double.parseDouble(monitoredUsers.get(position).getLatitud()),Double.parseDouble(monitoredUsers.get(position).getLongitud())))
                                                .zoom(16).bearing(0).tilt(0).build();

                                        mgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));

                                        break;
                                    case R.id.agrega_evento_user:
                                        //Toast.makeText(getContext(),"Agrega evento",Toast.LENGTH_LONG).show();
                                        Intent agregarEve = new Intent(getContext(),AgregarEvento.class);
                                        agregarEve.putExtra("uid",ids.get(position));
                                        startActivity(agregarEve);
                                        break;
                                    case R.id.agrega_rutina_user:
                                        Toast.makeText(getContext(),"Agrega rutina",Toast.LENGTH_LONG).show();
                                        break;
                                    case R.id.ver_historial_rutas:
                                        Toast.makeText(getContext(),"Ver historial de rutas",Toast.LENGTH_LONG).show();
                                        break;
                                    case R.id.ver_historial_alertas:
                                        Toast.makeText(getContext(),"Ver historial de alertas",Toast.LENGTH_LONG).show();
                                        break;
                                    case R.id.ver_historial_caja:
                                        Toast.makeText(getContext(),"Ver historial de caja negra",Toast.LENGTH_LONG).show();
                                        break;
                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();

                    }
                });
            }

        };

        listaUM.setAdapter(adapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mgoogleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        CameraPosition liberty = CameraPosition.builder().target(new LatLng(19.257385, -99.577600))
                .zoom(16).bearing(0).tilt(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public String setLocation(double latitud, double longitud) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud

        String direccion="";

                try {
                    Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(
                            latitud, longitud, 1);
                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);
                        direccion=DirCalle.getAddressLine(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

              return direccion;

    }

    /*public void hilo(){

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public class time extends AsyncTask<Void,Integer,Boolean>{


        @Override
        protected Boolean doInBackground(Void... voids) {

            for(int i=1;i<=4;i++){
                hilo();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ejectutar();
        }
    }

    public void ejectutar(){
        time time = new time();
        time.execute();

Toast.makeText(getContext(),"Prueba",Toast.LENGTH_LONG).show();

    }*/

}
