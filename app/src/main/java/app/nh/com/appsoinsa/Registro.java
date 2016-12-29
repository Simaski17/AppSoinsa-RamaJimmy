package app.nh.com.appsoinsa;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.nh.com.appsoinsa.cls.Functions;
import app.nh.com.appsoinsa.cls.Imagenes;
import app.nh.com.appsoinsa.cls.LocationGps;
import app.nh.com.appsoinsa.cls.OutObject;
import app.nh.com.appsoinsa.cls.Preobra;


public class Registro extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    TextView idApp, txtGps;
    String idRegistro = "";
    EditText txtDirecc, txtDesc, txtAlias;
    LinearLayout btnEnviar;
    Button btnPhoto;
    ImageButton btnPosicion;
    ImageButton btnBuscar;
    LatLng pos;
    RelativeLayout rlImgExtra;
    ImageView imgExtra;

    byte[] inputData;
    String pathFile;

    public String latitud = "";
    public String longitud = "";
    public String direccion;

    public static GoogleMap mapa;
    public static Switch swActivarMapa;



    private String APP_DIRECTORY = "SoinsaMedia/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    String path_;

    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    LinearLayout contentImg;
    List<Imagenes> imgs = new ArrayList<Imagenes>();

    final static Handler hh = new Handler();

    ScrollView scrollme;
    boolean statusMap = false;


    Geocoder geocoder;
    List<Address> addresses;

    String TAG = "HOLA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        idRegistro = "";

        /*<meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="AIzaSyDC2Mj4Wi2OnXBVwsL8dneURatHCmoNgiE"/>*/

        idApp = (TextView) findViewById(R.id.idAppRegister);
        txtGps = (TextView) findViewById(R.id.txtGps);
        txtAlias = (EditText) findViewById(R.id.txtAlias);
        txtDesc = (EditText) findViewById(R.id.txtDesc);
        txtDirecc = (EditText) findViewById(R.id.txtDirecc);
        btnEnviar = (LinearLayout) findViewById(R.id.btnEnviar);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnPosicion =  (ImageButton) findViewById(R.id.btnPosicion);
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscar);
        contentImg = (LinearLayout) findViewById(R.id.contentImg);
        scrollme = (ScrollView) findViewById(R.id.scrollme);
        rlImgExtra = (RelativeLayout) findViewById(R.id.rlImgExtra);
        imgExtra = (ImageView) findViewById(R.id.imgExtra);

        //ViewTreeObserver obs = new ViewTreeObserver();
        //scrollme.getViewTreeObserver().removeOnPreDrawListener(this);
        scrollme.setScrollY(500);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            idRegistro = (String) extras.get("idAppRegister");
            idApp.setText(idRegistro);
            getValues(idRegistro);
            statusMap = false;
        }else{
            statusMap = false;
        }

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOption();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preobra p = new Preobra();
                p.setAlias(txtAlias.getText().toString());
                p.setBodegaId("1");
                p.setDireccion(txtDirecc.getText().toString());
                p.setEmpresaId("1");
                p.setEstadoId("0");
                p.setFechaApk("");

                p.setDescripcion(txtDesc.getText().toString());
                p.setIdPreObra("");
                p.setLatitud(latitud);
                p.setLongitud(longitud);
                p.setImgs(imgs);
                // Functions.desSerialize();

                if (idRegistro.equals("")) {
                    p.setFechaCreacion(Functions.datenow());
                    p.setIdApp(Functions.keygen());
                    addRegister(p);

                } else {
                    p.setIdApp(idRegistro);
                    update(p);
                }
            }
        });

         /*GPS*/
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //MyLocationListener mlocListener = new MyLocationListener(this);
        LocationGps gps = new LocationGps(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e("Location", "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        }else{
            Log.i("Location", "Permisos necesarios OK!.");
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) gps);
        }

        SupportMapFragment mfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mfragment.getMapAsync(this);

        swActivarMapa = (Switch) findViewById(R.id.swActivarMap);

        //set the switch to ON
        swActivarMapa.setChecked(true);

        swActivarMapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,  boolean isChecked) {
                if(isChecked){
                    statusMap = true;
                }else{
                    statusMap = false;
                }
            }
        });

        rlImgExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlImgExtra.setVisibility(View.GONE);
            }
        });


        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(longitud.equals("")){
                    Toast.makeText(Registro.this, "Aun no ha posicionado", Toast.LENGTH_SHORT).show();
                }else {
                    getAddress(latitud, longitud);
                }
                //buscarDireccion();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtDirecc.getText().toString().equals("")){
                    Toast.makeText(Registro.this, "Ingrese una direccion para buscar", Toast.LENGTH_SHORT).show();
                }else {
                    buscarDireccion();
                }
            }
        });

        txtDirecc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(conectadoRedMovil() || conectadoWifi()){
                    if(hasFocus) {
                        txtDesc.requestFocus();
                        findPlace();
                    }
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;

        LatLng santiago = new LatLng(-33.400601, -70.651336);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(santiago,15));
    }


    public void addImgs(String path){
        Imagenes img = new Imagenes();
        img.setImg(path);
        imgs.add(img);
    }

    public String update(Preobra po){
        String result = "";
        List<Preobra> pl = SplashScreen.obj.getListObject();
        int i = 0;
        for( Preobra a : pl) {
            String id = po.getIdApp();
            if(a.getIdApp() != null) {
                if (a.getIdApp().equals(id)) {
                    SplashScreen.obj.getListObject().get(i).setDescripcion(po.getDescripcion());
                    SplashScreen.obj.getListObject().get(i).setDireccion(po.getDireccion());
                    SplashScreen.obj.getListObject().get(i).setAlias(po.getAlias());
                    SplashScreen.obj.getListObject().get(i).getImgs().addAll(po.getImgs());
                }
            }
            i++;
        }
        Functions.desSerialize(true);
        finish();
        return result;
    }

    public void addRegister(Preobra po){
        SplashScreen.obj.getListObject().add(po);
            /*Actualiza el Json*/
        Functions.desSerialize(true);
        Toast.makeText(Registro.this,"Registro Guardado", Toast.LENGTH_LONG).show();
        finish();
    }

    public List<Preobra> getValues(String find){
        List<Preobra> pl = SplashScreen.obj.getListObject();
        List<Preobra> secondList = new ArrayList<Preobra>();
        for( Preobra a : pl) {

            if(a.getIdApp() != null) {
                if (a.getIdApp().equals(find)) {

                    if(a.getIdPreObra() != null){
                        btnEnviar.setBackgroundColor(getResources().getColor(R.color.coGris));
                        btnEnviar.setEnabled(false);
                        btnBuscar.setEnabled(false);
                        btnPosicion.setEnabled(false);
                        txtAlias.setText(a.getAlias());
                        txtAlias.setEnabled(false);
                        txtDirecc.setText(a.getDireccion());
                        txtDirecc.setEnabled(false);
                        txtDesc.setText(a.getDescripcion());
                        txtDesc.setEnabled(false);
                    }

                    secondList.add(a);
                    txtAlias.setText(a.getAlias());
                    txtDirecc.setText(a.getDireccion());
                    txtDesc.setText(a.getDescripcion());
                    if(a.getLatitud().trim().equals("")){
                        statusMap = false;
                    }else {
                        statusMap = false;
                        listenGpsStatic(a.getLatitud(),a.getLongitud());
                    }

                    if(a.getImgs() != null) {
                        for (Imagenes im : a.getImgs()) {
                            File file = new File(im.getImg());
                            Uri ur = Uri.fromFile(file);
                            addImgLayout(ur);
                        }
                    }
                }
            }
        }
        return secondList;
    }

    public void listenGps(String lat, String lon){
        if(statusMap) {
            this.longitud = lon;
            this.latitud = lat;
            String cadena = "v1. long:" + lon + " lat:" + lat;
            txtGps.setText(cadena);
            setLocationPoint(lat, lon);
            //getAddress(lat,lon);
        }
    }

    public void listenGpsStatic(String lat, String lon){

        this.longitud = lon;
        this.latitud = lat;
        String cadena = "v2. long:" + lon + " lat:" + lat;
        txtGps.setText(cadena);
        setLocationPoint(lat, lon);
        getAddress(lat,lon);
    }

    public void getAddress(String lat, String lon){

        Double long_ = Double.parseDouble(lon);
        Double lat_ = Double.parseDouble(lat);
        geocoder = new Geocoder(this,Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat_, long_, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            String dir = address + ", " + city;
            txtDirecc.setText(dir);
        }catch (Exception ex){
            //Toast.makeText(Registro.this,"No se pudo",Toast.LENGTH_LONG).show();
        }

    }

    public void setLocationPoint(String lat, String lng){

        Double long_ = Double.parseDouble(lng);
        Double lat_ = Double.parseDouble(lat);

        pos = new LatLng(lat_,long_);
        //mapa.setOnMarkerDragListener(this);
        Log.e("TAG", "POS: "+pos);

        try {
            mapa.setOnMarkerDragListener(this);
            mapa.clear();

            mapa.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Punto GPS")
                    .draggable(true));

            mapa.moveCamera(CameraUpdateFactory.newLatLng(pos));

            mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {

                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                    // Clears the previously touched position
                    mapa.clear();

                    // Animating to the touched position
                    mapa.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mapa.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Punto GPS")
                            .draggable(true));

                    Toast.makeText(Registro.this, "AQUI"+  latLng.longitude + latLng.latitude, Toast.LENGTH_SHORT).show();
                    latitud = String.valueOf(latLng.latitude);
                    longitud = String.valueOf(latLng.longitude);

                    getAddress(latitud, longitud);

                    /*String cadena = "v1. long:" +latLng.longitude + " lat:" + latLng.latitude;
                    txtGps.setText(cadena);*/

                }
            });

        }catch (Exception e){
            Log.e("TAG", "Mensaje: "+ e);
        }

    }

    /*FOTOGRAFIA FOTO!GALERIA*/
    public void showOption(){
        final CharSequence[] options = {"Tomar Foto","Elegir de Galeria","Salir"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setTitle("Opciones");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int seleccion){

                if(options[seleccion]=="Tomar Foto"){
                    openCamara();
                }else if(options[seleccion]=="Elegir de Galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    startActivityForResult(intent.createChooser(intent,"Selecciona IMG"), SELECT_PICTURE);
                }else if(options[seleccion]=="Salir"){
                    dialog.dismiss();
                }
            }
        } );
        builder.show();
    }

    /*Abre camara y obtiene path de la imagen tomada*/
    public void openCamara(){

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SourceApp/fotos/");
        file.mkdirs();
        path_ = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SourceApp/fotos/";
        path_ = path_ + generateName();
        File newfile = new File(path_);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(intent, PHOTO_CODE);
    }

    /*Genera un nombre estandar, que sera asignado al archivo*/
    public String generateName(){
        Long timestamp = System.currentTimeMillis() / 1000;
        String name = "IMG_" + timestamp.toString() + ".jpg";
        return name;
    }

    public void findPlace() {
        try {
            Intent intent =  new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    /*Resultado de camara o galeria*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pathFile = "";
        switch (requestCode) {
            //Si es tomada por camara
            case PHOTO_CODE:
                if (resultCode == RESULT_OK) {

                    BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
                    bmOptions1.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path_, bmOptions1);
                    int photoW = bmOptions1.outWidth;
                    int photoH = bmOptions1.outHeight;
                    // Determinar cuanto escalamos la imagen
                    int scaleFactor1 = Math.min(photoW / 400, photoH / 400);
                    // Decodificar la imagen en un Bitmap escalado a View
                    bmOptions1.inJustDecodeBounds = false;
                    bmOptions1.inSampleSize = scaleFactor1;
                    bmOptions1.inPurgeable = true;
                    Bitmap bitmap1 = BitmapFactory.decodeFile(path_, bmOptions1);
                    File file = new File(path_);
                    try {
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        // bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, out);//Convertimos la imagen a JPEG
                        bitmap1.compress(Bitmap.CompressFormat.PNG, 50, out);//Convertimos la imagen a JPEG
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Registro.this, "Cargando Imágen", Toast.LENGTH_SHORT).show();
                    MediaScannerConnection.scanFile(Registro.this, new String[]{path_}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Path Img PHOTO CODE ", path);

                                }


                            });
                    addImgs(path_);
                    File filex = new File(path_);
                    Uri ur = Uri.fromFile(filex);
                    addImgLayout(ur);

                }
                break;
            //Si es tomada desde la galeria
            case SELECT_PICTURE:

                if (resultCode == RESULT_OK) {
                    Toast.makeText(Registro.this, "Generando imágen", Toast.LENGTH_SHORT).show();

                    Uri imgUri = data.getData();
                    Bitmap bitmap = null;
                    try {

                         bitmap = getThumbnail(imgUri);

                    }catch(Exception ex){

                    }

                    String path_2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SourceApp/fotos/";
                    path_2 = path_2 + generateName();
                    addImgs(path_2);
                    File f = new File(path_2);




                    try {
                        f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);
                        // bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, out);//Convertimos la imagen a JPEG
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);//Convertimos la imagen a JPEG
                        out.flush();
                        out.close();
                        Uri uri = Uri.fromFile(f);
                        addImgLayout(uri);
                        Log.i("Path Img SELECT PICTURE", data.getData().getPath());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }






                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    // retrive the data by using getPlace() method.
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    String direccion = (String) place.getAddress() + (String) place.getPhoneNumber();
                    txtDirecc.setText(direccion);

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.e("Tag", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }

    }


    public  Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 500) ? (originalSize / 500) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


    public void addImgLayout(Uri uri){
        ImageView imgAdd = new ImageView(this);
        imgAdd.setImageURI(uri);
        final Uri uriFile = uri;

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Registro.this,"Path img:" + uriFile.getPath(),Toast.LENGTH_LONG).show();
                rlImgExtra.setVisibility(View.VISIBLE);
                imgExtra.setImageURI(Uri.parse(uriFile.getPath()));
            }
        });

        LinearLayout relativeLayout1 = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(70,70);
        layoutParams.setMargins(7,2,7,2);
        //layoutParams.
        relativeLayout1.setBackgroundColor(Color.parseColor("#CCCCCC"));
        relativeLayout1.addView(imgAdd);


        contentImg.addView(relativeLayout1,layoutParams);

    }


    public void buscarDireccion() {

        direccion = txtDirecc.getText().toString();


        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addresses;
        try
        {
            addresses = geocoder.getFromLocationName(direccion, 5);
            if(addresses.size() > 0)
            {
                for (int a = 0; a < addresses.size(); a++) {
                    double latitudebd = addresses.get(0).getLatitude();
                    double longitudebd = addresses.get(0).getLongitude();
                    latitud = String.valueOf(latitudebd);
                    longitud = String.valueOf(longitudebd);
                    System.out.println(latitudebd);
                    System.out.println(longitudebd);
                    Log.d(TAG, "MENSAJE " + latitudebd + longitudebd);
                    Toast.makeText(this, "Buscando" + latitudebd + longitudebd, Toast.LENGTH_SHORT).show();
                    //return new LatLng(latitude, longitude);

                    pos = new LatLng(latitudebd,longitudebd);

                    mapa.clear();

                    mapa.animateCamera(CameraUpdateFactory.newLatLng(pos));
                    mapa.addMarker(new MarkerOptions()
                            .position(pos)
                            .title("Punto GPS")
                            .draggable(true));

                }
                mapa.animateCamera(CameraUpdateFactory.newLatLng(pos));
                getAddress(latitud,longitud);
            }
            else
            {
                //return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            //return null;
        }

    };

    private boolean conectadoRedMovil() {

        ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitud = String.valueOf(marker.getPosition().latitude);
        longitud = String.valueOf(marker.getPosition().longitude);
        getAddress(latitud,longitud);
    }


    @Override
    public void onBackPressed() {
        if (rlImgExtra.getVisibility() == View.VISIBLE) {
            rlImgExtra.setVisibility(View.GONE);
        } else {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("Salir del Registro");
            // Setting Dialog Message
            alertDialog.setMessage("\u00bfQuieres salir del Registro? Los datos que no haya guardado ser perderan");
            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.delete);
            // On pressing Settings button
            alertDialog.setPositiveButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // on pressing cancel button
            alertDialog.setNegativeButton("Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
            //finish();

        }
    }
}
