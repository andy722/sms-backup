package com.nsu.smsbackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import static android.content.Context.MODE_PRIVATE;

public class MessageReceiver extends BroadcastReceiver {

    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION.equals(intent.getAction()))
            return;

        final Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;

        final SmsMessage[] messages = readMessages(bundle);
        if (messages.length == 0)
            return;

        final SharedPreferences prefs = context.getSharedPreferences("smsbackup", MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);
        if (username == null || password == null)
            return; // unspecified?

        final Postman postman = new Postman(username, password);

        for (SmsMessage mesage : messages) {
            // send e-mail with message contents to the current user from himself
            postman.send("SMS backup", mesage.getMessageBody(), username);
        }
    }

    private SmsMessage[] readMessages(Bundle bundle) {
        final Object[] pdus = (Object[]) bundle.get("pdus");
        final SmsMessage[] messages = new SmsMessage[pdus.length];

        for (int i = 0; i < messages.length; i++)
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

        return messages;
    }
}