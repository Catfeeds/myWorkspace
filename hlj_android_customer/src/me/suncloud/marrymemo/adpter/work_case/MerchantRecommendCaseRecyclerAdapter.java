package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantRecommendCaseViewHolder;

/**
 * 商家主页案例推荐
 * Created by chen_bin on 2017/5/22 0022.
 */
public class MerchantRecommendCaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<Work> cases;
    private LayoutInflater inflater;

    public MerchantRecommendCaseRecyclerAdapter(Context context, List<Work> cases) {
        this.context = context;
        this.cases = cases;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCases(List<Work> cases) {
        if(this.cases!=cases){
            this.cases = cases;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return cases==null?0:cases.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MerchantRecommendCaseViewHolder(inflater.inflate(R.layout
                        .merchant_recommend_case_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, cases.get(position), position, 0);
    }
}
