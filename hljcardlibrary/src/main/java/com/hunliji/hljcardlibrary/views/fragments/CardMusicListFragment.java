package com.hunliji.hljcardlibrary.views.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Audio;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.models.wrappers.MusicNotifyWrapper;
import com.hunliji.hljcardlibrary.views.activities.CardMusicListActivity;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.SelectMode;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Suncloud on 2015/12/26.
 */
public class CardMusicListFragment extends RefreshFragment implements RecordingFragment
        .RecordingListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private long markId;
    private Unbinder unbinder;
    private MusicAdapter adapter;
    private View headerView;
    private View footerView;
    private View endView;
    private View loadView;
    private HeaderViewHolder headerViewHolder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private List<SelectMode<Music>> musics;
    private String audioPath;
    private Subscription rxBusSubscription;

    public static CardMusicListFragment newInstance(
            long markId, Audio audio) {
        CardMusicListFragment fragment = new CardMusicListFragment();
        Bundle arg = new Bundle();
        arg.putLong("markId", markId);
        arg.putParcelable("audio", audio);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            markId = getArguments().getLong("markId", 0L);
        }
        musics = new ArrayList<>();
        adapter = new MusicAdapter(getContext());
        registerRxBusEvent();
    }

    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NOTIFY_CARD_MUSIC:
                                    MusicNotifyWrapper wrapper = (MusicNotifyWrapper) rxEvent
                                            .getObject();
                                    if (wrapper != null && wrapper.getMarkId() != markId) {
                                        notifyItemCheckChanged(wrapper.getPath());
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
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
        initWidget();
        return rootView;
    }

    private void initWidget() {
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        Audio audio = null;
        if (getArguments() != null) {
            audio = getArguments().getParcelable("audio");
            if (audio != null && audio.getKind() == 3 && audio.getClassicMusic() != null) {
                audioPath = audio.getClassicMusicPath();
            }
        }
        if (markId == CardMusicListActivity.ALL_MARK_ID) {
            headerView = View.inflate(getContext(), R.layout.music_list_header_card, null);
            headerViewHolder = new HeaderViewHolder(headerView);
            if (audio != null) {
                if (audio.getKind() == 3 && audio.getClassicMusic() != null) {
                    audioPath = audio.getClassicMusicPath();
                }
                setRecordView(audio);
                setFileView(audio);
                switch (audio.getKind()) {
                    case 1:
                        headerViewHolder.imgRecordStatus.setImageResource(R.mipmap
                                .icon_check_primary_34_26);
                        break;
                    case 2:
                        headerViewHolder.imgUploadStatus.setImageResource(R.mipmap
                                .icon_check_primary_34_26);
                        break;
                    case 3:
                        //线上音乐
                        break;
                    default:
                        headerViewHolder.layoutNoMusic.setChecked(true);
                        break;
                }
            }
        } else {
            headerView = null;
        }

        recyclerView.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .setItemAnimator(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    public void setRecordView(Audio audio) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        ViewHolder holder = (ViewHolder) headerViewHolder.layoutRecord.getTag();
        if (holder == null) {
            holder = new ViewHolder(headerViewHolder.layoutRecord);
            holder.line.setVisibility(View.GONE);
            holder.name.setText(R.string.label_recording__card);
            holder.action.setText(R.string.label_recording__card);
            holder.action.setOnClickListener(new OnActionClickListener(1));
            headerViewHolder.layoutRecord.setTag(holder);
        }
        if (!CommonUtil.isEmpty(audio.getRecordMusicPath())) {
            holder.time.setVisibility(View.VISIBLE);
            holder.iconArrow.setVisibility(View.GONE);
            holder.action.setVisibility(View.VISIBLE);
            if (getMusicCallBack() != null) {
                getMusicCallBack().onMediaTimePrepare(audio.getRecordMusicPath(), holder.time);
            }
        } else {
            holder.time.setVisibility(View.GONE);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.GONE);
        }
    }

    public void setRecordTimeView(int recordTime, int currentPosition) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        ViewHolder holder = (ViewHolder) headerViewHolder.layoutRecord.getTag();
        if (holder == null) {
            holder = new ViewHolder(headerViewHolder.layoutRecord);
            holder.line.setVisibility(View.GONE);
            holder.name.setText(R.string.label_recording__card);
            holder.action.setText(R.string.label_recording__card);
            holder.action.setOnClickListener(new OnActionClickListener(1));
            headerViewHolder.layoutRecord.setTag(holder);
        }
        int minute1 = currentPosition / 60000;
        int second1 = (currentPosition / 1000) % 60;
        int minute2 = recordTime / 60000;
        int second2 = (recordTime / 1000) % 60;
        holder.time.setText(getString(R.string.label_time_down__card,
                minute1,
                second1,
                minute2,
                second2));
    }

    public void setFileView(Audio audio) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (headerViewHolder == null) {
            return;
        }
        ViewHolder holder = (ViewHolder) headerViewHolder.layoutUpload.getTag();
        if (holder == null) {
            holder = new ViewHolder(headerViewHolder.layoutUpload);
            holder.line.setVisibility(View.GONE);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setText(R.string.label_change__card);
            holder.action.setOnClickListener(new OnActionClickListener(2));
            headerViewHolder.layoutUpload.setTag(holder);
        }
        if (!CommonUtil.isEmpty(audio.getFileMusicPath())) {
            holder.name.setText(audio.getFileMusicName());
            holder.iconArrow.setVisibility(View.GONE);
            holder.action.setVisibility(View.VISIBLE);
        } else {
            holder.name.setText(R.string.label_upload_sound__card);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecordDone(String filePtah) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (!CommonUtil.isEmpty(filePtah)) {
            onCheckChange(R.id.layout_record);
            if (getMusicCallBack() != null) {
                getMusicCallBack().onRecordDone(filePtah);
            }
        }
    }

    public void notifyItemCheckChanged(String audioPath) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (TextUtils.isEmpty(audioPath) || CommonUtil.isCollectionEmpty(musics)) {
            return;
        }
        this.audioPath = audioPath;
        for (int i = 0, size = musics.size(); i < size; i++) {
            SelectMode<Music> selectMusic = musics.get(i);
            Music music = selectMusic.getMode();
            if (music.getAudioPath()
                    .equals(audioPath)) {
                selectMusic.setSelected(!selectMusic.isSelected());
            } else {
                selectMusic.setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public long getMarkId() {
        return markId;
    }

    public void notifyItemCheckFalse() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (musics == null) {
            return;
        }
        for (SelectMode selectMode : musics) {
            selectMode.setSelected(false);
        }
    }

    private void onCheckChange(int id) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (markId != CardMusicListActivity.ALL_MARK_ID) {
            return;
        }
        boolean record;
        boolean upload;
        boolean noMusic;
        if (id == R.id.layout_no_music) {
            noMusic = true;
            record = false;
            upload = false;
        } else if (id == R.id.layout_record) {
            noMusic = false;
            record = true;
            upload = false;
        } else if (id == R.id.layout_upload) {
            noMusic = false;
            record = false;
            upload = true;
        } else {
            noMusic = false;
            record = false;
            upload = false;
        }
        if (record) {
            headerViewHolder.imgRecordStatus.setImageResource(R.mipmap.icon_check_primary_34_26);
        } else {
            headerViewHolder.imgRecordStatus.setImageDrawable(new ColorDrawable(getResources()
                    .getColor(
                    R.color.transparent)));
        }
        if (upload) {
            headerViewHolder.imgUploadStatus.setImageResource(R.mipmap.icon_check_primary_34_26);
        } else {
            headerViewHolder.imgUploadStatus.setImageDrawable(new ColorDrawable(getResources()
                    .getColor(
                    R.color.transparent)));
        }
        headerViewHolder.layoutNoMusic.setChecked(noMusic);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (getMusicCallBack() == null) {
            return;
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        final Audio audio = getMusicCallBack().getAudio();
        audioPath = audio.getCurrentPath();
        Observable<HljHttpData<List<Music>>> obb = CardApi.getMusicList(markId, 1);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Music>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Music>> listData) {
                        musics.clear();
                        int pageCount = 0;
                        List<Music> musicList = listData.getData();
                        if (musicList != null && musicList.size() > 0) {
                            pageCount = listData.getPageCount();
                            emptyView.hideEmptyView();
                            if (audio.getKind() == 3 && audio.getClassicMusic() != null) {
                                Music classMusic = audio.getClassicMusic();
                                if (markId == CardMusicListActivity.ALL_MARK_ID) {
                                    SelectMode<Music> selectMode = new SelectMode<>();
                                    selectMode.setSelected(true);
                                    selectMode.setMode(classMusic);
                                    musics.add(selectMode);
                                    if (getMusicCallBack() != null) {
                                        getMusicCallBack().onItemInitCheck(classMusic);
                                    }
                                }
                            }

                            for (int i = 0, size = musicList.size(); i < size; i++) {
                                Music music = musicList.get(i);
                                boolean check = false;
                                if (!TextUtils.isEmpty(audioPath)) {
                                    if (music.getAudioPath()
                                            .equals(audioPath)) {
                                        if (markId == CardMusicListActivity.ALL_MARK_ID) {
                                            continue;
                                        } else {
                                            check = true;
                                        }
                                    }
                                }
                                SelectMode<Music> selectMode = new SelectMode<>();
                                selectMode.setMode(music);
                                selectMode.setSelected(check);
                                musics.add(selectMode);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            emptyView.showEmptyView();
                        }
                        initPagination(pageCount);
                    }
                })
                .build();
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Music>>> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Music>>>() {
                    @Override
                    public Observable<HljHttpData<List<Music>>> onNextPage(int page) {
                        return CardApi.getMusicList(markId, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Music>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Music>> listData) {
                        if (listData != null && listData.getData() != null) {
                            List<Music> musics = listData.getData();
                            int start = CardMusicListFragment.this.musics.size();
                            for (int i = 0, size = musics.size(); i < size; i++) {
                                Music music = musics.get(i);
                                boolean check = false;
                                if (!TextUtils.isEmpty(audioPath)) {
                                    if (music.getAudioPath()
                                            .equals(audioPath)) {
                                        if (markId == CardMusicListActivity.ALL_MARK_ID) {
                                            continue;
                                        } else {
                                            check = true;
                                        }
                                    }
                                }
                                SelectMode<Music> selectMode = new SelectMode<>();
                                selectMode.setSelected(check);
                                selectMode.setMode(music);
                                CardMusicListFragment.this.musics.add(selectMode);
                            }
                            adapter.notifyItemRangeInserted(start,
                                    CardMusicListFragment.this.musics.size());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private class OnActionClickListener implements View.OnClickListener {

        private int kind;

        private OnActionClickListener(int kind) {
            this.kind = kind;
        }

        @Override
        public void onClick(View v) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (getMusicCallBack() != null) {
                getMusicCallBack().onActionClick(kind);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBusSubscription);
        super.onDestroyView();
    }

    @Override
    public void refresh(Object... params) {

    }


    class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ITEM_TYPE_HEADER = 10;
        private final int ITEM_TYPE_ITEM = 11;
        private final int ITEM_TYPE_FOOTER = 12;

        private Context mContext;

        public MusicAdapter(Context mContext) {
            this.mContext = mContext;
        }

        //position 是list中的位置 notifyItemChanged 需要考虑header有无的情况
        public void notifyCurrentFragmentItemChanged(int position) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (musics != null && !musics.isEmpty()) {
                musics.get(position)
                        .setSelected(!musics.get(position)
                                .isSelected());
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_TYPE_HEADER:
                    CardMusicListFragment.HeaderViewHolder headerViewHolder = new
                            CardMusicListFragment.HeaderViewHolder(
                            headerView);
                    headerView.setTag(headerViewHolder);
                    return headerViewHolder;
                case ITEM_TYPE_FOOTER:
                    return new ExtraViewHolder(footerView);
                case ITEM_TYPE_ITEM:
                    View itemView = LayoutInflater.from(mContext)
                            .inflate(R.layout.music_list_item_new__card, parent, false);
                    return new CardMusicListFragment.ItemViewHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_TYPE_ITEM) {
                return;
            }
            final ItemViewHolder holder = (ItemViewHolder) h;
            if (holder == null) {
                return;
            }

            final int musicPosition = position - (headerView == null ? 0 : 1);
            final SelectMode<Music> selectMode = musics.get(musicPosition);
            final Music music = selectMode.getMode();
            if (markId == CardMusicListActivity.ALL_MARK_ID) {
                holder.space.setVisibility(View.GONE);
            } else {
                holder.space.setVisibility(musicPosition == 0 ? View.VISIBLE : View.GONE);
            }
            if (selectMode.isSelected()) {
                holder.imgStatus.setImageResource(R.mipmap.icon_check_primary_34_26);
            } else {
                holder.imgStatus.setImageDrawable(new ColorDrawable(getResources().getColor(R
                        .color.transparent)));
            }
            holder.line.setVisibility(musicPosition > 0 ? View.VISIBLE : View.GONE);
            holder.name.setTextColor(ContextCompat.getColor(holder.name.getContext(),
                    selectMode.isSelected() ? R.color.colorPrimary : R.color.colorBlack3));
            holder.name.setText(music.getName());
            holder.name.getLayoutParams().width = 0;
            holder.name.requestLayout();
            holder.iconHot.setVisibility(music.isHot() ? View.VISIBLE : View.GONE);
            holder.iconNew.setVisibility(music.isNew() ? View.VISIBLE : View.GONE);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getMusicCallBack() == null) {
                        return;
                    }
                    onCheckChange(-1);
                    if (selectMode.isSelected()) {
                        return;
                    }
                    //markId 用来区分fragment
                    if (!selectMode.isSelected()) {
                        notifyItemCheckFalse();
                    }
                    notifyCurrentFragmentItemChanged(musicPosition);
                    getMusicCallBack().onMusicClick(music, markId);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return musics.size() + (headerView == null ? 0 : 1) + (musics.isEmpty() ? 0 : 1);
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position == 0) {
                if (headerView != null) {
                    type = ITEM_TYPE_HEADER;
                } else {
                    type = ITEM_TYPE_ITEM;
                }
            } else if (position == getItemCount() - 1) {
                type = ITEM_TYPE_FOOTER;
            } else {
                type = ITEM_TYPE_ITEM;
            }
            return type;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.time)
        TextView time;
        @BindView(R2.id.icon_hot)
        ImageView iconHot;
        @BindView(R2.id.icon_new)
        ImageView iconNew;
        @BindView(R2.id.icon_arrow)
        ImageView iconArrow;
        @BindView(R2.id.action)
        TextView action;
        LinearLayout linearLayout;
        @BindView(R2.id.space)
        Space space;
        @BindView(R2.id.img_status)
        ImageView imgStatus;

        ItemViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view;
            ButterKnife.bind(this, view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        CheckableLinearLayout layoutNoMusic;
        LinearLayout layoutRecord;
        ImageView imgRecordStatus;
        LinearLayout layoutUpload;
        ImageView imgUploadStatus;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layoutNoMusic = (CheckableLinearLayout) itemView.findViewById(R.id.layout_no_music);
            RelativeLayout layoutRecordRelative = (RelativeLayout) itemView.findViewById(R.id
                    .layout_record);
            layoutRecord = (LinearLayout) layoutRecordRelative.getChildAt(0);
            imgRecordStatus = (ImageView) layoutRecord.findViewById(R.id.img_status);
            RelativeLayout layoutUploadRelative = (RelativeLayout) itemView.findViewById(R.id
                    .layout_upload);
            layoutUpload = (LinearLayout) layoutUploadRelative.getChildAt(0);
            imgUploadStatus = (ImageView) layoutUpload.findViewById(R.id.img_status);
        }

        @OnClick(R2.id.layout_no_music)
        void onNoMusic() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (getMusicCallBack() != null) {
                onCheckChange(R.id.layout_no_music);
                getMusicCallBack().onNoMusic();
                notifyItemCheckFalse();
                adapter.notifyDataSetChanged();
            }
        }

        @OnClick(R2.id.layout_record)
        void onRecord() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (getMusicCallBack() != null) {
                onCheckChange(R.id.layout_record);
                getMusicCallBack().onRecord();
                notifyItemCheckFalse();
                adapter.notifyDataSetChanged();
            }
        }

        @OnClick(R2.id.layout_upload)
        void onUpload() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (getMusicCallBack() != null) {
                onCheckChange(R.id.layout_upload);
                getMusicCallBack().onUpload();
                notifyItemCheckFalse();
                adapter.notifyDataSetChanged();
            }
        }
    }

    class ViewHolder {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.time)
        TextView time;
        @BindView(R2.id.icon_hot)
        ImageView iconHot;
        @BindView(R2.id.icon_new)
        ImageView iconNew;
        @BindView(R2.id.icon_arrow)
        ImageView iconArrow;
        @BindView(R2.id.action)
        TextView action;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public OnMusicCallBack getMusicCallBack() {
        if (getActivity() != null && !getActivity().isFinishing() && getActivity() instanceof
                OnMusicCallBack) {
            return (OnMusicCallBack) getActivity();
        } else {
            return null;
        }
    }

    public interface OnMusicCallBack {

        void onMusicClick(Music music, long markId);

        void onItemInitCheck(Music music);

        void onNoMusic();

        void onRecord();

        void onUpload();

        void onRecordDone(String path);

        void onActionClick(int kind);

        void onMediaTimePrepare(String path, TextView timeView);

        Audio getAudio();
    }
}
