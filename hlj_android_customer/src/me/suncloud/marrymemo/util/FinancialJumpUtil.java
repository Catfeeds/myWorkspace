package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljtrackerlibrary.TrackerHelper;

import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.wallet.FinancialProduct;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.view.AijiaWebViewActivity;
import me.suncloud.marrymemo.view.kepler.HljKeplerActivity;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by werther on 6/27/16.
 */
public class FinancialJumpUtil {

    public static void jumpFinancialProductWithNewTask(
            final Activity activity,
            @Nullable final View progressBar,
            FinancialProduct product,
            int position) {
        final RoundProgressDialog roundProgressDialog = new RoundProgressDialog(activity,
                R.style.BubbleDialogTheme);
        roundProgressDialog.setCancelable(false);
        roundProgressDialog.setCanceledOnTouchOutside(false);
        Intent intent = null;
        String type = product.getTargetType();
        String url = product.getUrl();
        TrackerHelper.hitFinancialProduct(activity, product.getId(), type, url, position);
        switch (type) {
            case "AiJia":
                intent = new Intent(activity, AijiaWebViewActivity.class);
                intent.putExtra("path",
                        Constants.getAbsUrl(Constants.HttpPath.AIJIA_FINANCIAL_USER_CENTER));
                break;
            case "common":
                intent = new Intent(activity, HljWebViewActivity.class);
                intent.putExtra("path", url);
                break;
            case "LiangHuaPai":
                if (Util.loginBindCheckedWithNewTask(activity, 0)) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        roundProgressDialog.show();
                    }
                    new HttpGetTask(new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            } else if (roundProgressDialog != null) {
                                roundProgressDialog.dismiss();
                            }
                            JSONObject jsonObject = (JSONObject) obj;
                            String url = jsonObject.optString("data");
                            if (!JSONUtil.isEmpty(url)) {
                                Intent intent = new Intent(activity, HljWebViewActivity.class);
                                intent.putExtra("path", url);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            } else if (roundProgressDialog != null) {
                                roundProgressDialog.dismiss();
                            }
                        }
                    }).executeOnExecutor(Constants.INFOTHEADPOOL,
                            Constants.getHttpsUrl(Constants.HttpPath.LIANG_HUA_PAI_URL));
                }
                break;
            case "PingAnPuHui":
                if (Util.loginBindChecked(activity)) {
                    intent = new Intent(activity, HljKeplerActivity.class);
                }
            default:
                break;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }
}