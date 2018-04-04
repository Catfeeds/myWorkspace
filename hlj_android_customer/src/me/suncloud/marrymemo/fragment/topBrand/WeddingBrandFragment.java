package me.suncloud.marrymemo.fragment.topBrand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.topBrand.BrandHall;
import me.suncloud.marrymemo.model.topBrand.BrandLabel;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.FlowLayout;

/**
 * 品牌馆fragment
 * Created by jinxin on 2016/11/11.
 */
public class WeddingBrandFragment extends ScrollAbleFragment {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    private View footerView;
    private Unbinder unbinder;
    private ArrayList<BrandHall> merchants;
    private int strokeWidth;
    private int coverWidth;
    private int logoSize;

    public static WeddingBrandFragment newInstance(ArrayList<BrandHall> merchants) {
        WeddingBrandFragment fragment = new WeddingBrandFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("merchants", merchants);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        merchants = new ArrayList<>();
        Point point = CommonUtil.getDeviceSize(getContext());
        DisplayMetrics display = getResources().getDisplayMetrics();
        strokeWidth = (int) Math.min(1, Math.round(display.density * 0.5));
        logoSize = CommonUtil.dp2px(getContext(), 32);
        coverWidth = point.x - CommonUtil.dp2px(getContext(), 28);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            ArrayList<BrandHall> merchants = getArguments().getParcelableArrayList("merchants");
            if (merchants != null) {
                this.merchants.clear();
                this.merchants.addAll(merchants);
            }
        }
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(new BrandAdapter());
        if (merchants.isEmpty()) {
            emptyView.showEmptyView();
        } else {
            emptyView.hideEmptyView();
        }
        return rootView;
    }

    class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int FOOTER = 1;
        private int ITEM = 2;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM) {
                View itemView = LayoutInflater.from(getContext())
                        .inflate(R.layout.wedding_brand_item, parent, false);
                return new ViewHolder(itemView);
            } else if (viewType == FOOTER) {
                return new ExtraViewHolder(footerView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM) {
                return;
            }
            ViewHolder holder = (ViewHolder) h;
            BrandHall brandMerchant = merchants.get(position);
            if (brandMerchant == null || brandMerchant.getId() == 0) {
                return;
            }
            final Merchant merchant = brandMerchant.getMerchant();
            if (merchant == null || merchant.getId() == 0) {
                return;
            }
            String imagePath = ImageUtil.getImagePath(brandMerchant.getImg(), coverWidth);
            if (TextUtils.isEmpty(imagePath)) {
                imagePath = ImageUtil.getImagePath(merchant.getCoverPath(), coverWidth);
            }
            if (!TextUtils.isEmpty(imagePath)) {
                Glide.with(getContext())
                        .load(imagePath)
                        .apply(new RequestOptions().dontAnimate())
                        .into(holder.imgCover);
            } else {
                Glide.with(getContext())
                        .clear(holder.imgCover);
                holder.imgCover.setImageBitmap(null);
            }
            BrandLabel label = BrandLabel.getBrandLabel(brandMerchant.getLabel());
            if (label == null) {
                holder.imgLabel.setVisibility(View.GONE);
            } else {
                holder.imgLabel.setVisibility(View.VISIBLE);
                holder.imgLabel.setImageResource(label.getDrawable());
            }
            String logoPath = ImageUtil.getImagePath2(merchant.getLogoPath(), logoSize);
            if (!JSONUtil.isEmpty(logoPath)) {
                Glide.with(getContext())
                        .load(logoPath)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(holder.imgAvatar);
            } else {
                Glide.with(getContext())
                        .clear(holder.imgAvatar);
                holder.imgAvatar.setImageResource(R.mipmap.icon_avatar_primary);
            }
            String title = brandMerchant.getTitle();
            if (JSONUtil.isEmpty(title)) {
                title = merchant.getName();
            }
            holder.tvTitle.setText(title);
            holder.tvFans.setText(String.valueOf(merchant.getFansCount()));
            holder.tvComments.setText(String.valueOf(merchant.getCommentCount()));
            holder.tvFollow.setText(merchant.isCollected() ? getString(R.string.btn_look) :
                    getString(
                    R.string.label_follow));
            holder.tvFollow.setOnClickListener(new OnCollectedClickListener(merchant, holder));
            addTagView(holder.flowTag, brandMerchant.getComments());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MerchantDetailActivity.class);
                    intent.putExtra("id", merchant.getId());
                    startActivity(intent);
                    ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }

        @Override
        public int getItemCount() {
            return merchants == null ? 0 : merchants.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return FOOTER;
            } else {
                return ITEM;
            }
        }
    }

    class OnCollectedClickListener implements View.OnClickListener {
        private Merchant merchant;
        private ViewHolder holder;

        public OnCollectedClickListener(Merchant merchant, ViewHolder holder) {
            this.merchant = merchant;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            if (merchant.isCollected()) {
                //去看看
                Intent intent = new Intent(getContext(), MerchantDetailActivity.class);
                intent.putExtra("id", merchant.getId());
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("merchant_id", merchant.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new NewHttpPostTask(getContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestFailed(Object obj) {

                    }

                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject object = (JSONObject) obj;
                        ReturnStatus status = new ReturnStatus(object.optJSONObject("status"));
                        if (status.getRetCode() == 0) {
                            merchant.setCollected(!merchant.isCollected());
                            if (merchant.isCollected()) {
                                Util.showToast(R.string.hint_merchant_collect_complete,
                                        getContext());
                            }
                        }
                        holder.tvFollow.setText(merchant.isCollected() ? getString(R.string
                                .btn_look) : getString(
                                R.string.label_follow));
                    }
                }).execute(Constants.getAbsUrl(Constants.HttpPath.FOCUS_MERCHANT_URL),
                        jsonObject.toString());
            }
        }
    }

    private void addTagView(FlowLayout layout, String comments) {
        if (!JSONUtil.isEmpty(comments)) {
            layout.setVisibility(View.VISIBLE);
            String[] tags = comments.split("、");
            int count = layout.getChildCount();
            int size = Math.min(5, tags.length);
            if (count > size) {
                layout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                TextView tv = null;
                String tag = tags[i];
                if (i < count) {
                    view = layout.getChildAt(i);
                    tv = (TextView) view.findViewById(R.id.mark);
                }
                if (view == null) {
                    View.inflate(getContext(), R.layout.mark_item, layout);
                    view = layout.getChildAt(layout.getChildCount() - 1);
                    tv = (TextView) view.findViewById(R.id.mark);
                }
                if (tag.length() > 6) {
                    tag = tag.substring(0, 6);
                }
                tv.setText(tag);
                tv.setBackgroundResource(R.drawable.sp_r2_stroke_gray3);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_golden));
                GradientDrawable drawable = (GradientDrawable) tv.getBackground();
                if (drawable != null) {
                    drawable.setStroke(strokeWidth,
                            ContextCompat.getColor(getContext(), R.color.color_golden));
                }
            }
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.img_label)
        ImageView imgLabel;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_fans)
        TextView tvFans;
        @BindView(R.id.tv_comments)
        TextView tvComments;
        @BindView(R.id.tv_follow)
        TextView tvFollow;
        @BindView(R.id.flow_tag)
        FlowLayout flowTag;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            coverLayout.getLayoutParams().height = Math.round(coverWidth / (16.0f / 10.0f));
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
