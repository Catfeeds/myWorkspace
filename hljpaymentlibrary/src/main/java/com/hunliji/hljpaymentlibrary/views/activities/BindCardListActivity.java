package com.hunliji.hljpaymentlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
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
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class BindCardListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<BankCard>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
        .OnItemClickListener {

    @BindView(R2.id.list)
    PullToRefreshListView listView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    private ArrayList<BankCard> cards;
    private ObjectBindAdapter<BankCard> adapter;
    private int width;
    private Subscription cardsSubscription;
    private Subscription restSubscriber;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card_list___pay);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = (int) (dm.density * 22);

        View headView = getLayoutInflater().inflate(R.layout.bind_card_hint_head___pay, null);
        View footView = getLayoutInflater().inflate(R.layout.bind_card_hint_footer___pay, null);
        TextView footText = (TextView) footView.findViewById(R.id.tv_hint);
        footText.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_select_card_footer___pay)));
        listView.getRefreshableView()
                .addHeaderView(headView);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        cards = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, cards, R.layout.bank_card_item___pay, this);

        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        cardsSubscription = PaymentApi.getBankCards()
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
        restSubscriber = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        if (payRxEvent.getType() == PayRxEvent.RxEventType.RESET_PASSWORD) {
                            onBackPressed();
                        }
                    }
                });
    }

    @Override
    public void setViewValue(View view, BankCard card, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        String logoPath = ImageUtil.getImagePath(card.getLogoPath(), width);
        if (!TextUtils.isEmpty(logoPath)) {
            Glide.with(this)
                    .load(logoPath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgBankLogo);
        } else {
            Glide.with(this)
                    .clear(holder.imgBankLogo);
            holder.imgBankLogo.setImageBitmap(null);
        }
        holder.tvBankName.setText(card.getBankName());
        holder.tvBankCardId.setText("**  " + card.getAccount());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (cardsSubscription == null || cardsSubscription.isUnsubscribed()) {
            cardsSubscription = PaymentApi.getBankCards()
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BankCard card = (BankCard) parent.getAdapter()
                .getItem(position);
        if (card != null && card.getId() > 0) {
            Intent intent = new Intent(this, FindPasswordActivity.class);
            intent.putExtra("user_bind_card", card);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'select_bank_card_list_item.xml'
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

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onFinish() {
        if (cardsSubscription != null && !cardsSubscription.isUnsubscribed()) {
            cardsSubscription.unsubscribe();
        }
        if (restSubscriber != null && !restSubscriber.isUnsubscribed()) {
            restSubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
