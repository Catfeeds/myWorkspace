package com.hunliji.hljcommonviewlibrary.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
/**
 * Created by wangtao on 2017/9/27.
 */

public abstract class GroupRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    private SparseArray<GroupStructure> groups;

    protected void resetGroup(){
        if(groups!=null){
            groups.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        GroupStructure group = null;
        int childPosition = position;
        for (int i = 0, size = groups.size(); i < size; i++) {
            if (childPosition >= groups.valueAt(i).getCount()) {
                childPosition -= groups.valueAt(i).getCount();
            } else {
                group = groups.valueAt(i);
                break;
            }
        }
        if (group == null) {
            return;
        }
        if (group.isHasHeader()) {
            if (childPosition == 0) {
                onBindGroupHeaderViewHolder(holder, group.getGroupType());
                return;
            } else {
                childPosition--;
            }
        }
        if (childPosition < group.getChildCount()) {
            onBindChildViewHolder(holder, group.getGroupType(), childPosition);
        } else {
            onBindGroupFooterViewHolder(holder, group.getGroupType());
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (groups == null || groups.size() == 0) {
            return count;
        }
        for (int i = 0, size = groups.size(); i < size; i++) {
            count += groups.valueAt(i)
                    .getCount();
        }
        return count;
    }

    protected void addGroupChild(int groupIndex, int groupType, int addCount) {
        if (groupIndex < 0) {
            return;
        }
        if (groups == null) {
            groups = new SparseArray<>();
        }
        GroupStructure oldGroup = groups.get(groupIndex);
        if(oldGroup!=null&&oldGroup.getGroupType()==groupType){
            int offset = getGroupOffset(groupIndex);
            oldGroup.addChildCount(addCount);
            notifyItemRangeInserted(offset+oldGroup.getCount(), addCount);
        }else {
            setGroup(groupIndex,groupType,addCount);
        }
    }

    protected void setGroup(int groupIndex, int groupType, int childCount) {
        if (groupIndex < 0) {
            return;
        }
        if (groups == null) {
            groups = new SparseArray<>();
        }
        GroupStructure group = new GroupStructure(groupType,
                childCount,
                hasGroupHeader(groupType),
                hasGroupFooter(groupType));
        int offset = getGroupOffset(groupIndex);
        GroupStructure oldGroup = groups.get(groupIndex);
        if (oldGroup != null) {
            groups.put(groupIndex, group);
            int removeCount = oldGroup.getCount() - group.getCount();
            if (removeCount == 0) {
                notifyItemRangeChanged(offset, group.getCount());
            } else if (removeCount > 0) {
                notifyItemRangeChanged(offset, group.getCount());
                notifyItemRangeRemoved(offset + group.getCount(),
                        oldGroup.getCount() - group.getCount());
            } else {
                notifyItemRangeChanged(offset, oldGroup.getCount());
                notifyItemRangeInserted(offset + oldGroup.getCount(), -removeCount);
            }
        } else {
            groups.put(groupIndex, group);
            notifyItemRangeInserted(offset, group.getCount());
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(groups==null){
            return 0;
        }
        GroupStructure group = null;
        int childPosition = position;
        for (int i = 0, size = groups.size(); i < size; i++) {
            if (childPosition >= groups.valueAt(i).getCount()) {
                childPosition -= groups.valueAt(i).getCount();
            } else {
                group = groups.valueAt(i);
                break;
            }
        }
        if (group == null) {
            return 0;
        }
        if (group.isHasHeader()) {
            if (childPosition == 0) {
                return getGroupHeaderType(group.getGroupType());
            } else {
                childPosition--;
            }
        }
        if (childPosition < group.getChildCount()) {
            return getItemViewType(group.getGroupType(), childPosition);
        } else {
            return getGroupFooterType(group.getGroupType());
        }
    }

    public int getGroupIndex(int position) {
        if(groups==null||groups.size()==0){
            return 0;
        }
        for (int i = 0, size = groups.size(); i < size; i++) {
            GroupStructure group=groups.valueAt(i);
            if (position >= group.getCount()) {
                position -= group.getCount();
            } else {
                return groups.keyAt(i);
            }
        }
        return 0;
    }

    public int getGroupOffset(int groupIndex) {
        if(groups==null||groups.size()==0){
            return 0;
        }
        int offset = 0;
        for (int i = 0; i < groupIndex; i++) {
            if (groups.get(i) == null) {
                continue;
            }
            offset += groups.get(i)
                    .getCount();
        }
        return offset;
    }

    public abstract boolean hasGroupHeader(int groupType);

    public abstract boolean hasGroupFooter(int groupType);


    public abstract int getGroupHeaderType(int groupType);

    public abstract int getGroupFooterType(int groupType);

    public abstract int getItemViewType(int groupType, int childPosition);

    public abstract void onBindChildViewHolder(VH holder, int groupType, int childPosition);

    public abstract void onBindGroupHeaderViewHolder(VH holder, int groupType);

    public abstract void onBindGroupFooterViewHolder(VH holder, int groupType);


    private class GroupStructure {
        private boolean hasHeader;
        private boolean hasFooter;
        private int childCount;
        private int groupType;

        public GroupStructure(
                int groupType, int childCount, boolean hasHeader, boolean hasFooter) {
            this.groupType = groupType;
            this.hasHeader = hasHeader;
            this.hasFooter = hasFooter;
            this.childCount = childCount;
        }

        public boolean isHasFooter() {
            return hasFooter;
        }

        public boolean isHasHeader() {
            return hasHeader;
        }

        public int getGroupType() {
            return groupType;
        }

        public int getCount() {
            int count = childCount;
            if (hasHeader) {
                count++;
            }
            if (hasFooter) {
                count++;
            }
            return count;
        }

        public int getChildCount() {
            return childCount;
        }

        public void addChildCount(int addCount) {
            this.childCount += addCount;
        }
    }
}
