package com.example.wiroon.test1.Fragment;


import android.content.Intent;
import android.net.http.RequestQueue;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wiroon.test1.Appconfig;
import com.example.wiroon.test1.AsyncTaskAdapter;
import com.example.wiroon.test1.MainActivity;
import com.example.wiroon.test1.R;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.text.DateFormat.getDateInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecievingFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    //scan
    private Button btnScan;
    private EditText editText_scan;
    //config
    Appconfig appconfig;
    //spinner
    private Spinner spn_date;

    public RecievingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recieving, container, false);

//        if (appconfig == null){
//            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
//        }
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        btnScan = v.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, CameraFragment.newInstance("Receive_Do"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //find id
        editText_scan = v.findViewById(R.id.edt_scan);
        //set default

        //scan
        if (editText_scan.getText().toString().equals("")) {
            editText_scan.setText("-");
            editText_scan.setEnabled(true);
            editText_scan.setTextSize(18);
            editText_scan.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        } else {
            editText_scan.setEnabled(false);
            editText_scan.setTextSize(18);
            editText_scan.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        }
        //spinner
        spn_date = v.findViewById(R.id.spn_date);
        Log.d("getSerial", "Serial: "+Appconfig.Serial);
        String dc = Appconfig.DCID;

        AsyncTaskAdapter SR = new AsyncTaskAdapter(dc);
        SR.execute("api/MobileReceiving/SearchReceivingInstructionEntry?DCID=" + dc);
        try {
            JSONArray data = new JSONArray(SR.get(10000, TimeUnit.MILLISECONDS).toString());
            String[] spinnerArray = new String[data.length()];
            HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
            String Pattern = "dd/MM/yyyy";
            SimpleDateFormat formatdate = new SimpleDateFormat(Pattern, Locale.getDefault());
            for (int i = 0; i < data.length(); i++)
            {
                JSONObject DC = new JSONObject(data.get(i).toString());
                String date = DC.getString("ReceiveDateStr");
//                String ResultDate = formatdate.format(date);
                String ResultDate = date.toString().replace("." ,"/");
                spinnerMap.put(i,ResultDate);
                spinnerArray[i] = ResultDate;
            }

            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_date.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //receive data
        if (getArguments() != null) {
            if (getArguments().getString("WhereScan") != null) {
                if (getArguments().getString("WhereScan").equals("Do")) {
                    editText_scan.setText(getArguments().getString("result"));
                }
            }
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
