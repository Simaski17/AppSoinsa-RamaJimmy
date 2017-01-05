package app.nh.com.appsoinsa;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.nh.com.appsoinsa.cls.Functions;
import app.nh.com.appsoinsa.cls.OutObject;
import app.nh.com.appsoinsa.cls.Preobra;

import static android.content.Context.POWER_SERVICE;
import static app.nh.com.appsoinsa.Login.json;

public class MyReceiver extends BroadcastReceiver {

    public static String dirHome =  "/SourceApp/";
    public static String fileNameJson = "Objects.json";
    public static String jsonData = "";
    final static Handler hh = new Handler();
    public static OutObject obj = null;
    public static  int i;
    ConnectivityManager connMgr;
    android.net.NetworkInfo wifi;
    android.net.NetworkInfo mobile;
    Context context;
    private Handler h = new Handler();
    private Runnable r;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        desSerialize();
        pruebaHilo();
        Log.e("TAG", "HOLA BROADCAST");

        this.context = context;

       connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        mobile = connMgr.getActiveNetworkInfo();

    }


    public void desSerialize() {

        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    //Metodo que realiza una accion que requiere ser llamada en segundo plano u otro Hilo
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + dirHome + fileNameJson;
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
                        jsonData = new String(bytes);
                        Log.i("Json Read:" , jsonData);
                    }else{
                        makeDirectories(path);
                        desSerialize(false);
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
                j = new JSONObject(jsonData);
                gig = gson.fromJson(j.toString(), OutObject.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            obj = gig;
            if (wifi.isAvailable() || mobile.isAvailable()) {
                postNewComment(context);
            }
            /*Intent i = new Intent(SplashScreen.this, Login.class);
            startActivity(i);
            finish();*/
        }
    };

    public static boolean makeDirectories(String dirPath)
            throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {

            for (String singleDir : pathElements) {

                if (!singleDir.equals("")) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath;
                    File dir = new File(path);
                    boolean existed = dir.exists();
                    boolean created = false;
                    if (!existed) {

                        try {
                            created = dir.mkdir();
                        } catch (Exception ex) {

                        }
                        if (created) {
                            Log.i("CREATED directory: ", singleDir);
                        } else {
                            Log.i("COULD NOT directory: ", singleDir);
                            //return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void desSerialize(boolean a){
        OutObject out = new OutObject();
        if (a) {
            out = obj;
        }else{
            List<Preobra> lp = new ArrayList<Preobra>();
            Preobra po = new Preobra();
            po.setAlias("Ini");
            po.setEstadoId("2");
            lp.add(po);
            out.setListObject(lp);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(out);
        Log.i("Json Generated: ", json.toString());

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + dirHome + fileNameJson;
            if(makeDirectories(dirHome)) {

                FileWriter file = new FileWriter(path);
                file.write(json);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  void postNewComment(Context context){
        //mPostCommentResponse.requestStarted();
        Log.e("TAG", "POST");
        if(obj.getListObject() != null) {
            List<Preobra> pl = obj.getListObject();

            i = 0;

            for (final Preobra a : pl) {
                if (a.getEstadoId().equals("0")) {
                    RequestQueue queue = Volley.newRequestQueue(context);
                    StringRequest sr = new StringRequest(Request.Method.POST, "http://200.111.184.243/api/index.php/insert", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //mPostCommentResponse.requestCompleted();
                            String respuesta = response.toString();
                            Log.e("TAG", "Response:%n %s" + respuesta);
                            if (respuesta != null) {
                                String result = "";
                                List<Preobra> pl2 = obj.getListObject();
                                int x = 0;
                                for( Preobra b : pl2) {

                                    if(b.getIdApp() != null) {
                                        if (b.getIdApp().equals(a.getIdApp())) {
                                             String aa = a.getIdApp();
                                             String bb = b.getIdApp();
                                            obj.getListObject().get(x).setEstadoId("1");
                                            obj.getListObject().get(x).setIdPreObra(respuesta);
                                        }
                                    }
                                    x++;
                                }
                                desSerialize(true);
                                // i++;

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //mPostCommentResponse.requestEndedWithError(error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            List<Preobra> pl = obj.getListObject();
                            List<Preobra> secondList = new ArrayList<Preobra>();
                            for (Preobra a : pl) {

                                if (a.getEstadoId().equals("0")) {
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    json = gson.toJson(a);

                                }
                            }

                            Log.e("TAG", "AQUIIII: " + json);
                            params.put("json", json);

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/x-www-form-urlencoded");
                            return params;
                        }
                    };
                    queue.add(sr);
                }
                i++;
            }

        }
    }

    public interface PostCommentResponseListener {
        public void requestStarted();
        public void requestCompleted();
        public void requestEndedWithError(VolleyError error);
    }

    public void  pruebaHilo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
				/*
				 * Creamos un Intent que lanzará nuestra Actividad principal (en
				 * nuestro caso Main.java)
				 */
                Log.e("TAG","MENSAJE");
                postNewComment(context);
                pruebaHilo();
            }
        }, 10000);
    }


}

