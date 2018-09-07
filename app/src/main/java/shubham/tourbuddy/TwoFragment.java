package shubham.tourbuddy;

/**
 * Created by shubh on 15-Feb-17.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class TwoFragment extends Fragment {
    private EditText restaurant_edits;
    private Button restaurant_buttons;
    private LinearLayout my_locations;
    private LatLng loc;
    private double lat,lon;
    private int ii=0;
    private static String s;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_two, container, false);
        restaurant_edits = (EditText) view.findViewById(R.id.restaurant_edit);
        restaurant_edits.clearFocus();
        restaurant_buttons=(Button)view.findViewById(R.id.restaurant_button);
        my_locations=(LinearLayout)view.findViewById(R.id.my_location);
        my_locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ii=1;
                Intent i=new Intent(getActivity(),shubham.tourbuddy.amberfog.mapslidingtest.app.Maps.class);
                i.putExtra("string","restaurant");
                i.putExtra("mode",ii);
                startActivity(i);
            }
        });
        restaurant_buttons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat!=0 && lon!=0)
                {ii=2;
                    Intent i=new Intent(getActivity(),shubham.tourbuddy.amberfog.mapslidingtest.app.Maps.class);
                    i.putExtra("lat",lat);
                    i.putExtra("lon",lon);
                    i.putExtra("string","restaurant");
                    i.putExtra("mode",ii);
                    startActivity(i);}
                else
                    Toast.makeText(getActivity(),"Enter Location",Toast.LENGTH_SHORT).show();
            }
        });
        restaurant_edits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });
        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                restaurant_edits.setText(place.getName());
                loc=place.getLatLng();
                lat=loc.latitude;
                lon=loc.longitude;


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }
}