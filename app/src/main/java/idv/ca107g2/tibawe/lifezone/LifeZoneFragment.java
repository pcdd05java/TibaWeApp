package idv.ca107g2.tibawe.lifezone;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import idv.ca107g2.tibawe.R;
import idv.ca107g2.tibawe.tools.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class LifeZoneFragment extends Fragment {
    String memberType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView infoRecycler =
                (RecyclerView) inflater.inflate(R.layout.recyclerview_fragment, container, false);


        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);

        memberType = preferences.getString("memberType", "");

            int[] infoTitles = new int[LifeZone.LIFE_ZONES.length];
            for(int i = 0 ; i <infoTitles.length; i++){
                infoTitles[i] = LifeZone.LIFE_ZONES[i].getInfoTitle();
        }

            int[] infoPics = new int[LifeZone.LIFE_ZONES.length];
            for(int i = 0; i<infoPics.length; i++){
                infoPics[i] = LifeZone.LIFE_ZONES[i].getInfoPicId();
        }
        if(memberType.equals("1")) {
            LifeZoneAdapter adapter = new LifeZoneAdapter(infoTitles, infoPics);
            infoRecycler.setAdapter(adapter);
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            infoRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new LifeZoneAdapter.Listener() {
                @Override
                public void onClick(int position) {
                    switch (position) {
//                        case 0:
//                            Intent intent0 = new Intent(getActivity(), ClassInformationActivity.class);
//                            getActivity().startActivity(intent0);
//                            getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//                            break;
    //                    case 1:
    //                        Intent intent1 = new Intent(getActivity(), CampusNewsActivity.class);
    //                        getActivity().startActivity(intent1);
//    //                        break;
                        case 0:
                            Intent intent2 = new Intent(getActivity(), DBDQueryActivity.class);
                            getActivity().startActivity(intent2);
                            break;
                        case 1:
                            Intent intent3 = new Intent(getActivity(), StoreInformationActivity.class);
                            getActivity().startActivity(intent3);
                            break;
                        case 2:
                            Intent intent4 = new Intent(getActivity(), RhiActivity.class);
                            getActivity().startActivity(intent4);
                            break;
                    }
                }
            });
        }else{
            LifeZoneAdapter adapterTeacher = new LifeZoneAdapter(infoTitles, infoPics);
            infoRecycler.setAdapter(adapterTeacher);
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            infoRecycler.setLayoutManager(layoutManager);

            adapterTeacher.setListener(new LifeZoneAdapter.Listener() {
                @Override
                public void onClick(int position) {
                    switch (position) {
//                        case 0:
//                            Intent intent0 = new Intent(getActivity(), ClassInformationActivity.class);
//                            getActivity().startActivity(intent0);
//                            getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//                            break;
                        //                    case 1:
                        //                        Intent intent1 = new Intent(getActivity(), CampusNewsActivity.class);
                        //                        getActivity().startActivity(intent1);
//    //                        break;
                        case 2:
                            Intent intent2 = new Intent(getActivity(), DBDQueryActivity.class);
                            getActivity().startActivity(intent2);
                            break;
                        case 3:
                            Intent intent3 = new Intent(getActivity(), StoreInformationActivity.class);
                            getActivity().startActivity(intent3);
                            break;
                        case 4:
                            Intent intent4 = new Intent(getActivity(), RhiActivity.class);
                            getActivity().startActivity(intent4);
                            break;
                    }
                }
            });
        }
        return infoRecycler;

    }


}
