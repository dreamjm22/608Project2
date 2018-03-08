package com.example.mvservice2;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;

/**
 * Created by sunting on 10/13/17.
 */

public class MVService extends Service {
    // for camera
    protected static final String TAG = "myLog";
    protected static final int CAMERACHOICE = CameraCharacteristics.LENS_FACING_BACK;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession session;
    protected ImageReader imageReader;

    // for socket communication
    Handler updateConversationHandler;
    Thread communicationThread = null;
    int portNum = 9999;   //the port number between 0 and 1023 are reserved for priviliged users
    ServerSocket echoServer = null;
    Socket clientSocket = null;
    DataInputStream is;
    DataOutputStream os;


    protected MotionDetecor m_detector;
    Point centroid;
    Mat curframe;
    boolean flag_ready = true;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(MVService.this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("", "OpenCV loaded successfully");
                    centroid = new Point(0, 0);
                    m_detector = new MotionDetecor();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    protected CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            actOnReadyCameraDevice();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.w(TAG, "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice.StateCallback onError " + error);
        }
    };

    protected CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onReady(CameraCaptureSession session) {
            MVService.this.session = session;
            try {
                session.setRepeatingRequest(createCaptureRequest(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }


        @Override
        public void onConfigured(CameraCaptureSession session) {

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    protected ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "onImageAvailable");
            Image img = reader.acquireLatestImage();
            if (img != null) {
                processImage(img);
                img.close();
            }
        }
    };

    public void readyCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String pickedCamera = getCamera(manager);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.openCamera(pickedCamera, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(800, 600, ImageFormat.JPEG, 2 /* images buffered */);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
            Log.d(TAG, "imageReader created");
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public String getCamera(CameraManager manager){
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation != CAMERACHOICE) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand flags " + flags + " startId " + startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        readyCamera();
        updateConversationHandler = new Handler();
        this.communicationThread = new Thread(new CommunicationThread());
        this.communicationThread.start();

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate service");
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        super.onCreate();
    }

    public void actOnReadyCameraDevice()
    {
        try {
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        try {
            session.abortCaptures();
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }

        super.onDestroy();

        try {
            echoServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.close();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    private void processImage(Image image){
        //Process image data
        Mat buf = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buf.put(0, 0, bytes);

        curframe = Imgcodecs.imdecode(buf, IMREAD_COLOR);
        image.close();

        flag_ready = false;
        centroid = m_detector.MotionDetection(curframe);
        flag_ready = true;

    }

    protected CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class CommunicationThread implements Runnable {

        public void run()
        {
            // ---- create the server and listen -----------------
            try
            {
                echoServer = new ServerSocket(portNum);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }

            // ----- connect to a client and get the input/output stream -----------------
            try
            {
                clientSocket = echoServer.accept();
                is = new DataInputStream(clientSocket.getInputStream());
                os = new DataOutputStream(clientSocket.getOutputStream());
            }
            catch (IOException e)
            {
                System.out.println(e);
            }

            while (true)
            {
                // ------send the message to server --------------
                if (centroid.x > 0 & centroid.y >0)
                {
                    try
                    {
                        os.writeDouble(centroid.x);
                        os.writeDouble(centroid.y);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                /*
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                */
            }
        }
    }

}
