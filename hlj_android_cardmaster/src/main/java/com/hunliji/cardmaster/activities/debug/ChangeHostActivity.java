package com.hunliji.cardmaster.activities.debug;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.BuildConfig;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.R;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangtao on 2018/3/14.
 */

@Route(path = RouterPath.IntentPath.Customer.Debug.CHANGE_HOST)
public class ChangeHostActivity extends HljBaseActivity {

    public enum HOSTS {

        正式("http://www.hunliji.com/"),
        测试("http://dev.hunliji.com:" + BuildConfig.FEATURE_PORT + "/"),
        PHP7("http://www7.hunliji.com/"),
        CURRENT();

        String hljHost;

        HOSTS() {
        }

        HOSTS(String hljHost) {
            this.hljHost = hljHost;
        }

        public String getHljHost() {
            return hljHost;
        }
    }

    @BindView(R.id.btn_config_official)
    Button btnConfigOfficial;
    @BindView(R.id.btn_config_test)
    Button btnConfigTest;
    @BindView(R.id.btn_config_www7)
    Button btnConfigWww7;
    @BindView(R.id.tv_http_host)
    EditText tvHttpHost;
    @BindView(R.id.tv_http_port)
    EditText tvHttpPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_host);
        ButterKnife.bind(this);
        setOkText("确认");
        initData();
    }

    private void initData() {
        HOSTS.CURRENT.hljHost = Constants.HOST;
        initHostView(HOSTS.CURRENT);
    }

    private void initHostView(HOSTS hosts) {
        setHost(hosts.hljHost, tvHttpHost, tvHttpPort);
    }

    private void setHost(String path, EditText etHost, EditText etPort) {
        Uri uri = Uri.parse(path);
        String scheme = uri.getScheme();
        int port = uri.getPort();
        etHost.setText(uri.getHost());
        if (scheme.equals("http") && (port < 0 || port == 80)) {
            etPort.setHint("80");
            etPort.setText(null);
        } else if (scheme.equals("https") && (port < 0 || port == 443)) {
            etPort.setHint("443");
            etPort.setText(null);
        } else {
            etPort.setText(String.valueOf(port));
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        String hljHost = getUri("http", tvHttpHost, tvHttpPort);
        SPUtils.put(this, "HOST", hljHost);
        Constants.setHOST(hljHost);
        HljHttp.setHOST(Constants.HOST);
        ToastUtil.showToast(this, "服务器地址修改成功", 0);
        onBackPressed();
    }

    private String getUri(String scheme, EditText etHost, EditText etPort) {
        String host = etHost.getText()
                .toString();
        boolean isDefaultPort = false;
        int port = -1;
        if (etPort.length() > 0) {
            try {
                port = Integer.valueOf(etPort.getText()
                        .toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (scheme.equals("http") && (port < 0 || port == 80)) {
            isDefaultPort = true;
        } else if (scheme.equals("https") && (port < 0 || port == 443)) {
            isDefaultPort = true;
        }
        if (isDefaultPort) {
            return scheme + "://" + host + "/";
        } else {
            return scheme + "://" + host + ":" + port + "/";
        }
    }


    @OnClick(R.id.btn_config_official)
    public void onBtnConfigOfficialClicked() {
        initHostView(HOSTS.正式);
    }

    @OnClick(R.id.btn_config_test)
    public void onBtnConfigTestClicked() {
        initHostView(HOSTS.测试);
    }

    @OnClick(R.id.btn_config_www7)
    public void onBtnConfigWww7Clicked() {
        initHostView(HOSTS.PHP7);
    }
}
