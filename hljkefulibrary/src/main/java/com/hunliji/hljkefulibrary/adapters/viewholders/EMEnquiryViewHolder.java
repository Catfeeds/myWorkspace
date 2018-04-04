package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/24.
 */

public class EMEnquiryViewHolder extends EMChatMessageBaseViewHolder {

    private RadioButton btnRating1;
    private RadioButton btnRating2;
    private RadioButton btnRating3;
    private RadioButton btnRating4;
    private RadioButton btnRating5;

    public EMEnquiryViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.em_chat_enquiry_item___kefu, parent, false));
    }

    private EMEnquiryViewHolder(View itemView) {
        super(itemView);
        final RadioGroup ratingMenu = itemView.findViewById(R.id.rating_menu);
        btnRating1 = itemView.findViewById(R.id.rating1);
        btnRating2 = itemView.findViewById(R.id.rating2);
        btnRating3 = itemView.findViewById(R.id.rating3);
        btnRating4 = itemView.findViewById(R.id.rating4);
        btnRating5 = itemView.findViewById(R.id.rating5);
        ratingMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EMChat chat = getItem();
                int summary = 5;
                if (checkedId == R.id.rating4) {
                    summary = 4;
                } else if (checkedId == R.id.rating3) {
                    summary = 3;
                } else if (checkedId == R.id.rating2) {
                    summary = 2;
                } else if (checkedId == R.id.rating1) {
                    summary = 1;
                }
                chat.setEnquirySummary(summary);
                chat.setEnquiryDetail(((RadioButton) ratingMenu.findViewById(checkedId)).getText()
                        .toString());
            }
        });
        itemView.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChatClickListener != null) {
                            onChatClickListener.onEnquiryCancelClick(getItem());
                        }
                    }
                });
        itemView.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChatClickListener != null) {
                            onChatClickListener.onEnquiryConfirmClick(getItem());
                        }
                    }
                });
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        switch (item.getEnquirySummary()) {
            case 1:
                btnRating1.setChecked(true);
                break;
            case 2:
                btnRating2.setChecked(true);
                break;
            case 3:
                btnRating3.setChecked(true);
                break;
            case 4:
                btnRating4.setChecked(true);
                break;
            default:
                btnRating5.setChecked(true);
                break;
        }
    }
}
