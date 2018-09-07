package shubham.tourbuddy;

/**
 * Created by shubh on 15-Feb-17.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;


public class ThreeFragment extends Fragment {
    private CheckBox atms;
    private CheckBox bus_stations;
    private CheckBox banks;
    private CheckBox gas_stations;
    private CheckBox hospitals;
    private CheckBox movie_theaters;
    private CheckBox parks;
    private CheckBox police_stations;
    private CheckBox shopping_malls;
    private CheckBox book_stores;
    private CheckBox car_repairs;
    private CheckBox pharmacys;
    private Button click_mes;

    public ThreeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        atms=(CheckBox)view.findViewById(R.id.atm);
        bus_stations=(CheckBox)view.findViewById(R.id.bus_station);
        banks=(CheckBox)view.findViewById(R.id.bank);
        gas_stations=(CheckBox)view.findViewById(R.id.gas_station);
        hospitals=(CheckBox)view.findViewById(R.id.hospital);
        movie_theaters=(CheckBox)view.findViewById(R.id.movie_theater);
        parks=(CheckBox)view.findViewById(R.id.park);
        police_stations=(CheckBox)view.findViewById(R.id.police_station);
        shopping_malls=(CheckBox)view.findViewById(R.id.shopping_mall);
        book_stores=(CheckBox)view.findViewById(R.id.book_store);
        car_repairs=(CheckBox)view.findViewById(R.id.car_repair);
        pharmacys=(CheckBox)view.findViewById(R.id.pharmacy);
        click_mes=(Button)view.findViewById(R.id.click_me);
        click_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer result=new StringBuffer();
                if(atms.isChecked())
                    result.append("atm|");
                if(bus_stations.isChecked())
                    result.append("bus_station|");
                if(banks.isChecked())
                    result.append("bank|");
                if(gas_stations.isChecked())
                    result.append("gas_station|");
                if(hospitals.isChecked())
                    result.append("hospital|");
                if(movie_theaters.isChecked())
                    result.append("movie_theater|");
                if(parks.isChecked())
                    result.append("park|");
                if(police_stations.isChecked())
                    result.append("police|");
                if(shopping_malls.isChecked())
                    result.append("shopping_mall|");
                if(book_stores.isChecked())
                    result.append("book_store|");
                if(car_repairs.isChecked())
                    result.append("car_repair|");
                if(pharmacys.isChecked())
                    result.append("pharmacy|");
                int ii=0;
                ii=1;
                Intent i=new Intent(getActivity(),shubham.tourbuddy.amberfog.mapslidingtest.app.Maps.class);
                i.putExtra("string",result.toString());
                i.putExtra("mode",ii);
                startActivity(i);

            }
        });
        return view;
    }
}