package com.hunliji.hljpaymentlibrary.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class SupportCardListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<BankCard>, PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.listView)
    PullToRefreshListView listView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    private ArrayList<BankCard> cards;
    private ObjectBindAdapter<BankCard> adapter;
    private Subscription cardsSubscription;
    private int width;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_card_list___pay);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = (int) (dm.density * 22);

        cards = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, cards, R.layout.bank_card_item___pay, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        cardsSubscription = PaymentApi.getSupportCards()
                .subscribe(HljHttpSubscriber.
                        buildSubscriber(this)
                        .setProgressBar(progressBar)
                        .setListView(listView.getRefreshableView())
                        .setEmptyView(emptyView)
                        .setPullToRefreshBase(listView)
                        .setOnNextListener(new SubscriberOnNextListener<List<BankCard>>() {
                            @Override
                            public void onNext(List<BankCard> bankCards) {
                                cards.clear();
                                cards.addAll(bankCards);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .build());
    }


    @Override
    public void setViewValue(View view, BankCard card, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);
            holder.tvCardType.setVisibility(View.VISIBLE);
            holder.icArrow.setVisibility(View.GONE);
            view.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        String logoPath = ImageUtil.getImagePath(card.getLogoPath(), width);
        if (!TextUtils.isEmpty(logoPath)) {
            Glide.with(this)
                    .load(logoPath)
                    .apply(new RequestOptions()
                    .dontAnimate()
                    .placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgBankLogo);
        } else {
            Glide.with(this).clear(holder.imgBankLogo);
            holder.imgBankLogo.setImageBitmap(null);
        }
        holder.tvBankName.setText(card.getBankName());
        holder.tvCardType.setText(card.getCardTypeStr());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (cardsSubscription == null || cardsSubscription.isUnsubscribed()) {
            cardsSubscription = PaymentApi.getSupportCards()
                    .subscribe(HljHttpSubscriber.
                            buildSubscriber(this)
                            .setListView(listView.getRefreshableView())
                            .setEmptyView(emptyView)
                            .setPullToRefreshBase(listView)
                            .setOnNextListener(new SubscriberOnNextListener<List<BankCard>>() {
                                @Override
                                public void onNext(List<BankCard> bankCards) {
                                    cards.clear();
                                    cards.addAll(bankCards);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .build());
        }
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'support_bank_card_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R2.id.img_bank_logo)
        ImageView imgBankLogo;
        @BindView(R2.id.tv_bank_name)
        TextView tvBankName;
        @BindView(R2.id.tv_bank_card_id)
        TextView tvBankCardId;
        @BindView(R2.id.tv_card_type)
        TextView tvCardType;
        @BindView(R2.id.ic_arrow)
        ImageView icArrow;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onFinish() {
        if (cardsSubscription != null && !cardsSubscription.isUnsubscribed()) {
            cardsSubscription.unsubscribe();
        }
        super.onFinish();
    }
}
