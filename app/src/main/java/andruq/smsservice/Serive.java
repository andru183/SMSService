package andruq.smsservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Andru on 06/04/2015.
 */
public class Serive extends Service {


    private ArrayList<String> list = new ArrayList<String>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Reader();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }



    public List<String> getWordList() {
        return list;
    }
}
