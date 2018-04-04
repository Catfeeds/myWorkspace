package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.views.activities.LocationMapActivity;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

public class TextEditActivity extends HljBaseActivity {
    private EditText editText;
    private TextView location;
    private TextView textCount;
    private View locationLayout;
    private LinearLayout btnLocation;
    private String addr;
    private double lat;
    private double lon;
    private boolean showLocation;
    private int maxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        setOkText(R.string.action_ok);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        setTitle(title);
        String text = intent.getStringExtra("text");
        location = (TextView) findViewById(R.id.location_name);
        showLocation = intent.getBooleanExtra("showLocation", false);
        btnLocation = (LinearLayout) findViewById(R.id.location);
        locationLayout = findViewById(R.id.location_layout);
        if (showLocation) {
            addr = intent.getStringExtra("address");
            lat = intent.getDoubleExtra("lat", 0);
            lon = intent.getDoubleExtra("lon", 0);
            if (lat != 0 && lon != 0 && !JSONUtil.isEmpty(addr)) {
                btnLocation.setVisibility(View.GONE);
                locationLayout.setVisibility(View.VISIBLE);
                location.setText(addr);
            } else {
                locationLayout.setVisibility(View.INVISIBLE);
                btnLocation.setVisibility(View.VISIBLE);
            }

            locationLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TextEditActivity.this, LocationMapActivity.class);
                    intent.putExtra(LocationMapActivity.ARG_ADDRESS, addr);
                    intent.putExtra(LocationMapActivity.ARG_LATITUDE, lat);
                    intent.putExtra(LocationMapActivity.ARG_LONGITUDE, lon);
                    startActivityForResult(intent, Constants.RequestCode.LOCATION);
                }
            });
            findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    addr = "";
                    lat = 0;
                    lon = 0;
                    locationLayout.setVisibility(View.INVISIBLE);
                    btnLocation.setVisibility(View.VISIBLE);
                }
            });
        }
        btnLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextEditActivity.this, LocationMapActivity.class);
                startActivityForResult(intent, Constants.RequestCode.LOCATION);
            }
        });
        maxCount = getIntent().getIntExtra("textcount", 20);
        editText = (EditText) findViewById(R.id.edit);
        editText.addTextChangedListener(mTextWatcher);
        textCount = (TextView) findViewById(R.id.count);

        if (!JSONUtil.isEmpty(text)) {
            editText.setText(text);
        } else {
            textCount.setText(String.valueOf(maxCount));
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(null);
        super.onBackPressed();
    }


    @Override
    public void onOkButtonClick() {

        Intent intent = getIntent();
        if (showLocation) {
            intent.putExtra("address", addr);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
        }
        intent.putExtra("text",
                editText.getText()
                        .toString());
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(
                CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = editText.getSelectionStart();
            editEnd = editText.getSelectionEnd();
            editText.removeTextChangedListener(mTextWatcher);
            if (editStart == 0) {
                editStart = s.length();
                editEnd = s.length();
                if (editStart > maxCount * 2) {
                    editStart = maxCount * 2 + 1;
                }
            }
            while (Util.calculateLength(s.toString()) > maxCount) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
                if (editEnd > s.length()) {
                    editEnd = s.length();
                }
            }
            editText.setSelection(editStart);
            editText.addTextChangedListener(mTextWatcher);
            textCount.setText(String.valueOf(maxCount - Util.calculateLength(s.toString())));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.LOCATION) {
                addr = data.getStringExtra(LocationMapActivity.ARG_ADDRESS);
                lat = data.getDoubleExtra(LocationMapActivity.ARG_LATITUDE, 0);
                lon = data.getDoubleExtra(LocationMapActivity.ARG_LONGITUDE, 0);
                if (!JSONUtil.isEmpty(addr)) {
                    btnLocation.setVisibility(View.GONE);
                    locationLayout.setVisibility(View.VISIBLE);
                    location.setText(addr);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}