package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityMaterial;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by jinxin on 2018/3/15 0015.
 */

public class ObtainMaterialViewHolder extends BaseViewHolder<CommunityMaterial> {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_obtain)
    TextView tvObtain;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.line)
    View line;
    private Context mContext;
    private OnObtainClickListener onObtainClickListener;

    public ObtainMaterialViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setOnObtainClickListener(OnObtainClickListener onObtainClickListener) {
        this.onObtainClickListener = onObtainClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, final CommunityMaterial material, int position, int viewType) {
        if (material == null) {
            return;
        }
        tvTitle.setText(material.getTitle());
        tvDes.setText(material.getContent());
        switch (material.getStatus()) {
            case CommunityMaterial.NOT_START:
                //未开始
                tvCount.setText(mContext.getResources()
                        .getString(R.string.label_obtain_get1,
                                String.valueOf(material.getFinishCount())));
                if (position > 0) {
                    tvComplete.setVisibility(View.VISIBLE);
                    tvObtain.setVisibility(View.GONE);
                }else{
                    tvComplete.setVisibility(View.GONE);
                    tvObtain.setVisibility(View.VISIBLE);
                    tvObtain.setText(mContext.getResources()
                            .getString(R.string.label_obtain_get2,
                                    String.valueOf(material.getCurrentNum()),
                                    String.valueOf(material.getNum())));
                    tvObtain.setBackgroundResource(R.drawable.sp_r11_gray3);
                }
                break;
            case CommunityMaterial.UNDER_WAY:
                //进行中
                tvCount.setText(mContext.getResources()
                        .getString(R.string.label_obtain_get1,
                                String.valueOf(material.getFinishCount())));
                tvComplete.setVisibility(View.GONE);
                tvObtain.setVisibility(View.VISIBLE);
                tvObtain.setText(mContext.getResources()
                        .getString(R.string.label_obtain_get2,
                                String.valueOf(material.getCurrentNum()),
                                String.valueOf(material.getNum())));
                tvObtain.setBackgroundResource(R.drawable.sp_r11_gray3);
                tvObtain.setEnabled(false);
                break;
            case CommunityMaterial.COMPLETED:
                //已完成
                tvCount.setText(mContext.getResources()
                        .getString(R.string.label_obtain_get1,
                                String.valueOf(material.getFinishCount())));
                tvComplete.setVisibility(View.GONE);
                tvObtain.setVisibility(View.VISIBLE);
                tvObtain.setText("获取");
                tvObtain.setBackgroundResource(R.drawable.sp_r11_primary);
                tvObtain.setEnabled(true);
                break;
            default:
                break;
        }
        tvObtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onObtainClickListener != null) {
                    onObtainClickListener.onObtain(material);
                }
            }
        });
    }

    public void setLineVisible(boolean visible){
        line.setVisibility(visible?View.VISIBLE:View.GONE);
    }

    public interface OnObtainClickListener {
        void onObtain(CommunityMaterial material);
    }
}
