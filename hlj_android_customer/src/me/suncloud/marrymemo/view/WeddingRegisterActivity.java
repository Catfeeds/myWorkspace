package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljmaplibrary.HljMap;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Register;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Util;


public class WeddingRegisterActivity extends HljBaseNoBarActivity implements ObjectBindAdapter
        .ViewBinder<Register>, View.OnClickListener, AdapterView.OnItemClickListener {

    @Override
    public String pageTrackTagName() {
        return "婚姻登记处";
    }

    private PullToRefreshListView listView;
    private ArrayList<Register> registers;
    private City city;
    private TextView label_city;
    private View progressBar, empty;
    private ImageView mapView;
    private View footView;
    private ObjectBindAdapter<Register> adapter;
    private ArrayList<String> contactPhones;
    private Dialog contactDialog;
    private View bottomView;
    private View footerEmptyView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_register);
        setDefaultStatusBarPadding();
        bottomView = findViewById(R.id.bottom_layout);
        progressBar = findViewById(R.id.progressBar);
        empty = findViewById(R.id.empty);

        findViewById(R.id.city_layout).setOnClickListener(this);
        label_city = (TextView) findViewById(R.id.label_city);

        listView = (PullToRefreshListView) findViewById(R.id.list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        View headView = getLayoutInflater().inflate(R.layout.wedding_register_head, null);
        mapView = (ImageView) headView.findViewById(R.id.map);
        listView.getRefreshableView()
                .addHeaderView(headView);
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_3, null);
        footerEmptyView = footView.findViewById(R.id.empty_view);
        footView.findViewById(R.id.handin_error)
                .setOnClickListener(this);
        listView.getRefreshableView()
                .addFooterView(footView);
        registers = new ArrayList<>();
        contactPhones = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, registers, R.layout.wedding_register_item, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        city = Session.getInstance()
                .getMyCity(this);
        if (city.getId() == 0) {
            city = new City(78l, getString(R.string.label_defaultcity));
        }
        label_city.setText(Util.maxEmsText(city.getName(), 5));

        new GetWeddingRegisterTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_WEDDING_REGISTER_URL,
                        String.valueOf(city.getId()),
                        "1",
                        "99")));
        new GetRegisterInfo().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_REGISTER_INFO_URL,
                        city.getId())));


    }

    @Override
    public void setViewValue(View view, final Register register, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.label_name = (TextView) view.findViewById(R.id.label_name);
            holder.label_address = (TextView) view.findViewById(R.id.label_address);
            holder.label_time = (TextView) view.findViewById(R.id.label_time);
            holder.btn_contact = view.findViewById(R.id.btn_contact);
            holder.btn_go_here = view.findViewById(R.id.btn_go_here);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.label_name.setText(register.getName());
        holder.label_address.setText(register.getAddress());
        holder.btn_go_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register.getLatitude() > 0 && register.getLongitude() > 0) {
                    String address = register.getAddress();
                    try {
                        Uri url = Uri.parse("geo:" + "0,0" + "?q=" + address);
                        Intent intent = new Intent(Intent.ACTION_VIEW, url);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(WeddingRegisterActivity.this,
                                R.string.label_no_map,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
        holder.label_time.setText(register.getBusiness_time());
        holder.btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray contactArray = register.getContacts();
                if (contactArray.length() > 0) {
                    contactPhones.clear();
                    for (int i = 0; i < contactArray.length(); i++) {
                        try {
                            String str = (String) contactArray.get(i);
                            contactPhones.add(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    callUp();
                }
            }
        });
    }

    private void callUp() {
        if (contactPhones.size() == 1) {
            String phone = contactPhones.get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                } catch (Exception ignored) {
                }
            }
            return;
        }

        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }

        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            Point point = JSONUtil.getDeviceSize(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_contact_phones, null);
            ListView listView = (ListView) view.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactPhones);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                        } catch (Exception e) {

                        }
                    }
                }
            });
            contactDialog.setContentView(view);
            Window win = contactDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }

        contactDialog.show();
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    public void feedback() {
        if (Util.loginBindChecked(WeddingRegisterActivity.this,
                Constants.Login.WEDDING_REGISTER_LOGIN)) {
            progressBar.setVisibility(View.VISIBLE);
            CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_layout:
                Intent intent = new Intent(this, CityListActivity.class);
                intent.putExtra("resultCity", true);
                intent.putExtra("nonNull", true);
                intent.putExtra("city", city);
                startActivityForResult(intent, Constants.RequestCode.CITY_CHANGE);
                overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
                break;
            case R.id.handin_error:
                feedback();
                break;
        }

    }


    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            label_city.setText(Util.maxEmsText(city.getName(), 5));
            adapter.notifyDataSetChanged();
            empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if (registers.isEmpty()) {
                listView.getRefreshableView()
                        .setSelection(0);
            }
            new GetWeddingRegisterTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.GET_WEDDING_REGISTER_URL,
                            String.valueOf(city.getId()),
                            "1",
                            "999")));
            new GetRegisterInfo().executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.GET_REGISTER_INFO_URL,
                            city.getId())));
        }
    }


    public void onRegisterUrl(View view) {
        if (!JSONUtil.isEmpty(path)) {
            Intent intent = new Intent(this, HljWebViewActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.CITY_CHANGE:
                    City city = (City) data.getSerializableExtra("city");
                    if (city != null) {
                        cityRefresh(city);
                    }
                    break;
                case Constants.Login.WEDDING_REGISTER_LOGIN:
                    feedback();
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Register register = (Register) parent.getAdapter()
                .getItem(position);
        if (register != null) {
            if (register.getLatitude() > 0 && register.getLongitude() > 0) {
                Intent intent = new Intent(WeddingRegisterActivity.this, NavigateMapActivity.class);
                LatLng latLng = HljMap.convertBDPointToAMap(this,
                        register.getLatitude(),
                        register.getLongitude());
                intent.putExtra(NavigateMapActivity.ARG_TITLE, register.getName());
                intent.putExtra(NavigateMapActivity.ARG_ADDRESS, register.getAddress());
                intent.putExtra(NavigateMapActivity.ARG_LATITUDE, latLng.latitude);
                intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, latLng.longitude);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    private class ViewHolder {
        TextView label_name;
        TextView label_address;
        TextView label_time;
        View btn_contact;
        View btn_go_here;
    }

    private class GetRegisterInfo extends AsyncTask<String, Object, JSONObject> {

        private String url;

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(Constants.getAbsUrl(String.format(Constants.HttpPath
                            .GET_REGISTER_INFO_URL,
                    city.getId())))) {
                if (jsonObject != null) {
                    path = JSONUtil.getString(jsonObject, "url");
                }
                if (JSONUtil.isEmpty(path)) {
                    bottomView.setVisibility(View.GONE);
                    footerEmptyView.setVisibility(View.GONE);
                } else {
                    bottomView.setVisibility(View.VISIBLE);
                    footerEmptyView.setVisibility(View.VISIBLE);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private class GetWeddingRegisterTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            registers.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Register register = new Register(result.optJSONObject(i));
                        registers.add(register);
                    }
                    Point point = JSONUtil.getDeviceSize(WeddingRegisterActivity.this);

                    int mapWidth = Math.round(point.x);
                    int mapHeight = Math.round(mapWidth * 9 / 16);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mapView
                            .getLayoutParams();
                    params.height = mapHeight;
                    if (registers.size() > 0) {
                        mapView.setOnClickListener(WeddingRegisterActivity.this);
                        List<LatLng> latLngList = new ArrayList<>();
                        for (int i = 0; i < registers.size(); i++) {
                            latLngList.add(HljMap.convertBDPointToAMap(WeddingRegisterActivity.this,
                                    registers.get(i)
                                            .getLatitude(),
                                    registers.get(i)
                                            .getLongitude()));
                        }
                        String mapUrl = HljMap.getAmapUrlWithMultiMarkers(latLngList,
                                mapWidth,
                                mapHeight);
                        Glide.with(WeddingRegisterActivity.this)
                                .load(mapUrl)
                                .apply(new RequestOptions().centerInside())
                                .into(mapView);
                    }

                }

            }
            if (registers.isEmpty()) {
                footView.setVisibility(View.GONE);
            } else {
                footView.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
            empty.setVisibility(View.GONE);
            if (registers.isEmpty()) {
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

                imgEmptyHint.setVisibility(View.VISIBLE);
                imgNetHint.setVisibility(View.VISIBLE);
                textEmptyHint.setVisibility(View.VISIBLE);

                if (JSONUtil.isNetworkConnected(WeddingRegisterActivity.this)) {
                    imgNetHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.hint_no_wedding_register);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.net_disconnected);
                }

            }
            super.onPostExecute(result);
        }
    }
}