package com.wix.reactnativenotifications.core.notification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotificationProps {

    protected Bundle mBundle;

    public Boolean shouldNotPost() {
        return mBundle.get("mp_message") == null;
    }

    public PushNotificationProps(Bundle bundle) {
        mBundle = bundle;
    }

    public String getTitle() {
        try {
            String title = mBundle.getString("title");
            if (title != null) {
                return title;
            }
            JSONObject json = new JSONObject(mBundle.getString("aps"));
            JSONObject alert = json.getJSONObject("alert");
            return alert.getString("title");

        } catch (JSONException e) {
        } catch (NullPointerException e) {
        }

        return null;
    }

    public String getBody() {
        try {
            String body = mBundle.getString("alert");
            if (body != null) {
                return body;
            }
            String aps = mBundle.getString("aps");
            JSONObject json = new JSONObject(aps);
            JSONObject alert = json.getJSONObject("alert");
            return alert.getString("body");
        } catch (JSONException e) {
        } catch (NullPointerException e) {
        }
        return mBundle.getString("mp_message");
    }

    public Bitmap getIcon() {
        try {
            JSONObject json = new JSONObject(mBundle.getString("att"));
            return getBitmapFromURL(json.getString("id1"));
        } catch (JSONException e) {
        } catch (NullPointerException e) {
        }
        return null;
    }

    public Bundle asBundle() {
        return (Bundle) mBundle.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        for (String key : mBundle.keySet()) {
            sb.append(key).append("=").append(mBundle.get(key)).append(", ");
        }
        return sb.toString();
    }

    protected PushNotificationProps copy() {
        return new PushNotificationProps((Bundle) mBundle.clone());
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
