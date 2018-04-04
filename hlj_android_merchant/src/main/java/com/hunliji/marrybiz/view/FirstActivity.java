package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.RecyclingPagerAdapter;
import com.slider.library.Indicators.CirclePageIndicator;

/**
 * Created by Suncloud on 2016/1/22.
 */
public class FirstActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ViewPager pager = (ViewPager) findViewById(R.id.items_view);
        pager.setAdapter(new FirstAdapter(this));
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.flow_indicator);
        indicator.setViewPager(pager);
    }

    private class FirstAdapter extends RecyclingPagerAdapter {

        private int[] ids = {R.drawable.image_first1, R.drawable.image_first2, R.drawable
                .image_first3};

        private Context context;

        private FirstAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.first_view, null);
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                holder.button = (Button) convertView.findViewById(R.id.button);
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                                .putBoolean("isFirst", false)
                                .commit();
                        Intent intent = new Intent(context, PreLoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.imageView.setImageResource(ids[position]);
            holder.button.setVisibility(position == getCount() - 1 ? View.VISIBLE : View.GONE);
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            Button button;
        }
    }
}
