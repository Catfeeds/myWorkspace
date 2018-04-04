package me.suncloud.marrymemo.view.tools;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.PinyinUtil;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.WeddingGuestContactListAdapter;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingGuestsData;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 导入宾客名单
 * Created by chen_bin on 2017/11/22 0022.
 */
public class ImportWeddingGuestsActivity extends HljBaseActivity implements
        OnItemClickListener<WeddingGuest>, SideBar.OnLetterChangedListener {

    @BindView(R.id.tv_table_no)
    TextView tvTableNo;
    @BindView(R.id.table_no_layout)
    RelativeLayout tableNoLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.side_bar)
    SideBar sideBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private WeddingGuestContactListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private StickyRecyclerHeadersDecoration headersDecor;
    private List<WeddingGuest> selectedGuests;
    private WeddingTable table;
    private int type;

    private Subscription getContactsSub;
    private HljHttpSubscriber getGuestsSub;

    public final static String ARG_GUESTS = "guests";
    public final static String ARG_TABLE = "table";
    public final static String ARG_TYPE = "type";

    public final static int TYPE_GUEST = 0;
    public final static int TYPE_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_guest_contact_list);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        selectedGuests = new ArrayList<>();
        table = getIntent().getParcelableExtra(ARG_TABLE);
        type = getIntent().getIntExtra(ARG_TYPE, TYPE_GUEST);
        setTitle(type == TYPE_GUEST ? R.string.label_import_from_guest : R.string
                .label_import_from_contact);
        setOkText(getString(R.string.label_import_count, 0));
        tvTableNo.setText(table == null || table.getId() == 0 ? getString(R.string
                        .label_import_to_table_no,
                getString(R.string.label_to_be_arranged)) : getString(R.string
                        .label_import_to_table_no,
                getString(R.string.label_table_no, table.getTableNo())));
    }

    private void initViews() {
        sideBar.setOnLetterChangedListener(this);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WeddingGuestContactListAdapter(this);
        adapter.setOnItemClickListener(this);
        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
    }

    private void initLoad() {
        if (type == TYPE_GUEST) {
            getGuests();
        } else {
            getContacts();
        }
    }

    private void getContacts() {
        if (getContactsSub == null || getContactsSub.isUnsubscribed()) {
            getContactsSub = Observable.create(new Observable.OnSubscribe<HljWeddingGuestsData>() {
                @Override
                public void call(Subscriber<? super HljWeddingGuestsData> subscriber) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    HljWeddingGuestsData guestsData = null;
                    try {
                        ContentResolver cr = getContentResolver();
                        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                        if (cursor != null && cursor.getCount() > 0) {
                            guestsData = new HljWeddingGuestsData();
                            List<WeddingGuest> guests = new ArrayList<>();
                            List<String> firstChars = new ArrayList<>();
                            List<String> names = new ArrayList<>();
                            while (cursor.moveToNext()) {
                                String name = cursor.getString(cursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                if (!TextUtils.isEmpty(name) && !names.contains(name)) {
                                    names.add(name);
                                    String pinyin = PinyinUtil.getPinyin(name);
                                    if (!TextUtils.isEmpty(pinyin)) {
                                        WeddingGuest guest = new WeddingGuest();
                                        guest.setNum(1);
                                        guest.setPinyin(pinyin);
                                        guest.setFullName(name);
                                        String firstChar = pinyin.substring(0, 1);
                                        if (firstChar.matches("[A-Z]")) {
                                            guest.setFirstChar(firstChar);
                                        } else {
                                            guest.setFirstChar("#");
                                        }
                                        if (!firstChars.contains(guest.getFirstChar())) {
                                            firstChars.add(guest.getFirstChar());
                                        }
                                        guests.add(guest);
                                    }
                                }
                            }
                            if (!CommonUtil.isCollectionEmpty(guests)) {
                                Collections.sort(guests, new Comparator<WeddingGuest>() {
                                    @Override
                                    public int compare(WeddingGuest o1, WeddingGuest o2) {
                                        return o1.getPinyin()
                                                .compareTo(o2.getPinyin());
                                    }
                                });
                                Collections.sort(guests, new Comparator<WeddingGuest>() {
                                    @Override
                                    public int compare(WeddingGuest o1, WeddingGuest o2) {
                                        if (o1.getFirstChar()
                                                .equals("#")) {
                                            return 1;
                                        } else if (o2.getFirstChar()
                                                .equals("#")) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                Collections.sort(firstChars, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        if (o1.equals("#")) {
                                            return 1;
                                        } else if (o2.equals("#")) {
                                            return -1;
                                        } else {
                                            return o1.compareTo(o2);
                                        }
                                    }
                                });
                            }
                            guestsData.setData(guests);
                            guestsData.setFirstChars(firstChars);
                            cursor.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(guestsData);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<HljWeddingGuestsData>() {

                        @Override
                        public void call(HljWeddingGuestsData guestsData) {
                            List<WeddingGuest> guests = null;
                            List<String> firstChars = null;
                            if (guestsData != null) {
                                guests = guestsData.getData();
                                firstChars = guestsData.getFirstChars();
                            }
                            if (CommonUtil.isCollectionEmpty(guests)) {
                                ToastUtil.showToast(ImportWeddingGuestsActivity.this,
                                        getString(R.string.msg_err_get_contact___cm,
                                                getString(R.string.app_name)),
                                        0);
                                tableNoLayout.setVisibility(View.GONE);
                                emptyView.showEmptyView();
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tableNoLayout.setVisibility(View.VISIBLE);
                                emptyView.hideEmptyView();
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            adapter.setGuests(guests);
                            sideBar.setLetters(firstChars);
                        }
                    });
        }
    }

    private void getGuests() {
        if (getGuestsSub == null || getContactsSub.isUnsubscribed()) {
            getGuestsSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljWeddingGuestsData>() {
                        @Override
                        public void onNext(HljWeddingGuestsData guestsData) {
                            tableNoLayout.setVisibility(View.VISIBLE);
                            adapter.setGuests(guestsData.getData());
                            sideBar.setLetters(guestsData.getFirstChars());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();
            ToolsApi.getGuestsObb()
                    .subscribe(getGuestsSub);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (!CommonUtil.isCollectionEmpty(selectedGuests)) {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra(ARG_GUESTS,
                    (ArrayList<? extends Parcelable>) selectedGuests);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

    @Override
    public void onItemClick(int position, WeddingGuest guest) {
        if (guest == null) {
            return;
        }
        if (guest.isSelected()) {
            guest.setSelected(false);
            selectedGuests.remove(guest);
        } else {
            guest.setSelected(true);
            selectedGuests.add(guest);
        }
        setOkText(getString(R.string.label_import_count, selectedGuests.size()));
        adapter.notifyItemChanged(position);
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
        CommonUtil.unSubscribeSubs(getContactsSub, getGuestsSub);
    }
}
