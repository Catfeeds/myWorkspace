package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.AddressArea;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShippingAddress;
import me.suncloud.marrymemo.util.JSONUtil;

public class ShippingAddressListActivity extends HljBaseActivity implements
        ObjectBindAdapter.ViewBinder<ShippingAddress>, AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private View headView;
    private ArrayList<ShippingAddress> shippingAddresses;
    private ObjectBindAdapter<ShippingAddress> adapter;
    private int emptyHintId;
    private int emptyHintDrawableId;
    private ArrayList<AddressArea> areaLists;
    private boolean isLoad;
    private boolean selectMode; // 是否是选择模式
    private ShippingAddress selectedAddress; // 选择模式的时候,有传入已选择的地址
    private boolean deletedSelectedAddress; // 删除了选中的地址

    @Override
    public String pageTrackTagName() {
        return "收货地址";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address_list);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        selectMode = getIntent().getBooleanExtra("select", false);

        emptyHintId = R.string.hint_no_shipping_address;
        emptyHintDrawableId = R.drawable.icon_empty_address;

        getAddressAreaData();

        setOkText(R.string.label_add2);
        if (selectMode) {
            setTitle(R.string.label_select_shipping_address);
            selectedAddress = (ShippingAddress) getIntent().getSerializableExtra
                    ("selected_address");
        } else {
            setTitle(R.string.title_activity_shipping_address_list);
        }

        headView = getLayoutInflater().inflate(R.layout.empty_placeholder, null);
        View emptyView = headView.findViewById(R.id.place_holder);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) emptyView
                .getLayoutParams();
        params.height = Math.round(dm.density * 12);
        listView.getRefreshableView()
                .addHeaderView(headView);

        shippingAddresses = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                shippingAddresses,
                R.layout.shipping_address_list_item,
                this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);

        progressBar.setVisibility(View.VISIBLE);
        if (shippingAddresses.isEmpty()) {
            new GetAddressTask().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, EditShippingAddressActivity.class);
        if (shippingAddresses.isEmpty()) {
            intent.putExtra("is_first", true);
        }
        startActivityForResult(intent, Constants.RequestCode.EDIT_SHIPPING_ADDRESS);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        super.onOkButtonClick();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (selectMode) {
            if (selectedAddress != null) {
                intent.putExtra("address", selectedAddress);
            }
            intent.putExtra("deleted_selected_address", deletedSelectedAddress);
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void setViewValue(View view, final ShippingAddress shippingAddress, int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvBuyerName.setText(getString(R.string.label_receiver_name,
                shippingAddress.getBuyerName()));
        holder.tvDefault.setVisibility(shippingAddress.isDefault() ? View.VISIBLE : View.GONE);
        holder.tvMobilePhone.setText(getString(R.string.label_receiver_phone,
                shippingAddress.getMobilePhone()));
        holder.tvAddress.setText(getString(R.string.label_receiver_address,
                shippingAddress.toString()));
        if (selectMode) {
            if (selectedAddress != null) {
                // 如果有已选择的selectedAddress,则选中的地址高亮
                if (selectedAddress.getId()
                        .equals(shippingAddress.getId()) || (deletedSelectedAddress &&
                        shippingAddress.isDefault())) {
                    holder.holderLayout.setBackgroundResource(R.drawable.sl_color_primary_2_dark);
                    holder.tvBuyerName.setTextColor(getResources().getColor(R.color.colorWhite));
                    holder.tvMobilePhone.setTextColor(getResources().getColor(R.color.colorWhite));
                    holder.tvAddress.setTextColor(getResources().getColor(R.color.colorWhite));
                    holder.tvDefault.setBackgroundResource(R.drawable.sp_r4_white);
                    holder.tvDefault.setTextColor(getResources().getColor(R.color.colorPrimary));
                    holder.imgEdit.setImageResource(R.drawable.icon_edit_white_36_36);
                } else {
                    holder.holderLayout.setBackgroundResource(R.drawable.sl_color_white_2_background2);
                    holder.tvBuyerName.setTextColor(getResources().getColor(R.color.colorBlack2));
                    holder.tvMobilePhone.setTextColor(getResources().getColor(R.color.colorBlack2));
                    holder.tvAddress.setTextColor(getResources().getColor(R.color.colorGray));
                    holder.tvDefault.setBackgroundResource(R.drawable.sp_r4_primary);
                    holder.tvDefault.setTextColor(getResources().getColor(R.color.colorWhite));
                    holder.imgEdit.setImageResource(R.drawable.icon_edit_gray_36_36);
                }
            }

            holder.imgEdit.setClickable(true);
            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShippingAddressListActivity.this,
                            EditShippingAddressActivity.class);
                    intent.putExtra("edit_address", shippingAddress);
                    if (shippingAddress.getId()
                            .equals(selectedAddress.getId())) {
                        intent.putExtra("edit_selected_address", true);
                    }
                    intent.putExtra("is_first", false);
                    startActivityForResult(intent, Constants.RequestCode.EDIT_SHIPPING_ADDRESS);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        } else {
            holder.imgEdit.setClickable(false);
            holder.imgEdit.setImageResource(R.mipmap.icon_arrow_right_gray_14_26);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShippingAddress shippingAddress = (ShippingAddress) parent.getAdapter()
                .getItem(position);
        if (shippingAddress != null) {
            if (selectMode) {
                // 如果是选择模式,则携带选中的地址返回给上一个界面
                Intent intent = getIntent();
                intent.putExtra("address", shippingAddress);
                setResult(RESULT_OK, intent);
                super.onBackPressed();
            } else {
                // 如果不是的话则进入编辑界面
                Intent intent = new Intent(this, EditShippingAddressActivity.class);
                intent.putExtra("edit_address", shippingAddress);
                intent.putExtra("is_first", false);
                startActivityForResult(intent, Constants.RequestCode.EDIT_SHIPPING_ADDRESS);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            new GetAddressTask().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'shipping_address_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.holder_layout)
        View holderLayout;
        @BindView(R.id.tv_buyer_name)
        TextView tvBuyerName;
        @BindView(R.id.tv_mobile_phone)
        TextView tvMobilePhone;
        @BindView(R.id.tv_default)
        TextView tvDefault;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.img_edit)
        ImageView imgEdit;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    private class GetAddressTask extends AsyncTask<String, Integer, JSONObject> {

        public GetAddressTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.SHIPPING_ADDRESS_LIST);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            shippingAddresses.clear();
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        JSONArray jsonArray = dataObject.optJSONArray("list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ShippingAddress shippingAddress = new ShippingAddress(jsonArray
                                        .optJSONObject(
                                        i));
                                shippingAddresses.add(shippingAddress);
                                // 在选择模式下,刷新后更新已选中的地址
                                if (selectMode && selectedAddress != null && selectedAddress.getId()
                                        .equals(shippingAddress.getId())) {
                                    selectedAddress = shippingAddress;
                                }
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            if (shippingAddresses.isEmpty()) {
                hideOkText();
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                TextView btnAction = (TextView) emptyView.findViewById(R.id.btn_empty_hint);
                btnAction.setText(R.string.label_add_address);

                imgEmptyHint.setVisibility(View.VISIBLE);
                imgNetHint.setVisibility(View.VISIBLE);
                textEmptyHint.setVisibility(View.VISIBLE);

                if (JSONUtil.isNetworkConnected(ShippingAddressListActivity.this)) {
                    imgNetHint.setVisibility(View.GONE);
                    textEmptyHint.setText(emptyHintId);
                    imgEmptyHint.setImageResource(emptyHintDrawableId);
                    btnAction.setVisibility(View.VISIBLE);
                } else {
                    btnAction.setVisibility(View.GONE);
                    imgEmptyHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.net_disconnected);
                }

                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShippingAddressListActivity.this,
                                EditShippingAddressActivity.class);
                        if (shippingAddresses.isEmpty()) {
                            intent.putExtra("is_first", true);
                        }
                        startActivityForResult(intent, Constants.RequestCode.EDIT_SHIPPING_ADDRESS);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else {
                showOkText();
            }

            isLoad = false;
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_SHIPPING_ADDRESS:
                    if (data != null) {
                        if (!deletedSelectedAddress) {
                            deletedSelectedAddress = data.getBooleanExtra
                                    ("deleted_selected_address",
                                    false);
                        }

                        if (data.getBooleanExtra("changed", false)) {
                            progressBar.setVisibility(View.VISIBLE);
                            new GetAddressTask().executeOnExecutor(Constants.LISTTHEADPOOL);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 从以存储的文件中读取地址区域文件
     */
    private void getAddressAreaData() {
        try {
            InputStream inputStream = openFileInput(Constants.ADDRESS_AREA_FILE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }

            JSONArray addressAreaArray = new JSONArray(stream.toString());
            parseAddressArea(addressAreaArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseAddressArea(JSONArray addressAreaArray) {
        if (addressAreaArray != null && addressAreaArray.length() > 0) {
            areaLists = new ArrayList<>();

            for (int i = 0; i < addressAreaArray.length(); i++) {
                JSONObject firstLevelAreaObj = addressAreaArray.optJSONObject(i);
                AddressArea firstLevelArea = new AddressArea(firstLevelAreaObj);
                areaLists.add(firstLevelArea);

                // 下面开始解析省市区相互对应的级联map
                ArrayList<ArrayList<String>> secondLevelArrayLists = new ArrayList<>();
                LinkedHashMap<String, ArrayList<String>> secondLevelMap = new LinkedHashMap<>();

                if (!firstLevelAreaObj.isNull("children")) {
                    JSONArray secondLevelAreaArray = firstLevelAreaObj.optJSONArray("children");
                    if (secondLevelAreaArray != null && secondLevelAreaArray.length() > 0) {
                        for (int j = 0; j < secondLevelAreaArray.length(); j++) {
                            JSONObject secondLevelAreaObj = secondLevelAreaArray.optJSONObject(j);
                            AddressArea secondLevelArea = new AddressArea(secondLevelAreaObj);
                            ArrayList<String> thirdLevelList = new ArrayList<>();
                            if (!secondLevelAreaObj.isNull("children")) {
                                JSONArray thirdLevelAreaArray = secondLevelAreaObj.optJSONArray(
                                        "children");
                                if (thirdLevelAreaArray != null && thirdLevelAreaArray.length() >
                                        0) {
                                    for (int k = 0; k < thirdLevelAreaArray.length(); k++) {
                                        JSONObject thirdLevelObj = thirdLevelAreaArray
                                                .optJSONObject(
                                                k);
                                        AddressArea thirdLevelArea = new AddressArea(thirdLevelObj);

                                        thirdLevelList.add(thirdLevelArea.getAreaName());
                                    }
                                }
                            }

                            secondLevelArrayLists.add(thirdLevelList);
                            secondLevelMap.put(secondLevelArea.getAreaName(), thirdLevelList);
                        }

                    }
                }
            }
        }
    }

    private String convertAreaIdToString(long id) {
        AddressArea selectedProvince;
        AddressArea selectedCity;
        AddressArea selectedArea;
        for (AddressArea province : areaLists) {
            if (province.getChildren()
                    .size() > 0) {
                for (AddressArea city : province.getChildren()) {
                    if (city.getId()
                            .equals(id)) {
                        selectedProvince = province;
                        selectedCity = city;
                        return selectedProvince.getAreaName() + selectedCity.getAreaName();
                    } else if (city.getChildren()
                            .size() > 0) {
                        for (AddressArea area : city.getChildren()) {
                            if (area.getId()
                                    .equals(id)) {
                                selectedProvince = province;
                                selectedCity = city;
                                selectedArea = area;
                                return selectedProvince.getAreaName() + selectedCity.getAreaName
                                        () + selectedArea.getAreaName();
                            }
                        }
                    }
                }
            }
        }

        return "";
    }
}
