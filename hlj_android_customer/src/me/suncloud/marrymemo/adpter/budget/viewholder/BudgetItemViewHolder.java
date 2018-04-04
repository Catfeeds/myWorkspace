package me.suncloud.marrymemo.adpter.budget.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.BudgetCpmWorkAdapter;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.model.budget.BudgetCategoryListWrapper;
import me.suncloud.marrymemo.util.NewBudgetUtil;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by jinxin on 2017/11/22 0022.
 */

public class BudgetItemViewHolder extends BaseViewHolder {

    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.type_item_divider)
    View typeItemDivider;
    @BindView(R.id.item_type)
    TextView itemType;
    @BindView(R.id.type_layout)
    LinearLayout typeLayout;
    @BindView(R.id.budget_icon)
    ImageView budgetIcon;
    @BindView(R.id.budget_name)
    TextView budgetName;
    @BindView(R.id.budget_money)
    EditText budgetMoney;
    @BindView(R.id.budget_edit)
    ImageView budgetEdit;
    @BindView(R.id.category_layout)
    RelativeLayout categoryLayout;
    @BindView(R.id.cpm_recycler_view)
    RecyclerView cpmRecyclerView;
    @BindView(R.id.cpm_layout)
    LinearLayout cpmLayout;

    private int logoSize;
    private Context mContext;
    private BudgetCpmWorkAdapter cpmWorkAdapter;
    private List<Work> cpmWorks;
    private onBudgetItemViewListener onBudgetItemViewListener;

    public BudgetItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        logoSize = CommonUtil.dp2px(mContext, 30);
        cpmWorks = new ArrayList<>();

        initWidget();
    }

    private void initWidget() {
        cpmRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, HORIZONTAL, false));
        cpmWorkAdapter = new BudgetCpmWorkAdapter(mContext, cpmWorks);
        cpmRecyclerView.setAdapter(cpmWorkAdapter);
    }

    public void setOnBudgetItemViewListener(
            onBudgetItemViewListener onBudgetItemViewListener) {
        this.onBudgetItemViewListener = onBudgetItemViewListener;
    }

    @Override
    protected void setViewData(
            Context mContext, Object item, int position, int viewType) {
    }

    public void setBudgetItem(
            BudgetCategoryListWrapper wrapper,
            List<BudgetCategory> allCategory,
            List<Work> cpmWorks,
            int position) {
        if (wrapper == null) {
            return;
        }
        boolean showDivider = position != 0 && wrapper.getCollectPosition() == 0;
        typeItemDivider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
        if (wrapper.isShowHeader()) {
            typeLayout.setVisibility(View.VISIBLE);
            BudgetCategory p = NewBudgetUtil.getInstance()
                    .getCategoryById(allCategory, wrapper.getPid());
            if (p != null) {
                itemType.setText(p.getTitle());
            }
        } else {
            typeLayout.setVisibility(View.GONE);
        }
        BudgetCategory category = wrapper.getBudgetCategory();
        if (category == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(category.getIcon())
                        .width(logoSize)
                        .height(logoSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(budgetIcon);
        budgetMoney.setTag(category);
        budgetMoney.addTextChangedListener(new OnEditListener(budgetMoney));
        budgetMoney.setText(category.getMoney() <= 0 ? null : CommonUtil.formatDouble2String(
                category.getMoney()));
        budgetMoney.setSelection(budgetMoney.getText().length());
        budgetName.setText(category.getTitle());
        if (category.getTitle()
                .equals(BudgetCategory.CPM_TITLE)) {
            if (!CommonUtil.isCollectionEmpty(cpmWorks)) {
                cpmLayout.setVisibility(View.VISIBLE);
                this.cpmWorks.clear();
                this.cpmWorks.addAll(cpmWorks);
                cpmRecyclerView.setFocusable(false);
            }else{
                cpmLayout.setVisibility(View.GONE);
            }
            cpmWorkAdapter.notifyDataSetChanged();
        } else {
            cpmLayout.setVisibility(View.GONE);
        }
    }


    class OnEditListener implements TextWatcher {
        private EditText editText;
        private boolean isInit;

        public OnEditListener(EditText editText) {
            this.editText = editText;
            isInit = false;
        }

        @Override
        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(
                CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isInit) {
                isInit = !isInit;
                return;
            }
            BudgetCategory category = (BudgetCategory) editText.getTag();
            double oldMoney = category.getMoney();
            if (category != null && category.getPid() != 0) {
                if (s != null && s.length() > 0) {
                    double money = Double.parseDouble(s.toString());
                    if (money < 0) {
                        Toast.makeText(mContext,
                                mContext.getString(R.string.label_error_money),
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    category.setMoney(money);
                } else {
                    category.setMoney(0);
                }
            }
            double cateGoryMoney = category.getMoney();
            editText.setSelection(s.length());
            editText.getSelectionStart();

            if (onBudgetItemViewListener != null) {
                onBudgetItemViewListener.onEditTextChanged(category, oldMoney, cateGoryMoney);
            }
        }
    }

    public interface onBudgetItemViewListener {
        void onEditTextChanged(BudgetCategory category, double oldMoney, double categoryMoney);
    }
}
