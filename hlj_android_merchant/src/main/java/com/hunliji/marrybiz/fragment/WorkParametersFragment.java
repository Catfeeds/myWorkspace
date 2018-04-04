package com.hunliji.marrybiz.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.model.WorkParameter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/1/11.
 */
public class WorkParametersFragment extends RefreshFragment implements ObjectBindAdapter.ViewBinder<WorkParameter> {

    @BindView(R.id.list)
    PullToRefreshListView list;

    private Unbinder unbinder;
    private ObjectBindAdapter<WorkParameter> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ArrayList<WorkParameter> parameters = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), parameters, R.layout.work_parameter_view, this);
        if (getArguments() != null) {
            Work work = (Work) getArguments().getSerializable("work");
            if (work != null && work.getParameters() != null) {
                parameters.addAll(work.getParameters());
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview2, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        list.setMode(PullToRefreshBase.Mode.DISABLED);
        list.getRefreshableView().setDividerHeight(Math.round(getResources().getDisplayMetrics().density * 10));
        list.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean isTop() {
        return list == null || list.isReadyForPullStart();
    }

    @Override
    public void setViewValue(View view, WorkParameter workParameter, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.lineLayout.setVisibility(View.GONE);
            view.setTag(holder);
        }
        holder.name.setText(workParameter.getTitle());
        int size = workParameter.getChildren() == null ? 0 : workParameter.getChildren().size();
        int childCount = holder.itemList.getChildCount();
        if (childCount > size) {
            holder.itemList.removeViews(size, childCount - size);
        }
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                WorkParameter parameter = workParameter.getChildren().get(i);
                View childView = null;
                if (i < childCount) {
                    childView = holder.itemList.getChildAt(i);
                }
                if (childView == null) {
                    childView = View.inflate(getActivity(), R.layout.work_parameter_item_view, null);
                    holder.itemList.addView(childView);
                }
                ChildViewHolder childViewHolder= (ChildViewHolder) childView.getTag();
                if(childViewHolder==null){
                    childViewHolder=new ChildViewHolder(childView);
                    childView.setTag(childViewHolder);
                }
                childViewHolder.key.setText(parameter.getTitle());
                childViewHolder.value.setText(parameter.getValues());
            }
        }

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'work_parameter_view.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.item_list)
        LinearLayout itemList;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'work_parameter_item_view.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ChildViewHolder {
        @BindView(R.id.key)
        TextView key;
        @BindView(R.id.value)
        TextView value;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
