package com.example.wiroon.test1.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wiroon.test1.Appconfig;
import com.example.wiroon.test1.AsyncTaskAdapter;
import com.example.wiroon.test1.MainActivity;
import com.example.wiroon.test1.R;
import com.example.wiroon.test1.TransitAdapter;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransitFragment extends Fragment {

    private EditText txtBarcode;
    private Button btnScan;
    private Button btnCamera;
    private Button btnConfirm;
    private Button btnCancel;
    private Button btnOk;
    private EditText txt_qty;
    private EditText txt_item;
    private EditText txt_lot;

    private Appconfig appconfig;
    private JSONObject js;
    private RecyclerView mRecyclerViewTransit;
    private TransitAdapter itemadapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout ln;
    private ScaleAnimation animbye;

    private EditText location;
    private Dialog dialog;
    private Context mContext;
    private JSONObject temp;

    public TransitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_transit, container, false);


//
//        mContext = getContext();
//        mRecyclerViewTransit = (RecyclerView) view.findViewById(R.id.Recyclerview_Transit);
//        if (appconfig == null) appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
//        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
//        if (itemadapter != null) {
//            mLayoutManager = new LinearLayoutManager(getActivity());
//            mRecyclerViewTransit.setLayoutManager(mLayoutManager);
//            mRecyclerViewTransit.setAdapter(itemadapter);
//        } else {
//            itemadapter = new TransitAdapter(mContext);
//        }

        btnCamera = view.findViewById(R.id.btn_transit_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, CameraFragment.newInstance("Transit_Barcode"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });

        txtBarcode = view.findViewById(R.id.txt_transit_sticker);
        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtBarcode.getText().toString().trim().isEmpty())
                    MainActivity.typingTransit(true);
                else MainActivity.typingTransit(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        txt_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                {
//                    if (Double.parseDouble(txt_qty.getText().toString()) == 0) {
//                        txt_qty.getText().clear();
//                    }
//                } else {
//                    if (txt_qty.getText().toString().equals("") || Double.parseDouble(txt_qty.getText().toString()) == 0) {
//                        txt_qty.setText("0.0");
//                    }
//                }
//            }
//        });
//        txt_qty.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (txt_qty.getText().toString().equals(".")) {
//                    txt_qty.setText("0.");
//                    txt_qty.setSelection(txt_qty.getText().length());
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        btnScan = view.findViewById(R.id.btn_transit_search);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String QRcode = new Gson().toJson(txtBarcode.getText().toString().trim());
                    String dc = new Gson().toJson(Appconfig.DCID);

                    JSONObject job = new JSONObject();
                    job.accumulate("QRcode", QRcode);
                    job.accumulate("DCID", dc);

                    AsyncTaskAdapter sr = new AsyncTaskAdapter(job);
                    sr.execute("api/MobileTransit/SearchTransitInstructionByQRCode");
                    Log.d("sr","value of sr"+sr);

                    String g = " no internet ";
                        g = sr.get(10000, TimeUnit.MILLISECONDS).toString();
                        Log.d("value","g =>"+g);
                        js = new JSONObject(g.substring(2, g.length() - 1));
                        Log.d("js","value of js"+js);
                        if (js.has("Message")) {
                            new AlertDialog.Builder(getActivity()).setMessage(js.getString("Message")).setNegativeButton("OK", null).show();
                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                        } else if (js.getString("IsBarCode").toString().equals("false") && itemadapter.checkSticker(js.getString("Sticker"))) {
                            new AlertDialog.Builder(getActivity()).setMessage("Sticker already scan.").setNegativeButton("OK", null).show();
                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                        } else if (js.getString("IsBarCode").toString().equals("false")) {
                            itemadapter.add(js.toString());
                            btnConfirm.setEnabled(true);
                            btnConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                            MainActivity.checkBackTransit(true);
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerViewTransit.setLayoutManager(mLayoutManager);
                            mRecyclerViewTransit.setAdapter(itemadapter);
                            itemadapter.notifyDataSetChanged();
                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                        } else if (js.getString("HasSticker").toString().equals("true")) {
                            new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("The item has StickerControl.\nPlease scan sticker code.").show();
                            txtBarcode.setText("");
                        } else {
                            txt_item.setText("Item : " + js.getString("ItemName"));
                            txt_qty.setText("0.0");
                            anim(ln, 0, 1, 0, 1, 300, true);
                            txtBarcode.setEnabled(false);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel = view.findViewById(R.id.btn_transit_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                txt_lot.setText("");
//                txt_qty.setText("");
//                txtBarcode.setEnabled(true);
//                txtBarcode.setText("");
//                anim(ln, 1, 0, 1, 0, 100, false);
//                if (temp != null) {
//                    itemadapter.add(temp.toString());
//                    temp = null;
//                }
            }
        });

        btnOk = view.findViewById(R.id.btn_transit_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnConfirm = view.findViewById(R.id.btn_transit_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mRecyclerViewTransit.getAdapter() != null && itemadapter.getItemCount() != 0 && itemadapter != null) {
//                    dialog = new Dialog(getActivity());
//                    dialog.setCancelable(false);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_transit);
//                    dialog.show();
//                    location = (EditText) dialog.findViewById(R.id.txt_dialogt_location);
//                    location.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable ss) {
//                            String s = ss.toString();
//                            if (!s.equals(s.toUpperCase())) {
//                                s = s.toUpperCase();
//                                location.setText(s);
//                                location.setSelection(s.length());
//                            }
//                        }
//                    });
//                    final Button btncameradialog = (Button) dialog.findViewById(R.id.btn_transit_camera);
//                    final Button btncanceldialog = (Button) dialog.findViewById(R.id.btn_dialogt_cancel);
//                    final Button btnconfirmdialog = (Button) dialog.findViewById(R.id.btn_dialogt_confirm);
//                    btncameradialog.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Transit_Location"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
//                            dialog.dismiss();
//
//                        }
//                    });
//                    btncanceldialog.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    btnconfirmdialog.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            confirmTransit(location.getText().toString());
//                        }
//                    });
//                } else {
//                    new AlertDialog.Builder(getActivity()).setMessage("No data to confirm").setNegativeButton("OK", null).setTitle("Warning!").show();
//                }
            }
        });

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (itemadapter.getItemCount() > 0) {
//            btnConfirm.setEnabled(true);
//            btnConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
//        }
//        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
//
//        if (getArguments().getString("Barcode") != null) {
//            txtBarcode.setText(getArguments().getString("Barcode"));
//            btnScan.callOnClick();
//            getArguments().remove("Barcode");
//        }
//        if (getArguments().getString("Location") != null) {
//            confirmTransit(getArguments().getString("Location"));
//            getArguments().remove("Location");
//        }
//    }

//    private void confirmTransit(String locationQR) {
//        String chlocation = "no internet";
//        try {
//            chlocation = new AsyncTaskAdapter(new Gson().toJson(locationQR), appconfig).execute("api/MobileTransit/ConfirmTransit").get(10000, TimeUnit.MILLISECONDS).toString();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (locationQR.trim().isEmpty()) {
//            Toast.makeText(getActivity(), "Input location", Toast.LENGTH_LONG).show();
//        } else if (chlocation.equals("false")) {
//            Toast.makeText(getActivity(), "Location " + locationQR + " not exist", Toast.LENGTH_LONG).show();
//            location.setText("");
//            location.requestFocus();
//        } else if (chlocation.equals("no internet")) {
//            Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
//        } else {
//            JSONObject tmp = new JSONObject();
//            try {
//                tmp.accumulate("LocationCode", locationQR);
//                tmp.accumulate("DtItem", new JSONArray(itemadapter.getList_transit()));
//                tmp.accumulate("CurrentUser", appconfig.getUser());
//                AsyncTaskAdapter ae = new AsyncTaskAdapter(tmp.toString(), appconfig);
//                ae.execute("api/MobileTransit/ConfirmTransit");
//                String g = ae.get(10000, TimeUnit.MILLISECONDS).toString();
//                if (g.equals("true")) {
//                    dialog.cancel();
//                    MainActivity.typingTransit(false);
//                    MainActivity.checkBackTransit(false);
//                    getActivity().onBackPressed();
//                    new AlertDialog.Builder(getActivity()).setMessage("Transit complete").setTitle("Transit").setPositiveButton("OK", null).show();
//                    appconfig.clear();
//                    btnConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
//                    btnConfirm.setEnabled(false);
//                    MainActivity.checkBackTransit(false);
//
//                } else {
//                    new AlertDialog.Builder(getActivity()).setMessage("Transit not complete " +
//                            "\nPlease check internet").setTitle("Transit").setPositiveButton("OK", null).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//    }

    private void anim(LinearLayout lnn, int fx, int tx, int fy, int ty, int duration, boolean appear) {
        if (appear) {
            mRecyclerViewTransit.setEnabled(false);
            MainActivity.setDialogTransit(true);
            animbye = new ScaleAnimation(fx, tx, fy, ty);
            animbye.setDuration(duration);
            lnn.startAnimation(animbye);
            lnn.setVisibility(View.VISIBLE);
        } else {
            mRecyclerViewTransit.setEnabled(true);
            MainActivity.setDialogTransit(false);
            lnn.setVisibility(View.GONE);
        }
    }

}
