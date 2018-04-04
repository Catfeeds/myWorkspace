package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by mo_yu on 2016/11/16.档期管理
 */

public class HotelCalendarRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int ITEM = 1;
    private Context context;
    private ArrayList<Calendar> calendars;
    private OnDeleteItem onDeleteItem;

    public HotelCalendarRecyclerAdapter(Context context, ArrayList<Calendar> calendars) {
        this.context = context;
        this.calendars = calendars;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.hotel_calendar_list_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM) {
            holder.setView(context, calendars.get(position), position, type);
        }
    }


    @Override
    public int getItemCount() {
        return calendars.size();
    }

    class ViewHolder extends BaseViewHolder<Calendar> {
        @BindView(R.id.tv_selected_calendar)
        TextView tvSelectedCalendar;
        @BindView(R.id.btn_delete)
        ImageView btnDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, final Calendar item, final int position, int viewType) {
            tvSelectedCalendar.setText(getCalendarStr(item));
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onDeleteItem!=null){
                        onDeleteItem.onDelete(position,item);
                    }
                }
            });
        }
    }

    public String getCalendarStr(Calendar item) {
        int day = item.get(Calendar.DATE);       //日
        int month = item.get(Calendar.MONTH) + 1;//月
        int year = item.get(Calendar.YEAR);      //年
        int week = item.get(Calendar.DAY_OF_WEEK);//星期
        String weekStr = null;
        switch (week) {
            case Calendar.MONDAY:
                weekStr = "星期一";
                break;
            case Calendar.TUESDAY:
                weekStr = "星期二";
                break;
            case Calendar.WEDNESDAY:
                weekStr = "星期三";
                break;
            case Calendar.THURSDAY:
                weekStr = "星期四";
                break;
            case Calendar.FRIDAY:
                weekStr = "星期五";
                break;
            case Calendar.SATURDAY:
                weekStr = "星期六";
                break;
            case Calendar.SUNDAY:
                weekStr = "星期日";
                break;
        }
        return year + "年" + month + "月" + day + "日" + " " + weekStr;
    }

    public void setOnDeleteItem(OnDeleteItem onDeleteItem) {
        this.onDeleteItem = onDeleteItem;
    }

    public interface OnDeleteItem{
       void onDelete(int position,Object object);
    }
}
