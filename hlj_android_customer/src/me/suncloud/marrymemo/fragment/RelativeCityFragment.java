package me.suncloud.marrymemo.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CityWorkRecyclerAdapter;
import me.suncloud.marrymemo.model.RelativeCity;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.MainActivity;

/**
 * Created by Suncloud on 2015/10/29.
 */
public class RelativeCityFragment extends DialogFragment {

    private SpacesItemDecoration spacesItemDecoration;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.bubble_dialog_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relative_city, container, false);
        LinearLayout cityList= (LinearLayout) rootView.findViewById(R.id.relative_city_list);
        rootView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
        if(getArguments()!=null) {
            ArrayList<RelativeCity> cities = (ArrayList<RelativeCity>) getArguments().getSerializable("cities");
            if(cities!=null&&!cities.isEmpty()){
                for(RelativeCity city:cities){
                    View view=inflater.inflate(R.layout.relative_city_item,null);
                    setViewValue(view, city);
                    cityList.addView(view);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialog_anim_rise_style);
            getDialog().setCanceledOnTouchOutside(true);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public MainActivity getMainActivity() {
        if(getActivity() instanceof MainActivity) {
            if (mainActivity == null) {
                mainActivity = (MainActivity) getActivity();
            }
        }
        return mainActivity;
    }

    private void setViewValue(View view, final RelativeCity relativeCity){
        view.findViewById(R.id.tv_city_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                try {
                    Session.getInstance().setMyCity(getActivity(), relativeCity.getCity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(getMainActivity()!=null){
                    getMainActivity().onCityChange(relativeCity.getCity(),true);
                }
            }
        });
        TextView cityName= (TextView) view.findViewById(R.id.city_name);
        TextView count= (TextView) view.findViewById(R.id.count);
        RecyclerView workList= (RecyclerView) view.findViewById(R.id.work_list);
        cityName.setText(relativeCity.getCity().getName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        workList.setLayoutManager(layoutManager);
        if(spacesItemDecoration==null){
            spacesItemDecoration=new SpacesItemDecoration(getActivity());
        }
        workList.addItemDecoration(spacesItemDecoration);
        CityWorkRecyclerAdapter adapter=new CityWorkRecyclerAdapter(getActivity());
        adapter.setWorks(relativeCity.getWorks());
        workList.setAdapter(adapter);
        count.setText("（"+relativeCity.getCount()+"）");

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(Context context) {
            this.space = Math.round(context.getResources().getDisplayMetrics().density * 10);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.left = space;
        }
    }
}
