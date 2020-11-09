package com.asligresik.inventoryApp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.util.RPResultListener;
import com.asligresik.inventoryApp.util.RuntimePermissionUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

//import github.nisrulz.qreader.QRDataListener;
//import github.nisrulz.qreader.QREader;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScanFragment extends Fragment {
    private String scan;
    private Integer urutan;
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    private boolean isReadyScan = true;
    TextView textViewScanResult;
    public static final int PERMISSION_REQUEST_CODE = 1;


    public ScanFragment() {
    }

    private static final String cameraPerm = Manifest.permission.CAMERA;

    // UI
    private TextView text;

    // QREader
//  private SurfaceView mySurfaceView;
    //private QREader qrEader;

    boolean hasCameraPermission = false;

    void restartActivity() {
        startActivity(new Intent(getActivity(), StockActivity.class));
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            RuntimePermissionUtil.onRequestPermissionsResult(grantResults, new RPResultListener() {
                @Override
                public void onPermissionGranted() {
                    if ( RuntimePermissionUtil.checkPermissonGranted(getActivity(), cameraPerm)) {
                        restartActivity();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    // do nothing
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(getContext(), cameraPerm);
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        cameraView = (SurfaceView) view.findViewById(R.id.camera_view);


        textViewScanResult = (TextView) view.findViewById(R.id.code_info);
        if (hasCameraPermission) {
            // Setup QREader
            startScan();
        } else {
            RuntimePermissionUtil.requestPermission((AppCompatActivity) getActivity(), cameraPerm, 100);
        }
        Bundle b = getArguments();
        scan = b.getString("scan");
        urutan = b.getInt("urutan",-1);
        return view;
    }


    private void startScan() {
        barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                     //   .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        if(!barcodeDetector.isOperational()){
            Toast.makeText(getActivity(), "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();

        }
        cameraSource = new CameraSource
                .Builder(getActivity(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(640, 480)
                //            .setRequestedFps(15.0f)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.CAMERA"}, PERMISSION_REQUEST_CODE);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (isReadyScan) {
                    if (barcodes.size() != 0) {
                        isReadyScan = false;
                        String rScan = barcodes.valueAt(0).displayValue.toString();
                        textViewScanResult.post(new Runnable() {    // Use the post method of the TextView
                            public void run() {
                                String rScan = barcodes.valueAt(0).displayValue.toString();
                                if (!rScan.equalsIgnoreCase("")) {
                                    handlerResultScan(rScan);
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void handlerResultScan(final String hasilScan){
        switch (scan){
            case "kodeBarang":
                ((StockActivity)getActivity()).setKodeBarang(hasilScan);
                break;
            case "kodeRak":
                ((StockActivity)getActivity()).setKodeRak(hasilScan);
                break;
            case "kodeBatchOrder":
                ((StockActivity)getActivity()).setKodeBatchOrder(hasilScan);
                break;
            case "kodeRakAsal":
                ((StockActivity)getActivity()).setKodeRakAsal(hasilScan);
                break;
            case "kodeRakTujuan":
                ((StockActivity)getActivity()).setKodeRakTujuan(hasilScan);
                break;
        }
        ((StockActivity)getActivity()).setUrutan(urutan);
        ((StockActivity)getActivity()).loadDefaultFragment();
    }
}

