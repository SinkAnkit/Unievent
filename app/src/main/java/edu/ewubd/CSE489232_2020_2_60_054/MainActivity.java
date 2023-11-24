package edu.ewubd.CSE489232_2020_2_60_054;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    Button createBtn, historyBtn, exitBtn;
    private ListView eventList;
    private ArrayList<Event> events;
    private ArrayList<Event> pastEvents;
    private CustomEventAdapter adapter; //dynamically add the data
    private CustomEventAdapter adapterPast;

    EventDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // runtime call the abstract method
        events = new ArrayList<>();
        pastEvents = new ArrayList<>();

        //modify after that
        setContentView(R.layout.main_activity); // resource.layout_directory.activityxml_file_name
        eventList = findViewById(R.id.eventList);

        db = new EventDB(this);

        createBtn = findViewById(R.id.createBtn);
        historyBtn = findViewById(R.id.historyBtn);
        exitBtn = findViewById(R.id.exitBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(i);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences pref = getSharedPreferences("savedUserInfo", MODE_PRIVATE);
                //SharedPreferences.Editor editor = pref.edit();
                //editor.putBoolean("isLoggedIn", false);
                //editor.apply();
                //finish();
                moveTaskToBack(true);
            }
        });

        adapterPast = new CustomEventAdapter(this, pastEvents);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList.setAdapter(adapterPast);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadEvents(); //loading from local database
        String[] keys = {"action", "sid", "semester"};
        String[] values = {"restore", "2020-2-60-054", "2023-2"};
        httpRequest(keys, values);
    }

    private void loadEvents(){
        events.clear();
        String q = "SELECT * FROM events";
        Cursor cur = db.selectEvents(q);
        if(cur != null){
            if(cur.getCount() > 0){ // number of rows
                while (cur.moveToNext()){
                    String ID = cur.getString(0);
                    String name = cur.getString(1);
                    String place = cur.getString(2);
                    long _date = cur.getLong(3);
                    int _capacity = cur.getInt(4);
                    double _budget = cur.getDouble(5);
                    String email = cur.getString(6);
                    String phone = cur.getString(7);
                    String desc = cur.getString(8);
                    String type = cur.getString(9);
                    String remind = cur.getString(10);

                    if(_date < System.currentTimeMillis()){
                        Event event = new Event(ID, name, place, String.valueOf(_date), String.valueOf(_capacity), String.valueOf(_budget), email, phone, desc, type, remind);
                        System.out.println(event.toString());
                        pastEvents.add(event);
                    }
                    else{
                        Event event = new Event(ID, name, place, String.valueOf(_date), String.valueOf(_capacity), String.valueOf(_budget), email, phone, desc, type, remind);
                        System.out.println(event.toString());
                        events.add(event);
                    }

                }
            }
            cur.close();
        }
        db.close();

        adapter = new CustomEventAdapter(this, events); // render
        eventList.setAdapter(adapter); // setting in listview using adapter
        // handle the click on an event-list item
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int
            position, long id) {
                // String item = (String) parent.getItemAtPosition(position);
                System.out.println(position);
                Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                i.putExtra("EventID", events.get(position).id);
                i.putExtra("EventName", events.get(position).name);
                i.putExtra("EventPlace", events.get(position).place);
                i.putExtra("EventType", events.get(position).eventType);
                i.putExtra("EventDateTime", events.get(position).datetime);
                i.putExtra("EventCapacity", events.get(position).capacity);
                i.putExtra("EventBudget", events.get(position).budget);
                i.putExtra("EventEmail", events.get(position).email);
                i.putExtra("EventPhone", events.get(position).phone);
                i.putExtra("EventDesc", events.get(position).description);
                startActivity(i);
            }
        });

        //1. click create event 2. longpress edit or delete

        // handle the long-click on an event-list item
        eventList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete event - " + events.get(position).name + " ?";
                System.out.println(message);
                showDialog(message, "Delete Event", events.get(position)); // key can be id
                return true;
            }
        });
    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                //Tested my own server:
                //https://cse489.helloworlddev.software/index.php?action=restore&sid=2020-2-60-054&semester=2023-2
                //https://www.muthosoft.com/univ/cse489/index.php
                String url= "https://cse489.helloworlddev.software/index.php"; //TODO Update it
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    System.out.println(data);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("Ok2");
                    updateEventListByServerData(data);
                    Log.d("remoteDB", data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    // string format data will be received
    private void updateEventListByServerData(String data){
        System.out.println("found");
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){ // server send it as keyword
                events.clear();
                JSONArray ja = jo.getJSONArray("events");
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i); // object as key value pair
                    String id = event.getString("id");
                    String title = event.getString("title");
                    String place = event.getString("place");
                    String type = event.getString("type");
                    Long date_time = event.getLong("date_time");
                    int capacity = event.getInt("capacity");
                    double budget = event.getDouble("budget");
                    String email = event.getString("email");
                    String phone = event.getString("phone");
                    String des = event.getString("des");
                    String remind = event.getString("reminder");

                    //Event(String id, String name, String place, String datetime,String capacity,String budget,String email,String phone,String description,String eventType)
                    Event e = new Event(id, title, place, String.valueOf(date_time), String.valueOf(capacity), String.valueOf(budget), email, phone, des, type, remind);

                    if(date_time < System.currentTimeMillis()){
                        pastEvents.add(e);
                    }
                    else{
                        events.add(e);
                    }
                }
                //System.out.println(events.size());
                adapter.notifyDataSetChanged();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //todo showdiaglog
    private void showDialog(String message, String title, Event event){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //System.out.println(event.id);
                db.deleteEvent(event.id);
                deleteRemote(event.id);
                events.remove(event);
                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void updateRemote(String eventId){
        String[] keys = {"action", "sid", "semester", "id"};
        String[] values = {"backup", "2020-2-60-054", "2023-2", eventId};
        httpRequest(keys, values);
    }
    private void deleteRemote(String eventId){
        String[] keys = {"action", "sid", "semester", "id"};
        String[] values = {"remove", "2020-2-60-054", "2023-2", eventId};
        httpRequest(keys, values);
    }
}