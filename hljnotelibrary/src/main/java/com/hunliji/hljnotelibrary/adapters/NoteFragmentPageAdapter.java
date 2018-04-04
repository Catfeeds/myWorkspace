package com.hunliji.hljnotelibrary.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.hunliji.hljnotelibrary.views.fragments.BaseNoteDetailFragment;
import com.hunliji.hljcommonlibrary.models.note.Note;

import java.util.List;

public class NoteFragmentPageAdapter extends FragmentStatePagerAdapter {

    private long[] noteIds;
    private SparseArray<BaseNoteDetailFragment> fragments;
    private boolean isLargerView;
    private long inspirationId;
    private int inspirationPosition;
    private String url;
    private int notePosition;

    public NoteFragmentPageAdapter(FragmentManager fm, long[] userNotes) {
        super(fm);
        this.noteIds = userNotes;
    }

    public BaseNoteDetailFragment getFragment(int position) {
        if (fragments == null) {
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments == null) {
            fragments = new SparseArray<>();
        }
        BaseNoteDetailFragment fragment = fragments.get(position);
        if (fragment != null) {
            return fragment;
        }
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putLong("note_id", noteIds[position]);
        args.putBoolean("is_larger_view", isLargerView);
        args.putLong("inspiration_id", inspirationId);
        args.putInt("note_position", notePosition);
        args.putString("url", url);
        if (notePosition == position) {
            args.putInt("inspiration_position", inspirationPosition);
        }
        fragment = BaseNoteDetailFragment.newInstance(args);
        if (fragment != null) {
            fragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return noteIds == null ? 0 : noteIds.length;
    }

    public void setLargerView(boolean largerView) {
        isLargerView = largerView;
    }

    public void setInspirationId(long inspirationId) {
        this.inspirationId = inspirationId;
    }

    public void setInspirationPosition(int inspirationPosition) {
        this.inspirationPosition = inspirationPosition;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNotePosition(int notePosition) {
        this.notePosition = notePosition;
    }

}