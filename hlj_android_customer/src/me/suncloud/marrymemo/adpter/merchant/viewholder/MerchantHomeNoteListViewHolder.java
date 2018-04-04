package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljnotelibrary.adapters.UserNoteRecyclerAdapter;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 大图模式列表
 * Created by chen_bin on 2017/5/19 0019.
 */
public class MerchantHomeNoteListViewHolder extends BaseViewHolder<List<Note>> {


    @BindView(R.id.recycler_view)
    RecyclerView noteRecyclerView;

    public MerchantHomeNoteListViewHolder(
            ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_layout, parent, false));
    }

    private MerchantHomeNoteListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        noteRecyclerView.setFocusable(false);
        noteRecyclerView.setBackgroundColor(ThemeUtil.getAttrColor(itemView.getContext(),
                R.attr.hljColorSegmentBackground,
                Color.WHITE));
        noteRecyclerView.setPadding(0, 0, 0, CommonUtil.dp2px(itemView.getContext(), 16));
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        noteRecyclerView.addItemDecoration(new SpacesItemDecoration(view.getContext()));
        UserNoteRecyclerAdapter adapter = new UserNoteRecyclerAdapter(view.getContext(), null);
        adapter.setOnUserNoteItemListener(new UserNoteRecyclerAdapter.OnUserNoteItemListener() {
            @Override
            public void onUserItemClick(int position, Note note) {
                if (note != null && note.getId() > 0) {
                    Intent intent = new Intent(itemView.getContext(), NoteDetailActivity.class);
                    intent.putExtra("note_id", note.getId());
                    itemView.getContext()
                            .startActivity(intent);
                }
            }
        });
        noteRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void setViewData(
            Context mContext, List<Note> notes, int position, int viewType) {
        if (noteRecyclerView.getAdapter() != null && noteRecyclerView.getAdapter() instanceof
                UserNoteRecyclerAdapter) {
            ((UserNoteRecyclerAdapter) noteRecyclerView.getAdapter()).setNotes(notes);
        }

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;

        SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 16);
            this.middleSpace = CommonUtil.dp2px(context, 4);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = middleSpace;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                left = space;
            } else if (position == parent.getAdapter()
                    .getItemCount() - 1) {
                right = space;
            }
            outRect.set(left, 0, right, 0);
        }
    }

}
