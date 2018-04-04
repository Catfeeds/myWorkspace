package com.hunliji.hljnotelibrary.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by mo_yu on 2017/7/19.
 * 用于获取笔记详情，详情加载完成后，如果是运营笔记嵌入NoteWebDetailFragment
 */

public class BaseNoteDetailFragment extends RefreshFragment {

    @BindView(R2.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private Note note;
    private long noteId;
    private long inspirationId;
    private int inspirationPosition;
    private int position;
    private int notePosition;
    private String url;
    private boolean isLargerView;//大图模式
    private boolean isVisibility = true;
    private HljHttpSubscriber initSubscriber;
    private Subscription backSubscription;

    public static BaseNoteDetailFragment newInstance(Bundle args) {
        BaseNoteDetailFragment fragment = new BaseNoteDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_content, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            noteId = getArguments().getLong("note_id", 0);
            isLargerView = getArguments().getBoolean("is_larger_view");
            inspirationId = getArguments().getLong("inspiration_id", 0);
            inspirationPosition = getArguments().getInt("inspiration_position", 0);
            url = getArguments().getString("url");
            position = getArguments().getInt("position", 0);
            notePosition = getArguments().getInt("note_position", 0);
        }
        if (TextUtils.isEmpty(url)) {
            refresh();
        } else {
            intWebNoteDetail(url);
        }
        return rootView;
    }

    @Override
    public void refresh(Object... params) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<Note>() {
                        @Override
                        public void onNext(Note noteData) {
                            note = noteData;
                            if (note == null || note.isDeleted()) {
                                //该问题已被删除，2秒后，页面返回至上一页
                                ToastUtil.showToast(getContext(), "笔记已被删除!", 0);
                                CommonUtil.unSubscribeSubs(backSubscription);
                                backSubscription=Observable.timer(2, TimeUnit.SECONDS)
                                        .subscribe(new Action1<Long>() {
                                            @Override
                                            public void call(Long aLong) {
                                                getActivity().onBackPressed();
                                            }
                                        });
                            } else {
                                if (note.getNoteType() == Note.TYPE_RICH) {
                                    intWebNoteDetail(note.getUrl());
                                } else {
                                    initCommonNoteDetail();
                                }

                            }
                        }
                    })
                    .build();
            NoteApi.getNoteDetailObb(noteId)
                    .subscribe(initSubscriber);
        }
    }

    private void intWebNoteDetail(String url) {
        NoteWebDetailFragment noteWebDetailFragment = (NoteWebDetailFragment) getChildFragmentManager()
                .findFragmentByTag(
                        "NoteWebDetailFragment");
        if (noteWebDetailFragment != null) {
            noteWebDetailFragment.refresh(note);
        } else {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            User user = UserSession.getInstance()
                    .getUser(getContext());
            String path;
            if (user != null) {
                path = url;
            } else {
                path = url;
            }
            transaction.add(R.id.fragment_content,
                    NoteWebDetailFragment.newInstance(path),
                    "NoteWebDetailFragment");
            NoteDetailActivity noteDetailActivity = (NoteDetailActivity) getActivity();
            if (noteDetailActivity != null && position == notePosition) {
                noteDetailActivity.hideNativeBottomView();
                noteDetailActivity.hideMoreMenu();
            }
            transaction.commitAllowingStateLoss();
        }
    }

    private void initCommonNoteDetail() {
        NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getChildFragmentManager()
                .findFragmentByTag(
                "NoteDetailFragment");
        if (noteDetailFragment != null) {
            noteDetailFragment.refresh(note);
        } else {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putLong("note_id", noteId);
            args.putBoolean("is_larger_view", isLargerView);
            args.putLong("inspiration_id", inspirationId);
            args.putParcelable("note", note);
            args.putInt("inspiration_position", inspirationPosition);
            transaction.add(R.id.fragment_content,
                    NoteDetailFragment.newInstance(args),
                    "NoteDetailFragment");
            NoteDetailActivity noteDetailActivity = getNoteDetailActivity();
            if (noteDetailActivity != null && position == notePosition) {
                noteDetailActivity.showNativeBottomView();
                noteDetailActivity.showMoreMenu();
                noteDetailActivity.showCreatePosterByNote(note);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(initSubscriber,backSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public Note getNote() {
        return note;
    }

    public String getNoteUrl() {
        if (note != null) {
            return note.getUrl();
        }
        return url;
    }

    public View getBottomView() {
        if (!isAdded()) {
            return null;
        }
        if (TextUtils.isEmpty(getNoteUrl())) {
            NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getChildFragmentManager
                    ().findFragmentByTag(
                    "NoteDetailFragment");
            if (noteDetailFragment != null) {
                return noteDetailFragment.getBottomView();
            }
        }
        return null;
    }

    /**
     * 评论笔记
     */
    public void onComment() {
        NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getChildFragmentManager()
                .findFragmentByTag(
                "NoteDetailFragment");
        if (noteDetailFragment != null) {
            noteDetailFragment.onComment(null);
        }
    }

    public float getAlpha() {
        if (!isAdded()) {
            return 0;
        }
        if (TextUtils.isEmpty(getNoteUrl())) {
            NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getChildFragmentManager
                    ().findFragmentByTag(
                    "NoteDetailFragment");
            if (noteDetailFragment != null) {
                return noteDetailFragment.getAlpha();
            }
        } else {
            NoteWebDetailFragment noteWebDetailFragment = (NoteWebDetailFragment)
                    getChildFragmentManager().findFragmentByTag(
                    "NoteWebDetailFragment");
            if (noteWebDetailFragment != null) {
                return noteWebDetailFragment.getAlpha();
            }
        }
        return 0;
    }

    public int getCollectCount() {
        if (!isAdded()) {
            return -1;
        }
        if (TextUtils.isEmpty(getNoteUrl())) {
            NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getChildFragmentManager
                    ().findFragmentByTag(
                    "NoteDetailFragment");
            if (noteDetailFragment != null) {
                return noteDetailFragment.getCollectCount();
            }
        }
        return -1;
    }

    private NoteDetailActivity getNoteDetailActivity() {
        if (getActivity() instanceof NoteDetailActivity) {
            return (NoteDetailActivity) getActivity();
        }
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibility = isVisibleToUser;
    }

    public boolean isVisibility() {
        return isVisibility;
    }
}
