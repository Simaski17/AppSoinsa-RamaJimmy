package app.nh.com.appsoinsa.cls;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.nh.com.appsoinsa.SplashScreen;

/**
 * Created by Dev21 on 28-11-16.
 */

public class Functions {

    public static String dirHome =  "/SourceApp/";
    public static String fileNameJson = "Objects.json";
    public static String jsonData = "";
    /** Convierte bytes en texto
     * @param inputStream de tipo InputStream
     * */
    public static String btoString( InputStream inputStream ) throws IOException
    {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int len=0;
        while ( (len=inputStream.read(bytes))>0 )
        {
            b.write(bytes,0,len);
        }
        return new String( b.toByteArray(),"UTF8");
    }
    //end: New code

    public static String keygen(){
        SimpleDateFormat sm1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d1 = new Date();
        String key = sm1.format(d1);
        return key;
    }

    public static String datenow(){

        SimpleDateFormat sm2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d2 = new Date();
        String f = sm2.format(d2);
        return f;
    }


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


    /**
     * Metodo Serializa Objeto a Json a Archivo Json
     */
    public static void desSerialize(boolean a){
        /*Objeto de salida*/
        OutObject out = new OutObject();
        if (a) {
            out = SplashScreen.obj;
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

        /*Exportamos Data en Json*/
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + dirHome + fileNameJson;
            if(Functions.makeDirectories(dirHome)) {

                FileWriter file = new FileWriter(path);
                file.write(json);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
