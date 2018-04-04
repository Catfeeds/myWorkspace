package me.suncloud.marrymemo.view.debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljpushlibrary.HljPush;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.BuildConfig;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2018/3/14.
 */

@Route(path = RouterPath.IntentPath.Customer.Debug.CHANGE_HOST)
public class ChangeHostActivity extends HljBaseActivity {

    public enum HOSTS {

        正式("http://www.hunliji.com/",
                "http://message.hunliji.com/",
                "https://message.hunliji.com:5051/",
                "http://notify.hunliji.com:8100/api/ws"),
        测试("http://dev.hunliji.com:" + BuildConfig.FEATURE_PORT + "/",
                "http://test.hunliji.com:8083/",
                "https://test.hunliji.com:5051/",
                "http://test.hunliji.com:8100/api/ws"),
        PHP7("http://www7.hunliji.com/",
                "http://www7.hunliji.com:7070/",
                "https://message.hunliji.com:5051/",
                "http://notify.hunliji.com:8100/api/ws"),
        CURRENT();

        String hljHost;
        String socketHost;
        String liveHost;
        String pushHost;

        HOSTS() {
        }

        HOSTS(
                String hljHost, String socketHost, String liveHost, String pushHost) {
            this.hljHost = hljHost;
            this.socketHost = socketHost;
            this.liveHost = liveHost;
            this.pushHost = pushHost;
        }

        public String getHljHost() {
            return hljHost;
        }

        public String getHttpsHost() {
            return hljHost.replace("http://","https://");
        }


        public String getLiveHost() {
            return liveHost;
        }

        public String getPushHost() {
            return pushHost;
        }

        public String getSocketHost() {
            return socketHost;
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
    @BindView(R.id.tv_ws_host)
    EditText tvWsHost;
    @BindView(R.id.tv_ws_port)
    EditText tvWsPort;
    @BindView(R.id.tv_live_host)
    EditText tvLiveHost;
    @BindView(R.id.tv_live_port)
    EditText tvLivePort;
    @BindView(R.id.tv_push_host)
    EditText tvPushHost;
    @BindView(R.id.tv_push_port)
    EditText tvPushPort;

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
        HOSTS.CURRENT.socketHost = WebSocket.SOCKET_HOST;
        HOSTS.CURRENT.liveHost = HljLive.getLiveHost();
        HOSTS.CURRENT.pushHost = HljPush.PUSH_HOST;
        initHostView(HOSTS.CURRENT);
    }

    private void initHostView(HOSTS hosts) {
        setHost(hosts.hljHost, tvHttpHost, tvHttpPort);
        setHost(hosts.socketHost, tvWsHost, tvWsPort);
        setHost(hosts.liveHost, tvLiveHost, tvLivePort);
        setHost(hosts.pushHost, tvPushHost, tvPushPort);
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
        String hljHttpsHost = getUri("https", tvHttpHost, tvHttpPort);
        String socketHost = getUri("http", tvWsHost, tvWsPort);
        String liveHost = getUri("https", tvLiveHost, tvLivePort);
        String pushHost = getUri("http", tvPushHost, tvPushPort) + "api/ws";
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        preferences.edit()
                .putString("HOST", hljHost)
                .putString("HTTPS_HOST", hljHttpsHost)
                .putString("SOCKET_HOST", socketHost)
                .putString("LIVE_HOST", liveHost)
                .putString("PUSH_HOST", pushHost)
                .apply();
        Constants.setHOST(hljHost);
        Constants.setHttpsHost(hljHttpsHost);
        WebSocket.setSocketHost(socketHost);
        HljLive.setLiveHost(liveHost);
        HljHttp.setHOST(Constants.HOST);
        HljPush.setPushHost(pushHost);
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
