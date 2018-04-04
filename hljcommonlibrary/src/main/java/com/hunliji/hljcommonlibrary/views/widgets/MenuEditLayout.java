package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 带菜单按钮的编辑框
 * Created by wangtao on 2017/3/18.
 */

public class MenuEditLayout extends RelativeLayout {

    private ViewGroup menuLayout;
    protected EditText etContent;
    private SparseArray<View> childMenuArray;
    private List<ImageButton> imageButtons; //互斥的菜单按钮
    private boolean showMenuOnImmHide;
    protected MenuEditLayoutInterface menuLayoutInterface;
    private List<MenuVisibleListener> menuVisibleListeners;
    private OnBindMenuViewInterface onBindInterface;

    public MenuEditLayout(Context context) {
        super(context);
    }

    public MenuEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMenuEditLayoutInterface(MenuEditLayoutInterface menuEditLayoutInterface) {
        this.menuLayoutInterface = menuEditLayoutInterface;
    }

    public void addTextWatcherListener(TextWatcher textWatcher) {
        etContent.addTextChangedListener(textWatcher);
    }

    public void addMenuVisibleListener(MenuVisibleListener listener) {
        if (menuVisibleListeners == null) {
            menuVisibleListeners = new ArrayList<>();
        }
        menuVisibleListeners.add(listener);
    }

    public void setOnBindMenuViewInterface(OnBindMenuViewInterface onBindInterface) {
        this.onBindInterface = onBindInterface;
    }

    public void initView() {
        super.onFinishInflate();
        if (onBindInterface == null) {
            throw new NullPointerException("onBindInterface is null");
        }
        etContent =  onBindInterface.etContent();
        menuLayout =  onBindInterface.menuLayout();
        if(etContent==null||menuLayout==null){
            throw new RuntimeException("bind view is Null");
        }
        if (imageButtons == null) {
            imageButtons = new ArrayList<>();
        }
    }

    public void addMenu(int id, View menuView) {
        View view = findViewById(id);
        if (view != null) {
            if (imageButtons == null) {
                imageButtons = new ArrayList<>();
            }
            if (view instanceof ImageButton) {
                imageButtons.add((ImageButton) view);
            }
            if (childMenuArray == null) {
                childMenuArray = new SparseArray<>();
            }
            if(menuView!=null) {
                menuLayout.addView(menuView);
                childMenuArray.put(id, menuView);
            }
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeMenuVisible(v);
                }
            });
        }
    }

    private void onChangeMenuVisible(View v) {
        boolean isCurrentMenu = false;
        View menuView = null;
        if (childMenuArray != null) {
            menuView = childMenuArray.get(v.getId());
            isCurrentMenu = menuView != null && menuView.getVisibility() == VISIBLE;
        }
        if (menuLayout.getVisibility() == VISIBLE && isCurrentMenu) {
            if (menuLayoutInterface != null) {
                menuLayoutInterface.showImm();
            }
            onMenuButtonCheckedChange(v, false);
            return;
        }

        onMenuButtonCheckedChange(v, true);
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            View view = menuLayout.getChildAt(i);
            if (menuView == view) {
                if (view.getVisibility() != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (menuLayout.getVisibility() != VISIBLE) {
            onMenuVisibleChange(true);
            if (menuLayoutInterface != null && menuLayoutInterface.isImmShow()) {
                showMenuOnImmHide = true;
                menuLayoutInterface.hideImm();
            } else {
                menuLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 编辑栏菜单按钮图标点击变化
     *
     * @param v         当前点击
     * @param isChecked 是否选中按钮
     */
    protected void onMenuButtonCheckedChange(View v, boolean isChecked) {
        if (menuLayoutInterface == null) {
            return;
        }
        if (isChecked) {
            for (ImageButton imageButton : imageButtons) {
                if (imageButton != v) {
                    menuLayoutInterface.onImageButtonChecked(imageButton, false);
                } else {
                    menuLayoutInterface.onImageButtonChecked(imageButton, true);
                }
            }
        } else if (v instanceof ImageButton) {
            menuLayoutInterface.onImageButtonChecked((ImageButton) v, false);
        }
    }


    /**
     * 键盘隐藏时由外部调用
     */
    public void onImmHide() {
        if (showMenuOnImmHide) {
            menuLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 键盘显示时由外部调用
     */
    public void onImmShow() {
        showMenuOnImmHide = false;
        hideMenu();
        etContent.requestFocus();
    }

    /**
     * 隐藏菜单
     */
    public void hideMenu() {
        if (menuLayout.getVisibility() == VISIBLE) {
            menuLayout.setVisibility(View.GONE);
            onMenuVisibleChange(false);
        }
        if (menuLayoutInterface != null) {
            for (ImageButton imageButton : imageButtons) {
                menuLayoutInterface.onImageButtonChecked(imageButton, false);
            }
        }
        requestLayout();
    }

    /**
     * 当前是否有菜单显示
     */
    public boolean isMenuShow() {
        return menuLayout.getVisibility() == VISIBLE;
    }

    private void onMenuVisibleChange(boolean isVisible) {
        if (menuVisibleListeners != null) {
            for (MenuVisibleListener listener : menuVisibleListeners) {
                listener.onMenuVisibleChanged(isVisible);
            }
        }
    }

    public interface MenuVisibleListener {

        void onMenuVisibleChanged(boolean isVisible);
    }

    public interface MenuEditLayoutInterface {

        void onImageButtonChecked(ImageButton imageButton, boolean isChecked);

        void hideImm();

        void showImm();

        boolean isImmShow();
    }



    public interface OnBindMenuViewInterface {

        ViewGroup menuLayout();

        EditText etContent();
    }
}
