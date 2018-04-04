package com.hunliji.marrybiz.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.marrybiz.R;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * author:SUNCLOUD
 * 2015/4/27 14:00
 */
@RuntimePermissions
public class BaseSwipeBackActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setScrimColor(Color.TRANSPARENT);
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private long startTimeMillis;

    public void startActivityForResult(Intent intent, int requestCode) {
        if (System.currentTimeMillis() - startTimeMillis > 1000) {
            startTimeMillis = System.currentTimeMillis();
            super.startActivityForResult(intent, requestCode);
        }
    }

    public void hideKeyboard(View v) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    this.getCurrentFocus()
                            .getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void callUp(Uri uri) {
        if (!uri.toString()
                .contains(",")) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, uri);
            super.startActivity(phoneIntent);
        } else {
            BaseSwipeBackActivityPermissionsDispatcher.onCallUpWithCheck(this, uri);
        }
    }


    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void onCallUp(Uri uri) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, uri);
        super.startActivity(phoneIntent);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void onRationaleCallUP(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(com.hunliji.hljcommonlibrary.R.string.msg_permission_r_for_call_up___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaseSwipeBackActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
