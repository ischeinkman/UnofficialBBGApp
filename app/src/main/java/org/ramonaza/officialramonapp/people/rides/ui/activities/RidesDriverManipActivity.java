package org.ramonaza.officialramonapp.people.rides.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import org.ramonaza.officialramonapp.R;
import org.ramonaza.officialramonapp.helpers.ui.activities.BaseActivity;
import org.ramonaza.officialramonapp.people.rides.ui.fragments.RidesDriverManipFragment;

public class RidesDriverManipActivity extends BaseActivity {

    private int driverId;
    public static final String EXTRA_DRIVERID="org.ramonaza.officialramonapp.DRIVER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_driver_manip);
        Intent intent=getIntent();
        driverId=intent.getIntExtra(EXTRA_DRIVERID,0);
        FragmentManager fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, RidesDriverManipFragment.newInstance(driverId)).commit();
    }
}
