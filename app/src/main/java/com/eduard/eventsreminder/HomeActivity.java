package com.eduard.eventsreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import entities.EventModel;
import entities.UserModel;

public class HomeActivity extends AppCompatActivity {

    Button saveBTN;
    Button dateBTN;
    Button timeBTN;
    EditText edtEventName;
    String timeToNotify;
    ListView myList;
    ArrayAdapter<EventModel> adapter;
    dbManager db = new dbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password","");
        Toast.makeText(getApplicationContext(), "WELCOME " + username + "  !", Toast.LENGTH_LONG).show();               //show a welcome message with the username used in login activity

        edtEventName = findViewById(R.id.edtEventName);
        saveBTN = findViewById(R.id.btnSubmit);
        dateBTN = findViewById(R.id.btnDate);
        timeBTN = findViewById(R.id.btnTime);
        myList = findViewById(R.id.lv_events);

        String email = getIntent().getStringExtra("email");                                  //get the email to creat a UserModel
        UserModel userModel = new UserModel(username,password,email);
        String iduser = String.valueOf(db.getUserId(userModel));                                    //call the method getUserId to get the id from TABLE_USERS, for displaying only the events of the logged user with that particular id

        displayEvents(iduser);                                                                      //call the method displayEvents to display the evnts of the logged user


        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getIntent().getStringExtra("email");                          //get the email to creat a UserModel
                UserModel userModel = new UserModel(username,password,email);
                String iduser = String.valueOf(db.getUserId(userModel));                            //initialize the iduser with the help of getUserId method

                EventModel eventModel = new EventModel(1,edtEventName.getText().toString(),dateBTN.getText().toString(),timeBTN.getText().toString(),Integer.parseInt(iduser));     //creat an EventModel object with datas from the edittext and buttons
                if (eventModel.getEventName().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your event", Toast.LENGTH_SHORT).show();
                } else {
                    if (eventModel.getTime().equals("time") || eventModel.getDate().equals("date")) {
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else {

                        processInsert(eventModel);                                                  //add in db
                        displayEvents(iduser);                                                      //display imediately after adding in db

                        edtEventName.setText("");                                                   //set the text of buttons as EMPTY to prevent remaining date and time used previously
                        dateBTN.setText("");
                        timeBTN.setText("");
                    }
                }
            }
        });

        dateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        timeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventModel eventModel = (EventModel) parent.getItemAtPosition(position);            //identify the EventModel from that position of we make click
                db.deleteEvent(eventModel);                                                         //delete the event find on that position
                displayEvents(iduser);                                                              //display again the events of the logged user to make sure it disappear in real time from the listview without need to restart the app
            }
        });
    }


    /** displays all the events for the user with logged with iduser
     * creating an ArrayAdapter of EventModel
     * set the adapter on a List called myList and display them as a simple_list_item_1 from android resources
     * */
    private void displayEvents(String iduser) {
        adapter = new ArrayAdapter<EventModel>(HomeActivity.this, android.R.layout.simple_list_item_1,db.getEvents(Integer.parseInt(iduser)));
        myList.setAdapter(adapter);
    }


    /** Insert the event in db and set the alarm
     * set the edittext as EMPTY after the insert, to let the text empty to creat the next eventname
     * */
    private void processInsert(EventModel eventModel) {
        String result =db.createEvent(eventModel);
        setAlarm(eventModel);
        edtEventName.setText("");
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(EventModel eventModel) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("event", eventModel.getEventName());                                    //acces the datas with an intent
        intent.putExtra("time", eventModel.getTime());
        intent.putExtra("date", eventModel.getDate());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = eventModel.getDate() + " " + timeToNotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm ON", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void selectTime () {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeToNotify = i + ":" + i1;
                timeBTN.setText(FormatTime(i, i1));                                                 //set the text of the button as a selected time
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate () {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateBTN.setText(day + "-" + (month + 1) + "-" + year);                              //set the text of the button as a selected date
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime ( int hour, int minute){

        String time;
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }

        return time;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);                                               //creating a menu an set with a personalized main_menu from resources

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_log_out){                                                  //calling the menuLogOut method
            menuLogOut(item);
        }

        return super.onOptionsItemSelected(item);
    }

    /** goes to the login activity
     * */
    public void menuLogOut (MenuItem item){
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }
}