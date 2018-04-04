package me.suncloud.marrymemo.view.marry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.MarryBookAdapter;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.marry.BookSort;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by hua_rong on 2017/11/3
 * 结婚账本
 */

public class MarryBookActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>,
        OnItemClickListener<MarryBook> {

    private LinearLayoutManager linearLayoutManager;

    @Override
    public String pageTrackTagName() {
        return "结婚账本";
    }

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_tip_content)
    TextView tvTipContent;
    @BindView(R.id.tv_get)
    TextView tvGet;
    @BindView(R.id.rl_tips)
    RelativeLayout rlTips;
    private View headerView;
    private MarryBookAdapter adapter;
    private HeaderViewHolder viewHolder;
    private HljHttpSubscriber refreshSubscriber;
    private double totalMoney;
    private boolean bookBind;
    private User user;
    private static final String BOOK_KEY = "marry_book_bind";
    private StickyRecyclerHeadersDecoration headersDecor;
    private double totalGiftCount;
    private boolean isScrollToGift;//是否定位到礼金

    public final static String ARG_IS_SCROLL_TO_GIFT = "is_scroll_to_gift";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marry_book);
        ButterKnife.bind(this);
        initValue();
        initHeader();
        initView();
        initError();
        onRefresh(recyclerView);
    }

    private void initValue() {
        isScrollToGift = getIntent().getBooleanExtra(ARG_IS_SCROLL_TO_GIFT, false);
        user = Session.getInstance()
                .getCurrentUser(this);
        bookBind = user.getPartnerUid() > 0;
    }

    @OnClick(R.id.tv_get)
    void onBtnGet() {
        if (bookBind) {
            SPUtils.put(this, BOOK_KEY + user.getId(), true);
            rlTips.setVisibility(View.GONE);
        } else {
            rlTips.setVisibility(View.GONE);
            Intent intent = new Intent(this, BindingPartnerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }


    private void initHeader() {
        headerView = View.inflate(this, R.layout.marry_book_header, null);
        viewHolder = new HeaderViewHolder(headerView);
    }

    private void initView() {
        recyclerView.setOnRefreshListener(this);
        adapter = new MarryBookAdapter(this);
        adapter.setOnItemClickListener(this);
        adapter.setHeaderView(headerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.getRefreshableView()
                .addItemDecoration(headersDecor);

        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        if (SPUtils.getBoolean(this, BOOK_KEY + user.getId(), false)) {
            rlTips.setVisibility(View.GONE);
        } else {
            rlTips.setVisibility(View.VISIBLE);
        }
        if (bookBind) {//已经绑定
            tvTipContent.setText(R.string.hint_account_bond_with_spouse);
            tvGet.setText(R.string.label_get);
        } else {
            tvTipContent.setText(R.string.hint_to_bond_with_spouse_book);
            tvGet.setText(R.string.label_to_bind_partner);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<List<MarryBook>> observable = MarryApi.getCashBookList()
                .map(new Func1<HljHttpData<List<MarryBook>>, List<BookSort>>() {
                    @Override
                    public List<BookSort> call(HljHttpData<List<MarryBook>> hljHttpData) {
                        List<BookSort> bookSorts = new ArrayList<>();
                        if (!CommonUtil.isCollectionEmpty(hljHttpData.getData())) {
                            for (MarryBook marryBook : hljHttpData.getData()) {
                                boolean isContained = false;
                                for (BookSort sort : bookSorts) {
                                    if (sort.getParentId() != 0 && sort.getParentId() ==
                                            marryBook.getType()
                                            .getParent()
                                            .getId()) {
                                        sort.setTypeId(marryBook.getTypeId());
                                        sort.setMoney(sort.getMoney() + marryBook.getMoney());
                                        sort.getMarryBooks()
                                                .add(marryBook);
                                        isContained = true;
                                        break;
                                    }
                                }
                                if (!isContained) {
                                    bookSorts.add(getMarrySort(marryBook));
                                }
                            }
                            Collections.sort(bookSorts, new Comparator<BookSort>() {
                                @Override
                                public int compare(BookSort o1, BookSort o2) {
                                    return o1.getParentId()
                                            .compareTo(o2.getParentId());
                                }
                            });
                            for (BookSort bookSort : bookSorts) {
                                if (bookSort.getTypeId() == MarryApi.TYPE_GIFT_ID && !CommonUtil
                                        .isCollectionEmpty(
                                        bookSort.getMarryBooks())) {
                                    Collections.sort(bookSort.getMarryBooks(),
                                            new Comparator<MarryBook>() {
                                                @Override
                                                public int compare(MarryBook o1, MarryBook o2) {
                                                    return o2.getMoney()
                                                            .compareTo(o1.getMoney());
                                                }
                                            });
                                }
                            }
                        }
                        return bookSorts;
                    }
                })
                .map(new Func1<List<BookSort>, List<MarryBook>>() {
                    @Override
                    public List<MarryBook> call(List<BookSort> bookSorts) {
                        List<MarryBook> marryBooks = new ArrayList<>();
                        totalMoney = 0;
                        for (BookSort bookSort : bookSorts) {
                            List<MarryBook> list = bookSort.getMarryBooks();
                            if (!CommonUtil.isCollectionEmpty(list)) {
                                for (MarryBook marryBook : list) {
                                    marryBook.setParentPrice(bookSort.getMoney());
                                }
                                MarryBook marryBook = list.get(0);
                                if (marryBook.getTypeId() == MarryApi.TYPE_GIFT_ID) {
                                    totalGiftCount = bookSort.getMoney();
                                } else {
                                    totalMoney = totalMoney + bookSort.getMoney();
                                }
                                marryBook.setFirstLine(true);
                                marryBooks.addAll(list);
                            }
                        }
                        return marryBooks;
                    }
                });
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<List<MarryBook>>() {
                    @Override
                    public void onNext(List<MarryBook> marryBooks) {
                        viewHolder.setViewData(marryBooks);
                        adapter.setMarryBooks(marryBooks);
                        if (isScrollToGift) {
                            isScrollToGift = false;
                            scrollToGiftPosition();
                        }
                    }
                })
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setDataNullable(true)
                .build();
        observable.subscribe(refreshSubscriber);
    }

    private BookSort getMarrySort(MarryBook marryBook) {
        BookSort bookSort = new BookSort();
        bookSort.setParentId(marryBook.getType()
                .getParent()
                .getId());
        bookSort.setMoney(marryBook.getMoney());
        List<MarryBook> marryBooks = bookSort.getMarryBooks();
        marryBooks.add(marryBook);
        bookSort.setMarryBooks(marryBooks);
        return bookSort;
    }

    @Override
    public void onItemClick(int position, MarryBook marryBook) {
        if (marryBook.getTypeId() == MarryApi.TYPE_GIFT_ID) {
            goMarryBookEditActivity(marryBook, MarryBook.TYPE_GIFT_INCOME);
        } else {
            goMarryBookEditActivity(marryBook, MarryBook.TYPE_MARRY_BOOK);
        }
    }

    private void goMarryBookEditActivity(MarryBook marryBook, int type) {
        Intent intent = new Intent(this, MarryBookEditActivity.class);
        intent.putExtra(MarryBookEditActivity.ARG_MARRY_BOOK, marryBook);
        intent.putExtra(MarryBookEditActivity.ARG_TYPE, type);
        startActivityForResult(intent, Constants.RequestCode.EDIT_WEDDING_ACCOUNT);
        overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_WEDDING_ACCOUNT:
                    if (data != null) {
                        isScrollToGift = data.getBooleanExtra(MarryBookActivity
                                        .ARG_IS_SCROLL_TO_GIFT,
                                false);
                    }
                    onRefresh(recyclerView);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class HeaderViewHolder {

        @BindView(R.id.tv_consume_money)
        TextView tvConsumeCount;
        @BindView(R.id.tv_gift_money)
        TextView tvGiftMoney;
        @BindView(R.id.rl_bottom)
        RelativeLayout rlBottom;
        @BindView(R.id.ll_header)
        LinearLayout llHeader;
        @BindView(R.id.btn_record)
        Button btnRecord;
        private Context context;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            context = view.getContext();

            initTracker();
        }

        private void initTracker() {
            HljVTTagger.buildTagger(btnRecord)
                    .tagName("first_account_book_item")
                    .hitTag();
        }

        @OnClick({R.id.btn_record, R.id.btn_record_book})
        void onRecord() {
            goMarryBookEditActivity(null, MarryBook.TYPE_MARRY_BOOK);
        }

        @OnClick(R.id.btn_setting)
        void onBudgetSetting() {
            goMarryBookEditActivity(null, MarryBook.TYPE_GIFT_INCOME);
        }

        @OnClick(R.id.card_view_book)
        void onCardViewBook() {
            if (linearLayoutManager != null) {
                int position = headerView == null ? 0 : 1;
                linearLayoutManager.scrollToPositionWithOffset(position, 0);
            }
        }

        @OnClick(R.id.card_view_gift)
        void onCardViewGift() {
            scrollToGiftPosition();
        }

        public void setViewData(
                List<MarryBook> marryBooks) {
            llHeader.setVisibility(View.VISIBLE);
            if (totalMoney >= 1000000) {
                tvConsumeCount.setText(String.valueOf(Double.valueOf(totalMoney)
                        .intValue()));
            } else {
                tvConsumeCount.setText(String.format(Locale.getDefault(), "%.2f", totalMoney));
            }
            if (totalGiftCount >= 1000000) {
                tvGiftMoney.setText(String.valueOf(Double.valueOf(totalGiftCount)
                        .intValue()));
            } else {
                tvGiftMoney.setText(String.format(Locale.getDefault(), "%.2f", totalGiftCount));
            }
            if (CommonUtil.isCollectionEmpty(marryBooks)) {
                rlBottom.setVisibility(View.VISIBLE);
                recyclerView.getRefreshableView()
                        .setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            } else {
                rlBottom.setVisibility(View.GONE);
                recyclerView.getRefreshableView()
                        .setBackgroundColor(ContextCompat.getColor(context,
                                R.color.colorBackground));
                recyclerView.getRefreshableView()
                        .setPadding(0, 0, 0, CommonUtil.dp2px(context, 20));
            }
        }
    }

    /**
     * 滑到礼金位置
     */
    private void scrollToGiftPosition() {
        if (adapter != null) {
            int position = adapter.getGiftPosition();
            if (position >= 0) {
                if (linearLayoutManager != null) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }

}
