package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/8/17.
 */
public class WishListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private int userLimitCount;
    private List<Author> users;
    private Context mContext;
    public int size;
    private View footView;
    public static final int FOOT_TYPE = 1;
    public static final int ITEM_TYPE = 2;

    public WishListAdapter(Context context) {
        this.mContext = context;
        size = Math.round(30 * mContext.getResources()
                .getDisplayMetrics().density);
        this.users = new ArrayList<>();
    }

    public void setLimitCount(int limitCount) {
        this.userLimitCount = limitCount;
    }

    public void setFootView(View footView) {
        this.footView = footView;
    }

    public void setUsers(List<Author> users) {
        if (users != null) {
            this.users.clear();
            this.users.addAll(users);
        }
        notifyDataSetChanged();
    }

    public void addUser(Author user) {
        users.add(0, user);
        notifyDataSetChanged();
    }

    public void removeUser(Author user) {
        if (!users.isEmpty()) {
            int size = users.size();
            for (int i = 0; i < size; i++) {
                Author u = users.get(i);
                if (u.getId() == user.getId()) {
                    users.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOT_TYPE:
                return new ExtraBaseViewHolder(footView);
            default:
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.user_icon_view, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(mContext, users.get(position), position, getItemViewType(position));
    }


    @Override
    public int getItemCount() {
        if (users != null && users.size() > userLimitCount) {
            return userLimitCount + (footView == null ? 0 : 1);
        }
        return Math.min(userLimitCount, users != null ? users.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (footView != null && users != null && users.size() > userLimitCount && position ==
                getItemCount() - 1) {
            return FOOT_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    public class ViewHolder extends BaseViewHolder<Author> {
        private ImageView imageView;

        private ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.user_avatar);
        }

        @Override
        protected void setViewData(Context mContext, Author user, int position, int viewType) {
            if (imageView != null) {
                String logoPath = ImageUtil.getAvatar(user.getAvatar(), size);
                Glide.with(imageView.getContext())
                        .load(logoPath)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(imageView);
            }
        }

    }

}
