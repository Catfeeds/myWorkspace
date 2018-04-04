package com.hunliji.hljnotelibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.models.note.NoteSpotLayout;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.views.widgets.NoteInspirationView;
import com.hunliji.hljnotelibrary.views.widgets.TagViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

/**
 * 发布-单张图编辑
 * Created by chen_bin on 2017/6/27 0027.
 */
public class SelectedNoteInspirationFragment extends RefreshFragment implements TagViewGroup
        .OnTagGroupClickListener, TagViewGroup.OnTagGroupDragListener {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.note_inspiration_view)
    NoteInspirationView noteInspirationView;
    private NoteInspiration inspiration;
    private float currentX;
    private float currentY;
    private int imageWidth;
    private int imageHeight;
    private TagViewGroup tagViewGroup;
    private GestureDetector gestureDetector;
    private Unbinder unbinder;

    public final static String ARG_INSPIRATION = "inspiration";

    public static SelectedNoteInspirationFragment newInstance(NoteInspiration inspiration) {
        SelectedNoteInspirationFragment fragment = new SelectedNoteInspirationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_INSPIRATION, inspiration);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inspiration = getArguments().getParcelable(ARG_INSPIRATION);
        }
        imageWidth = CommonUtil.getDeviceSize(getContext()).x;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selected_note_inspiration___note,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        noteInspirationView.setEdit(true);
        noteInspirationView.setOnTagGroupClickListener(this);
        noteInspirationView.setOnTagGroupDragListener(this);
        gestureDetector = new GestureDetector(getContext(), new OnGestureListener());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageHeight = Math.round(imageWidth * inspiration.getNoteMedia()
                .getRatio());
        noteInspirationView.loadImage(inspiration.getNoteMedia()
                .getPhoto()
                .getImagePath(), imageWidth, imageHeight);
        noteInspirationView.setDate(inspiration.getNoteSpots());
        noteInspirationView.setShowNoteSpotEmptyView(CommonUtil.isCollectionEmpty(inspiration
                .getNoteSpots()));
    }

    @OnTouch(R2.id.note_inspiration_view)
    boolean onNoteInspirationViewTouched(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class OnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            currentX = e.getX();
            currentY = e.getY();
            AddNoteSpotFragment fragment = AddNoteSpotFragment.newInstance(null);
            fragment.show(getChildFragmentManager(), "AddNoteSpotFragment");
            return super.onSingleTapUp(e);
        }
    }

    @Override
    public void onCircleClick(TagViewGroup tagViewGroup) {
        NoteSpot noteSpot = inspiration.getNoteSpots()
                .get(noteInspirationView.getNoteSpotPosition(tagViewGroup));
        NoteSpotLayout noteSpotLayout = noteSpot.getNoteSpotLayout();
        noteSpotLayout.setType((noteSpotLayout.getType() + 1) % 4);
        tagViewGroup.getTagAdapter()
                .notifyDataSetChanged();
    }

    @Override
    public void onTagClick(TagViewGroup tagViewGroup, ITagView iTagView, int position) {
        this.tagViewGroup = tagViewGroup;
        NoteSpot noteSpot = inspiration.getNoteSpots()
                .get(noteInspirationView.getNoteSpotPosition(tagViewGroup));
        AddNoteSpotFragment addNoteSpotFragment = AddNoteSpotFragment.newInstance(noteSpot);
        addNoteSpotFragment.show(getChildFragmentManager(), "AddNoteSpotFragment");
    }

    @Override
    public void onLongPress(final TagViewGroup tagViewGroup) {
        DialogUtil.createDoubleButtonDialog(getContext(),
                getString(R.string.label_prompt___note),
                getString(R.string.msg_confirm_delete_tag___note),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeNoteSpot(tagViewGroup);
                    }
                },
                null)
                .show();
    }

    @Override
    public void onDrag(TagViewGroup tagViewGroup, float percentX, float percentY) {
        NoteSpot noteSpot = inspiration.getNoteSpots()
                .get(noteInspirationView.getNoteSpotPosition(tagViewGroup));
        noteSpot.getNoteSpotLayout()
                .setX(percentX);
        noteSpot.getNoteSpotLayout()
                .setY(percentY);
    }

    public void addNoteSpot(NoteSpot noteSpot) {
        noteSpot.getTags()
                .clear();
        if (CommonUtil.isCollectionEmpty(noteSpot.getTags())) {
            return;
        }
        float percentX = 0.5f;
        float percentY = 0.5f;
        if (imageWidth > 0) {
            percentX = currentX / imageWidth;
        }
        if (imageHeight > 0) {
            percentY = currentY / imageHeight;
        }
        noteSpot.getNoteSpotLayout()
                .setX(percentX);
        noteSpot.getNoteSpotLayout()
                .setY(percentY);
        inspiration.getNoteSpots()
                .add(noteSpot);
        noteInspirationView.addNoteSpotView(noteSpot);
        noteInspirationView.setShowNoteSpotEmptyView(false);
    }

    public void editNoteSpot(NoteSpot noteSpot) {
        if (tagViewGroup == null) {
            return;
        }
        noteSpot.getTags()
                .clear();
        if (CommonUtil.isCollectionEmpty(noteSpot.getTags())) {
            removeNoteSpot(tagViewGroup);
            return;
        }
        inspiration.getNoteSpots()
                .set(noteInspirationView.getNoteSpotPosition(tagViewGroup), noteSpot);
        tagViewGroup.getTagAdapter()
                .notifyDataSetChanged();
    }

    private void removeNoteSpot(TagViewGroup tagViewGroup) {
        inspiration.getNoteSpots()
                .remove(noteInspirationView.getNoteSpotPosition(tagViewGroup));
        noteInspirationView.removeNoteSpotView(tagViewGroup);
        noteInspirationView.setShowNoteSpotEmptyView(CommonUtil.isCollectionEmpty(inspiration
                .getNoteSpots()));
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}