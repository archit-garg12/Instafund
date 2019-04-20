package com.fbla.test.fblainstafund;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 09/02/17.
 */

public class PostDataToLoderManager extends AsyncTask<String,String,Integer> {
    Handler myEventHandler;

    public PostDataToLoderManager(Handler myEventHandler){
        this.myEventHandler = myEventHandler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myEventHandler.sendEmptyMessage(1);

    }

    @Override
    protected Integer doInBackground(String... params) {
    int responceCode =0;
        URL url = null;
        try {
          url = new URL(FBLAUtility.URL+"/v1/items");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);

        conn.setRequestProperty("Content-type","application/json");
        conn.setRequestProperty("Accept","application/json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId",params[0]);
            jsonObject.put("ItemName",params[1]);
            jsonObject.put("itemState",params[2]);
            jsonObject.put("ItemPrice",params[3]);
            jsonObject.put("ItemDescription",params[4]);
            jsonObject.put("ItemImage",params[5]);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
            Log.d(TAG,jsonObject.toString());
        writer.write(jsonObject.toString());

        writer.flush();
        writer.close();
        os.close();
            responceCode =  conn.getResponseCode();
            Log.d(TAG,"posted status: " + responceCode);
            if(responceCode == 200){
                Log.d(TAG,"posted....");
            }
        conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responceCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if(integer == 200){
            myEventHandler.sendEmptyMessage(2);
        }
        else{
            myEventHandler.sendEmptyMessage(3);

        }

    }
}
