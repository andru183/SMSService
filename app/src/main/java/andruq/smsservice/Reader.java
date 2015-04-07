package andruq.smsservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andru on 06/04/2015.
 */
public class Reader extends BroadcastReceiver{

    private SharedPreferences preferences;



    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody().trim().toLowerCase();
                        String sender = msgs[i].getOriginatingAddress();

                        if(msgBody.toLowerCase().startsWith("buy"))
                        {
                            buy(msgBody,msg_from);
                        }

                        else if(msgBody.toLowerCase().startsWith("transfer"))
                        {
                            transfer(msgBody,msg_from);
                        }

                        else if(msgBody.toLowerCase().startsWith("register"))
                        {
                            register(msgBody,msg_from);
                        }
                        else
                        {
                            Log.e("SMS info", "no action was taken");
                        }


                        Log.e("Message", msgBody);
                        Log.e("Sender", sender);
                        Toast.makeText(context,msgBody,Toast.LENGTH_LONG);




                    }
                }catch(Exception e){
                    e.printStackTrace();
                            Log.e("Exception caught",e.toString());
                }
            }
        }
    }



    private String getSingleLineResponse(List<NameValuePair> pairs, String subAddress)
    {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(subAddress);

        String responseBody;
        HttpResponse response;
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response = client.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            responseBody = reader.readLine();

            return responseBody;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void transfer(String message,String sender)
    {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("sender", sender));
        pairs.add(new BasicNameValuePair("message", message));

        Log.e("return",getSingleLineResponse(pairs,"http://home.andruquinn.com/fyp/transfer.php"));
    }

    public void buy(String message, String sender)
    {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("sender", sender));
        pairs.add(new BasicNameValuePair("message", message));

        Log.e("return",getSingleLineResponse(pairs,"http://home.andruquinn.com/fyp/buy.php"));
    }

    public void register(String message, String sender)
    {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("sender", sender));
        pairs.add(new BasicNameValuePair("message", message));

        Log.e("return",getSingleLineResponse(pairs,"http://home.andruquinn.com/fyp/register.php"));
    }
}
