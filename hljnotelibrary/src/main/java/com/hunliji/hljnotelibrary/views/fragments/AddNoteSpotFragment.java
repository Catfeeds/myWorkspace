package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.models.note.NoteSpotEntity;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.views.activities.CreatePhotoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.SelectNoteSimpleCategoryMarkActivity;
import com.hunliji.hljnotelibrary.views.activities.SelectNoteSpotEntityActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 发布-添加标签
 * Created by chen_bin on 2017/6/27 0027.
 */
public class AddNoteSpotFragment extends DialogFragment {

    @BindView(R2.id.btn_select_note_spot_entity)
    Button btnSelectNoteSpotEntity;
    @BindView(R2.id.btn_clear_note_spot_entity)
    ImageButton btnClearNoteSpotEntity;
    @BindView(R2.id.btn_select_note_mark)
    Button btnSelectNoteMark;
    @BindView(R2.id.btn_clear_note_mark)
    ImageButton btnClearNoteMark;
    @BindView(R2.id.et_price)
    EditText etPrice;

    private InputMethodManager imm;

    private NoteSpot noteSpot;
    private NoteSpotEntity noteSpotEntity;
    private NoteMark noteMark;

    private boolean isEdit;

    private Unbinder unbinder;

    private final static String ARG_NOTE_SPOT = "note_spot";

    public static AddNoteSpotFragment newInstance(NoteSpot noteSpot) {
        AddNoteSpotFragment fragment = new AddNoteSpotFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_NOTE_SPOT, noteSpot);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getArguments() != null) {
            noteSpot = getArguments().getParcelable(ARG_NOTE_SPOT);
        }
        if (noteSpot == null) {
            noteSpot = new NoteSpot();
        } else {
            isEdit = true;
            noteSpotEntity = noteSpot.getNoteSpotEntity();
            noteMark = noteSpot.getNoteMark();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_add_note_spot___note,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialog_anim_fade_short_in_style);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        if (noteSpotEntity != null) {
            setNoteSpotEntityTitle(noteSpotEntity.getTitle());
        }
        if (noteMark != null) {
            setNoteMarkName(noteMark.getName());
        }
        if (noteSpot.getPrice() > 0) {
            etPrice.setText(CommonUtil.formatDouble2String(noteSpot.getPrice()));
            etPrice.setSelection(etPrice.length());
        }
    }

    @OnClick(R2.id.btn_select_note_spot_entity)
    void onSelectNoteSpotEntity() {
        Intent intent = new Intent(getContext(), SelectNoteSpotEntityActivity.class);
        startActivityForResult(intent, HljNote.RequestCode.SELECT_NOTE_SPOT_ENTITY);
    }

    @OnClick(R2.id.btn_select_note_mark)
    void onSelectNoteMark() {
        Intent intent = new Intent(getContext(), SelectNoteSimpleCategoryMarkActivity.class);
        startActivityForResult(intent, HljNote.RequestCode.SELECT_NOTE_CATEGORY_MARK);
    }

    @OnClick(R2.id.btn_clear_note_spot_entity)
    void onClearNoteSpotEntity() {
        noteSpotEntity = null;
        setNoteSpotEntityTitle("");
    }

    @OnClick(R2.id.btn_clear_note_mark)
    void onClearNoteMark() {
        noteMark = null;
        setNoteMarkName("");
    }

    @OnClick(R2.id.btn_close)
    void onClose() {
        hideKeyboard();
        dismiss();
    }

    @OnClick(R2.id.btn_confirm)
    void onConfirm() {
        noteSpot.setNoteSpotEntity(noteSpotEntity);
        noteSpot.setNoteMark(noteMark);
        if (TextUtils.isEmpty(etPrice.getText())) {
            noteSpot.setPrice(0);
        } else {
            noteSpot.setPrice(Double.valueOf(etPrice.getText()
                    .toString()));
        }
        if (getParentFragment() instanceof SelectedNoteInspirationFragment) {
            SelectedNoteInspirationFragment fragment = (SelectedNoteInspirationFragment)
                    getParentFragment();
            if (isEdit) {
                fragment.editNoteSpot(noteSpot);
            } else {
                fragment.addNoteSpot(noteSpot);
            }
        }
        onClose();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getContext() instanceof CreatePhotoNoteActivity) {
            ((CreatePhotoNoteActivity) getContext()).setExpanded(true, false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.SELECT_NOTE_SPOT_ENTITY:
                    if (data != null) {
                        noteSpotEntity = data.getParcelableExtra("note_spot_entity");
                        setNoteSpotEntityTitle(noteSpotEntity.getTitle());
                    }
                    break;
                case HljNote.RequestCode.SELECT_NOTE_CATEGORY_MARK:
                    if (data != null) {
                        noteMark = data.getParcelableExtra("note_mark");
                        setNoteMarkName(noteMark.getName());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setNoteSpotEntityTitle(String str) {
        btnSelectNoteSpotEntity.setText(str);
        btnClearNoteSpotEntity.setVisibility(!TextUtils.isEmpty(str) ? View.VISIBLE : View.GONE);
    }

    private void setNoteMarkName(String str) {
        btnSelectNoteMark.setText(str);
        btnClearNoteMark.setVisibility(!TextUtils.isEmpty(str) ? View.VISIBLE : View.GONE);
    }

    private void hideKeyboard() {
        if (imm != null) {
            imm.hideSoftInputFromWindow(etPrice.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
