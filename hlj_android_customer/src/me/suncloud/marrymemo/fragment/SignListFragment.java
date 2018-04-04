package me.suncloud.marrymemo.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter.ViewBinder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Sign;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class SignListFragment extends Fragment implements ViewBinder<Sign> {
    private ArrayList<Sign> currentSigns;
    private ObjectBindAdapter<Sign> adapter;
    private View footView;
    private PullToRefreshListView listView;
    private View rootView;
    private View progressBar;
    private Dialog dialog;
    private long userId;

    public SignListFragment() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        userId = Session.getInstance()
                .getCurrentUser(getContext())
                .getId();
        currentSigns = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), currentSigns, R.layout.sign_list_item);
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more, null);
        if (getArguments() != null) {
            ArrayList<Sign> signs = (ArrayList<Sign>) getArguments().get("signs");
            if (signs != null) {
                Collections.sort(signs, new Comparator<Sign>() {
                    @Override
                    public int compare(Sign item1, Sign item2) {
                        if (item1.getTime() == null) {
                            return -1;
                        } else if (item2.getTime() == null) {
                            return 1;
                        }
                        return item2.getTime()
                                .compareTo(item1.getTime());
                    }
                });
                currentSigns.addAll(signs);
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setMode(Mode.DISABLED);
        listView.setAdapter(adapter);
        adapter.setViewBinder(this);
        setEmptyHintView();
        return rootView;
    }

    public void setSign(ArrayList<Sign> signs) {
        if (signs != null) {
            Collections.sort(signs, new Comparator<Sign>() {
                @Override
                public int compare(Sign item1, Sign item2) {
                    if (item1.getTime() == null) {
                        return -1;
                    } else if (item2.getTime() == null) {
                        return 1;
                    }
                    return item2.getTime()
                            .compareTo(item1.getTime());
                }
            });
            this.currentSigns.clear();
            this.currentSigns.addAll(signs);
            adapter.notifyDataSetChanged();
        }
    }

    public void setEmptyHintView() {
        if (currentSigns.isEmpty()) {
            View emptyView = listView.getRefreshableView()
                    .getEmptyView();
            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
                listView.getRefreshableView()
                        .setEmptyView(emptyView);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
            ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            imgNetHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);

            if (JSONUtil.isNetworkConnected(getActivity())) {
                imgNetHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.label_hint_sign_empty);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.net_disconnected);
            }
        }
    }

    private void deleteItem(final Sign sign) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                getActivity(),
                getString(R.string.hint_detele_sign),
                null,
                null,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        progressBar.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", sign.getId());
                        } catch (JSONException ignored) {
                        }
                        String url = sign.isV2() ? Constants.HttpPath.CARD_V2_REPLY_DELETE :
                                Constants.HttpPath.NEW_SIGN_REPLY_DETELE;
                        new StatusHttpPostTask(getActivity(), new StatusRequestListener() {
                            @Override
                            public void onRequestCompleted(
                                    Object object, ReturnStatus returnStatus) {
                                progressBar.setVisibility(View.GONE);
                                currentSigns.remove(sign);
                                adapter.notifyDataSetChanged();
                                setEmptyHintView();
                            }

                            @Override
                            public void onRequestFailed(
                                    ReturnStatus returnStatus, boolean network) {
                                progressBar.setVisibility(View.GONE);
                                Util.postFailToast(getActivity(),
                                        returnStatus,
                                        R.string.msg_fail_to_delete,
                                        network);
                            }
                        }).execute(Constants.getAbsUrl(url), jsonObject.toString());
                    }
                });
        dialog.show();
    }

    @Override
    public void setViewValue(View view, final Sign t, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.count = (TextView) view.findViewById(R.id.count);
            holder.tvFrom = (TextView) view.findViewById(R.id.tv_from);
            holder.deleteView = view.findViewById(R.id.delete_view);
            holder.deleteIcon = view.findViewById(R.id.delete_icon);
            holder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        }
        holder.name.getLayoutParams().width = 0;
        holder.name.requestLayout();
        holder.name.setText(t.getName());
        holder.content.setText(t.getContent());
        if (t.getCount() > 0 && t.getState() == 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(getString(R.string.label_reply_count, t.getCount()));
        } else {
            holder.count.setVisibility(View.GONE);
        }
        if (t.getUserId() != userId) {
            holder.tvFrom.setText(R.string.label_reply_from_other);
            holder.deleteView.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
        } else {
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.deleteIcon.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(t.getCardTitle())) {
                holder.tvFrom.setText(R.string.label_reply_from_card_untitled);
            } else {
                holder.tvFrom.setText(getString(R.string.label_reply_from_card, t.getCardTitle()));
            }
        }
        if (t.getTime() != null) {
            holder.time.setText(DateUtils.getRelativeTimeSpanString(t.getTime()
                            .getTime(),
                    Calendar.getInstance()
                            .getTimeInMillis(),
                    DateUtils.MINUTE_IN_MILLIS));
        }
        holder.deleteView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteItem(t);
            }
        });
    }

    public class ViewHolder {
        TextView name;
        TextView time;
        TextView count;
        TextView tvFrom;
        TextView content;
        View deleteView;
        View deleteIcon;
    }

}