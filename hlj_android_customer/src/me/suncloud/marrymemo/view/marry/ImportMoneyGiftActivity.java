package me.suncloud.marrymemo.view.marry;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.SideBar;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.ImportMoneyGiftAdapter;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.GuestGift;

/**
 * Created by hua_rong on 2017/12/11  礼金批量导入
 */

public class ImportMoneyGiftActivity extends HljBaseActivity implements SideBar
        .OnLetterChangedListener, ImportMoneyGiftAdapter.OnSelectListener {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.side_bar)
    SideBar sideBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.layout)
    RelativeLayout layout;

    private ImportMoneyGiftAdapter adapter;
    private List<GuestGift> guestGifts;
    private StickyRecyclerHeadersDecoration headersDecor;
    private LinearLayoutManager layoutManager;
    private List<GuestGift> selectedGuests;
    private HljHttpSubscriber importSubscriber;
    private HljHttpSubscriber subscriber;

    private InputMethodManager imm;
    private boolean immIsShow;
    private int prePosition = 0;//初始化默认选中第一个

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_money_gift);
        ButterKnife.bind(this);
        initValue();
        initView();
        onLoad();
    }

    private void initView() {
        sideBar.setOnLetterChangedListener(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImportMoneyGiftAdapter(this);
        adapter.setOnSelectListener(this);
        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        setKeyboardListener();
    }

    @OnTouch(R.id.recycler_view)
    public boolean onRecyclerView() {
        hideKeyboard(null);
        return false;
    }

    private void setKeyboardListener() {
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (!immIsShow) {
                    removeUnSelectGuest();
                    if (prePosition > -1 && !CommonUtil.isCollectionEmpty(guestGifts) &&
                            guestGifts.size() > prePosition) {
                        GuestGift guestGift = guestGifts.get(prePosition);
                        if (guestGift != null && guestGift.getMoney() == 0) {
                            guestGift.setSelected(false);
                            adapter.notifyItemChanged(prePosition);
                        }
                    }
                    setOkText(getString(R.string.label_import_count, selectedGuests.size()));
                }
            }
        });
    }

    private void initValue() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        selectedGuests = new ArrayList<>();
        guestGifts = new ArrayList<>();
    }

    private void onLoad() {
        CommonUtil.unSubscribeSubs(subscriber);
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<List<GuestGift>>() {
                    @Override
                    public void onNext(List<GuestGift> guestGiftList) {
                        guestGifts.clear();
                        guestGifts.addAll(guestGiftList);
                        List<String> firstChars = new ArrayList<>();
                        for (GuestGift guestGift : guestGifts) {
                            String firstChar = guestGift.getFirstChar();
                            if (!TextUtils.isEmpty(firstChar) && !firstChars.contains(firstChar)) {
                                firstChars.add(firstChar);
                            }
                        }
                        Collections.sort(guestGifts, new Comparator<GuestGift>() {
                            @Override
                            public int compare(GuestGift o1, GuestGift o2) {
                                return o1.getPinyin()
                                        .compareTo(o2.getPinyin());
                            }
                        });
                        Collections.sort(guestGifts, new Comparator<GuestGift>() {
                            @Override
                            public int compare(GuestGift o1, GuestGift o2) {
                                if ("#".equals(o1.getFirstChar())) {
                                    return 1;
                                } else if ("#".equals(o2.getFirstChar())) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        Collections.sort(firstChars, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                if ("#".equals(o1)) {
                                    return 1;
                                } else if ("#".equals(o2)) {
                                    return -1;
                                } else {
                                    return o1.compareTo(o2);
                                }
                            }
                        });
                        if (!CommonUtil.isCollectionEmpty(guestGifts) && guestGifts.get(0) !=
                                null) {
                            guestGifts.get(0)
                                    .setSelected(true);
                            selectedGuests.add(guestGifts.get(0));
                        }
                        setOkText(getString(R.string.label_import_count, selectedGuests.size()));
                        adapter.setGuestGifts(guestGifts);
                        sideBar.setLetters(firstChars);
                    }
                })
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(progressBar)
                .build();
        MarryApi.getGuestNameList()
                .subscribe(subscriber);
    }

    @Override
    public void onSelectClick(int position, GuestGift guestGift, EditText editText) {
        editText.requestFocus();
        removeUnSelectGuest();
        if (guestGift.isSelected()) {
            guestGift.setSelected(false);
            selectedGuests.remove(guestGift);
        } else {
            guestGift.setSelected(true);
            selectedGuests.add(guestGift);
            if (guestGift.getMoney() <= 0 && !immIsShow) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        for (GuestGift guest : guestGifts) {
            if (position != guestGifts.indexOf(guest)) {
                if (guest.isSelected() && guest.getMoney() > 0) {
                    guest.setSelected(true);
                } else {
                    guest.setSelected(false);
                }
            }
        }
        reSetStatus(position);
    }

    private void reSetStatus(int position) {
        if (prePosition >= 0) {
            adapter.notifyItemChanged(prePosition);
        }
        adapter.notifyItemChanged(position);
        prePosition = position;
        setOkText(getString(R.string.label_import_count, selectedGuests.size()));
    }

    @Override
    public void onItemClick(int position, GuestGift guestGift, EditText editText) {
        if (!immIsShow) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (editText.hasFocus() && guestGift.isSelected()) {
            return;
        }
        editText.requestFocus();
        removeUnSelectGuest();
        for (GuestGift guest : guestGifts) {
            if (guest.isSelected() && guest.getMoney() > 0) {
                guest.setSelected(true);
            } else {
                guest.setSelected(false);
            }
        }
        if (!guestGift.isSelected()) {
            guestGift.setSelected(true);
            selectedGuests.add(guestGift);
        }
        reSetStatus(position);
    }

    @Override
    public void onOkButtonClick() {
        if (isEmpty()) {
            ToastUtil.showToast(this, "请输入礼金金额", 0);
            return;
        }
        removeUnSelectGuest();
        CommonUtil.unSubscribeSubs(importSubscriber);
        importSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .build();
        MarryApi.postBatchAdd(selectedGuests)
                .subscribe(importSubscriber);
    }

    private void removeUnSelectGuest() {
        Iterator<GuestGift> iterator = selectedGuests.iterator();
        while (iterator.hasNext()) {
            GuestGift guestGift = iterator.next();
            if (guestGift.getMoney() <= 0 || !guestGift.isSelected()) {
                iterator.remove();
            }
        }
    }

    private boolean isEmpty() {
        if (CommonUtil.isCollectionEmpty(selectedGuests)) {
            return true;
        }
        for (GuestGift guestName : selectedGuests) {
            if (guestName.getMoney() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onLetterChanged(String str) {
        int position = adapter.getPositionForSection(str);
        if (position != -1) {
            layoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber, importSubscriber);
    }

}
