//Author: KeyLo99
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class PreviewCam extends SurfaceView implements SurfaceHolder.Callback {
    public static int CAM_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static int CAM_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private SurfaceHolder mHolder;
    public Camera camera;
    public interface CamAction {
        void onTaken (byte[] data);
    }
    private int camInfo = 0;
    private CamAction delegate = null;
    public PreviewCam(Context context) {
        super(context);
    }
    public PreviewCam(Context context, int camInfo, CamAction delegate) {
        super(context);
        this.delegate = delegate;
        this.camInfo = camInfo;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == camInfo) {
                try {
                    camera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    try {
                        camera = Camera.open();
                    } catch (RuntimeException e2) {
                        e2.printStackTrace();
                        delegate.onTaken(null);
                    }
                }
            }
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(new PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera arg1) {
                    PreviewCam.this.invalidate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera = null;
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(w, h);
        camera.setParameters(parameters);
        camera.startPreview();
    }
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {}
    };
    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {}
    };
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            delegate.onTaken(data);
        }
    };
    public void takePicture(){
        try {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    camera.takePicture(shutterCallback, rawCallback,
                            jpegCallback);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}