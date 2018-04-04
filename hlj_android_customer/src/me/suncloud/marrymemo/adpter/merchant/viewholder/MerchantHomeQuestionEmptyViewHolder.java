package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by wangtao on 2017/9/29.
 */

public class MerchantHomeQuestionEmptyViewHolder extends ExtraBaseViewHolder {


    @BindView(R.id.ask_question_layout)
    LinearLayout askQuestionLayout;
    private Merchant merchant;

    public MerchantHomeQuestionEmptyViewHolder(ViewGroup parent,Merchant merchant) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_home_question_empty, parent, false));
        this.merchant=merchant;
    }

    private MerchantHomeQuestionEmptyViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        askQuestionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AskQuestionListActivity.class);
                intent.putExtra("merchant_id", merchant.getId());
                intent.putExtra("is_show_key_board", true);
                view.getContext().startActivity(intent);
            }
        });
    }
}
