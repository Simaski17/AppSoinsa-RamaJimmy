package app.nh.com.appsoinsa;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import app.nh.com.appsoinsa.cls.Functions;
import app.nh.com.appsoinsa.cls.OutObject;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIEMPO = 3000;
    final static Handler hh = new Handler();


    public static OutObject obj = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.activity_splash_screen, null);
        desSerialize();
    }
    /**
     * Metodo para desserializar un Json String a Objeto
     */
    public void desSerialize() {

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
                    }else{
                        Functions.makeDirectories(path);
                        Functions.desSerialize(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Handler que ejecuta el metodo cuando termina el Hilo
                hh.postDelayed(toObject,3000);
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
            obj = gig;

            Intent i = new Intent(SplashScreen.this, Login.class);
            startActivity(i);
            finish();
        }
    };
}
