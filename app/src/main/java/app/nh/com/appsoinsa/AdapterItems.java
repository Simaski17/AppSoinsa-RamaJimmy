package app.nh.com.appsoinsa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import app.nh.com.appsoinsa.cls.Preobra;

/**
 * Created by Dev21 on 29-11-16.
 */

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ItemViewHolder>{

    private List<Preobra> items;
    private String dia;
    private int diaColor;

    SharedPreferences nDatos;
    SharedPreferences.Editor eDatos;
    Context context;

    public AdapterItems(List<Preobra> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ItemViewHolder items = new ItemViewHolder(view);

        return items;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        holder.desc1.setText("#"+items.get(position).getIdApp());
        holder.desc2.setText(items.get(position).getAlias() + " - " + items.get(position).getFechaCreacion());
        holder.fecha.setText("");


        //holder.img.setImageURI(Uri.parse(items.get(position).getUrl()));
        //holder.name.setText(items.get(position).getName());

        //dia = "" + items.get(position).getDiaTxt().toLowerCase().charAt(0);
        //dia += items.get(position).getDiaTxt().toLowerCase().charAt(1);

        dia = holder.getDayOfTheWeek(items.get(position).getFechaCreacion());
        switch (dia) {
            case "lu":
                diaColor = Color.parseColor("#ffcd40");
                break;
            case "ma":
                diaColor = Color.parseColor("#8877d0");
                break;
            case "mi":
                diaColor = Color.parseColor("#d86257");
                break;
            case "ju":
                diaColor = Color.parseColor("#5baa72");
                break;
            case "vi":
                diaColor = Color.parseColor("#4285f4");
                break;
            case "sa":
                diaColor = Color.parseColor("#777d91");
                break;
            case "do":
                diaColor = Color.parseColor("#62a38d");
                break;
        }

//date format is:  "Date-Month-Year Hour:Minutes am/pm"

        holder.layoutMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Registro.class);
                intent.putExtra("idAppRegister", items.get(position).getIdApp());
                context.startActivity(intent);

                //eDatos.putString("idCheckListaLectura", items.get(position).getCheckListId());
                //eDatos.commit();
            }
        });
        /*String cadena1 = "Checklist #" + items.get(position).getCheckListId() + " creado el " + items.get(position).getDiaTxt();
        holder.desc1.setText(cadena1);

        String cadena2 = "Equipo " + items.get(position).getNumeroCamion() + " -- OM" + items.get(position).getOrdenManifiesto() + " " + items.get(position).getDestino();
        holder.desc2.setText(cadena2);

        holder.fecha.setText(items.get(position).getFechaRegistro());
        holder.changeColor(dia, diaColor);
        if(items.get(position).getStatus().equals("0")) {
            holder.imgIcon.setImageResource(R.drawable.ic_borrador);
        }else if(items.get(position).getStatus().equals("1")){
            holder.imgIcon.setImageResource(R.drawable.ic_checkitem);
        }
*/
        holder.changeColor(dia, diaColor);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView desc1;
        TextView desc2;
        TextView fecha;
        ImageView image;
        LinearLayout layoutMaster;
        TextDrawable drawable;
        ImageView imgIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            desc1 = (TextView) itemView.findViewById(R.id.desc1);
            desc2 = (TextView) itemView.findViewById(R.id.desc2);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            image = (ImageView) itemView.findViewById(R.id.headerLetra);
            layoutMaster = (LinearLayout) itemView.findViewById(R.id.layoutMaster);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgStatus);
        }

        public void changeColor(String ds, int color) {
            drawable = TextDrawable.builder()
                    .buildRound(ds, color);
            image.setImageDrawable(drawable);
        }

        public String getDayOfTheWeek(String dateInString){

            String diaText = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date d = new Date();
            try {
                 d = formatter.parse(dateInString);
            }catch (Exception ex){

            }
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(d);

            int day =  cal.get(Calendar.DAY_OF_WEEK);
            switch(day){
                case 1:
                    diaText = "do";
                    break;
                case 2:
                    diaText = "lu";
                    break;
                case 3:
                    diaText = "ma";
                    break;
                case 4:
                    diaText = "mi";
                    break;
                case 5:
                    diaText = "ju";
                    break;
                case 6:
                    diaText = "vi";
                    break;
                case 7:
                    diaText = "sa";
                    break;
            }
            return diaText;
        }


    }
}
