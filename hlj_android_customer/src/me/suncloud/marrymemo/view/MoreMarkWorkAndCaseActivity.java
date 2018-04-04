package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Rule;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import me.suncloud.marrymemo.widget.MarkHeaderView;
import me.suncloud.marrymemo.widget.MarkOrderView;

/**
 * 更多标签 套餐 案例 页
 * Created by jinxin on 16/4/26.
 */
public class MoreMarkWorkAndCaseActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Work>, AbsListView.OnItemClickListener, PullToRefreshBase.OnRefreshListener,
        AbsListView.OnScrollListener {
    // 套餐 案例
    protected int markType;
    private ArrayList<Work> mData;
    private ObjectBindAdapter<Work> adapter;
    private PullToRefreshListView listView;
    private MarkOrderView markOrderView;
    private long markId;
    private View progressBar;

    protected int workImageWidth;
    private int workImageHeight;
    private int iconMargin;
    private DisplayMetrics dm;
    private View emptyView;

    private OnOrderCheckChangeListener onOrderCheckChangeListener;
    private OnOrderClickListener onOrderClickListener;
    private String sortType;
    private String order;
    protected boolean isLoad;
    private boolean isEnd;
    protected int currentPage;
    protected View footView;
    private View loadView;
    private TextView endView;
    private String currentUrl;
    private boolean isMore;
    private MarkHeaderView markHeaderLabelView;
    private MarkOrderView markHeaderOrderView;
    private City city;
    private SuspendRunnable mRunnable;
    //1已关注 0未关注
    private int follow;
    private int badgeWidth;
    private int headerLabelHeight;
    private int headerOrderHeight;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MarkHeaderView.MARK) {
                headerLabelHeight = (int) msg.obj;
            } else if (msg.what == MarkOrderView.ORDER) {
                headerOrderHeight = (int) msg.obj;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markType = getIntent().getIntExtra("markType", 0);
        markId = getIntent().getLongExtra("markId", 0);
        isMore = getIntent().getBooleanExtra("isMore", false);
        String markTitle = getIntent().getStringExtra("markTitle");
        dm = getResources().getDisplayMetrics();
        city = Session.getInstance()
                .getMyCity(this);
        setContentView(R.layout.activity_listview_mark_order);
        if (isMore) {
            markOrderView = (MarkOrderView) findViewById(R.id.mark_order_view);
            findViewById(R.id.order_layout).setVisibility(View.VISIBLE);
            hideOkText();
        } else {
            markOrderView = (MarkOrderView) findViewById(R.id.mark_order_view1);
            setOkText(R.string.label_follow);
        }

        onOrderCheckChangeListener = new OnOrderCheckChangeListener();
        onOrderClickListener = new OnOrderClickListener();
        initConstant();
        initFoot();
        initHeader();
        initRunnable();
        badgeWidth = Math.round(120 * dm.density);
        listView = (PullToRefreshListView) findViewById(R.id.list_view);
        progressBar = findViewById(R.id.progressBar);

        int resource = 0;
        if (markType == Constants.MARK_TYPE.WORK) {
            if (isMore && !JSONUtil.isEmpty(markTitle) && !markTitle.contains("-套餐")) {
                markTitle += "-套餐";
            }
            resource = R.layout.work_list_item3;
            markOrderView.setOrderTextVisible(true, false, true, true, true);
        } else if (markType == Constants.MARK_TYPE.CASE) {
            if (isMore && !JSONUtil.isEmpty(markTitle) && !markTitle.contains("-案例")) {
                markTitle += "-案例";
            }
            resource = R.layout.case_list_item3;
            markOrderView.setOrderTextVisible(true, false, false, true, true);
        }

        setTitle(JSONUtil.isEmpty(markTitle) ? getString(R.string.label_subject_wedding) :
                markTitle);
        if (!isMore) {
            listView.getRefreshableView()
                    .addHeaderView(markHeaderLabelView);
            listView.getRefreshableView()
                    .addHeaderView(markHeaderOrderView);
            markHeaderOrderView.setContentVisible(false);
        }
        markOrderView.setOnCheckedChangeListener(onOrderCheckChangeListener);
        markOrderView.setOnTextClickListener(onOrderClickListener);
        mData = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, mData, resource, this);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getData(currentPage);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (!Util.loginBindChecked(this)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", markId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //1已关注 0未关注
        if (follow == 1) {
            //取消关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpDeleteTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 0;
                    setOkText(getString(R.string.label_follow));
                    Util.showToast(R.string.hint_discollect_complete2,
                            MoreMarkWorkAndCaseActivity.this);
                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(MoreMarkWorkAndCaseActivity.this,
                            returnStatus,
                            R.string.msg_fail_to_cancel_follow,
                            network);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_CANCEL_FOLLOW, markId));
        } else if (follow == 0) {
            //添加关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpPostTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 1;
                    setOkText(getString(R.string.label_followed));
                    Util.showToast(R.string.label_mark_followed, MoreMarkWorkAndCaseActivity.this);

                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(MoreMarkWorkAndCaseActivity.this,
                            returnStatus,
                            R.string.msg_fail_to_follow,
                            network);
                }

            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_ADD_FOLLOW),
                    jsonObject.toString());
        }
    }

    private void initRunnable() {
        mHandler = new Handler();
        mRunnable = new SuspendRunnable();
    }

    private void initHeader() {
        if (!isMore) {
            markHeaderOrderView = new MarkOrderView(this);
            if (markType == Constants.MARK_TYPE.WORK) {
                markHeaderOrderView.setOrderTextVisible(true, false, true, true, true);
            } else if (markType == Constants.MARK_TYPE.CASE) {
                markHeaderOrderView.setOrderTextVisible(true, false, false, true, true);
            }
            markHeaderOrderView.setHeightHandler(mHandler);
            markHeaderOrderView.setOnCheckedChangeListener(onOrderCheckChangeListener);
            markHeaderOrderView.setOnTextClickListener(onOrderClickListener);

            markHeaderLabelView = new MarkHeaderView(this);
            markHeaderLabelView.setActivity(this);
            markHeaderLabelView.setPaddingVisible(false);
            markHeaderLabelView.setRelativeId(markId);
            markHeaderLabelView.setPaddingVisible(true);
            markHeaderLabelView.setHeightHandler(mHandler);
            markHeaderLabelView.setOnItemClickListener(new MarkHeaderView.OnItemClickListener() {
                @Override
                public void onItemClick(
                        View view, JSONObject data, int position) {
                    if (data != null) {
                        long id = data.optLong("id");
                        String markTitle = data.optString("name");
                        int type = data.optInt("marked_type");
                        Util.markActionActivity(MoreMarkWorkAndCaseActivity.this,
                                type,
                                markTitle,
                                id,
                                false);
                    }
                }
            });
            markHeaderLabelView.setOnDataChangeListener(new MarkHeaderView.OnDataChangeListener() {
                @Override
                public void onDataChanged(
                        ArrayList<JSONObject> data,
                        MarkHeaderView.MarkHeaderAdapter adapter,
                        ViewGroup parent) {
                    if (data.size() <= 0) {
                        markHeaderLabelView.setContentVisible(false, false);
                    }
                }
            });
        }
    }

    private void initFoot() {
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing just consume event
            }
        });
        endView = (TextView) footView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        loadView = footView.findViewById(R.id.loading);
    }

    private void getData(int currentPage) {
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_LIST,
                markId,
                markType,
                order,
                sortType,
                city == null ? 0 : city.getId(),
                currentPage);
        if (JSONUtil.isEmpty(sortType) && JSONUtil.isEmpty(order)) {
            currentUrl = currentUrl.replace("order=null&", "")
                    .replace("sort=null&", "");
        }
        new GetWorkTask().executeOnExecutor(Constants.LISTTHEADPOOL, currentUrl);
    }

    private void initConstant() {
        Point point = JSONUtil.getDeviceSize(this);
        workImageWidth = Math.round(point.x - 24 * dm.density);
        workImageHeight = Math.round(workImageWidth * 1.0f * 10 / 16);
        iconMargin = Math.round(4 * dm.density);
    }

    private void setEmptyView() {
        if (mData.isEmpty()) {
            footView.setVisibility(View.GONE);
            if (emptyView == null) {
                emptyView = findViewById(R.id.empty_hint_layout);
                emptyView.findViewById(R.id.img_empty_hint)
                        .setVisibility(View.VISIBLE);
                TextView emptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                if (JSONUtil.isNetworkConnected(this)) {
                    emptyHint.setText(getString(R.string.no_item));
                } else {
                    emptyHint.setText(getString(R.string.hint_net_disconnected));
                }
                if (!isMore) {
                    markOrderView.setVisibility(View.GONE);
                    markHeaderOrderView.setContentVisible(false);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emptyView
                            .getLayoutParams();
                    if (params != null) {
                        if (markHeaderLabelView.getData()
                                .size() > 0) {
                            params.height = Math.round(JSONUtil.getDeviceSize(this).y -
                                    headerLabelHeight - Math.round(
                                    dm.density * (45 + 10 + 10 + 2)));
                        } else {
                            params.height = JSONUtil.getDeviceSize(this).y;
                        }
                    }
                }
                emptyView.invalidate();
                emptyHint.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (!isMore && markHeaderOrderView != null) {
                markHeaderOrderView.setContentVisible(true);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setViewValue(View view, Work work, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (work != null) {
            view.setVisibility(View.VISIBLE);
            view.setPadding(view.getPaddingLeft(),
                    Math.round(dm.density * 10),
                    view.getPaddingRight(),
                    view.getPaddingBottom());
            RelativeLayout.LayoutParams imgParam = (RelativeLayout.LayoutParams) holder.image
                    .getLayoutParams();
            if (imgParam != null) {
                imgParam.height = workImageHeight;
            }
            String imgPath = JSONUtil.getImagePath(work.getCoverPath(), workImageWidth);
            if (!JSONUtil.isEmpty(imgPath)) {
                holder.image.setTag(imgPath);
                ImageLoadTask task = new ImageLoadTask(holder.image, 0);
                task.loadImage(imgPath,
                        workImageWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
            holder.property.setText(work.getKind());
            holder.property.setVisibility(JSONUtil.isEmpty(work.getKind()) ? View.GONE : View
                    .VISIBLE);
            holder.title.setText(work.getTitle());
            holder.name.setText(work.getMerchantName());
            if (work.getBondSign() != null) {
                holder.bond.setVisibility(View.VISIBLE);
                holder.name.setPadding(0, 0, iconMargin, 0);
            } else {
                holder.bond.setVisibility(View.GONE);
                holder.name.setPadding(0, 0, 0, 0);
            }
            holder.pricesLayout.setVisibility(View.VISIBLE);
            if (markType == Constants.MARK_TYPE.WORK) {
                holder.iconInstallment.setVisibility(View.GONE);
                holder.discountPrice.setText(Util.formatDouble2String(work.getShowPrice()));
                if (work.getMarketPrice() > 0) {
                    holder.originalPrice.setVisibility(View.VISIBLE);
                    holder.originalPrice.setText(" " + Util.formatDouble2String(work
                            .getMarketPrice()) + " ");
                } else {
                    holder.originalPrice.setVisibility(View.GONE);
                }
                showTimeDown(view, work);
            } else if (markType == Constants.MARK_TYPE.CASE) {
                holder.collectCount.setText(String.valueOf(work.getCollectorCount()));
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected void showTimeDown(View v, Work work) {
        ViewHolder holder = (ViewHolder) v.getTag();
        showTimeDown(work, holder);
    }

    protected void showTimeDown(Work work, ViewHolder holder) {
        if (holder == null) {
            return;
        }
        //        holder.originalPrice.setText(" " + Util.formatDouble2String(work
        // .getMarketPrice()) + " ");
        //        holder.discountPrice.setText(Util.formatDouble2String(work
        // .getNowPrice()));
        Rule rule = work.getRule();
        if (rule != null && rule.getId() > 0) {
            Date date = new Date();
            if (rule.isTimeAble() && rule.getEnd_time() != null && rule.getEnd_time()
                    .before(date)) {
                //结束
                holder.activiteView.setVisibility(View.GONE);
                holder.badge321.setVisibility(View.GONE);
                holder.lineView.setVisibility(View.GONE);
                holder.label.setVisibility(View.GONE);
                holder.discountPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            } else {
                boolean isTime = rule.isTimeAble() && rule.getStart_time() != null && rule
                        .getStart_time()
                        .before(date);
                boolean isLimit = work.getLimit_num() > 0 && (!rule.isTimeAble() || rule
                        .getStart_time() == null || rule.getStart_time()
                        .before(date));
                String badgePath = JSONUtil.getImagePath(rule.getBigImg(), badgeWidth);
                if (!JSONUtil.isEmpty(badgePath)) {
                    holder.badge321.setVisibility(View.VISIBLE);
                    if (!badgePath.equals(holder.badge321.getTag())) {
                        ImageLoadTask task = new ImageLoadTask(holder.badge321,
                                null,
                                null,
                                0,
                                true,
                                true);
                        holder.badge321.setTag(badgePath);
                        task.loadImage(badgePath,
                                badgeWidth,
                                ScaleMode.WIDTH,
                                new AsyncBitmapDrawable(getResources(),
                                        R.mipmap.icon_empty_image,
                                        task));
                    }
                } else {
                    holder.badge321.setVisibility(View.GONE);
                }
                if (isTime || isLimit) {
                    holder.activiteView.setVisibility(View.VISIBLE);
                    if (isTime && isLimit) {
                        holder.lineView.setVisibility(View.VISIBLE);
                    } else {
                        holder.lineView.setVisibility(View.GONE);
                    }
                    if (!JSONUtil.isEmpty(rule.getShowtxt())) {
                        holder.label.setVisibility(View.VISIBLE);
                        holder.label.setText(work.getRule()
                                .getShowtxt());
                    } else {
                        holder.label.setVisibility(View.GONE);
                    }
                    if (isTime) {
                        //限时活动
                        holder.timeView.setVisibility(View.VISIBLE);
                        long millisUntil = rule.getEndTimeInMillis() - date.getTime();
                        long leftMillis;
                        int days = (int) (millisUntil / (1000 * 60 * 60 * 24));
                        leftMillis = millisUntil % (1000 * 60 * 60 * 24);
                        int hours = (int) (leftMillis / (1000 * 60 * 60));
                        leftMillis %= 1000 * 60 * 60;
                        int minutes = (int) (leftMillis / (1000 * 60));
                        leftMillis %= 1000 * 60;
                        int seconds = (int) (leftMillis / 1000);
                        if (days > 0) {
                            holder.countDayView.setVisibility(View.VISIBLE);
                            holder.day.setText(String.valueOf(days));
                            holder.countNoDayView.setVisibility(View.GONE);
                        } else {
                            holder.countDayView.setVisibility(View.GONE);
                            holder.countNoDayView.setVisibility(View.VISIBLE);
                            holder.hour1.setText(String.valueOf(hours / 10));
                            holder.hour2.setText(String.valueOf(hours % 10));
                            holder.minute1.setText(String.valueOf(minutes / 10));
                            holder.minute2.setText(String.valueOf(minutes % 10));
                            holder.second1.setText(String.valueOf(seconds / 10));
                            holder.second2.setText(String.valueOf(seconds % 10));
                        }
                    } else {
                        holder.timeView.setVisibility(View.GONE);
                    }
                    if (isLimit) {
                        //限量活动
                        holder.countView.setVisibility(View.VISIBLE);
                        holder.leaveCount.setText(String.valueOf(work.getLimit_num() - work
                                .getLimit_sold_out()));
                    } else {
                        holder.countView.setVisibility(View.GONE);
                    }
                } else {
                    holder.activiteView.setVisibility(View.GONE);
                    holder.label.setVisibility(View.GONE);
                }
            }
        } else {
            //正常
            holder.activiteView.setVisibility(View.GONE);
            holder.badge321.setVisibility(View.GONE);
            holder.label.setVisibility(View.GONE);
            holder.discountPrice.setText(Util.formatDouble2String(work.getShowPrice()));
        }
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        Work work = (Work) parent.getAdapter()
                .getItem(position);
        Intent intent = null;
        if (markType == Constants.MARK_TYPE.WORK) {
            intent = new Intent(this, WorkActivity.class);
        } else if (markType == Constants.MARK_TYPE.CASE) {
            intent = new Intent(this, CaseDetailActivity.class);
        }
        if (intent != null) {
            if (work != null) {
                intent.putExtra("id", work.getId());
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void getOrderType(int id) {
        switch (id) {
            case R.id.result_detail_all:
                //综合
                sortType = null;
                order = null;
                break;
            case R.id.result_detail_newest:
                //最新
                sortType = "created_at";
                order = "desc";
                break;
            case R.id.result_detail_likecount:
                //喜欢数
                sortType = "collectors_count";
                order = "desc";
                break;

        }
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        getData(currentPage);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            currentPage = 1;
            getData(currentPage);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        getData(currentPage);
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem < 2) {
            removeSuspendView();
        } else if (firstVisibleItem >= 2) {
            addSuspendView();
        }
    }

    @Override
    protected void onResume() {
        if (markType == Constants.MARK_TYPE.WORK) {
            mHandler.post(timeRunnable);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (markType == Constants.MARK_TYPE.WORK) {
            mHandler.removeCallbacks(timeRunnable);
        }
        super.onPause();
    }

    private void addSuspendView() {
        if (!isMore) {
            mRunnable.setVisible(View.VISIBLE);
            mHandler.post(mRunnable);
        }
    }

    private void removeSuspendView() {
        if (!isMore) {
            mRunnable.setVisible(View.INVISIBLE);
            mHandler.post(mRunnable);
        }
    }

    class OnOrderCheckChangeListener implements CheckableLinearLayout2.OnCheckedChangeListener {

        @Override
        public void onCheckedChange(View view, boolean checked) {
            if (checked) {
                sortType = "actual_price";
                order = "desc";
            } else {
                sortType = "actual_price";
                order = "asc";
            }
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;
            getData(currentPage);
        }
    }

    class OnOrderClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getOrderType(v.getId());
        }
    }

    class ViewHolder {
        ImageView image;
        TextView property;
        TextView title;
        TextView name;
        ImageView bond;
        //套餐 (价格layout) 案例(收藏layout)
        View pricesLayout;
        TextView discountPrice;
        TextView originalPrice;
        TextView collectCount;
        View countView;
        View countNoDayView;
        View countDayView;
        View timeView;
        View lineView;
        ImageView badge321;
        View activiteView;
        TextView label;
        TextView day;
        TextView hour1;
        TextView hour2;
        TextView minute1;
        TextView minute2;
        TextView second1;
        TextView second2;
        TextView leaveCount;
        ImageView iconInstallment;

        public ViewHolder(View view) {
            if (markType == Constants.MARK_TYPE.WORK) {
                iconInstallment = (ImageView) view.findViewById(R.id.img_installment);
                image = (ImageView) view.findViewById(R.id.work_cover);
                property = (TextView) view.findViewById(R.id.property);
                title = (TextView) view.findViewById(R.id.title_name);
                name = (TextView) view.findViewById(R.id.merchant_name);
                bond = (ImageView) view.findViewById(R.id.bond_icon);
                pricesLayout = view.findViewById(R.id.prices_layout);
                discountPrice = (TextView) view.findViewById(R.id.discount_price);
                originalPrice = (TextView) view.findViewById(R.id.original_price);
                originalPrice.getPaint()
                        .setAntiAlias(true);
                originalPrice.getPaint()
                        .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                countView = view.findViewById(R.id.leave_count_layout);
                countNoDayView = view.findViewById(R.id.count_not_day_layout);
                countDayView = view.findViewById(R.id.count_day_layout);
                timeView = view.findViewById(R.id.leave_heures_layout);
                lineView = view.findViewById(R.id.line_layout);
                badge321 = (ImageView) view.findViewById(R.id.badge);
                activiteView = view.findViewById(R.id.work_activite_layout);
                label = (TextView) view.findViewById(R.id.discount_label);
                day = (TextView) view.findViewById(R.id.tv_count_day);
                hour1 = (TextView) view.findViewById(R.id.tv_count_hour1);
                hour2 = (TextView) view.findViewById(R.id.tv_count_hour2);
                minute1 = (TextView) view.findViewById(R.id.tv_count_minute1);
                minute2 = (TextView) view.findViewById(R.id.tv_count_minute2);
                second1 = (TextView) view.findViewById(R.id.tv_count_second1);
                second2 = (TextView) view.findViewById(R.id.tv_count_second2);
                leaveCount = (TextView) view.findViewById(R.id.leave_count);
            } else if (markType == Constants.MARK_TYPE.CASE) {
                image = (ImageView) view.findViewById(R.id.case_cover);
                property = (TextView) view.findViewById(R.id.property);
                title = (TextView) view.findViewById(R.id.title_name);
                name = (TextView) view.findViewById(R.id.merchant_name);
                bond = (ImageView) view.findViewById(R.id.bond_icon);
                pricesLayout = view.findViewById(R.id.case_collect_layout);
                collectCount = (TextView) view.findViewById(R.id.case_collect_count);
            }
        }
    }


    class SuspendRunnable implements Runnable {
        private int visible;

        public void setVisible(int visible) {
            this.visible = visible;
        }

        @Override
        public void run() {
            if (!isMore && markHeaderLabelView.getData()
                    .size() > 0) {
                if (markOrderView != null) {
                    markOrderView.setVisibility(visible);
                }
            }
        }
    }

    /**
     * 倒计时runable
     */
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing() || markType != Constants.MARK_TYPE.WORK) {
                return;
            }
            if (!mData.isEmpty() && listView != null) {
                int first = listView.getRefreshableView()
                        .getFirstVisiblePosition();
                int end = listView.getRefreshableView()
                        .getLastVisiblePosition();
                if (first <= end && first >= 0) {
                    for (int i = first; i <= end; i++) {
                        View v = listView.getRefreshableView()
                                .getChildAt(i - first);
                        if (v != null && v.findViewById(R.id.work_item) != null) {
                            Work work = (Work) listView.getRefreshableView()
                                    .getAdapter()
                                    .getItem(i);
                            if (work != null) {
                                if (work.getCommodityType() != 1 && work.isPreSaleOrOnSale()) {
                                    showTimeDown(v, work);
                                }
                            }
                        }
                    }
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    class GetWorkTask extends AsyncTask<String, Void, JSONObject> {
        private String url;

        private GetWorkTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isFinishing()) {
                return;
            }
            if (url.equalsIgnoreCase(currentUrl)) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    if (currentPage == 1) {
                        mData.clear();
                    }
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {

                        int pageCount = data.optInt("page_count", 0);
                        isEnd = pageCount <= currentPage;
                        if (isEnd) {
                            endView.setVisibility(View.VISIBLE);
                            loadView.setVisibility(View.GONE);
                            endView.setText(R.string.no_more);
                        } else {
                            endView.setVisibility(View.GONE);
                            loadView.setVisibility(View.INVISIBLE);
                        }

                        follow = data.optInt("is_follow", 0);
                        //1已关注 0未关注
                        if(isMore) {
                            if (follow == 0) {
                                setOkText(getString(R.string.label_follow));
                            } else if (follow == 1) {
                                setOkText(getString(R.string.label_followed));
                            }
                        }
                        JSONArray list = data.optJSONArray("list");
                        if (list != null) {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject item = list.optJSONObject(i);
                                Work work = new Work(item);
                                if (work.getId() > 0) {
                                    mData.add(work);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
            super.onPostExecute(jsonObject);
        }
    }
}
