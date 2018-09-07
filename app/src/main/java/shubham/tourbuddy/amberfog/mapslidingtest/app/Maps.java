/**
 * Copyright 2014-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shubham.tourbuddy.amberfog.mapslidingtest.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import shubham.tourbuddy.R;


public class Maps extends AppCompatActivity {
public static double lat,lon;
    public static String string;
    public static  int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        Bundle bundle = getIntent().getExtras();

        lat = bundle.getDouble("lat");
        lon = bundle.getDouble("lon");
        string = bundle.getString("string");
        mode = bundle.getInt("mode");
        if (savedInstanceState == null) {

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.fragment, MainFragment.newInstance(null));
            trans.commit();
        }


    }


}
