package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityMaterial;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder.ObtainMaterialPosterViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.ObtainMaterialViewHolder;

/**
 * Created by jinxin on 2018/3/15 0015.
 */

public class ObtainWeddingMaterialAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int TYPE_POSTER = 10;
    private final int TYPE_MATERIAL = 11;
    private final int TYPE_FOOTER = 12;

    private Context mContext;
    private Poster poster;
    private View footerView;
    private LayoutInflater inflater;
    private List<CommunityMaterial> materialList;
    private ObtainMaterialViewHolder.OnObtainClickListener onObtainClickListener;

    public ObtainWeddingMaterialAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnObtainClickListener(ObtainMaterialViewHolder.OnObtainClickListener
                                                 onObtainClickListener) {
        this.onObtainClickListener = onObtainClickListener;
    }

    public void setMaterialList(List<CommunityMaterial> materialList) {
        this.materialList = materialList;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_POSTER:
                itemView = inflater.inflate(R.layout.image_item___cm, parent, false);
                ObtainMaterialPosterViewHolder holder = new ObtainMaterialPosterViewHolder
                        (itemView);
                return holder;
            case TYPE_MATERIAL:
                itemView = inflater.inflate(R.layout.obtain_wedding_mateial_item, parent, false);
                ObtainMaterialViewHolder viewHolder =  new ObtainMaterialViewHolder(itemView);
                viewHolder.setOnObtainClickListener(onObtainClickListener);
                return viewHolder;
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ObtainMaterialPosterViewHolder) {
            holder.setView(mContext, poster, position, TYPE_POSTER);
        }else if(holder instanceof  ObtainMaterialViewHolder){
            if (getPosterCount() > 0) {
                position--;
            }
            ((ObtainMaterialViewHolder) holder).setLineVisible(position != getItemCount() - 2);
            holder.setView(mContext, materialList.get(position), position, TYPE_MATERIAL);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && getPosterCount() > 0) {
            return TYPE_POSTER;
        } else if (position == getItemCount() - 1 && getFooterCount() > 0) {
            return TYPE_FOOTER;
        } else {
            return TYPE_MATERIAL;
        }
    }

    private int getPosterCount() {
        return poster == null ? 0 : 1;
    }

    private int getFooterCount() {
        return footerView == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(materialList) + getPosterCount() + getFooterCount();
    }
}
