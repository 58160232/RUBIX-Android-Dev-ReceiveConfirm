package com.example.wiroon.test1.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wiroon.test1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickingFragment extends Fragment {


    public PickingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picking, container, false);
    }

}
