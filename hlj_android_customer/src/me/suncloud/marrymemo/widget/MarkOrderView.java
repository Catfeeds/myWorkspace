package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.suncloud.marrymemo.R;

/**
 * 标签页的排序header
 * Created by jinxin on 2016/4/26.
 */
public class MarkOrderView extends LinearLayout implements CheckableLinearLayout2.OnCheckedChangeListener, View
        .OnClickListener {
    public static final int ORDER = 0;
    private SparseArray<TextView> textViews;
    private ImageView priceImage;
    private CheckableLinearLayout2 priceCheckLayout;
    private View allLayout;
    private View hotLayout;
    private View priceLayout;
    private View newestLayout;
    private TextView likeCount;
    private TextView price;
    private Handler heightHandler;
    private static int height;
    private View orderLayout;

    private OnClickListener onClickListener;
    private CheckableLinearLayout2.OnCheckedChangeListener onCheckedChangeListener;

    public MarkOrderView(Context context) {
        this(context, null);
    }

    public MarkOrderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkOrderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.mark_activity_order_header, this, true);
        init();
    }

    private void init() {
        orderLayout = findViewById(R.id.order_layout);
        textViews = new SparseArray<>();
        priceImage = (ImageView) findViewById(R.id.result_sort);
        priceCheckLayout = (CheckableLinearLayout2) findViewById(R.id.result_detail_price_layout);
        priceCheckLayout.setOnCheckedChangeListener(this);
        allLayout = findViewById(R.id.all_layout);
        hotLayout = findViewById(R.id.hot_layout);
        priceLayout = findViewById(R.id.price_layout);
        newestLayout = findViewById(R.id.newest_layout);
        likeCount = (TextView) findViewById(R.id.result_detail_likecount);

        TextView all = (TextView) findViewById(R.id.result_detail_all);
        TextView hot = (TextView) findViewById(R.id.result_detail_hot);
        price = (TextView) findViewById(R.id.result_detail_price_text);
        TextView newest = (TextView) findViewById(R.id.result_detail_newest);
        all.setOnClickListener(this);
        hot.setOnClickListener(this);
        newest.setOnClickListener(this);
        likeCount.setOnClickListener(this);
        textViews.put(R.id.result_detail_all, all);
        textViews.put(R.id.result_detail_hot, hot);
        textViews.put(R.id.result_detail_price_text, price);
        textViews.put(R.id.result_detail_newest, newest);
        textViews.put(R.id.result_detail_likecount, likeCount);
    }

    private void changeHeaderColor(int id) {
        for (int i = 0; i < textViews.size(); i++) {
            int key = textViews.keyAt(i);
            TextView tv = textViews.valueAt(i);
            if (key == id) {
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tv.setTextColor(getResources().getColor(R.color.colorBlack2));
            }
        }
    }

    public void setContentVisible(boolean order) {
        if (orderLayout != null) {
            orderLayout.setVisibility(order ? VISIBLE : GONE);
        }
    }

    public void setHeightHandler(Handler heightHandler) {
        this.heightHandler = heightHandler;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        if (heightHandler != null) {
            Message heightMsg = new Message();
            heightMsg.what = ORDER;
            heightMsg.obj = h;
            heightHandler.sendMessage(heightMsg);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public static int getMarkHeight() {
        return height;
    }

    public void setOrderTextVisible(boolean all, boolean hot, boolean price, boolean newest, boolean like) {
        allLayout.setVisibility(all ? VISIBLE : GONE);
        hotLayout.setVisibility(hot ? VISIBLE : GONE);
        priceLayout.setVisibility(price ? VISIBLE : GONE);
        newestLayout.setVisibility(newest ? VISIBLE : GONE);
        likeCount.setVisibility(like ? VISIBLE : GONE);
    }

    public void setOnCheckedChangeListener(CheckableLinearLayout2.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnTextClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onCheckedChange(View view, boolean checked) {
        priceImage.setImageResource(R.drawable.sl_ic_sort_asc_2_desc);
        price.setTextColor(getResources().getColor(R.color.colorPrimary));
        changeHeaderColor(R.id.result_detail_price_text);
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChange(view, checked);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        changeHeaderColor(id);
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

}
