package com.eduard.eventsreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationMessage extends AppCompatActivity {
    TextView tvNotfMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);

        tvNotfMessage = findViewById(R.id.tv_message);
        Bundle bundle = getIntent().getExtras();                                                    //get the name of event from AlarmBroadcast
        tvNotfMessage.setText(bundle.getString("message"));                                    //set the text with the name of event
    }
}
