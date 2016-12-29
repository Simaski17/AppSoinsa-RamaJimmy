package app.nh.com.appsoinsa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.nh.com.appsoinsa.cls.Functions;
import app.nh.com.appsoinsa.cls.Imagenes;
import app.nh.com.appsoinsa.cls.OutObject;
import app.nh.com.appsoinsa.cls.Preobra;

public class Login extends AppCompatActivity {
    Button btnIngresar;
    EditText txtUser;
    public static String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.activity_login, null);
       // getSupportActionBar().hide();
        btnIngresar = (Button) findViewById(R.id.ingresar);
        txtUser = (EditText) findViewById(R.id.username);

        //http://200.111.184.243/api/index.php/insert

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("user", txtUser.getText().toString());
                startActivity(i);
                finish();
                postNewComment(getApplicationContext());
                //Intent intent = new Intent(context, MainCheck.class);
                //context.startActivity(intent);
                //eDatos.putString("idCheckListaLectura", items.get(position).getCheckListId());
                //eDatos.commit();

            }
        });
    }


    public static void postNewComment(Context context){
        //mPostCommentResponse.requestStarted();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,"http://200.111.184.243/api/index.php/insert", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mPostCommentResponse.requestCompleted();
                String respuesta = response.toString();
                Log.e("TAG", "Response:%n %s"+respuesta);
                if(respuesta != null){
                    String result = "";
                    List<Preobra> pl = SplashScreen.obj.getListObject();
                    int i = 0;
                    for( Preobra a : pl) {
                        if(a.getEstadoId().equals("0")) {
                                SplashScreen.obj.getListObject().get(i).setIdPreObra(respuesta);
                            }
                    }
                    Functions.desSerialize(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                List<Preobra> pl = SplashScreen.obj.getListObject();
                List<Preobra> secondList = new ArrayList<Preobra>();
                for( Preobra a : pl) {

                    if(a.getEstadoId().equals("0")) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        json = gson.toJson(a);

                    }
                }

                Log.e("TAG","AQUIIII: "+ json);
                params.put("json", json);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    public interface PostCommentResponseListener {
        public void requestStarted();
        public void requestCompleted();
        public void requestEndedWithError(VolleyError error);
    }

}
