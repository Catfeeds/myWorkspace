package com.hunliji.marrybiz.adapter.merchantservice.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2018/1/29 0029.
 */

public class MerchantServerViewHolder extends BaseViewHolder<MerchantServer> {

    @BindView(R.id.divider)
    Space divider;
    @BindView(R.id.img_icon)
    RoundedImageView imgIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.layout_action)
    LinearLayout layoutAction;
    @BindView(R.id.line)
    View line;

    private MerchantServerViewHolderClickListener merchantServerViewHolderClickListener;
    private Context mContext;

    public MerchantServerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setMerchantServerViewHolderClickListener(MerchantServerViewHolderClickListener
                                                                 merchantServerViewHolderClickListener) {
        this.merchantServerViewHolderClickListener = merchantServerViewHolderClickListener;
    }

    public void setLineVisible(boolean show) {
        line.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void setViewData(
            Context mContext, MerchantServer merchantServer, int position, int viewType) {
        if (merchantServer == null) {
            return;
        }
        divider.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        BdProduct serverProduct = merchantServer.getProduct();
        tvName.setText(serverProduct == null ? null : serverProduct.getTitle());
        tvStatus.setText(getStatusString(merchantServer));
        tvStatus.setTextColor(getStatusTextColor(merchantServer));
        StringBuilder builder = new StringBuilder();
        if (merchantServer.getServerStart() != null) {
            builder.append(merchantServer.getServerStart()
                    .toString(HljCommon.DateFormat.DATE_FORMAT_SHORT1))
                    .append(" - ");
        }
        if (merchantServer.getServerEnd() != null) {
            builder.append(merchantServer.getServerEnd()
                    .toString(HljCommon.DateFormat.DATE_FORMAT_SHORT1));
        }
        tvDate.setText(builder.toString());
        List<ButtonAction> actionList = new ArrayList<>();
        if (serverProduct != null) {
            int iconRes = getImgRes(serverProduct.getId());
            if (iconRes != -1) {
                Glide.with(mContext)
                        .load(iconRes)
                        .into(imgIcon);
            }
            if (serverProduct.getId() == BdProduct.BAO_ZHENG_JIN || serverProduct.getId() ==
                    BdProduct.QI_JIAN_BAN) {
                actionList.add(new ButtonAction(ButtonAction.ACTION_LOOK));
                int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                if ((day > 0 && day <= 30L) || merchantServer.getStatus() == MerchantServer
                        .STATUS_OUT_DATE) {
                    //即将过期和过期
                    actionList.add(new ButtonAction(ButtonAction.ACTION_FEED));
                }
            } else if (serverProduct.getId() == BdProduct.ZHUAN_YE_BAN) {
                actionList.add(new ButtonAction(ButtonAction.ACTION_UPDATE));
            } else {
                if(serverProduct.getId() != BdProduct.XIAO_CHENG_XU){
                    actionList.add(new ButtonAction(ButtonAction.ACTION_USE));
                }
                actionList.add(new ButtonAction(ButtonAction.ACTION_LOOK));
                int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                if ((day > 0 && day <= 30L) || merchantServer.getStatus() == MerchantServer
                        .STATUS_OUT_DATE) {
                    //即将过期和过期
                    actionList.add(new ButtonAction(ButtonAction.ACTION_FEED));
                }
            }
        }

        for (int i = 0, childCount = layoutAction.getChildCount(); i < childCount; i++) {
            TextView tv = (TextView) layoutAction.getChildAt(i);
            if (i < actionList.size()) {
                ButtonAction action = actionList.get(i);
                setAction(tv, action, merchantServer);
            } else {
                tv.setVisibility(View.GONE);
            }
        }
    }

    private void setAction(
            TextView tv,
            final ButtonAction action,
            final MerchantServer merchantServer) {
        if (tv == null || action == null) {
            return;
        }
        tv.setVisibility(View.VISIBLE);
        tv.setText(action.getActionStr());
        tv.setTextColor(action.getActionTextColor());
        tv.setBackground(action.getActionBgRes());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (merchantServerViewHolderClickListener == null) {
                    return;
                }
                switch (action.getAction()) {
                    case ButtonAction.ACTION_LOOK:
                        merchantServerViewHolderClickListener.onLook(merchantServer);
                        break;
                    case ButtonAction.ACTION_FEED:
                        merchantServerViewHolderClickListener.onFeed(merchantServer);
                        break;
                    case ButtonAction.ACTION_USE:
                        merchantServerViewHolderClickListener.onUse(merchantServer);
                        break;
                    case ButtonAction.ACTION_UPDATE:
                        merchantServerViewHolderClickListener.onUpdate(merchantServer);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private int getImgRes(long id) {
        if (id == BdProduct.ZHUAN_YE_BAN) {
            return R.mipmap.icon_zhuan_ye_ban;
        } else if (id == BdProduct.BAO_ZHENG_JIN) {
            return R.mipmap.icon_bao_zhengz_jin;
        } else if (id == BdProduct.QI_JIAN_BAN) {
            return R.mipmap.icon_qi_jian_ban;
        } else if (id == BdProduct.XIAO_CHENG_XU) {
            return R.mipmap.icon_xiao_cheng_xu;
        } else if (id == BdProduct.TAO_CAN_RE_BIAO) {
            return R.mipmap.icon_tao_can_re_biao;
        } else if (id == BdProduct.DING_DAN_KE_TUI) {
            return R.mipmap.icon_ding_dan_ke_tui;
        } else if (id == BdProduct.SHANG_JIA_CHENG_NUO) {
            return R.mipmap.icon_shang_jia_cheng_nuo;
        } else if (id == BdProduct.DAO_DIAN_LI) {
            return R.mipmap.icon_dao_dian_li;
        } else if (id == BdProduct.DUO_DIAN_GUAN_LI) {
            return R.mipmap.icon_duo_dian_guan_li;
        } else if (id == BdProduct.TUI_JIAN_CHU_CHUANG) {
            return R.mipmap.icon_tui_jian_chu_chuang;
        } else if (id == BdProduct.ZHU_TI_MU_BAN) {
            return R.mipmap.icon_zhu_ti_mu_ban;
        } else if (id == BdProduct.WEI_GUAN_WANG) {
            return R.mipmap.icon_wei_guan_wang;
        } else if (id == BdProduct.HUO_DONG_WEI_CHUAN_DAN) {
            return R.mipmap.icon_huo_dong_wei_chuan_dan;
        } else if (id == BdProduct.QING_SONG_LIAO) {
            return R.mipmap.icon_qing_song_liao;
        } else if (id == BdProduct.TIAN_YAN_XI_TONG) {
            return R.mipmap.icon_tian_yan_xi_tong;
        } else if (id == BdProduct.YOU_HUI_JUAN) {
            return R.mipmap.icon_you_hui_juan;
        }
        return -1;
    }

    private String getStatusString(MerchantServer merchantServer) {
        if (merchantServer == null) {
            return null;
        }

        if (merchantServer.getProductId() == BdProduct.BAO_ZHENG_JIN) {
            return "已加入";
        }

        switch (merchantServer.getStatus()) {
            case MerchantServer.STATUS_IN_SERVICE:
                int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                if (day < 0) {
                    return null;
                } else if (day <= 30) {
                    return "即将到期";
                } else {
                    return "使用中";
                }
            case MerchantServer.STATUS_OUT_DATE:
                return "已到期";
            default:
                return null;
        }
    }

    private int getStatusTextColor(MerchantServer merchantServer) {
        if (merchantServer == null) {
            return mContext.getResources()
                    .getColor(R.color.colorPrimary);
        }
        if (merchantServer.getProductId() == BdProduct.BAO_ZHENG_JIN) {
            return mContext.getResources()
                    .getColor(R.color.colorPrimary);
        }
        switch (merchantServer.getStatus()) {
            case MerchantServer.STATUS_IN_SERVICE:
                int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                if (day < 0) {
                    return mContext.getResources()
                            .getColor(R.color.colorPrimary);
                } else if (day <= 30L) {
                    return mContext.getResources()
                            .getColor(R.color.colorAccent);
                } else {
                    return mContext.getResources()
                            .getColor(R.color.colorPrimary);
                }
            case MerchantServer.STATUS_OUT_DATE:
                return mContext.getResources()
                        .getColor(R.color.colorAccent);
            default:
                return mContext.getResources()
                        .getColor(R.color.colorPrimary);
        }
    }

    class ButtonAction {
        public final static int ACTION_LOOK = 1;
        public final static int ACTION_USE = 2;
        public final static int ACTION_FEED = 3;
        public final static int ACTION_UPDATE = 4;

        private int action;

        public ButtonAction(int action) {
            this.action = action;
        }

        public int getAction() {
            return action;
        }

        public String getActionStr() {
            switch (action) {
                case ACTION_LOOK:
                    return "查看";
                case ACTION_FEED:
                    return "续费";
                case ACTION_USE:
                    return "使用";
                case ACTION_UPDATE:
                    return "升级";
                default:
                    break;
            }
            return null;
        }

        public int getActionTextColor() {
            switch (action) {
                case ACTION_LOOK:
                case ACTION_USE:
                    return mContext.getResources()
                            .getColor(R.color.colorBlack3);
                case ACTION_FEED:
                case ACTION_UPDATE:
                    return mContext.getResources()
                            .getColor(R.color.colorPrimary);
                default:
                    break;
            }
            return mContext.getResources()
                    .getColor(R.color.colorBlack3);
        }

        public Drawable getActionBgRes() {
            switch (action) {
                case ACTION_LOOK:
                case ACTION_USE:
                    return mContext.getResources()
                            .getDrawable(R.drawable.sp_r12_stroke_line2);
                case ACTION_FEED:
                case ACTION_UPDATE:
                    return mContext.getResources()
                            .getDrawable(R.drawable.sp_r12_stroke_primary);
                default:
                    break;
            }
            return mContext.getResources()
                    .getDrawable(R.drawable.sp_r12_stroke_line2);
        }
    }

    public interface MerchantServerViewHolderClickListener {
        void onLook(MerchantServer server);//查看

        void onUse(MerchantServer server);//使用

        void onFeed(MerchantServer server);//续费

        void onUpdate(MerchantServer server);//升级
    }
}
