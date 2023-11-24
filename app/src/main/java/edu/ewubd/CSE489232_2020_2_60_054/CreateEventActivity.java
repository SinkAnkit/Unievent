package edu.ewubd.CSE489232_2020_2_60_054;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEventActivity extends AppCompatActivity {

    EditText etName, etPlace, etDate, etCapacity, etBudget, etEmail, etPhone, etDsc, etRemind;
    Button cancelBtn, shareBtn, saveBtn;
    TextView errorTv;

    RadioButton rIndoor, rOutdoor, rOnline;
    private String eventID = "";
    private EventDB eventDB; // Database

    static final int ALARM_CODE = 100;
    AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // database object
        eventDB = new EventDB(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // edittext fields
        etName = findViewById(R.id.etName);
        etPlace= findViewById(R.id.etPlace);
        etDate = findViewById(R.id.etDate);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etEmail= findViewById(R.id.etEmail);
        etPhone= findViewById(R.id.etPhone);
        etDsc = findViewById(R.id.etDsc);
        etRemind = findViewById(R.id.etRemind);

        //radio button
        rIndoor = findViewById(R.id.radioIndoor);
        rOutdoor = findViewById(R.id.radioOutdoor);
        rOnline = findViewById(R.id.radioOnline);


        //intent data (if event is need to updated)
        Intent i = getIntent();
        if(i.hasExtra("EventID")) {
            eventID = i.getStringExtra("EventID");
            etName.setText(i.getStringExtra("EventName"));
            etPlace.setText(i.getStringExtra("EventPlace"));
            etCapacity.setText(i.getStringExtra("EventCapacity"));
            etBudget.setText(i.getStringExtra("EventBudget"));
            etEmail.setText(i.getStringExtra("EventEmail"));
            etPhone.setText(i.getStringExtra("EventPhone"));
            etDsc.setText(i.getStringExtra("EventDesc"));

            String datetime = i.getStringExtra("EventDateTime");
            String type = i.getStringExtra("EventType");

            //date use the date formatter
            if(datetime != null)
            {
                long timeInMilli = Long.parseLong(datetime);
                Date date = new Date(timeInMilli);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(date);
                etDate.setText(formattedDate);

            }

            assert type != null;
            if(type.equals("Indoor")){
                rIndoor.setChecked(true);
            }
            if(type.equals("Outdoor")){
                rOutdoor.setChecked(true);
            }
            if(type.equals("Online")){
                rOnline.setChecked(true);
            }
        }

        // buttons
        cancelBtn = findViewById(R.id.cancelBtn);
        shareBtn = findViewById(R.id.shareBtn);
        saveBtn = findViewById(R.id.saveBtn);

        // textviews
        errorTv = findViewById(R.id.errorTv);

        // button works
        /*
        * physical event convert into software event
        * // 1. retrieve all field data from XML
                // 2. check the validity of data
                    // a. if not valid, then show error message
                    // b. if everything ok, then save
        * */
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //etName.getText() --> return editable object
                String name = etName.getText().toString();
                String capacity = etCapacity.getText().toString();
                String place = etPlace.getText().toString();
                String date = etDate.getText().toString();
                String budget = etBudget.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String desc = etDsc.getText().toString();
                String type = "";
                String reminder = etRemind.getText().toString();

                String err = "";

                int _capacity = Integer.parseInt(capacity);
                double _budget = Double.parseDouble(budget);
                long _date = 0;
                long remind = Long.parseLong(reminder);

                // input field check
                if(!name.isEmpty() && !place.isEmpty() && !date.isEmpty() && !capacity.isEmpty() && !budget.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !desc.isEmpty() && !reminder.isEmpty()){
                    //name
                    if(name.length() < 4 || name.length() > 12 || !name.matches("^[a-zA-Z ]+$")){
                        err += "Invalid Name (4-12 long and only alphabets)\n";
                    }

                    //place
                    if(place.length() >= 6 && place.length() <= 64 && !place.matches("^[a-zA-Z0-9, ]+$")){
                        err += "Invalid Place (only alpha-numeric and , and 6-64 characters)\n";
                    }

                    // radio button check
                    boolean isIndoor = rIndoor.isChecked();
                    boolean isOutdoor = rOutdoor.isChecked();
                    boolean isOnline = rOnline.isChecked();

                    if(!isIndoor && !isOutdoor && !isOnline){
                        err += "Please select event type\n";
                    }
                    else{
                        if(isIndoor){
                            type = "Indoor";
                        }
                        if(isOutdoor){
                            type = "Outdoor";
                        }
                        if(isOnline){
                            type = "Online";
                        }
                    }
                    // converting string to Integer
                    int cap;
                    double event_budget;

                    cap = _capacity;
                    event_budget = _budget;

//                    if(cap <= 0){
//                        err += "Invalid capacity (number greater than zero)\n";
//                    }


//                    if(event_budget < 1000){
//                        err += "Invalid budget (number greater than 1000.00)\n";
//                    }

                    // Date and time checking

                    String format = "yyyy-MM-dd HH:mm";
                    try {
                        DateTimeFormatter formatter = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            formatter = DateTimeFormatter.ofPattern(format);

                            LocalDateTime inputDate = LocalDateTime.parse(date, formatter);
                            LocalDateTime curDate = LocalDateTime.now();

                            if (inputDate.isBefore(curDate)) {
                                err += "Input date and time is before the current date and time\n" +
                                        "or Invalid date format (yyyy-MM-dd HH:mm)\n";
                            }
                            else {
                                _date = inputDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                                Log.d("mydate1", String.valueOf(_date));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d("mydate", String.valueOf(ex));
                        err += "Invalid date format (yyyy-MM-dd HH:mm)";
                    }

                    //email
                    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    if(!email.matches(EMAIL_REGEX)){
                        err += "Invalid email format\n";
                    }

                    //phone
                    Pattern pattern = Pattern.compile("^\\+\\d{12}$");
                    Matcher matcher = pattern.matcher(phone);
                    if(!matcher.matches()){
                        err += "Invalid phone number (format +8801234556789)\n";
                    }

                    //description
                    Log.d("desc", String.valueOf(desc.length()));
                    if(desc.length() < 10 || desc.length() > 1000){
                        err += "Invalid description format (10-1000 characters)\n";
                    }

                    if(remind <= 0){
                        err += "Invalid remind time\n";
                    }
                    else{
                        remind = _date - remind * 60000;
                    }
                }
                else{
                    err += "Fill all the fields\n";
                }

                //dialog
                if(err.length() > 0){
                    showErrorDialog(err);
                    // error text show
                    errorTv.setText(err);
                }
                else{
                    // if data is valid save into database
                    if(eventID.isEmpty()){
                        eventID = name + System.currentTimeMillis();
                        Log.d("emailcheck", email);
                        eventDB.insertEvent(eventID, name, place, _date, _capacity, _budget, email, phone, desc, type, remind);

                        String keys[] = {"action", "sid", "semester", "id", "title", "place", "type", "date_time", "capacity", "budget", "email", "phone", "des", "reminder"};
                        String values[] = {"backup", "2020-2-60-054", "2023-2", eventID, name, place, type, String.valueOf(_date),String.valueOf(_capacity), String.valueOf(_budget), email, phone, desc, String.valueOf(remind)};
                        httpRequest(keys, values);

                        Log.d("Reminder", String.valueOf(remind));
                        setAlarm(remind);
                        //after creating event
                        Intent i = new Intent(CreateEventActivity.this, MainActivity.class);
                        startActivity(i);

                        //activity onDestory will be called
                        finish();
                    }
                    else {
                        eventDB.updateEvent(eventID, name, place, _date, _capacity, _budget, email, phone, desc, type, remind);
                        String keys[] = {"action", "sid", "semester", "id", "title", "place", "type", "date_time", "capacity", "budget", "email", "phone", "des", "reminder"};
                        String values[] = {"backup", "2020-2-60-054", "2023-2", eventID, name, place, type, String.valueOf(_date),String.valueOf(_capacity), String.valueOf(_budget), email, phone, desc, String.valueOf(remind)};
                        httpRequest(keys, values);
                        setAlarm(remind);

                        Intent i = new Intent(CreateEventActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateEventActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, "event details");
                i.setType("text/plain");

                startActivity(i);
            }
        });
        // end of on create
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
                String url= "https://cse489.helloworlddev.software/index.php"; // TODO Change it
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
                    //updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void setAlarm(long time) {
        Intent i = new Intent(CreateEventActivity.this, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateEventActivity.this, ALARM_CODE, i, PendingIntent.FLAG_MUTABLE);
        // Use RTC_WAKEUP for real-time alarms
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }


    private void showErrorDialog(String errMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errMsg);
        builder.setCancelable(true);

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}