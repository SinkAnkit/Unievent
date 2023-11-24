package edu.ewubd.CSE489232_2020_2_60_054;
//Sagar Karmoker
//ID: 2020-2-60-054
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomEventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ArrayList<Event> events;

    public CustomEventAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, -1, events);
        this.context = context;
        this.events = events;
    }

    @Override
    // parent = listview
    // custom getview
    public View getView(int position, View convertView, ViewGroup parent) { // it will be called for each row (0 to n-1)

        // inflater is renderer
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.event_row, parent, false); // works as a view

        TextView eventName = rowView.findViewById(R.id.eventNameTv);
        TextView eventDateTime = rowView.findViewById(R.id.eventDateTv);
        TextView eventPlaceName = rowView.findViewById(R.id.eventPlaceTv);
        //TextView eventType = rowView.findViewById(R.id.tvEventType);
        //ImageView imgEventLogo = rowView.findViewById(R.id.imgLogo);

        //imgEventLogo.setDrawble();
        try{
            Event e = events.get(position);
            Log.d("inAdapter", e.toString());
            long timeInMilli = Long.parseLong(e.datetime);
            Date date = new Date(timeInMilli);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(date);

            eventName.setText(e.name);
            eventDateTime.setText(formattedDate);
            eventPlaceName.setText(e.place);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        //eventType.setText(e.eventType);
        return rowView;
    }
}