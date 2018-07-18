package com.example.wiroon.test1.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wiroon.test1.MainActivity;
import com.example.wiroon.test1.R;
import com.example.wiroon.test1.login;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.Size;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CameraFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private String result = "empty";
    private int RequestCameraPermissionID = 1001;
    private String page;
    private Button mScanType;
    private int TypeScan = 0;
    private TextView state;
    private BarcodeView mBarcodeView;
    Collection<BarcodeFormat> formats1D,formats2D;

    public CameraFragment() {
    }

    public static CameraFragment newInstance(String a){
        CameraFragment ar = new CameraFragment();
        Bundle bd = new Bundle();
        bd.putString("Page",a);
        ar.setArguments(bd);
        return ar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            page = getArguments().getString("Page");
        }

        MainActivity.FragmentName = "ScanCodeFragment";
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
//        MainActivity.toggle.setDrawerIndicatorEnabled(false);
//        MainActivity.drawer.closeDrawer(GravityCompat.START);
//        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
        }

        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.camerabarcode);
        mBarcodeView = (BarcodeView) view.findViewById(R.id.zxing_barcode_surface);

        state = (TextView) view.findViewById(R.id.statebarcode) ;
        mScanType = (Button) view.findViewById(R.id.btn_scan_type);

        TypeScan = 0;
        formats1D = Arrays.asList(BarcodeFormat.CODE_128,
                                  BarcodeFormat.CODE_39,
                                  BarcodeFormat.CODE_93,
                                  BarcodeFormat.CODABAR,
                                  BarcodeFormat.UPC_A,
                                  BarcodeFormat.UPC_E,
                                  BarcodeFormat.EAN_8,
                                  BarcodeFormat.EAN_13
                                  );

        formats2D = Arrays.asList(BarcodeFormat.QR_CODE,
                                  BarcodeFormat.AZTEC,
                                  BarcodeFormat.DATA_MATRIX,
                                  BarcodeFormat.MAXICODE
                                  );

        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats1D, null, null, false));

        barcodeView.pause();
        mBarcodeView.setFramingRectSize(new Size(1400, 600));
        barcodeView.resume();

        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(callback);

        state.setText("1D");

        mScanType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TypeScan ==0) {
                    mScanType.setBackgroundResource(R.drawable.ic_barcode);
                    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats2D, null, null, false));
                    TypeScan = 1;

                    barcodeView.pause();
                    mBarcodeView.setFramingRectSize(new Size(1200,1200));
                    barcodeView.resume();
                    state.setText("2D");
                } else {
                    mScanType.setBackgroundResource(R.drawable.ic_qr);
                    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats1D, null, null, false));
                    TypeScan = 0;

                    barcodeView.pause();
                    mBarcodeView.setFramingRectSize(new Size(1400, 600));
                    barcodeView.resume();
                    state.setText("1D");
                }
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onDetect(){
        Fragment tmp;
        if (page.equals("Receive_Do")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Do");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, tmp, "tag_receiving").commit();
            Toast.makeText(getActivity(),result, Toast.LENGTH_SHORT).show();
        }
        if(page.equals("Transit_Barcode")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_transit");
            Bundle bd = new Bundle();
            bd.putString("Barcode",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content,tmp,"tag_transit").addToBackStack("MenuTransit").commit();
        }
        else if(page.equals("Transit_Location")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_transit");
            Bundle bd = new Bundle();
            bd.putString("Location",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content,tmp,"tag_transit").addToBackStack("MenuTransit").commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MainActivity.toggle.setDrawerIndicatorEnabled(true);
//        MainActivity.drawer.closeDrawer(GravityCompat.START);
//        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult resultReader) {
                if (resultReader.getText() != null) {
                    result = resultReader.getText();
                    onDetect();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }
}
