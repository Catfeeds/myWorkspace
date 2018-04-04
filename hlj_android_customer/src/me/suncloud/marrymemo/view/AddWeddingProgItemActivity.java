package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kankan.wheel.widget.DateTimePickerView;
import kankan.wheel.widget.TimePickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPatchTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

public class AddWeddingProgItemActivity extends HljBaseNoBarActivity implements
        DateTimePickerView.OnPickerTimeListener {

    private TextView timeTv;
    private EditText contentEt;
    private EditText crewEt;
    private TextView contentCounter;
    private TextView crewCounter;
    private TextView programTitleTv;
    private ImageView programTitleArrow;
    private ImageView timeArrow;
    private String timeStr;
    private String contentStr;
    private String crewStr;
    private int hour;
    private int minute;

    private String[] weddingProgramTitles;
    private long[] weddingProgramIds;
    private int belongGroupPosition; // 记录编辑的组项目，返回的时候自动打开并定位到对应的item
    private int childPosition;
    private String mode;
    private long programId;
    private long itemId;

    private Dialog progressDialog;
    private Dialog dialog;
    public static final int CONTENT_LIMIT = 300;
    public static final int CREW_LIMIT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wedding_prog_item);
        setDefaultStatusBarPadding();

        timeTv = (TextView) findViewById(R.id.plan_time);
        contentEt = (EditText) findViewById(R.id.plan_content);
        crewEt = (EditText) findViewById(R.id.plan_crew);
        programTitleTv = (TextView) findViewById(R.id.programs_title);
        programTitleArrow = (ImageView) findViewById(R.id.arrow_program_title);
        timeArrow = (ImageView) findViewById(R.id.arrow_time);
        contentCounter = (TextView) findViewById(R.id.content_counter);
        crewCounter = (TextView) findViewById(R.id.crew_counter);

        belongGroupPosition = getIntent().getIntExtra("group_position", 0);
        childPosition = getIntent().getIntExtra("child_position", 0);
        mode = getIntent().getStringExtra("mode");
        programId = getIntent().getLongExtra("program_id", 0);
        weddingProgramTitles = getIntent().getStringArrayExtra("wedding_program_titles");
        weddingProgramIds = getIntent().getLongArrayExtra("wedding_program_ids");

        if (!JSONUtil.isEmpty(mode) && mode.equals("edit")) {
            // 编辑,先设置原来的字段
            contentStr = getIntent().getStringExtra("summary");
            crewStr = getIntent().getStringExtra("partners");
            hour = getIntent().getIntExtra("hour", 12);
            minute = getIntent().getIntExtra("minute", 0);
            itemId = getIntent().getLongExtra("item_id", 0);

            contentEt.setText(contentStr);
            crewEt.setText(crewStr);
        }
        if (weddingProgramTitles != null && weddingProgramTitles.length > 0) {
            programTitleTv.setText(weddingProgramTitles[belongGroupPosition]);
            programId = weddingProgramIds[belongGroupPosition];
        }

        timeTv.setText((hour > 9 ? hour : "0" + hour) + "" + " : " + (minute > 9 ? minute : "0" +
                minute));

        setUpTextCounter();
    }

    private void setUpTextCounter() {
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentCount = contentEt.getText()
                        .toString()
                        .length();
                if (currentCount >= CONTENT_LIMIT) {
                    contentCounter.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    contentCounter.setTextColor(getResources().getColor(R.color.colorGray));
                }

                contentCounter.setText(String.valueOf(CONTENT_LIMIT - currentCount));
            }
        });

        crewEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentCount = crewEt.getText()
                        .toString()
                        .length();
                if (currentCount >= CREW_LIMIT) {
                    crewCounter.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    crewCounter.setTextColor(getResources().getColor(R.color.colorGray));
                }

                crewCounter.setText(String.valueOf(CREW_LIMIT - currentCount));
            }
        });
    }

    public void onEditTime(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        timeArrow.startAnimation(AnimUtil.getAnimArrowUp(this));

        dialog = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        TimePickerView timePickerView = (TimePickerView) v.findViewById(R.id.picker);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) timePickerView
                .getLayoutParams();
        params.height = (int) (getResources().getDisplayMetrics().density * (24 * 8));
        timePickerView.setOnPickerTimeListener(this);
        dialog.setContentView(v);
        Window win = dialog.getWindow();
        WindowManager.LayoutParams params2 = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params2.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        TextView close = (TextView) v.findViewById(R.id.close);
        TextView confirm = (TextView) v.findViewById(R.id.confirm);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                timeArrow.startAnimation(AnimUtil.getAnimArrowDown(AddWeddingProgItemActivity
                        .this));
            }
        });

        dialog.show();
    }

    public void onSelectProgramTitle(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        programTitleArrow.startAnimation(AnimUtil.getAnimArrowUp(this));
        final ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < weddingProgramTitles.length; i++) {
            titles.add(weddingProgramTitles[i]);
        }

        dialog = new Dialog(this, R.style.BubbleDialogTheme);
        Point point = JSONUtil.getDeviceSize(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_phones, null);
        ListView listView = (ListView) dialogView.findViewById(R.id.contact_list);
        ContactsAdapter contactsAdapter = new ContactsAdapter(this, titles);
        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                belongGroupPosition = position;
                programId = weddingProgramIds[belongGroupPosition];

                dialog.cancel();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                programTitleArrow.startAnimation(AnimUtil.getAnimArrowDown(
                        AddWeddingProgItemActivity.this));
                programTitleTv.setText(titles.get(belongGroupPosition));
            }
        });

        dialog.setContentView(dialogView);
        Window win = dialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        params.width = Math.round(point.x * 3 / 4);
        win.setGravity(Gravity.CENTER);

        dialog.show();
    }

    public void onSave(View view) {
        contentStr = contentEt.getText()
                .toString();
        crewStr = crewEt.getText()
                .toString();
        if (JSONUtil.isEmpty(contentStr)) {
            Util.showToast(getString(R.string.msg_empty_plan_content), this);
            return;
        }
        if (JSONUtil.isEmpty(crewStr)) {
            crewStr = "";
        }
        if (contentStr.length() > CONTENT_LIMIT) {
            Util.showToast(getString(R.string.msg_over_content_count, CONTENT_LIMIT), this);
            return;
        }
        if (crewStr.length() > CREW_LIMIT) {
            Util.showToast(getString(R.string.msg_over_crew_count, CREW_LIMIT), this);
            return;
        }

        if (mode.equals("edit")) {
            onPatchItem();
        } else {
            onPostNewItem();
        }
    }

    /**
     * 提交创建子流程项目
     */
    private void onPostNewItem() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject itemJson = new JSONObject();
            itemJson.put("wedding_program_id", programId);
            itemJson.put("hour", hour);
            itemJson.put("minute", minute);
            itemJson.put("summary", contentStr);
            itemJson.put("partners", crewStr);
            jsonObject.put("item", itemJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                goBack();

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(AddWeddingProgItemActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_create_program,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.WEDDING_PROGRAM_ITEM_URL),
                jsonObject.toString());

    }

    private void onPatchItem() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", itemId);
            JSONObject itemJson = new JSONObject();
            itemJson.put("wedding_program_id", programId);
            itemJson.put("hour", hour);
            itemJson.put("minute", minute);
            itemJson.put("summary", contentStr);
            itemJson.put("partners", crewStr);
            jsonObject.put("item", itemJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPatchTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                goBack();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(AddWeddingProgItemActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_update_program_item,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.WEDDING_PROGRAM_ITEM_URL),
                jsonObject.toString());
    }

    private void goBack() {
        Intent intent = getIntent();
        intent.putExtra("mode", mode);
        intent.putExtra("group_position", belongGroupPosition);
        intent.putExtra("child_position", childPosition);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public void onPickerTime(int hour, int minute) {
        timeStr = (hour > 9 ? hour : "0" + hour) + "" + " : " + (minute > 9 ? minute : "0" +
                minute);

        this.hour = hour;
        this.minute = minute;

        timeTv.setText(timeStr);
    }
}
