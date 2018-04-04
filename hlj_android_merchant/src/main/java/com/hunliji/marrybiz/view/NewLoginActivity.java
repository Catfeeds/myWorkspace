package com.hunliji.marrybiz.view;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.ClearableAutoCompleteTextView;
import com.hunliji.marrybiz.widget.ClearableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewLoginActivity extends HljBaseActivity {

    @BindView(R.id.img_account)
    ImageView imgAccount;
    @BindView(R.id.et_email_phone)
    ClearableAutoCompleteTextView etEmailPhone;
    @BindView(R.id.img_psw)
    ImageView imgPsw;
    @BindView(R.id.et_password)
    ClearableEditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forget_psw)
    TextView tvForgetPsw;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        ButterKnife.bind(this);

        setTextWatcher();
        setAutoTextEmailPhone();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        etEmailPhone.setText("");
        etPassword.setText("");
    }

    private void setTextWatcher() {
        etEmailPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imgAccount.setImageResource(R.drawable.icon_avatar_primary_45_45);
                    imgPsw.setImageResource(R.drawable.icon_lock_gray_40_45);
                }
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imgAccount.setImageResource(R.drawable.icon_avatar_gray_45_45);
                    imgPsw.setImageResource(R.drawable.icon_lock_primary_40_45);
                }
            }
        });
        etEmailPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnLogin.setEnabled(etEmailPhone.length() > 0 && etPassword.length() > 0);
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnLogin.setEnabled(etEmailPhone.length() > 0 && etPassword.length() > 0);
            }
        });
    }

    public void onLogin(View view) {
        if (TextUtils.isEmpty(etEmailPhone.getText()
                .toString())) {
            Toast.makeText(NewLoginActivity.this, R.string.msg_empty_account, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText()
                .toString())) {
            Toast.makeText(NewLoginActivity.this, R.string.msg_empty_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new LoginTask(etEmailPhone.getText()
                .toString(),
                etPassword.getText()
                        .toString()).execute();
    }

    public void onForgetPsw(View view) {
        Intent intent = new Intent(this, FindPswActivity.class);
        if (!JSONUtil.isEmpty(etEmailPhone.getText()
                .toString()) && Util.isMobileNO(etEmailPhone.getText()
                .toString())) {
            intent.putExtra("phone_number",
                    etEmailPhone.getText()
                            .toString());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public class LoginTask extends AsyncTask<String, Integer, JSONObject> {

        private String account;
        private String password;

        public LoginTask(String account, String password) {
            this.account = account;
            this.password = password;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            Map<String, Object> map = new HashMap<>();
            map.put("phone", account);
            String url = Constants.getAbsUrl(Constants.HttpPath.LOGIN_URL);
            map.put("password", password);
            try {
                String jsonStr = JSONUtil.postFormWithAttach(NewLoginActivity.this,
                        url,
                        map,
                        null,
                        null);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                int statusCode = jsonObject.optJSONObject("status")
                        .optInt("RetCode");
                String statusMsg = jsonObject.optJSONObject("status")
                        .optString("msg");
                if (statusCode == 0) {
                    JSONObject loginJsonObject = jsonObject.optJSONObject("data");
                    MerchantUser user = new MerchantUser(loginJsonObject);
                    if (user.getShopType() == MerchantUser.SHOP_TYPE_SERVICE || user.getShopType
                            () == MerchantUser.SHOP_TYPE_PRODUCT || user.getShopType() ==
                            MerchantUser.SHOP_TYPE_CAR) {
                        // 只有服务子账号,婚品账号和婚车账号才可以登陆
                        Session.getInstance()
                                .setCurrentUser(NewLoginActivity.this, loginJsonObject);

                        user = Session.getInstance()
                                .getCurrentUser(NewLoginActivity.this);
                        saveAccount(account);
                        if (JSONUtil.isEmpty(user.getPhone())) {
                            // 登陆成功,但没有设置手机号码登陆
                            Intent intent = new Intent(NewLoginActivity.this,
                                    LoginPhoneAlertActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                            finish();
                        } else {
                            Intent intent = new Intent(NewLoginActivity.this, HomeActivity.class);
                            intent.putExtra("is_login_done", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                            finish();
                        }
                    } else {
                        // 在登录界面弹出非法账号提示对话框
                        showAlertDialog(user.getShopType());
                    }
                } else {
                    Toast.makeText(NewLoginActivity.this, statusMsg, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(NewLoginActivity.this,
                        R.string.msg_fail_to_login,
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void showAlertDialog(int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(NewLoginActivity.this, R.style.BubbleDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_msg_single_button, null);
        TextView titleView = (TextView) dialogView.findViewById(R.id.dialog_msg_title);
        TextView contentView = (TextView) dialogView.findViewById(R.id.dialog_msg_content);
        titleView.setText(R.string.msg_after_login);
        String msgStr;
        if (type == MerchantUser.SHOP_TYPE_HOTEL) {
            msgStr = getString(R.string.msg_after_login_content_hotel);
        } else {
            msgStr = getString(R.string.msg_after_login_content_other);
        }
        contentView.setText(msgStr);
        Button confirmView = (Button) dialogView.findViewById(R.id.dialog_msg_confirm);
        confirmView.setText(R.string.label_confirm_text);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setContentView(dialogView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(NewLoginActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        params.height = Math.round(params.width * 256 / 380);
        window.setAttributes(params);
        dialog.show();
    }

    /**
     * 将这个email或phone存入自动完成的列表中
     *
     * @param account
     */
    private void saveAccount(String account) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_FILE,
                MODE_PRIVATE);
        ArrayList<String> emails = new ArrayList<>();
        String savedStr = sharedPreferences.getString("auto_account", "");
        boolean exists = false;
        if (!JSONUtil.isEmpty(savedStr)) {
            ArrayList<String> savedArray = new Gson().fromJson(savedStr,
                    new TypeToken<ArrayList<String>>() {}.getType());
            for (String saved : savedArray) {
                if (account.equals(saved)) {
                    exists = true;
                }
            }
            emails.addAll(savedArray);
        }
        if (!exists) {
            emails.add(account);
        }

        String jsonStr = new Gson().toJson(emails);

        sharedPreferences.edit()
                .putString("auto_account", jsonStr)
                .commit();

        // 存入最后一次登录的账号
        sharedPreferences.edit()
                .putString("auto_account_name", account)
                .commit();
    }

    private void setAutoTextEmailPhone() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_FILE,
                MODE_PRIVATE);
        ArrayList<String> emails = new ArrayList<>();
        String savedStr = sharedPreferences.getString("auto_account", "");
        String lastAccountName = sharedPreferences.getString("auto_account_name", "");
        etEmailPhone.setText(lastAccountName);
        if (!JSONUtil.isEmpty(savedStr)) {
            ArrayList<String> savedArray = new Gson().fromJson(savedStr,
                    new TypeToken<ArrayList<String>>() {}.getType());
            emails.addAll(savedArray);
        }
        if (Constants.DEBUG) {
            // 调试模式下, 填入几个常用的email
            emails.add("493070773@qq.com");
            emails.add("1059488083@qq.com");
            emails.add("2514127018@qq.com");
            emails.add("434757@qq.com");
            emails.add("1290146555@qq.com");
            emails.add("1603750070@qq.com");
        }
        if (emails.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.auto_email_dropdown_list_item,
                    emails);
            etEmailPhone.setAdapter(adapter);
            etEmailPhone.setThreshold(1);
        }
    }
}
