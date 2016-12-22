package app.nh.com.appsoinsa.cls;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import app.nh.com.appsoinsa.MainActivity;
import app.nh.com.appsoinsa.Registro;

/**
 * Created by Dev21 on 07-12-16.
 */

public class LocationGps implements LocationListener {

    Context ctx;
    Registro a;
    public LocationGps(Context context) {
        this.ctx = context;
        a = (Registro) context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
        // debido a la detecci—n de un cambio de ubicacion
        //loc.getLatitude();
        //loc.getLongitude();
        String lat = loc.getLatitude()+"";
        String lon = loc.getLongitude()+"";
        String msg = "Mi ubicacion actual es: " + "\n Lat = "	+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
        //Toast.makeText(this.ctx, loc.getLatitude()+"" , Toast.LENGTH_SHORT ).show();
        Log.d("appGPS",msg);

        a.listenGps(lat,lon);
        //flash.asignaLyl(msg);
        //flash.mostrarMarcador(loc.getLatitude(), loc.getLongitude());
        //flash.camaraPosicion(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Este metodo se ejecuta cada vez que se detecta un cambio en el
        // status del proveedor de localizaci—n (GPS)
        // Los diferentes Status son:
        // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
        // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
        // espera que este disponible en breve
        // AVAILABLE -> Disponible
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es activado
        //this.estado = "GPS Activado";
        String msg = "GPS Activado";
        //Toast.makeText(this.ctx, msg , Toast.LENGTH_SHORT ).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es desactivado
        //this.estado = "GPS Desactivado";
        String msg = "GPS Desactivado";
        //Toast.makeText(ctx, msg , Toast.LENGTH_SHORT ).show();
    }
}
