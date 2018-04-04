package me.suncloud.marrymemo.adpter.user.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.BaseServiceCommentViewHolder;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * 用户评价viewHolder
 * Created by chen_bin on 2017/6/12 0012.
 */
public class UserCommentViewHolder extends BaseServiceCommentViewHolder {
    @BindView(R.id.top_line_layout)
    View topLineLayout;
    @BindView(R.id.images_layout)
    HljGridView imagesLayout;
    @BindView(R.id.tv_comment_type_title)
    TextView tvCommentTypeTitle;
    @BindView(R.id.comment_type_layout)
    LinearLayout commentTypeLayout;
    @BindView(R.id.tv_praised_count)
    TextView tvPraisedCount;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.btn_user_menu)
    ImageButton btnUserMenu;
    private OnItemClickListener onItemClickListener;
    private OnMenuListener onMenuListener;

    public UserCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        imagesLayout.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(itemView
                        .getContext(),
                2)));
        imagesLayout.setItemClickListener(new HljGridView.GridItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ServiceComment comment = getItem();
                if (comment != null && !CommonUtil.isCollectionEmpty(comment.getPhotos())) {
                    Intent intent = new Intent(v.getContext(), PicsPageViewActivity.class);
                    intent.putExtra("photos", comment.getPhotos());
                    intent.putExtra("position", position);
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
        commentTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem() == null) {
                    return;
                }
                Work work = getItem().getWork();
                Merchant merchant = getItem().getMerchant();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    view.getContext()
                            .startActivity(intent);
                } else if (merchant != null && merchant.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), MerchantDetailActivity.class);
                    intent.putExtra("id", merchant.getId());
                    view.getContext()
                            .startActivity(intent);
                }
            }
        });
        btnUserMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuListener != null) {
                    onMenuListener.onMenu(getAdapterPosition(), getItem());
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, ServiceComment comment, int position, int viewType) {
        if (comment == null) {
            return;
        }
        super.setViewData(mContext, comment, position, viewType);
        tvPraisedCount.setText("有用" + comment.getLikesCount());
        tvCommentCount.setText("回复" + comment.getCommentCount());
        setPhotos(comment);
        setCommentType(mContext, comment);
    }

    private void setPhotos(ServiceComment comment) {
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            imagesLayout.setVisibility(View.VISIBLE);
            imagesLayout.setDate(comment.getPhotos());
        }
    }

    private void setCommentType(Context context, ServiceComment comment) {
        Work work = comment.getWork();
        Merchant merchant = comment.getMerchant();
        boolean isWork = work != null && work.getId() > 0;
        boolean isMerchant = merchant != null && merchant.getId() > 0;
        if (!isWork && !isMerchant) {
            commentTypeLayout.setVisibility(View.GONE);
        } else {
            commentTypeLayout.setVisibility(View.VISIBLE);
            tvCommentTypeTitle.setText(isWork ? context.getString(R.string.label_comment_type_work,
                    work.getTitle()) : context.getString(R.string.label_comment_type_merchant,
                    merchant.getName()));
        }
    }

    public void setShowMenu(boolean showMenu) {
        btnUserMenu.setVisibility(showMenu ? View.VISIBLE : View.GONE);
    }

    public void setShowTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        this.onMenuListener = onMenuListener;
    }

    public interface OnMenuListener {
        void onMenu(int position, ServiceComment comment);
    }

}