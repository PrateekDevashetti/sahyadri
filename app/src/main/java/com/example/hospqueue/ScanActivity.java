package com.example.hospqueue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.io.IOException;

public class ScanActivity  extends AppCompatActivity {
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        surfaceView=findViewById(R.id.camprview);
        surfaceView.setZOrderMediaOverlay(true);
        surfaceHolder=surfaceView.getHolder();
        barcodeDetector=new BarcodeDetector.Builder(this).setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS).build();
        if(!barcodeDetector.isOperational()){
            Toast.makeText(getApplicationContext(),"Sorry not operational",Toast.LENGTH_SHORT).show();
            this.finish();
        }
        cameraSource =new CameraSource.Builder(this,barcodeDetector).setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(24)
                .setRequestedPreviewSize(1920,1024)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                    //Toast.makeText(getApplicationContext(),"Working cam",Toast.LENGTH_SHORT).show();
                }
                catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"No cam",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i("SURFACE","CHANGING");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("SURFACE","Destroyed");
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode=detections.getDetectedItems();
                if(barcode.size()>0){
                    Intent intent=new Intent();
                    intent.putExtra("barcode",barcode.valueAt(0));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

}