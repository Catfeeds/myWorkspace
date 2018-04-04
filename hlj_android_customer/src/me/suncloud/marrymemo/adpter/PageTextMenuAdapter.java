package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.FlowColorLayout;
import me.suncloud.marrymemo.widget.PageTextView;
import me.suncloud.marrymemo.widget.TextViewContainerView;

/**
 * Created by Suncloud on 2016/5/26.
 */
public class PageTextMenuAdapter extends PagerAdapter implements AdapterView.OnItemClickListener,
        ObjectBindAdapter.ViewBinder<Font>, FlowColorLayout.OnChildCheckedChangeListener {


    private int colorMargin;
    private int logoHeight;
    private int circleSize;
    private int defaultColor;
    private String fontName;
    private int color;
    private ArrayList<Font> fonts;
    private RadioButton defaultColorView;
    private ObjectBindAdapter<Font> fontAdapter;
    private PageTextView textView;
    private FlowColorLayout colorLayout;
    private TextViewContainerView textViewContainerView;

    public PageTextMenuAdapter(Context context) {
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        colorMargin = Math.round(2 * dm.density);
        logoHeight = Math.round(30 * dm.density);
        circleSize = Math.round(30 * dm.density);
    }

    public void setTextView(TextViewContainerView textViewContainerView) {
        this.textViewContainerView = textViewContainerView;
        textView = textViewContainerView.getTextView();
        color = textView.getTextColor();
        TextHoleV2 textHole = textView.getTextHole();
        if (textHole != null) {
            fontName = textHole.getFontName();
            if (fonts != null) {
                for (Font font : fonts) {
                    if (JSONUtil.isEmpty(fontName)) {
                        font.setCheck(JSONUtil.isEmpty(font.getName()));
                    } else {
                        font.setCheck(fontName.equals(font.getName()));
                    }

                }
            }
            defaultColor = textHole.getDefaultColor();
            if (defaultColorView != null) {
                if (defaultColor != 0 && defaultColor != 0xffffffff) {
                    defaultColorView.setTag(defaultColor);
                    defaultColorView.setTextColor(defaultColor);
                }
            }
        }
        if (colorLayout != null && colorLayout.getChildCount() > 0) {
            for (int i = 0, size = colorLayout.getChildCount(); i < size; i++) {
                RadioButton child = (RadioButton) colorLayout.getChildAt(i);
                child.setChecked((int) child.getTag() == color);
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView;
        if (position == 0) {
            ListView listView = (ListView) View.inflate(container.getContext(),
                    R.layout.page_menu_font_view,
                    null);
            if (fonts == null) {
                fonts = new ArrayList<>(CardResourceUtil.getInstance()
                        .getFonts(container.getContext()));
                fonts.add(0, new Font(new JSONObject()));
            }
            for (Font font : fonts) {
                if (JSONUtil.isEmpty(fontName)) {
                    font.setCheck(JSONUtil.isEmpty(font.getUrl()));
                } else if (fontName.equals(font.getName())) {
                    font.setCheck(true);
                } else {
                    font.setCheck(false);
                }
            }
            listView.setOnItemClickListener(this);
            fontAdapter = new ObjectBindAdapter<>(container.getContext(),
                    fonts,
                    R.layout.page_font_item,
                    this);
            listView.setAdapter(fontAdapter);
            convertView = listView;
        } else {
            colorLayout = (FlowColorLayout) View.inflate(container.getContext(),
                    R.layout.page_menu_color_view,
                    null);
            int[] colors = container.getResources()
                    .getIntArray(R.array.font_colors);
            for (int color : colors) {
                RadioButton circleView = (RadioButton) View.inflate(container.getContext(),
                        R.layout.invitation_circle_item,
                        null);
                if (color == 0xffffffff) {
                    setWhiteDrawable(container.getResources(), color, circleView);
                } else {
                    setDrawable(color, circleView);
                }
                circleView.setTag(color);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(circleSize,
                        circleSize);
                colorLayout.addView2(circleView, layoutParams);
                circleView.setChecked(PageTextMenuAdapter.this.color == color);
            }
            if (defaultColor != 0 && defaultColor != 0xffffff) {
                defaultColorView = (RadioButton) View.inflate(container.getContext(),
                        R.layout.invitation_circle_item,
                        null);
                defaultColorView.setTag(defaultColor);
                defaultColorView.setText(R.string.label_default);
                defaultColorView.setTextColor(defaultColor);
                setDefaultDrawable(defaultColor, defaultColorView);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(circleSize,
                        circleSize);
                colorLayout.addView2(defaultColorView, layoutParams);
            }
            colorLayout.setOnChildCheckedChangeListener(this);
            convertView = colorLayout;
        }
        container.addView(convertView);
        return convertView;
    }

    //根据颜色自定义drawable
    public void setDrawable(int color, RadioButton checkBox) {
        //创建默认圆的drawable
        GradientDrawable normalGrad = new GradientDrawable();
        normalGrad.setColor(color);
        normalGrad.setCornerRadius(circleSize / 2);
        //画两个圆，一个空心圆，一个实心圆，嵌套
        GradientDrawable[] layers = new GradientDrawable[2];
        layers[0] = new GradientDrawable();
        layers[0].setColor(color);
        layers[0].setCornerRadius(circleSize / 2);
        layers[0].setStroke(colorMargin * 2, Color.parseColor("#ffffff"));
        layers[1] = new GradientDrawable();
        layers[1].setStroke(colorMargin, color);
        layers[1].setCornerRadius(90);
        LayerDrawable checkedGrad = new LayerDrawable(layers);
        checkBox.setBackground(newSelector(normalGrad, checkedGrad));
    }

    private void setWhiteDrawable(Resources resources, int color, RadioButton checkBox) {
        //创建默认圆的drawable
        //画两个圆，一个空心圆，一个实心圆，嵌套
        LayerDrawable checkedGrad = (LayerDrawable) resources.getDrawable(R.drawable
                .sp_circle_stroke_g);
        //白色特殊处理
        GradientDrawable normalWhiteGrad = new GradientDrawable();
        normalWhiteGrad.setColor(color);
        normalWhiteGrad.setCornerRadius(circleSize / 2);
        normalWhiteGrad.setStroke(colorMargin, Color.parseColor("#e7e7e7"));
        checkBox.setBackground(newSelector(normalWhiteGrad, checkedGrad));
    }

    private void setDefaultDrawable(int color, RadioButton checkBox) {
        GradientDrawable normalGrad = new GradientDrawable();
        normalGrad.setColor(Color.parseColor("#ffffff"));
        normalGrad.setCornerRadius(circleSize / 2);
        normalGrad.setStroke(colorMargin, color);
        checkBox.setBackground(normalGrad);
    }

    //设置选中状态
    public static StateListDrawable newSelector(Drawable normal, Drawable checked) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_pressed},
                normal);
        bg.addState(new int[]{android.R.attr.state_pressed}, checked);
        bg.addState(new int[]{android.R.attr.state_checked}, checked);
        return bg;
    }

    public void onFontDownLoadDone(Font font) {
        font.setDownloading(false);
        for (Font f : fonts) {
            f.setCheck(font == f);
        }
        fontAdapter.notifyDataSetChanged();
        textViewContainerView.onTypefaceChange(font);
    }

    public void onFontDownLoadCancelled(Font font) {
        font.setDownloading(false);
        font.setValue(0);
        fontAdapter.notifyDataSetChanged();
    }

    public void onFontProgressUpdate(Font font, int value) {
        if (value != font.getValue()) {
            font.setValue(value);
            fontAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Font font = (Font) parent.getAdapter()
                .getItem(position);
        if (font != null) {
            if (!JSONUtil.isEmpty(font.getUrl())) {
                if (font.isUnSaved(view.getContext())) {
                    font.setDownloading(true);
                    font.setValue(0);
                    fontAdapter.notifyDataSetChanged();
                    CardResourceUtil.getInstance()
                            .executeFontDownLoad(view.getContext(), font, PageTextMenuAdapter.this);
                    return;
                }
            }
            textViewContainerView.onTypefaceChange(font);
            for (Font f : fonts) {
                f.setCheck(font == f);
            }
            fontAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setViewValue(View view, Font font, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
        }
        holder.ivChecked.setVisibility(font.isCheck() ? View.VISIBLE : View.GONE);
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (JSONUtil.isEmpty(font.getUrl())) {
            holder.tvDefault.setVisibility(View.VISIBLE);
            holder.ivFontIcon.setVisibility(View.GONE);
            holder.progressbarLayout.setVisibility(View.GONE);
            holder.tvFontSize.setVisibility(View.GONE);
        } else {
            String url = JSONUtil.getImagePathForH(font.getLogo(), logoHeight);
            holder.tvDefault.setVisibility(View.GONE);
            holder.ivFontIcon.setVisibility(View.VISIBLE);
            ImageLoadUtil.loadImageView(view.getContext(),
                    url,
                    R.mipmap.icon_empty_image,
                    holder.ivFontIcon,
                    true);
            if (!font.isUnSaved(view.getContext())) {
                holder.progressbarLayout.setVisibility(View.GONE);
                holder.tvFontSize.setVisibility(View.GONE);
            } else if (font.isDownloading()) {
                holder.tvFontSize.setVisibility(View.GONE);
                holder.progressbarLayout.setVisibility(View.VISIBLE);
                holder.progressbar.setProgress(font.getValue());
            } else {
                holder.progressbarLayout.setVisibility(View.GONE);
                holder.tvFontSize.setVisibility(View.VISIBLE);
                if (font.getSize() > 1024) {
                    holder.tvFontSize.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat
                            (font.getSize() / 1024) + "M");
                } else {
                    holder.tvFontSize.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat
                            (font.getSize()) + "K");
                }
            }
        }

    }

    @Override
    public void onCheckedChange(View childView, int index) {
        textView.setTextColor((int) childView.getTag());
    }

    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.iv_checked)
        ImageView ivChecked;
        @BindView(R.id.tv_default)
        TextView tvDefault;
        @BindView(R.id.iv_font_icon)
        ImageView ivFontIcon;
        @BindView(R.id.progressbar)
        ProgressBar progressbar;
        @BindView(R.id.progressbar_layout)
        LinearLayout progressbarLayout;
        @BindView(R.id.tv_font_size)
        TextView tvFontSize;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
