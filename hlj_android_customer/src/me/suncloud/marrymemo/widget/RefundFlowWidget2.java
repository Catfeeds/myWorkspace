package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by werther on 16/4/8.
 */
public class RefundFlowWidget2 extends FrameLayout {

    @BindView(R.id.line_1)
    View line1;
    @BindView(R.id.line_2)
    View line2;
    @BindView(R.id.line_3)
    View line3;
    @BindView(R.id.line_4)
    View line4;
    @BindView(R.id.tv_circle_1)
    TextView tvCircle1;
    @BindView(R.id.tv_label_1)
    TextView tvLabel1;
    @BindView(R.id.tv_circle_2)
    TextView tvCircle2;
    @BindView(R.id.tv_label_2)
    TextView tvLabel2;
    @BindView(R.id.tv_circle_3)
    TextView tvCircle3;
    @BindView(R.id.tv_label_3)
    TextView tvLabel3;
    @BindView(R.id.tv_circle_4)
    TextView tvCircle4;
    @BindView(R.id.tv_label_4)
    TextView tvLabel4;
    @BindView(R.id.tv_circle_5)
    TextView tvCircle5;
    @BindView(R.id.tv_label_5)
    TextView tvLabel5;
    private Context mContext;
    private int step;


    public RefundFlowWidget2(Context context) {
        super(context);
        init(context);
    }

    public RefundFlowWidget2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefundFlowWidget2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.refund_flow_layout2, this, true);

        ButterKnife.bind(this);

        setCurrentStep(0);
    }

    /**
     * 设置当前流程所处于的步骤
     *
     * @param i 0,1,2,3,4,5
     */
    public void setCurrentStep(int i) {
        step = i;

        if (step == 0) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line1.setBackgroundResource(R.color.color_white5);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line2.setBackgroundResource(R.color.color_white5);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line3.setBackgroundResource(R.color.color_white5);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line4.setBackgroundResource(R.color.color_white5);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else if (step == 1) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line1.setBackgroundResource(R.color.color_white5);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line2.setBackgroundResource(R.color.color_white5);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line3.setBackgroundResource(R.color.color_white5);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line4.setBackgroundResource(R.color.color_white5);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else if (step == 2) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line1.setBackgroundResource(R.color.colorPrimary);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line2.setBackgroundResource(R.color.color_white5);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line3.setBackgroundResource(R.color.color_white5);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line4.setBackgroundResource(R.color.color_white5);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else if (step == 3) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line1.setBackgroundResource(R.color.colorPrimary);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line2.setBackgroundResource(R.color.colorPrimary);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line3.setBackgroundResource(R.color.color_white5);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));

            line4.setBackgroundResource(R.color.color_white5);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else if (step == 4) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line1.setBackgroundResource(R.color.colorPrimary);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line2.setBackgroundResource(R.color.colorPrimary);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line3.setBackgroundResource(R.color.colorPrimary);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line4.setBackgroundResource(R.color.color_white5);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_white5);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else if (step == 5) {
            tvCircle1.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel1.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line1.setBackgroundResource(R.color.colorPrimary);
            tvCircle2.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel2.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line2.setBackgroundResource(R.color.colorPrimary);
            tvCircle3.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel3.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line3.setBackgroundResource(R.color.colorPrimary);
            tvCircle4.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel4.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            line4.setBackgroundResource(R.color.colorPrimary);
            tvCircle5.setBackgroundResource(R.drawable.sp_oval_primary);
            tvLabel5.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }
}
