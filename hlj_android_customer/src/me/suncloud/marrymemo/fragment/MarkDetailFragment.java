package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.CommunityPost;
import me.suncloud.marrymemo.model.CommunityThread;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Rule;
import me.suncloud.marrymemo.model.ShopProduct;
import me.suncloud.marrymemo.model.User;
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
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.MoreMarkProductActivity;
import me.suncloud.marrymemo.view.MoreMarkThreadActivity;
import me.suncloud.marrymemo.view.MoreMarkWorkAndCaseActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.widget.MarkHeaderView;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by jinxin on 2016/6/24.
 */
public class MarkDetailFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, View.OnClickListener {
    @Override
    public void refresh(Object... params) {

    }

    private final int FOLLOW_LOGIN = 10;
    private long markId;
    private PullToRefreshVerticalRecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SpacesItemDecoration itemDecoration;
    private WeddingAdapter adapter;
    private MarkHeaderView headerView;
    private View footView;
    private View progressBar;
    private View emptyView;
    private LinkedList<JSONObject> mData;
    private DisplayMetrics dm;
    protected int workImageWidth;
    private int workImageHeight;
    private int iconMargin;
    private SimpleDateFormat shortSimpleDateFormat;
    private int storyLogoImageWidth;
    private int threadLogoWidth;
    private Button btnFollow;
    //1已关注 0未关注
    private int follow;
    private View backTopView;
    private boolean isHide;
    private int faceSize;
    private int faceSize2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MarkHeaderView.MARK) {
                headerHeight = (int) msg.obj;
            }
        }
    };
    private City city;
    private String markTitle;
    private int headerHeight;
    private TextView emptyHint;
    private boolean isHandlerCall;
    private int badgeWidth;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mark_detail, container, false);
        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvTitle.setText(JSONUtil.isEmpty(markTitle) ? getString(R.string.label_subject_wedding) :
                markTitle);
        initConstant();
        initRecycleList();
        initHeader();
        backTopView = rootView.findViewById(R.id.backtop_btn);
        progressBar = rootView.findViewById(R.id.progressBar);
        btnFollow = (Button) rootView.findViewById(R.id.btn_follow);
        btnFollow.setOnClickListener(this);
        backTopView.setOnClickListener(this);
        rootView.findViewById(R.id.back)
                .setOnClickListener(this);
        mData = new LinkedList<>();
        progressBar.setVisibility(View.VISIBLE);
        city = Session.getInstance()
                .getMyCity(getContext());
        String url = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_INFO,
                markId,
                city == null ? 0 : city.getId());
        new GetMarkInfoTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
        return rootView;
    }

    public static MarkDetailFragment newInstance(Bundle data) {
        MarkDetailFragment fragment = new MarkDetailFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle data = getArguments();
        if (data != null) {
            markId = data.getLong("markId", 0);
            dm = getResources().getDisplayMetrics();
            markTitle = data.getString("markTitle");
        }
        super.onCreate(savedInstanceState);
    }

    private void initConstant() {
        isHandlerCall = false;
        Point point = JSONUtil.getDeviceSize(getContext());
        workImageWidth = Math.round(point.x - 24 * dm.density);
        workImageHeight = Math.round(workImageWidth * 1.0f * 10 / 16);
        iconMargin = Math.round(4 * dm.density);
        storyLogoImageWidth = Math.round(dm.density * 30);
        shortSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT,
                Locale.getDefault());
        threadLogoWidth = Math.round(dm.density * 50);
        badgeWidth = Math.round(dm.density * 120);
        faceSize = Math.round(dm.density * 18);
        faceSize2 = Math.round(dm.density * 15);
    }

    private void initRecycleList() {
        layoutManager = new LinearLayoutManager(getContext());
        itemDecoration = new SpacesItemDecoration(getContext());
        adapter = new WeddingAdapter();
        recyclerView = (PullToRefreshVerticalRecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .addItemDecoration(itemDecoration);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        if (!mData.isEmpty()) {
                            if (layoutManager.findLastVisibleItemPosition() > 1) {
                                if (isHide) {
                                    if (backTopView.getVisibility() == View.GONE) {
                                        backTopView.setVisibility(View.VISIBLE);
                                    }
                                    showFiltrateAnimation();
                                }
                            } else if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initHeader() {
        headerView = new MarkHeaderView(getContext());
        headerView.setActivity(getActivity());
        headerView.setPaddingVisible(false);
        headerView.setRelativeId(markId);
        headerView.setHeightHandler(mHandler);
        headerView.setOnItemClickListener(new MarkHeaderView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, JSONObject data, int position) {
                if (data != null) {
                    long id = data.optLong("id");
                    String markTitle = data.optString("name");
                    int type = data.optInt("marked_type");
                    Util.markActionActivity(getContext(), type, markTitle, id, false);
                }
            }
        });
        headerView.setOnDataChangeListener(new MarkHeaderView.OnDataChangeListener() {
            @Override
            public void onDataChanged(
                    ArrayList<JSONObject> data,
                    MarkHeaderView.MarkHeaderAdapter adapter,
                    ViewGroup parent) {
                if (data.size() <= 0) {
                    headerView.setContentVisible(false, false);
                }
            }
        });
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        footView.setVisibility(View.INVISIBLE);
    }

    private void setEmptyView() {
        if (emptyView == null) {
            emptyView = rootView.findViewById(R.id.empty_hint_layout);
        }
        if (mData.isEmpty()) {
            if (footView != null) {
                footView.setVisibility(View.GONE);
            }
            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
                emptyView.findViewById(R.id.img_empty_list_hint)
                        .setVisibility(View.VISIBLE);
                emptyHint = (TextView) emptyView.findViewById(R.id.empty_hint);
                if (!JSONUtil.isNetworkConnected(getContext())) {
                    emptyHint.setText(getString(R.string.hint_net_disconnected));
                } else {
                    emptyHint.setText(getString(R.string.no_item));
                }
                emptyHint.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emptyView
                    .getLayoutParams();
            if (params != null) {
                if (headerView.getData()
                        .size() > 0) {
                    params.height = Math.round(JSONUtil.getDeviceSize(getContext()).y -
                            headerHeight - Math.round(
                            dm.density * (45 + 10 + 10 + 2)));
                } else {
                    params.height = JSONUtil.getDeviceSize(getContext()).y;
                }
            }
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private void hideFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private void showFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopView != null && (backTopView.getAnimation() == null || backTopView
                .getAnimation()
                .hasEnded());
    }

    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }


    public void onBack(View view) {
        recyclerView.getRefreshableView()
                .smoothScrollToPosition(0);
    }

    public void onFollow(View view) {
        if (!Util.loginBindChecked(getActivity(), FOLLOW_LOGIN)) {
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
            new StatusHttpDeleteTask(getContext(), new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 0;
                    btnFollow.setText(getString(R.string.label_follow));
                    Util.showToast(R.string.hint_discollect_complete2, getContext());
                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(getContext(),
                            returnStatus,
                            R.string.msg_fail_to_cancel_follow,
                            network);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_CANCEL_FOLLOW, markId));
        } else if (follow == 0) {
            //添加关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpPostTask(getContext(), new StatusRequestListener() {
                @Override
                public void onRequestCompleted(
                        Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 1;
                    btnFollow.setText(getString(R.string.label_followed));
                    Util.showToast(R.string.label_mark_followed, getContext());

                }

                @Override
                public void onRequestFailed(
                        ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(getContext(),
                            returnStatus,
                            R.string.msg_fail_to_follow,
                            network);
                }

            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_ADD_FOLLOW),
                    jsonObject.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOLLOW_LOGIN && resultCode == Activity.RESULT_OK) {
            //关注重新登录回来
            onFollow(null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        String url = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_INFO,
                markId,
                city == null ? 0 : city.getId());
        new GetMarkInfoTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.back:
                onBackPressed(v);
                break;
            case R.id.btn_follow:
                onFollow(v);
                break;
            case R.id.backtop_btn:
                onBack(v);
                break;
            default:
                break;
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(Context context) {
            this.space = Math.round(context.getResources()
                    .getDisplayMetrics().density * 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) > 0 && parent.getChildAdapterPosition(view)
                    < parent.getAdapter()
                    .getItemCount()) {
                outRect.set(0, space, 0, 0);
            }
        }
    }

    class WeddingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            if (viewType == Constants.MARK_TYPE.HEADER_LABEL) {
                //header
                return new HeaderViewHolder(headerView);
            } else if (viewType == Constants.MARK_TYPE.FOOTER) {
                return new FooterViewHolder(footView);
            } else if (viewType == Constants.MARK_TYPE.WORK) {
                //套餐
                View workView = getActivity().getLayoutInflater()
                        .inflate(R.layout.mark_detail_work_item, null);
                return new WorkViewHolder(workView);
            } else if (viewType == Constants.MARK_TYPE.CASE) {
                //案例
                View caseView = getActivity().getLayoutInflater()
                        .inflate(R.layout.mark_detail_case_item, null);
                return new CaseViewHolder(caseView);
            } else if (viewType == Constants.MARK_TYPE.THREAD) {
                //话题
                View threadView = getActivity().getLayoutInflater()
                        .inflate(R.layout.mark_detail_thread_item, null);
                return new ThreadViewHolder(threadView);
            } else if (viewType == Constants.MARK_TYPE.PRODUCT) {
                //婚品
                View productView = getActivity().getLayoutInflater()
                        .inflate(R.layout.mark_detail_product_item, null);
                return new ProductViewHolder(productView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(
                RecyclerView.ViewHolder holder, int position) {
            try {
                if (position == 0) {
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.bind();
                } else if (position == getItemCount() - 1) {
                    FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                    footerViewHolder.bind();
                } else {
                    int dataPosition = position - 1;
                    JSONObject item = mData.get(dataPosition);
                    if (item != null) {
                        int type = item.optInt("type");
                        if (type == Constants.MARK_TYPE.WORK) {
                            //套餐
                            WorkViewHolder workViewHolder = (WorkViewHolder) holder;
                            if (workViewHolder != null) {
                                workViewHolder.bind(item);
                            }
                        } else if (type == Constants.MARK_TYPE.CASE) {
                            //案例
                            CaseViewHolder caseViewHolder = (CaseViewHolder) holder;
                            if (caseViewHolder != null) {
                                caseViewHolder.bind(item);
                            }
                        } else if (type == Constants.MARK_TYPE.THREAD) {
                            //话题
                            ThreadViewHolder threadViewHolder = (ThreadViewHolder) holder;
                            if (threadViewHolder != null) {
                                threadViewHolder.bind(item);
                            }
                        } else if (type == Constants.MARK_TYPE.PRODUCT) {
                            //婚品
                            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                            if (productViewHolder != null) {
                                productViewHolder.bind(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            //加的是 footer header
            return mData.size() + 1 + 1;
        }

        @Override
        public int getItemViewType(int position) {
            int type = -1;
            if (position == 0) {
                type = Constants.MARK_TYPE.HEADER_LABEL;
            } else if (position == getItemCount() - 1) {
                type = Constants.MARK_TYPE.FOOTER;
            } else {
                if (!mData.isEmpty()) {
                    JSONObject item = mData.get(position - 1);
                    if (item != null) {
                        type = item.optInt("type");
                    }
                }
            }
            return type;
        }
    }

    private void setImage(ImageView img, String path, int size) {
        if (img != null) {
            if (!JSONUtil.isEmpty(path)) {
                String imgPath = JSONUtil.getImagePath(path, size);
                img.setTag(imgPath);
                ImageLoadTask task = new ImageLoadTask(img, 0);
                task.loadImage(imgPath,
                        size,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                img.setImageResource(R.drawable.image_mark_default);
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private MarkHeaderView headerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = (MarkHeaderView) itemView;
        }

        public void bind() {
            if (headerView != null) {
                headerView.viewHolderNotify();
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        private View foot;

        public FooterViewHolder(View itemView) {
            super(itemView);
            foot = itemView;
        }

        public void bind() {
            if (foot != null) {
                foot.findViewById(R.id.no_more_hint)
                        .setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 套餐在data中的位置
     */
    private int workPostion;
    private JSONObject workData;
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && getActivity().isFinishing()) {
                return;
            }
            if (workData != null) {
                //+1  把header的position算进去
                View workView = layoutManager.findViewByPosition(workPostion + 1);
                if (workView != null) {
                    LinearLayout contentLayout = (LinearLayout) workView.findViewById(R.id
                            .content_layout);
                    JSONArray works = workData.optJSONArray("examples");
                    if (contentLayout != null) {
                        for (int i = 0; i < works.length(); i++) {
                            JSONObject item = works.optJSONObject(i);
                            Work work = new Work(item);
                            View itemView = contentLayout.getChildAt(i);
                            showTimeDown(itemView, work);
                        }
                    }
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public void showTimeDown(View view, Work work) {
        View activiteView = view.findViewById(R.id.work_activite_layout);
        ImageView badge321 = (ImageView) view.findViewById(R.id.badge);
        View lineView = view.findViewById(R.id.line_layout);
        View timeView = view.findViewById(R.id.leave_heures_layout);
        TextView label = (TextView) view.findViewById(R.id.discount_label);
        View countDayView = view.findViewById(R.id.count_day_layout);
        TextView day = (TextView) view.findViewById(R.id.tv_count_day);
        View countNoDayView = view.findViewById(R.id.count_not_day_layout);
        TextView hour1 = (TextView) view.findViewById(R.id.tv_count_hour1);
        TextView hour2 = (TextView) view.findViewById(R.id.tv_count_hour2);
        TextView minute1 = (TextView) view.findViewById(R.id.tv_count_minute1);
        TextView minute2 = (TextView) view.findViewById(R.id.tv_count_minute2);
        TextView second1 = (TextView) view.findViewById(R.id.tv_count_second1);
        TextView second2 = (TextView) view.findViewById(R.id.tv_count_second2);
        View countView = view.findViewById(R.id.leave_count_layout);
        TextView leaveCount = (TextView) view.findViewById(R.id.leave_count);
        //        TextView disCountPrice = (TextView) view.findViewById(R.id
        // .discount_price);
        //        TextView originalPrice = (TextView) view.findViewById(R.id
        // .original_price);
        //        originalPrice.setText(" " + Util.formatDouble2String(work
        // .getShowPrice()) + " ");
        //        disCountPrice.setText(Util.formatDouble2String(work
        // .getNowPrice()));
        //        originalPrice.getPaint().setAntiAlias(true);
        //        originalPrice.getPaint().setFlags(Paint
        // .STRIKE_THRU_TEXT_FLAG | Paint
        // .ANTI_ALIAS_FLAG);
        Rule rule = work.getRule();
        if (rule != null && rule.getId() > 0) {
            Date date = new Date();
            if (rule.isTimeAble() && rule.getEnd_time() != null && rule.getEnd_time()
                    .before(date)) {
                //结束
                activiteView.setVisibility(View.GONE);
                badge321.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
                label.setVisibility(View.GONE);
            } else {
                boolean isTime = rule.isTimeAble() && rule.getStart_time() != null && rule
                        .getStart_time()
                        .before(date);
                boolean isLimit = work.getLimit_num() > 0 && (!rule.isTimeAble() || rule
                        .getStart_time() == null || rule.getStart_time()
                        .before(date));
                String badgePath = JSONUtil.getImagePath(rule.getBigImg(), this.badgeWidth);
                if (!JSONUtil.isEmpty(badgePath)) {
                    badge321.setVisibility(View.VISIBLE);
                    if (!badgePath.equals(badge321.getTag())) {
                        ImageLoadTask task = new ImageLoadTask(badge321, null, null, 0, true, true);
                        badge321.setTag(badgePath);
                        task.loadImage(badgePath,
                                this.badgeWidth,
                                ScaleMode.WIDTH,
                                new AsyncBitmapDrawable(getResources(),
                                        R.mipmap.icon_empty_image,
                                        task));
                    }
                } else {
                    badge321.setVisibility(View.GONE);
                }
                if (isTime || isLimit) {
                    activiteView.setVisibility(View.VISIBLE);
                    if (isTime && isLimit) {
                        lineView.setVisibility(View.VISIBLE);
                    } else {
                        lineView.setVisibility(View.GONE);
                    }
                    if (!JSONUtil.isEmpty(rule.getShowtxt())) {
                        label.setVisibility(View.VISIBLE);
                        label.setText(work.getRule()
                                .getShowtxt());
                    } else {
                        label.setVisibility(View.GONE);
                    }
                    if (isTime) {
                        //限时活动
                        timeView.setVisibility(View.VISIBLE);
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
                            countDayView.setVisibility(View.VISIBLE);
                            day.setText(String.valueOf(days));
                            countNoDayView.setVisibility(View.GONE);
                        } else {
                            countDayView.setVisibility(View.GONE);
                            countNoDayView.setVisibility(View.VISIBLE);
                            hour1.setText(String.valueOf(hours / 10));
                            hour2.setText(String.valueOf(hours % 10));
                            minute1.setText(String.valueOf(minutes / 10));
                            minute2.setText(String.valueOf(minutes % 10));
                            second1.setText(String.valueOf(seconds / 10));
                            second2.setText(String.valueOf(seconds % 10));
                        }
                    } else {
                        timeView.setVisibility(View.GONE);
                    }
                    if (isLimit) {
                        //限量活动
                        countView.setVisibility(View.VISIBLE);
                        leaveCount.setText(String.valueOf(work.getLimit_num() - work
                                .getLimit_sold_out()));
                    } else {
                        countView.setVisibility(View.GONE);
                    }
                } else {
                    activiteView.setVisibility(View.GONE);
                    label.setVisibility(View.GONE);
                }
            }
        } else {
            //正常
            activiteView.setVisibility(View.GONE);
            badge321.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
        }
    }

    class WorkViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearLayout contentLayout;
        private View moreWorkLayout;
        private TextView workAboutCount;
        private View itemView;
        private TextView moreWork;


        public WorkViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
            moreWorkLayout = itemView.findViewById(R.id.more_work_layout);
            workAboutCount = (TextView) itemView.findViewById(R.id.work_about_count);
            moreWork = (TextView) itemView.findViewById(R.id.more_work);
        }

        public void setWorkView(Work work, View view) {
            ImageView img = (ImageView) view.findViewById(R.id.work_cover);
            RelativeLayout.LayoutParams imgParam = (RelativeLayout.LayoutParams) img
                    .getLayoutParams();
            if (imgParam != null) {
                imgParam.height = workImageHeight;
            }
            setImage(img, work.getCoverPath(), workImageWidth);
            ImageView iconInstallment = (ImageView) view.findViewById(R.id.img_installment);
            TextView property = (TextView) view.findViewById(R.id.property);
            TextView titleName = (TextView) view.findViewById(R.id.title_name);
            TextView name = (TextView) view.findViewById(R.id.merchant_name);
            ImageView bondIcon = (ImageView) view.findViewById(R.id.bond_icon);
            TextView disCountPrice = (TextView) view.findViewById(R.id.discount_price);
            TextView originalPrice = (TextView) view.findViewById(R.id.original_price);
            originalPrice.getPaint()
                    .setAntiAlias(true);
            originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            property.setText(work.getKind());
            property.setVisibility(View.VISIBLE);
            titleName.setText(work.getTitle());
            if (work.getBondSign() != null) {
                bondIcon.setVisibility(View.VISIBLE);
                name.setPadding(0, 0, iconMargin, 0);
            } else {
                bondIcon.setVisibility(View.GONE);
                name.setPadding(0, 0, 0, 0);
            }
            iconInstallment.setVisibility(View.GONE);
            name.setText(work.getMerchantName());
            disCountPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            if (work.getMarketPrice() > 0) {
                originalPrice.setVisibility(View.VISIBLE);
                originalPrice.setText(" " + Util.formatDouble2String(work.getMarketPrice()) + " ");
            } else {
                originalPrice.setVisibility(View.GONE);
            }
            view.findViewById(R.id.prices_layout)
                    .setVisibility(View.VISIBLE);
            showTimeDown(view, work);
        }

        public void bind(JSONObject data) {
            if (data != null) {
                this.itemView.setVisibility(View.VISIBLE);
                this.title.setText(getString(R.string.label_correlation,
                        getString(R.string.label_work)));
                this.moreWork.setText(getString(R.string.label_more_work1,
                        getString(R.string.label_work)));
                int workCount = data.optInt("count", 0);
                this.workAboutCount.setText(getString(R.string.label_about_count, workCount));
                this.workAboutCount.setVisibility(workCount > 0 ? View.VISIBLE : View.GONE);
                JSONArray examples = data.optJSONArray("examples");
                if (examples != null) {
                    int examplesLength = Math.min(examples.length(), 4);
                    for (int i = 0; i < examplesLength; i++) {
                        JSONObject item = examples.optJSONObject(i);
                        if (item != null) {
                            final Work work = new Work(item);
                            View workView = this.contentLayout.getChildAt(i);
                            workView.setTag(work);
                            workView.setVisibility(View.VISIBLE);
                            if (i == 0) {
                                //第一个设置上边距
                                workView.setPadding(workView.getPaddingLeft(),
                                        0,
                                        workView.getPaddingRight(),
                                        workView.getPaddingBottom());
                            }
                            workView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Work workTag = (Work) v.getTag();
                                    if (workTag != null) {
                                        Intent intent = new Intent(getContext(),
                                                WorkActivity.class);
                                        intent.putExtra("id", workTag.getId());
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim
                                                        .slide_in_right,
                                                R.anim.activity_anim_default);
                                    }
                                }
                            });
                            setWorkView(work, workView);
                        }
                    }
                }
                this.moreWorkLayout.setVisibility(workCount > 4 ? View.VISIBLE : View.GONE);
                this.moreWorkLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MoreMarkWorkAndCaseActivity.class);
                        intent.putExtra("markType", Constants.MARK_TYPE.WORK);
                        intent.putExtra("markId", new Long(markId));
                        intent.putExtra("isMore", true);
                        intent.putExtra("markTitle", markTitle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else {
                this.itemView.setVisibility(View.GONE);
            }
        }
    }

    class CaseViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearLayout contentLayout;
        private View moreCaseLayout;
        private TextView caseAboutCount;
        private View itemView;
        private TextView moreCase;

        public CaseViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
            moreCaseLayout = itemView.findViewById(R.id.more_Case_layout);
            caseAboutCount = (TextView) itemView.findViewById(R.id.case_about_count);
            moreCase = (TextView) itemView.findViewById(R.id.more_case);
        }

        public void bind(JSONObject data) {
            if (data != null) {
                this.itemView.setVisibility(View.VISIBLE);
                this.title.setText(getString(R.string.label_correlation,
                        getString(R.string.label_case)));
                this.moreCase.setText(getString(R.string.label_more_work1,
                        getString(R.string.label_case)));
                int caseCount = data.optInt("count", 0);
                this.caseAboutCount.setText(getString(R.string.label_about_count, caseCount));
                this.caseAboutCount.setVisibility(caseCount > 0 ? View.VISIBLE : View.GONE);
                JSONArray examples = data.optJSONArray("examples");
                if (examples != null) {
                    int examplesLength = Math.min(examples.length(), 4);
                    for (int i = 0; i < examplesLength; i++) {
                        JSONObject item = examples.optJSONObject(i);
                        if (item != null) {
                            View caseView = contentLayout.getChildAt(i);
                            caseView.setVisibility(View.VISIBLE);
                            Work work = new Work(item);
                            if (i == 0) {
                                caseView.setPadding(caseView.getPaddingLeft(),
                                        0,
                                        caseView.getPaddingRight(),
                                        caseView.getPaddingBottom());
                            }
                            setCaseView(caseView, work);
                        }
                    }
                }
                this.moreCaseLayout.setVisibility(caseCount > 4 ? View.VISIBLE : View.GONE);
                this.moreCaseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MoreMarkWorkAndCaseActivity.class);
                        intent.putExtra("markType", Constants.MARK_TYPE.CASE);
                        intent.putExtra("markId", new Long(markId));
                        intent.putExtra("isMore", true);
                        intent.putExtra("markTitle", markTitle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else {
                this.itemView.setVisibility(View.GONE);
            }
        }

        public void setCaseView(View caseView, Work work) {
            caseView.setTag(work);

            ImageView img = (ImageView) caseView.findViewById(R.id.case_cover);
            RelativeLayout.LayoutParams imgParam = (RelativeLayout.LayoutParams) img
                    .getLayoutParams();
            if (imgParam != null) {
                imgParam.height = workImageHeight;
            }
            setImage(img, work.getCoverPath(), workImageWidth);
            TextView property = (TextView) caseView.findViewById(R.id.property);
            TextView titleName = (TextView) caseView.findViewById(R.id.title_name);
            TextView name = (TextView) caseView.findViewById(R.id.merchant_name);
            ImageView bondIcon = (ImageView) caseView.findViewById(R.id.bond_icon);
            TextView caseCollectCount = (TextView) caseView.findViewById(R.id.case_collect_count);

            caseCollectCount.setText(String.valueOf(work.getCollectorCount()));
            property.setText(work.getKind());
            property.setVisibility(View.VISIBLE);
            titleName.setText(work.getTitle());
            if (work.getBondSign() != null) {
                bondIcon.setVisibility(View.VISIBLE);
                name.setPadding(0, 0, iconMargin, 0);
            } else {
                bondIcon.setVisibility(View.GONE);
                name.setPadding(0, 0, 0, 0);
            }
            name.setText(work.getMerchantName());
            caseView.findViewById(R.id.case_collect_layout)
                    .setVisibility(View.VISIBLE);
            caseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work workTag = (Work) v.getTag();
                    if (workTag != null) {
                        Intent intent = new Intent(getContext(), CaseDetailActivity.class);
                        intent.putExtra("id", workTag.getId());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }

    /**
     * shopping_list_single_item2
     */
    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView recyclerView;
        private View moreProductLayout;
        private TextView productAboutCount;
        private View itemView;
        private TextView moreProduct;
        private ProductItemViewAdapter adapter;
        private LinearLayoutManager manager;
        private int itemSpace;

        public ProductViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            moreProduct = (TextView) itemView.findViewById(R.id.more_product);
            title = (TextView) itemView.findViewById(R.id.title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.product_list);
            moreProductLayout = itemView.findViewById(R.id.more_product_layout);
            productAboutCount = (TextView) itemView.findViewById(R.id.product_about_count);
            manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
            itemSpace = Math.round(dm.density * 10);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(
                        Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (parent.getChildAdapterPosition(view) == parent.getAdapter()
                            .getItemCount() - 1) {
                        outRect.set(itemSpace, 0, itemSpace, 0);
                    } else {
                        outRect.set(itemSpace, 0, 0, 0);
                    }
                }
            });
        }

        public void bind(JSONObject data) {
            if (data != null) {
                this.itemView.setVisibility(View.VISIBLE);
                this.title.setText(getString(R.string.label_correlation,
                        getString(R.string.label_products1)));
                this.moreProduct.setText(getString(R.string.label_more_work1,
                        getString(R.string.label_products1)));
                int productCount = data.optInt("count", 0);
                this.productAboutCount.setText(getString(R.string.label_about_count, productCount));
                this.productAboutCount.setVisibility(productCount > 0 ? View.VISIBLE : View.GONE);
                JSONArray examples = data.optJSONArray("examples");
                if (this.adapter == null) {
                    this.adapter = new ProductItemViewAdapter(examples);
                    this.recyclerView.setAdapter(this.adapter);
                } else {
                    this.adapter.notifyDataSetChanged();
                }
                this.moreProductLayout.setVisibility(productCount > 12 ? View.VISIBLE : View.GONE);
                this.moreProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MoreMarkProductActivity.class);
                        intent.putExtra("markType", Constants.MARK_TYPE.PRODUCT);
                        intent.putExtra("isMore", true);
                        intent.putExtra("markId", markId);
                        intent.putExtra("markTitle", markTitle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            }
        }
    }

    class ThreadViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearLayout contentLayout;
        private View moreThreadLayout;
        private TextView threadAboutCount;
        private View itemView;
        private TextView moreThread;

        public ThreadViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
            moreThreadLayout = itemView.findViewById(R.id.more_thread_layout);
            threadAboutCount = (TextView) itemView.findViewById(R.id.thread_about_count);
            moreThread = (TextView) itemView.findViewById(R.id.more_case);
        }

        public void bind(JSONObject data) {
            if (data != null) {
                this.itemView.setVisibility(View.VISIBLE);
                this.title.setText(getString(R.string.label_correlation,
                        getString(R.string.label_topic)));
                this.moreThread.setText(getString(R.string.label_more_work1,
                        getString(R.string.label_topic)));
                int productCount = data.optInt("count", 0);
                this.threadAboutCount.setText(getString(R.string.label_about_count, productCount));
                this.threadAboutCount.setVisibility(productCount > 0 ? View.VISIBLE : View.GONE);
                JSONArray examples = data.optJSONArray("examples");
                if (examples != null) {
                    int examplesLength = Math.min(examples.length(), 4);
                    for (int i = 0; i < examplesLength; i++) {
                        View itemView = this.contentLayout.getChildAt(i);
                        JSONObject item = examples.optJSONObject(i);
                        CommunityThread thread = new CommunityThread(item);
                        if (i == 0) {
                            itemView.setPadding(itemView.getPaddingLeft(),
                                    0,
                                    itemView.getPaddingRight(),
                                    itemView.getPaddingBottom());
                        }
                        if (i == examplesLength - 1) {
                            itemView.findViewById(R.id.line)
                                    .setVisibility(View.GONE);
                        }
                        if (thread.getId() > 0) {
                            itemView.setVisibility(View.VISIBLE);
                            setThreadView(itemView, thread);
                        }
                    }
                }
                this.moreThreadLayout.setVisibility(productCount > 4 ? View.VISIBLE : View.GONE);
                this.moreThreadLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MoreMarkThreadActivity.class);
                        intent.putExtra("markType", Constants.MARK_TYPE.THREAD);
                        intent.putExtra("markId", markId);
                        intent.putExtra("isMore", true);
                        intent.putExtra("markTitle", markTitle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            }
        }

        private void setThreadView(View view, final CommunityThread thread) {
            if (view != null) {
                if (thread != null) {
                    view.setVisibility(View.VISIBLE);
                    final User user = thread.getUser();
                    if (user != null) {
                        RoundedImageView logo = (RoundedImageView) view.findViewById(R.id
                                .user_icon);
                        String logoPath = JSONUtil.getImagePathForWxH(user.getAvatar(),
                                storyLogoImageWidth,
                                storyLogoImageWidth);
                        if (!JSONUtil.isEmpty(logoPath)) {
                            ImageLoadTask task = new ImageLoadTask(logo, null, 0);
                            logo.setTag(logoPath);
                            task.loadImage(logoPath,
                                    storyLogoImageWidth,
                                    ScaleMode.ALL,
                                    new AsyncBitmapDrawable(getResources(),
                                            R.mipmap.icon_avatar_primary,
                                            task));
                        }

                        TextView weddingDate = (TextView) view.findViewById(R.id.user_wedding_date);
                        if (user.getWeddingDay() != null && user.getIsPending() != 0) {
                            if (user.getWeddingDay()
                                    .before(Calendar.getInstance()
                                            .getTime())) {
                                weddingDate.setText(user.getGender() == 1 ? getString(R.string
                                        .label_married2) : getString(
                                        R.string.label_married));
                            } else {
                                weddingDate.setText(getString(R.string.label_wedding_time1,
                                        shortSimpleDateFormat.format(user.getWeddingDay())));
                            }
                            weddingDate.setVisibility(View.VISIBLE);
                        } else {
                            weddingDate.setText(user.getGender() == 1 ? "" : getString(R.string
                                    .label_no_wedding_day));
                            weddingDate.setVisibility(View.VISIBLE);
                        }

                        TextView userTitle = (TextView) view.findViewById(R.id.user_title);
                        userTitle.setText(user.getNick());
                    }

                    TextView time = (TextView) view.findViewById(R.id.user_time);
                    time.setText(JSONUtil.isEmpty(Util.getRefrshTime(thread.getLastPostTime(),
                            getContext())) ? "" : Util.getRefrshTime(thread.getLastPostTime(),
                            getContext()));

                    TextView threadTitle = (TextView) view.findViewById(R.id.thread_title);

                    TextView threadCommentCount = (TextView) view.findViewById(R.id
                            .thread_comment_count);
                    threadCommentCount.setText(getString(R.string.label_comment_count2,
                            thread.getPostCount()));

                    TextView threadFrom = (TextView) view.findViewById(R.id.thread_from_content);
                    TextView threadFromTip = (TextView) view.findViewById(R.id.thread_from);


                    if (thread.isHidden()) {
                        View hidden = view.findViewById(R.id.thread_hidden);
                        hidden.setVisibility(View.VISIBLE);
                    }

                    RecyclingImageView img = (RecyclingImageView) view.findViewById(R.id
                            .thread_image);
                    CommunityPost post = thread.getPost();
                    TextView threadContent = (TextView) view.findViewById(R.id.thread_content);
                    String path = null;
                    //精编话题显示精编的标题和导读
                    if (thread.getThreadPages() != null) {
                        threadTitle.setText(JSONUtil.isEmpty(thread.getThreadPages()
                                .getTitle()) ? "" : EmojiUtil.parseEmojiByText2(getActivity(),
                                thread.getThreadPages()
                                        .getTitle(),
                                faceSize));
                        threadContent.setText(EmojiUtil.parseEmojiByText2(getActivity(),
                                thread.getThreadPages()
                                        .getSubTitle(),
                                faceSize2));
                        path = JSONUtil.getImagePathForWxH(thread.getThreadPages()
                                .getImgPath(), threadLogoWidth, threadLogoWidth);
                    } else {
                        threadTitle.setText(JSONUtil.isEmpty(thread.getTitle()) ? "" : EmojiUtil
                                .parseEmojiByText2(
                                getActivity(),
                                thread.getTitle(),
                                faceSize));
                        if (post != null) {
                            threadContent.setText(EmojiUtil.parseEmojiByText2(getActivity(),
                                    post.getContent(),
                                    faceSize2));
                            ArrayList<Photo> photos = post.getPhotos();
                            if (photos != null && photos.size() >= 1) {
                                Photo photo = photos.get(0);
                                path = JSONUtil.getImagePathForWxH(photo.getPath(),
                                        threadLogoWidth,
                                        threadLogoWidth);
                            } else {
                                img.setImageBitmap(null);
                                img.setVisibility(View.GONE);
                            }
                        }
                    }

                    if (!JSONUtil.isEmpty(path)) {
                        img.setVisibility(View.VISIBLE);
                        ImageLoadTask task = new ImageLoadTask(img, 0);
                        img.setTag(path);
                        task.loadImage(path,
                                threadLogoWidth,
                                ScaleMode.ALL,
                                new AsyncBitmapDrawable(getResources(),
                                        R.mipmap.icon_empty_image,
                                        task));
                    } else {
                        img.setVisibility(View.GONE);
                    }
                    View threadContentLayout = view.findViewById(R.id.thread_content_layout);
                    if (img.getVisibility() == View.GONE) {
                        threadContentLayout.setPadding(0,
                                threadContentLayout.getPaddingTop(),
                                threadContentLayout.getPaddingRight(),
                                threadContentLayout.getPaddingBottom());
                    } else {
                        threadContentLayout.setPadding(Math.round(dm.density * 8),
                                threadContentLayout.getPaddingTop(),
                                threadContentLayout.getPaddingRight(),
                                threadContentLayout.getPaddingBottom());
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),
                                    CommunityThreadDetailActivity.class);
                            intent.putExtra("id", thread.getId());
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });

                    view.findViewById(R.id.user_info_layout)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    User threadUser = thread.getUser();
                                    if (threadUser == null) {
                                        return;
                                    }
                                    User currentUser = Session.getInstance()
                                            .getCurrentUser();
                                    if ((currentUser != null && threadUser.getId()
                                            .equals(currentUser.getId()))) {
                                        return;
                                    }

                                    Intent intent = new Intent(getContext(),
                                            UserProfileActivity.class);
                                    intent.putExtra("id", threadUser.getId());
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.activity_anim_default);
                                }
                            });
                    //取消小组
                    threadFromTip.setVisibility(View.INVISIBLE);
                    threadFrom.setVisibility(View.INVISIBLE);

                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    class ProductItemViewHolder extends RecyclerView.ViewHolder {
        private RecyclingImageView imageView;
        private TextView title;
        private TextView price2;
        private TextView collectCount;
        private View itemView;
        private ImageView badge;
        private ImageView sign;
        private DecimalFormat decimalFormat;
        private TextView price;

        public ProductItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (RecyclingImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            price2 = (TextView) itemView.findViewById(R.id.price2);
            collectCount = (TextView) itemView.findViewById(R.id.collect_count);
            badge = (ImageView) itemView.findViewById(R.id.badge);
            sign = (ImageView) itemView.findViewById(R.id.sign);
            price = (TextView) itemView.findViewById(R.id.price);
            decimalFormat = new DecimalFormat("###.00");
        }
    }


    class ProductItemViewAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {
        private JSONArray array;
        private int badgeSize;
        private int signSize;

        public ProductItemViewAdapter(JSONArray array) {
            this.array = array;
            badgeSize = Math.round(40 * dm.density);
            signSize = Math.round(24 * dm.density);
        }

        @Override
        public ProductItemViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View item = getActivity().getLayoutInflater()
                    .inflate(R.layout.shopping_list_single_item2, parent, false);
            return new ProductItemViewHolder(item);
        }

        @Override
        public void onBindViewHolder(
                ProductItemViewHolder holder, int position) {
            JSONObject data = array.optJSONObject(position);
            ShopProduct product = new ShopProduct(data);
            if (data != null) {
                holder.title.setText(product.getTitle());
                String priceStr = holder.decimalFormat.format(product.getPrice());
                holder.price2.setText(priceStr);
                holder.collectCount.setText(String.valueOf(product.getLikeCount()));
                setImage(holder.imageView, product.getPhoto(), Math.round(dm.density * 144));
                holder.itemView.setTag(data);
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView
                    .getLayoutParams();
            if (params != null) {
                params.width = Math.round(dm.density * 144);
            }

            if (product.getRule() != null && !JSONUtil.isEmpty(product.getRule()
                    .getShowimg2())) {
                holder.badge.setVisibility(View.VISIBLE);
                String badgeUrl = JSONUtil.getImagePath2(product.getRule()
                        .getShowimg2(), badgeSize);
                if (!badgeUrl.equals(holder.badge.getTag())) {
                    holder.badge.setTag(badgeUrl);
                    ImageLoadTask task = new ImageLoadTask(holder.badge, null, null, 0, true, true);
                    task.loadImage(badgeUrl,
                            badgeSize,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            } else {
                holder.badge.setVisibility(View.GONE);
            }

            String signUrl = JSONUtil.getImagePath2(product.getShopImg(), signSize);
            if (!JSONUtil.isEmpty(signUrl)) {
                holder.sign.setVisibility(View.VISIBLE);
                if (!signUrl.equals(holder.sign.getTag())) {
                    holder.sign.setTag(signUrl);
                    ImageLoadTask task = new ImageLoadTask(holder.sign, null, null, 0, true, true);
                    task.loadImage(signUrl,
                            signSize,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            } else {
                holder.sign.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject tag = (JSONObject) v.getTag();
                    if (tag != null) {
                        Intent intent = new Intent(getContext(), ShopProductDetailActivity.class);
                        intent.putExtra("id", new Long(JSONUtil.getString(tag, "id")));
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.array == null ? 0 : this.array.length();
        }
    }

    class GetMarkInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
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
        protected void onPostExecute(JSONObject object) {
            if (getActivity() == null && getActivity().isFinishing()) {
                return;
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (recyclerView != null) {
                recyclerView.onRefreshComplete();
            }
            if (object != null) {
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    if (!mData.isEmpty()) {
                        mData.clear();
                    }

                    follow = data.optInt("is_follow", 0);
                    //1已关注 0未关注
                    if (follow == 0) {
                        btnFollow.setText(getString(R.string.label_follow));
                    } else if (follow == 1) {
                        btnFollow.setText(getString(R.string.label_followed));
                    }
                    JSONArray list = data.optJSONArray("export_list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.optJSONObject(i);
                        mData.add(item);
                        //套餐有倒计时 单独提出数据
                        int type = item.optInt("type");
                        if (type == Constants.MARK_TYPE.WORK) {
                            workPostion = i;
                            workData = item;
                        }
                    }
                }
                footView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
            setEmptyView();
            super.onPostExecute(object);
        }
    }


    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.post(timeRunnable);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        mHandler.removeCallbacks(timeRunnable);
        super.onResume();
    }
}
