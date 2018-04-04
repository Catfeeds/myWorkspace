package com.hunliji.marrybiz.fragment.market;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.model.SameBusiness;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.CompassCompeteDetailActivity;
import com.hunliji.marrybiz.widget.CheckableLinearLayout2;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jinxin on 2016/6/21.
 */
public abstract class BaseSameBusinessFragment extends RefreshFragment implements
        ObjectBindAdapter.ViewBinder<NewMerchant>, AbsListView.OnItemClickListener {

    @BindView(R.id.tab_work)
    TextView tabWork;
    @BindView(R.id.work_layout)
    CheckableLinearLayout2 workLayout;
    @BindView(R.id.tab_case)
    TextView tabCase;
    @BindView(R.id.case_layout)
    CheckableLinearLayout2 caseLayout;
    @BindView(R.id.tab_twitter)
    TextView tabTwitter;
    @BindView(R.id.twitter_layout)
    CheckableLinearLayout2 twitterLayout;
    @BindView(R.id.tab_fans)
    TextView tabFans;
    @BindView(R.id.fans_layout)
    CheckableLinearLayout2 fansLayout;
    @BindView(R.id.tab_layout)
    LinearLayout tabLayout;
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.img_empty_hint)
    ImageView imgEmptyHint;
    @BindView(R.id.text_empty_hint)
    TextView textEmptyHint;
    @BindView(R.id.empty_hint_layout)
    LinearLayout emptyHintLayout;
    private Unbinder unbinder;

    protected View headerView;
    protected SameBusiness sameBusiness;
    protected ObjectBindAdapter<NewMerchant> adapter;
    protected ArrayList<NewMerchant> merchantList;
    protected String order;
    protected int rectLogoSize;
    protected int roundLogoSize;
    protected String urlSpec;
    protected boolean showMyMerchant;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerView = View.inflate(getContext(), R.layout.same_business_rank_header, null);
        merchantList = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getContext(),
                merchantList,
                R.layout.round_merchant_list_item,
                this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        rectLogoSize = Math.round(dm.density * 64);
        roundLogoSize = Math.round(dm.density * 54);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compass_business, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        order = "active_works_pcount";
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getRefreshableView()
                .addHeaderView(headerView);
        listView.getRefreshableView()
                .setAdapter(adapter);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        return rootView;
    }

    /**
     * 判断是不是我的同业
     *
     * @return
     */
    private boolean isMySameBusiness(Long merchantId) {
        if (merchantId == null) {
            return false;
        }
        Long myId = null;
        MerchantUser currentUser = Session.getInstance()
                .getCurrentUser(getContext());
        if (currentUser != null) {
            myId = currentUser.getId();
        }
        return myId != null && myId.equals(merchantId);
    }

    private void setHeaderView(SameBusiness sameBusiness) {
        if (sameBusiness == null) {
            return;
        }
        setMyHeaderView();
    }

    private void setMyHeaderView() {
        tabLayout.setVisibility(View.VISIBLE);
        if (showMyMerchant) {
            headerView.findViewById(R.id.merchant_header_layout)
                    .setVisibility(View.VISIBLE);
            View merchantView = headerView.findViewById(R.id.merchant_header);
            setMerchantView(merchantView, sameBusiness.getMy());
        }
        View rankView = headerView.findViewById(R.id.rank_layout);
        TextView rank = (TextView) rankView.findViewById(R.id.rank);
        rank.setText(sameBusiness.getMyRank());
        rankView.setVisibility(JSONUtil.isEmpty(sameBusiness.getMyRank()) ? View.GONE : View
                .VISIBLE);
        TextView rankHintLeft = (TextView) headerView.findViewById(R.id.rank_rate_left);
        rankHintLeft.setText(getString(R.string.label_rank_hint1, sameBusiness.getCity()));
        TextView rankHintRight = (TextView) headerView.findViewById(R.id.rank_rate_right);
        rankHintRight.setText(getString(R.string.label_rank_hint2, sameBusiness.getProperty()));
        TextView rankRate = (TextView) headerView.findViewById(R.id.rank_rate);
        rankRate.setText(sameBusiness.getRate());
    }

    /**
     * 商家等级 logo Resource
     *
     * @param grade
     * @return
     */
    private int getMerchantLevelImageResource(int grade) {
        int source = -1;
        switch (grade) {
            case 0:
                source = R.drawable.icon_merchant_level0;
                break;
            case 1:
                source = R.drawable.icon_merchant_level1;
                break;
            case 2:
                source = R.drawable.icon_merchant_level2;
                break;
            case 3:
                source = R.drawable.icon_merchant_level3;
                break;
            case 4:
                source = R.drawable.icon_merchant_level4;
                break;
        }
        return source;
    }

    private void setMerchantView(View view, NewMerchant merchant) {
        if (merchant == null) {
            return;
        }
        RoundedImageView logo = (RoundedImageView) view.findViewById(R.id.logo);
        setRoundImageView(logo, merchant.getLogoPath(), rectLogoSize);
        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView levelIcon = (ImageView) view.findViewById(R.id.level_icon);
        View bondIcon = view.findViewById(R.id.bond_icon);
        View freeIcon = view.findViewById(R.id.free_icon);
        View promiseIcon = view.findViewById(R.id.promise_icon);
        View refundIcon = view.findViewById(R.id.refund_icon);
        View giftIcon = view.findViewById(R.id.gift_icon);
        TextView tvLabel1 = (TextView) view.findViewById(R.id.tv_label1);
        TextView tvLabel2 = (TextView) view.findViewById(R.id.tv_label2);
        TextView tvLabel4 = (TextView) view.findViewById(R.id.tv_label4);
        TextView tvLabel3 = (TextView) view.findViewById(R.id.tv_label3);

        levelIcon.setImageDrawable(getResources().getDrawable(getMerchantLevelImageResource
                (merchant.getGrade())));
        levelIcon.setVisibility(View.VISIBLE);
        if (merchant.getGrade() != 0) {
            levelIcon.setVisibility(View.VISIBLE);
        } else {
            levelIcon.setVisibility(View.GONE);
        }
        name.setText(merchant.getName());

        giftIcon.setVisibility(JSONUtil.isEmpty(merchant.getPlatformGift()) ? View.GONE : View
                .VISIBLE);
        bondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        freeIcon.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE : View.GONE);
        promiseIcon.setVisibility(merchant.getMerchantPromise() != null && merchant
                .getMerchantPromise()
                .size() > 0 ? View.VISIBLE : View.GONE);
        refundIcon.setVisibility(merchant.getChargeBack() != null && merchant.getChargeBack()
                .size() > 0 ? View.VISIBLE : View.GONE);
        tvLabel1.setText(getString(R.string.label_work_count, merchant.getActiveWorkCount()));
        tvLabel2.setText(getString(R.string.label_case_count, merchant.getActiveCaseCount()));
        tvLabel4.setText(getString(R.string.merchant_collect_count, merchant.getFansCount()));
        tvLabel3.setText(getString(R.string.merchant_twitter_count, merchant.getFeedCount()));
    }

    private void setRoundImageView(
            RoundedImageView imageView, String path, int size) {
        path = JSONUtil.getImagePath(path, size);
        if (!JSONUtil.isEmpty(path)) {
            ImageLoadTask task = new ImageLoadTask(imageView, 0);
            imageView.setTag(path);
            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                    R.mipmap.icon_avatar_primary,
                    task);
            task.loadImage(path, rectLogoSize, ScaleMode.ALL, image);
        }
    }

    @Override
    public void onResume() {
        refresh(order);
        super.onResume();
    }

    @OnClick({R.id.tab_work, R.id.tab_case, R.id.tab_twitter, R.id.tab_fans})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tab_work:
                order = "active_works_pcount";
                refresh(order);
                workLayout.setChecked(true);
                caseLayout.setChecked(false);
                twitterLayout.setChecked(false);
                fansLayout.setChecked(false);
                tabWork.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabCase.setTextColor(getResources().getColor(R.color.colorGray));
                tabTwitter.setTextColor(getResources().getColor(R.color.colorGray));
                tabFans.setTextColor(getResources().getColor(R.color.colorGray));
                break;
            case R.id.tab_case:
                order = "active_cases_pcount";
                refresh(order);
                workLayout.setChecked(false);
                caseLayout.setChecked(true);
                twitterLayout.setChecked(false);
                fansLayout.setChecked(false);
                tabWork.setTextColor(getResources().getColor(R.color.colorGray));
                tabCase.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabTwitter.setTextColor(getResources().getColor(R.color.colorGray));
                tabFans.setTextColor(getResources().getColor(R.color.colorGray));
                break;
            case R.id.tab_twitter:
                order = "feed_count";
                refresh(order);
                workLayout.setChecked(false);
                caseLayout.setChecked(false);
                twitterLayout.setChecked(true);
                fansLayout.setChecked(false);
                tabWork.setTextColor(getResources().getColor(R.color.colorGray));
                tabCase.setTextColor(getResources().getColor(R.color.colorGray));
                tabTwitter.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabFans.setTextColor(getResources().getColor(R.color.colorGray));
                break;
            case R.id.tab_fans:
                order = "fans_count";
                refresh(order);
                workLayout.setChecked(false);
                caseLayout.setChecked(false);
                twitterLayout.setChecked(false);
                fansLayout.setChecked(true);
                tabWork.setTextColor(getResources().getColor(R.color.colorGray));
                tabCase.setTextColor(getResources().getColor(R.color.colorGray));
                tabTwitter.setTextColor(getResources().getColor(R.color.colorGray));
                tabFans.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }


    @Override
    public void refresh(Object... params) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        String order = (String) params[0];
        String url = Constants.getAbsUrl(urlSpec, order);
        new GetSameBusinessTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void setEmptyView() {
    }

    @Override
    public void setViewValue(
            View view, NewMerchant newMerchant, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (position == 0) {
            holder.levelLogo.setImageResource(R.drawable.icon_level1);
            holder.levelLogo.setVisibility(View.VISIBLE);
            holder.levelRank.setVisibility(View.GONE);
        } else if (position == 1) {
            holder.levelLogo.setImageResource(R.drawable.icon_level2);
            holder.levelLogo.setVisibility(View.VISIBLE);
            holder.levelRank.setVisibility(View.GONE);
        } else if (position == 2) {
            holder.levelLogo.setImageResource(R.drawable.icon_level3);
            holder.levelLogo.setVisibility(View.VISIBLE);
            holder.levelRank.setVisibility(View.GONE);
        } else {
            holder.levelLogo.setVisibility(View.GONE);
            holder.levelRank.setVisibility(View.VISIBLE);
            holder.levelRank.setText(String.valueOf(position + 1));
        }

        setRoundImageView(holder.logo, newMerchant.getLogoPath(), roundLogoSize);
        holder.name.setText(newMerchant.getName());
        holder.levelIcon.setImageDrawable(getResources().getDrawable(getMerchantLevelImageResource(
                newMerchant.getGrade())));
        if (newMerchant.getGrade() != 0) {
            holder.levelIcon.setVisibility(View.VISIBLE);
        } else {
            holder.levelIcon.setVisibility(View.GONE);
        }

        holder.giftIcon.setVisibility(JSONUtil.isEmpty(newMerchant.getPlatformGift()) ? View.GONE
                : View.VISIBLE);
        holder.bondIcon.setVisibility(newMerchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        holder.freeIcon.setVisibility(newMerchant.getActiveWorkCount() > 0 ? View.VISIBLE : View
                .GONE);
        holder.promiseIcon.setVisibility(newMerchant.getMerchantPromise() != null && newMerchant
                .getMerchantPromise()
                .size() > 0 ? View.VISIBLE : View.GONE);
        holder.refundIcon.setVisibility(newMerchant.getChargeBack() != null && newMerchant
                .getChargeBack()
                .size() > 0 ? View.VISIBLE : View.GONE);
        holder.tvLabel1.setText(getString(R.string.label_work_count,
                newMerchant.getActiveWorkCount()));
        holder.tvLabel2.setText(getString(R.string.label_case_count,
                newMerchant.getActiveCaseCount()));
        holder.tvLabel4.setText(getString(R.string.merchant_collect_count,
                newMerchant.getFansCount()));
        holder.tvLabel3.setText(getString(R.string.merchant_twitter_count,
                newMerchant.getFeedCount()));
        boolean isMyShop = isMySameBusiness(newMerchant.getUserId());
        holder.iconMyShop.setVisibility(isMyShop ? View.VISIBLE : View.GONE);
        if (isMyShop) {
            holder.contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable
                    .image_bg_my_shop));
        } else {
            holder.contentLayout.setBackgroundDrawable(null);
        }
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        NewMerchant merchant = (NewMerchant) listView.getRefreshableView()
                .getAdapter()
                .getItem(position);
        if (merchant == null || merchant.getId() <= 0) {
            return;
        }
        Intent intent = new Intent(getActivity(), CompassCompeteDetailActivity.class);
        intent.putExtra("merchant", merchant);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    class GetSameBusinessTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(getContext(), url);
                if (!JSONUtil.isEmpty(json)) {
                    return new JSONObject(json);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (jsonObject != null) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    sameBusiness = new SameBusiness(data);
                    setHeaderView(sameBusiness);
                    if (sameBusiness.getMerchantList() != null) {
                        merchantList.clear();
                        merchantList.addAll(sameBusiness.getMerchantList());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            if (merchantList.isEmpty()) {
                setEmptyView();
            }
            super.onPostExecute(jsonObject);
        }
    }

    class ViewHolder {
        @BindView(R.id.level_logo)
        ImageView levelLogo;
        @BindView(R.id.level_rank)
        TextView levelRank;
        @BindView(R.id.level_layout)
        RelativeLayout levelLayout;
        @BindView(R.id.logo)
        RoundedImageView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.level_icon)
        ImageView levelIcon;
        @BindView(R.id.bond_icon)
        ImageView bondIcon;
        @BindView(R.id.refund_icon)
        ImageView refundIcon;
        @BindView(R.id.promise_icon)
        ImageView promiseIcon;
        @BindView(R.id.free_icon)
        ImageView freeIcon;
        @BindView(R.id.gift_icon)
        ImageView giftIcon;
        @BindView(R.id.exclusive_offer_icon)
        ImageView exclusiveOfferIcon;
        @BindView(R.id.promote)
        TextView promote;
        @BindView(R.id.icons_layout)
        LinearLayout iconsLayout;
        @BindView(R.id.tv_hotel_price)
        TextView tvHotelPrice;
        @BindView(R.id.price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.icon_my_shop)
        ImageView iconMyShop;
        @BindView(R.id.tv_label1)
        TextView tvLabel1;
        @BindView(R.id.tv_label2)
        TextView tvLabel2;
        @BindView(R.id.tv_label3)
        TextView tvLabel3;
        @BindView(R.id.tv_label4)
        TextView tvLabel4;
        @BindView(R.id.content_layout)
        RelativeLayout contentLayout;
        @BindView(R.id.shop_gift_content)
        TextView shopGiftContent;
        @BindView(R.id.shop_gift_layout)
        LinearLayout shopGiftLayout;
        @BindView(R.id.cost_effective_content)
        TextView costEffectiveContent;
        @BindView(R.id.cost_effective_layout)
        LinearLayout costEffectiveLayout;
        @BindView(R.id.merchant_list_line)
        View merchantListLine;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

}
