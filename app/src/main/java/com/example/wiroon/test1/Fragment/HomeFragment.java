package com.example.wiroon.test1.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.example.wiroon.test1.Appconfig;
import com.example.wiroon.test1.AsyncTaskAdapter;
import com.example.wiroon.test1.MainActivity;
import com.example.wiroon.test1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private OnClickMenu mMenu;
    Bundle bundle = new Bundle();
    Bundle extras;
    private NavigationView navigationView;
    //button
    private FragmentTransaction transaction;
    Button btn_recieving, btn_transit, btn_inguiry, btn_change, btn_picking, btn_shipping, btn_return, btn_counting;
    //spinner warehouse
    private Spinner spinner;
    private HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        //
        btn_recieving = v.findViewById(R.id.btn_recieving); //id = 1
        btn_transit   = v.findViewById(R.id.btn_transit); //id = 2
        btn_inguiry   = v.findViewById(R.id.btn_inguiry);//id = 3
        btn_change    = v.findViewById(R.id.btn_change);//id = 4
        btn_picking   = v.findViewById(R.id.btn_picking);//id = 5
        btn_shipping  = v.findViewById(R.id.btn_shipping);//id = 6
        btn_return    = v.findViewById(R.id.btn_return);//id = 7
        btn_counting  = v.findViewById(R.id.btn_counting);//id = 8

        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        //
        btn_recieving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(1);

            }
        });
        btn_transit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(2);
            }
        });
        btn_inguiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(3);
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(4);
            }
        });
        btn_picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(5);
            }
        });
        btn_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(6);
            }
        });
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(7);
            }
        });
        btn_counting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.Changefragment(8);
            }
        });

        //appconfig
//        if (appconfig == null){
//            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
//        }
        if (!Appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        //spinner
        spinner = v.findViewById(R.id.spn_date);
        spinner.setOnItemSelectedListener(this);

        String id = Appconfig.getUser();

        AsyncTaskAdapter SR = new AsyncTaskAdapter(id);
        SR.execute("api/MobileMain/LoadDCIDList?UserLogin=" + id);
        try {
            JSONArray data = new JSONArray(SR.get(10000, TimeUnit.MILLISECONDS).toString());
            String[] spinnerArray = new String[data.length()];

            for (int i = 0; i < data.length(); i++)
            {
                JSONObject DC = new JSONObject(data.get(i).toString());
                spinnerMap.put(i,DC.getString("DCID"));
                spinnerArray[i] = DC.getString("DCCode");
            }

            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            Appconfig.DCID = spinnerMap.get(spinner.getSelectedItemPosition());

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String name = spinner.getSelectedItem().toString();
        String id1 = spinnerMap.get(spinner.getSelectedItemPosition());
        Log.d("spin", "Key: "+id1+" , "+"Value: "+name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //Check nav menu follow menu
    public interface OnClickMenu {
        public void Changefragment(int i);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMenu = (OnClickMenu) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnClickMenu");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMenu = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationView = null;
    }


}
