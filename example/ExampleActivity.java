//Author: KeyLo99
//Preview Camera Example

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import java.io.FileOutputStream;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML must contain FrameLayout element.
        final PreviewCam previewCam = new PreviewCam(this, PreviewCam.CAM_FRONT , new PreviewCam.CamAction() {
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
        ((FrameLayout) findViewById(R.id.frameLayout)).addView(previewCam);
        ((Button) findViewById(R.id.btnAction)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCam.takePicture();
            }
        });
    }
}
