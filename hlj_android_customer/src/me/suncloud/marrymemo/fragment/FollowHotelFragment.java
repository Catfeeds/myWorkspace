package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.Hotel;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by Suncloud on 2016/2/26.
 */
public class FollowHotelFragment extends RefreshListFragment implements ObjectBindAdapter
        .ViewBinder<NewMerchant>, AdapterView.OnItemClickListener {

    private ArrayList<NewMerchant> hotelMerchants;
    private ObjectBindAdapter<NewMerchant> adapter;
    private View progressBar;
    private View rootView;
    private long userId;
    private int emptyId;
    private int logoSize;

    public static FollowHotelFragment newInstance(long userId, int emptyId) {
        FollowHotelFragment fragment = new FollowHotelFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        args.putInt("emptyId", emptyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        logoSize = Util.dp2px(getActivity(), 64);
        hotelMerchants = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(),
                hotelMerchants,
                R.layout.merchant_list_item,
                this);
        footerView = View.inflate(getActivity(), R.layout.list_foot_no_more_2, null);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", 0);
            emptyId = getArguments().getInt("emptyId", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        if (hotelMerchants.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(listView);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewMerchant hotelMerchant = (NewMerchant) parent.getAdapter()
                .getItem(position);
        if (hotelMerchant != null && hotelMerchant.getId() > 0) {
            Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
            intent.putExtra("id", hotelMerchant.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @Override
    public void onDataLoad(int page) {
        new GetHotelsTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        return Constants.getAbsUrl(String.format(Constants.HttpPath.FAVORITES_HOTEL_URL,
                page,
                userId));
    }

    @Override
    public void setViewValue(View view, NewMerchant hotelMerchant, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String url = JSONUtil.getImagePath2(hotelMerchant.getLogoPath(), logoSize);
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadUtil.loadImageView(this,
                    url,
                    R.mipmap.icon_empty_image,
                    holder.imageView,
                    true);
        } else {
            ImageLoadUtil.clear(getContext(), holder.imageView);
        }
        holder.name.setText(hotelMerchant.getName());
        Hotel hotel = hotelMerchant.getHotel();
        if (hotel != null) {
            boolean isIconShow = !JSONUtil.isEmpty(hotelMerchant.getFreeOrder()) || !JSONUtil
                    .isEmpty(
                    hotelMerchant.getPlatformGift()) || !JSONUtil.isEmpty(hotelMerchant
                    .getExclusiveOffer());
            holder.iconsLayout.setVisibility(isIconShow ? View.VISIBLE : View.GONE);
            holder.freeIcon.setVisibility(JSONUtil.isEmpty(hotelMerchant.getFreeOrder()) ? View
                    .GONE : View.VISIBLE);
            holder.giftIcon.setVisibility(JSONUtil.isEmpty(hotelMerchant.getPlatformGift()) ?
                    View.GONE : View.VISIBLE);
            holder.exclusiveOfferIcon.setVisibility(JSONUtil.isEmpty(hotelMerchant
                    .getExclusiveOffer()) ? View.GONE : View.VISIBLE);
            holder.priceLayout.setVisibility(View.VISIBLE);
            holder.tvHotelPrice.setText(hotel.getPriceStr());
            holder.tvLabel1.setText(getString(R.string.label_hotel_hall_contain_desk2,
                    hotel.getTable_num()));
            holder.tvLabel2.setText(hotel.getKind());
            holder.tvLabel3.setText(hotel.getArea());
        }
        holder.shopGiftContent.setText(hotelMerchant.getShopGift());
        holder.shopGiftLayout.setVisibility(JSONUtil.isEmpty(hotelMerchant.getShopGift()) ? View
                .GONE : View.VISIBLE);
    }

    private class GetHotelsTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        private GetHotelsTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (url.equals(getUrl(currentPage))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (result != null) {
                    int pageCount = result.optInt("page_count", 0);
                    onLoadCompleted(pageCount <= currentPage);
                    if (onTabTextChangeListener != null) {
                        int totalCount = result.optInt("total_count", 0);
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    JSONArray jsonArray = result.optJSONArray("list");
                    if (currentPage == 1) {
                        hotelMerchants.clear();
                    }
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            hotelMerchants.add(new NewMerchant(jsonArray.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (!hotelMerchants.isEmpty()) {
                    onLoadFailed();
                }
                if (hotelMerchants.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(getActivity(),
                            emptyView,
                            emptyId == 0 ? R.string.hint_collect_hotel_empty : emptyId,
                            R.mipmap.icon_empty_common,
                            0,
                            0);
                }
                isLoad = false;
            }
            super.onPostExecute(result);
        }
    }

    static class ViewHolder {
        @BindView(R.id.logo)
        RoundedImageView imageView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.free_icon)
        ImageView freeIcon;
        @BindView(R.id.gift_icon)
        ImageView giftIcon;
        @BindView(R.id.exclusive_offer_icon)
        ImageView exclusiveOfferIcon;
        @BindView(R.id.icons_layout)
        LinearLayout iconsLayout;
        @BindView(R.id.price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.tv_hotel_price)
        TextView tvHotelPrice;
        @BindView(R.id.tv_label1)
        TextView tvLabel1;
        @BindView(R.id.tv_label2)
        TextView tvLabel2;
        @BindView(R.id.tv_label3)
        TextView tvLabel3;
        @BindView(R.id.shop_gift_content)
        TextView shopGiftContent;
        @BindView(R.id.shop_gift_layout)
        LinearLayout shopGiftLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
