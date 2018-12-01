package com.fruithat.nutrigenie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CircleGraphFragment extends Fragment {

    public CircleGraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in quote_fragment.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        return inflater.inflate(R.layout.fragment_circlegraph, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);
    }

}
