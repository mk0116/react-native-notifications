package com.wix.reactnativenotifications.fcm;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mixpanel.android.mpmetrics.MixpanelPushNotification;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

/**
 * Instance-ID + token refreshing handling service. Contacts the FCM to fetch the updated token.
 *
 * @author amitd
 */
public class FcmInstanceIdListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message){
        Bundle bundle = message.toIntent().getExtras();
        Log.d(LOGTAG, "New message from FCM: " + bundle);

        try {
            final IPushNotification notification = PushNotification.get(getApplicationContext(), bundle);
            notification.onReceived();

            Intent messageIntent = message.toIntent();
            MixpanelPushNotification mixpanelPushNotification = new MixpanelPushNotification(getApplicationContext());
            Method privateStringMethod = MixpanelPushNotification.class.getDeclaredMethod("createNotification", Intent.class);
            privateStringMethod.setAccessible(true);
            privateStringMethod.invoke(mixpanelPushNotification, messageIntent);


        } catch (IPushNotification.InvalidNotificationException e) {
            // An FCM message, yes - but not the kind we know how to work with.
            Log.v(LOGTAG, "FCM message handling aborted", e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
