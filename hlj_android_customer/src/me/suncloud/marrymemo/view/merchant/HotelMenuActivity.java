package me.suncloud.marrymemo.view.merchant;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.HotelMenuInfoAdapter;
import me.suncloud.marrymemo.adpter.merchant.HotelMenuPriceAdapter;

/**
 * Created by wangtao on 2017/10/10.
 */

public class HotelMenuActivity extends HljBaseActivity {

    @BindView(R.id.menu_list)
    ListView menuList;
    @BindView(R.id.rc_content)
    RecyclerView rcContent;

    private HotelMenuInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_menu);
        ButterKnife.bind(this);

        int position = getIntent().getIntExtra("position", 0);
        Merchant merchant = getIntent().getParcelableExtra("merchant");
        menuList.setItemsCanFocus(true);
        menuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        menuList.setAdapter(new HotelMenuPriceAdapter(merchant.getHotel().getHotelMenus()));
        menuList.setItemChecked(position, true);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HotelMenu menu = (HotelMenu) adapterView.getAdapter().getItem(i);
                if (menu != null) {
                    adapter.setMenu(menu);
                    rcContent.scrollToPosition(0);
                }
            }
        });

        adapter = new HotelMenuInfoAdapter(this);
        adapter.setMenu(merchant.getHotel().getHotelMenus().get(position));
        rcContent.setLayoutManager(new LinearLayoutManager(this));
        rcContent.setAdapter(adapter);

    }
}
