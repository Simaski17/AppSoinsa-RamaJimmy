package app.nh.com.appsoinsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class Login extends AppCompatActivity {
    Button btnIngresar;
    EditText txtUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.activity_login, null);
       // getSupportActionBar().hide();
        btnIngresar = (Button) findViewById(R.id.ingresar);
        txtUser = (EditText) findViewById(R.id.username);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("user", txtUser.getText().toString());
                startActivity(i);
                finish();

                //Intent intent = new Intent(context, MainCheck.class);
                //context.startActivity(intent);
                //eDatos.putString("idCheckListaLectura", items.get(position).getCheckListId());
                //eDatos.commit();

            }
        });


    }
}
