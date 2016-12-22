package app.nh.com.appsoinsa;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.nh.com.appsoinsa.cls.Functions;
import app.nh.com.appsoinsa.cls.OutObject;
import app.nh.com.appsoinsa.cls.Preobra;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListItems.OnFragmentInteractionListener{

    private static String user =  "Invitado";

    final Handler hh = new Handler();

    private String jsonData = "";
    OutObject obj = null;

    TextView userTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.activity_main, null);
        getSupportActionBar().setTitle("Principal");
       // getSupportActionBar().hide();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Nuevo Elemento" , Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });

        userTxt = (TextView) findViewById(R.id.nombreUsuario);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new ListItems();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {
            user = (String)extras.get("user");
            //Toast.makeText(MainActivity.this,"User: " + user,Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Metodo Serializa Objeto a Json a Archivo Json
     */
    private void desSerialize(){
        /*Objeto de salida*/
        OutObject out = new OutObject();
        List<Preobra> list = new ArrayList<>();

        for(int i = 0; i< 2; i++) {

        /*Genera ID de la app Local*/
            SimpleDateFormat sm1 = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d1 = new Date();
            String idApp = sm1.format(d1);

        /*Fecha Actual*/
            SimpleDateFormat sm2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date d2 = new Date();
            String fechaCreacion = sm2.format(d2);

            Preobra objItem = new Preobra();
            objItem.setAlias("Un Alias");
            objItem.setBodegaId("15");
            objItem.setDireccion("Los Dev 1044");
            objItem.setEmpresaId("1");
            objItem.setEstadoId("0");
            objItem.setFechaApk("");
            objItem.setFechaCreacion(Functions.datenow());
            objItem.setIdApp(Functions.keygen());
            objItem.setDescripcion("Descripcion de la pre Obra");
            objItem.setIdPreObra("");
            objItem.setLatitud("13");
            objItem.setLongitud("11");

            list.add(objItem);
        }
        out.setListObject(list);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(out);
        Log.i("Json Generated: ", json.toString());

        /*Exportamos Data en Json*/
        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Functions.dirHome + Functions.fileNameJson;
            if(Functions.makeDirectories(Functions.dirHome)) {

                FileWriter file = new FileWriter(path);
                file.write(json);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /* Metodo para serializar un Json String a Objeto
    */
    public void Serialize() {

        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    //Metodo que realiza una accion que requiere ser llamada en segundo plano u otro Hilo
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Functions.dirHome + Functions.fileNameJson;
                    File f = new File(path);
                    if(f.exists()) {
                        File file = new File(path);
                        int length = (int) file.length();
                        byte[] bytes = new byte[length];
                        FileInputStream in = new FileInputStream(file);
                        try {
                            in.read(bytes);
                        } finally {
                            in.close();
                        }
                        Functions.jsonData = new String(bytes);
                        Log.i("Json Read:" , Functions.jsonData);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Handler que ejecuta el metodo cuando termina el Hilo
                hh.postDelayed(toObject,0);
            }
        });//end thread
        th.start();
    }

    final Runnable toObject = new Runnable() {
        public void run () {
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            JSONObject j;
            OutObject gig = null;
            try {
                j = new JSONObject(Functions.jsonData);
                gig = gson.fromJson(j.toString(), OutObject.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SplashScreen.obj = gig;
            Fragment fragment = new ListItems();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

            //finish();
        }
    };




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(MainActivity.this,"Vuelve" + user,Toast.LENGTH_LONG).show();
        Serialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
