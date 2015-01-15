package org.ramonaza.officialramonapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ilan Scheinkman on 1/13/15.
 */
public class ContactAlephFragment extends Fragment{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Contact aleph;

    public static ContactAlephFragment newInstance(int sectionNumber, Contact aleph) {
        ContactAlephFragment fragment = new ContactAlephFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setAlpeh(aleph);
        fragment.setArguments(args);
        return fragment;
    }
    public void setAlpeh(Contact aleph){
        this.aleph=aleph;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(this.aleph.getName());
        View rootView=inflater.inflate(R.layout.fragment_contact_page,container,false);
        LinearLayout rootLayout=(LinearLayout) rootView.findViewById(R.id.cPageLayout);
        TextView information=new TextView(getActivity());
        information.setTextSize(22);
        String infoDump=String.format("Name:   %s\nSchool:  %s\nEmail:  %s\nPhone:   %s\n",aleph.getName(),aleph.getSchool(),aleph.getEmail(),aleph.getPhoneNumber());
        information.setText(infoDump);
        rootLayout.addView(information);
        Button callButton=new Button(getActivity());
        callButton.setText("Call");
        callButton.setOnClickListener(new CallButtonListener().setAleph(this.aleph));
        rootLayout.addView(callButton);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((FrontPage) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public class CallButtonListener implements View.OnClickListener{
        Contact contactAleph;
        public CallButtonListener setAleph(Contact inAleph){
            this.contactAleph=inAleph;
            return this;
        }
        public void onClick(View v){
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contactAleph.getPhoneNumber()));
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling Phone Number: "+contactAleph.getPhoneNumber(), "Call failed", activityException);
            }
        }
    }

}
