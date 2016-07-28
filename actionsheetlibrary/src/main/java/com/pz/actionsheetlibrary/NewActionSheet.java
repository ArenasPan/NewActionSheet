package com.pz.actionsheetlibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * 新的IOS样式的ActionSheet菜单
 *
 * @author pz
 */
public class NewActionSheet extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * 点击背景
     */
    private static final int TAG_BG_CLICK = -2;

    /**
     * 点击取消
     */
    private static final int TAG_CANCEL_CLICK = -1;
    /**
     * 最多显示item的个数
     */
    private static final int MAX_ITEMS = 4;
    /**
     * 背景的TAG
     */
    private static final String BG_VIEW_TAG = "BG_VIEW_TAG";
    /**
     * 取消按钮的TAG
     */
    private static final String CANCEL_BTN_TAG = "CANCEL_BTN_TAG";
    /**
     * 位移动画的时间
     */
    private static final int TRANSLATE_DURATION = 300;
    /**
     * 透明度动画的时间
     */
    private static final int ALPHA_DURATION = 300;
    /**
     * 当前点击的标示
     */
    private int mCurrentClickTag = TAG_BG_CLICK;
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 总的视图
     */
    private View mParentView;

    /**
     * 容器
     */
    private LinearLayout mPanel;

    /**
     * 背景View
     */
    private View mBackground;

    /**
     * 列表视图
     */
    private ListView mListView;

    /**
     * 取消按钮
     */
    private Button mCancelBtn;

    /**
     * 属性
     */
    private Attributes mAttrs;

    /**
     * 所有的选项的名称数组
     */
    private List<String> mItems;

    /**
     *
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 监听
     */
    private NewActionSheet.MenuItemClickListener mListener;

    /**
     * 取消的名称
     */
    private String mCancelTitle = "";

    /**
     * 设置能不能点击空白处取消
     */
    private boolean mCancelableOnTouchOutside;

    /**
     * 是不是已经关闭
     */
    private boolean mDismissed = true;

    /**
     * 是不是取消
     */
    private boolean isCancel = true;

    /**
     * 适配器
     */
    private MyActionSheetAdapter mActionSheetAdapter;


    /**
     * 位移动画
     */
    private TranslateAnimation mTranslateOutAnimation;

    /**
     * 透明度动画
     */
    private AlphaAnimation mAlphaOutAnimation;

    /**
     * 识别码
     */
    private String mTag;


    /**
     * 构造函数
     *
     * @param context
     */
    public NewActionSheet(Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar);
        this.mContext = context;
        init();
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    /**
     * 初始化
     */
    private void init() {

        //初始化Windows
        initWindow();

        //初始化视图
        initView();
    }

    /**
     * 初始化Window
     */
    private void initWindow() {
        getWindow().setGravity(Gravity.BOTTOM);
        Drawable drawable = new ColorDrawable();
        drawable.setAlpha(0);// 设置透明背景
        getWindow().setBackgroundDrawable(drawable);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) mContext).getCurrentFocus();
            if (focusView != null)
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }

        mAttrs = readAttributes();
        mParentView = createView();
        createListView();
        mBackground.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());
    }


    /**
     * 读取属性
     *
     * @return
     */
    private Attributes readAttributes() {
        Attributes attrs = new Attributes(mContext);
        TypedArray a = mContext.getTheme().obtainStyledAttributes(null, R.styleable.ActionSheet,
                R.attr.actionSheetStyle, 0);

        Drawable background = a.getDrawable(R.styleable.ActionSheet_actionSheetBackground);
        if (background != null)
            attrs.background = background;

        Drawable cancelButtonBackground = a.getDrawable(R.styleable.ActionSheet_cancelButtonBackground);
        if (cancelButtonBackground != null)
            attrs.cancelButtonBackground = cancelButtonBackground;

        Drawable otherButtonTopBackground = a.getDrawable(R.styleable.ActionSheet_otherButtonTopBackground);
        if (otherButtonTopBackground != null)
            attrs.otherButtonTopBackground = otherButtonTopBackground;

        Drawable otherButtonMiddleBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
        if (otherButtonMiddleBackground != null)
            attrs.otherButtonMiddleBackground = otherButtonMiddleBackground;

        Drawable otherButtonBottomBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonBottomBackground);
        if (otherButtonBottomBackground != null)
            attrs.otherButtonBottomBackground = otherButtonBottomBackground;

        Drawable otherButtonSingleBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonSingleBackground);
        if (otherButtonSingleBackground != null)
            attrs.otherButtonSingleBackground = otherButtonSingleBackground;

        attrs.cancelButtonTextColor = a.getColor(R.styleable.ActionSheet_cancelButtonTextColor,
                attrs.cancelButtonTextColor);

        attrs.otherButtonTextColor = a.getColor(R.styleable.ActionSheet_otherButtonTextColor,
                attrs.otherButtonTextColor);

        attrs.padding = (int) a.getDimension(R.styleable.ActionSheet_actionSheetPadding, attrs.padding);

        attrs.otherButtonSpacing = (int) a.getDimension(R.styleable.ActionSheet_otherButtonSpacing,
                attrs.otherButtonSpacing);

        attrs.cancelButtonMarginTop = (int) a.getDimension(R.styleable.ActionSheet_cancelButtonMarginTop,
                attrs.cancelButtonMarginTop);

        attrs.actionSheetTextSize = a.getDimensionPixelSize(R.styleable.ActionSheet_actionSheetTextSize,
                (int) attrs.actionSheetTextSize);

        a.recycle();
        return attrs;
    }

    /**
     * 创建页
     *
     * @return
     */
    private View createView() {
        FrameLayout parent = new FrameLayout(mContext);
        FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        parentParams.gravity = Gravity.BOTTOM;
        parent.setLayoutParams(parentParams);

        mBackground = new View(mContext);
        mBackground.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mBackground.setBackgroundColor(Color.argb(136, 0, 0, 0));
        mBackground.setTag(BG_VIEW_TAG);
        mBackground.setOnClickListener(this);

        mPanel = new LinearLayout(mContext);
        FrameLayout.LayoutParams mPanelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mPanelParams.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams(mPanelParams);
        mPanel.setOrientation(LinearLayout.VERTICAL);
        parent.addView(mBackground);
        parent.addView(mPanel);

        return parent;
    }

    /**
     * 创建一个列表页
     */
    private void createListView() {
        if (mListView == null) {
            mListView = new ListView(mContext);
            mListView.setBackgroundColor(mContext.getResources().getColor(R.color.app_background_color));
            mListView.setDivider(new ColorDrawable(Color.GRAY));
            mListView.setDividerHeight(1);
        }

        mListView.addFooterView(createListViewFooter());

        if (mPanel != null) {
            mPanel.addView(mListView);
        }
    }

    /**
     * 返回列表页面的底部视图
     *
     * @return
     */
    private View createListViewFooter() {
        View footer = mLayoutInflater.inflate(R.layout.list_footer_actionsheet, null);
        if (mCancelBtn == null) {
            mCancelBtn = (Button) footer.findViewById(R.id.btn_actionsheet_cancel);
            mCancelBtn.setTag(CANCEL_BTN_TAG);
            mCancelBtn.setOnClickListener(this);
        }
        return footer;
    }

    /**
     * 创建item
     */
    private void createItems() {
        if (mActionSheetAdapter == null) {
            mActionSheetAdapter = new MyActionSheetAdapter();
            mListView.setAdapter(mActionSheetAdapter);
            mListView.setOnItemClickListener(this);
        }
        LinearLayout.LayoutParams lp = null;
        if (mItems.size() <= MAX_ITEMS) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.height = dp2px(44 * (MAX_ITEMS + 1));
        }
        mListView.setLayoutParams(lp);
        mActionSheetAdapter.notifyDataSetChanged();
    }


    /**
     * dismiss时的处理
     */
    private void onDismiss() {
        mBackground.startAnimation(createAlphaOutAnimation());
        mPanel.startAnimation(createTranslationOutAnimation());
    }

    /***********************************************
     * 添加数据
     ***********************************************/

    /**
     * 添加标题
     *
     * @param titles
     * @return
     */
    public NewActionSheet addItems(List<String> titles) {
        if (titles == null || titles.size() == 0)
            return this;
        mItems = titles;
        createItems();
        return this;
    }

    /***********************************************
     * 公共方法
     ***********************************************/

    /**
     * 显示菜单
     */
    public void showMenu() {
        if (!mDismissed)
            return;
        show();
        getWindow().setContentView(mParentView);
        mDismissed = false;
    }

    /**
     * 隐藏菜单
     */
    public void dismissMenu() {
        if (mDismissed)
            return;
        onDismiss();
//        dismiss();
        mDismissed = true;
    }

    /**
     * 点击外部边缘是否可取消
     *
     * @param cancelable
     * @return
     */
    public NewActionSheet setCancelableOnTouchMenuOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    /**
     * 设置监听
     *
     * @param listener
     * @return
     */
    public NewActionSheet setItemClickListener(NewActionSheet.MenuItemClickListener listener) {
        this.mListener = listener;
        return this;
    }


    /***********************************************
     * 动画
     ***********************************************/

    /**
     * 创建一个透明度动画
     *
     * @return
     */
    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    /**
     * 创建一个透明度动画
     *
     * @return
     */
    private Animation createAlphaOutAnimation() {
        if (mAlphaOutAnimation == null) {
            mAlphaOutAnimation = new AlphaAnimation(1, 0);
            mAlphaOutAnimation.setDuration(ALPHA_DURATION);
            mAlphaOutAnimation.setFillAfter(true);
        }
        return mAlphaOutAnimation;
    }

    /**
     * 创建一个位移动画
     *
     * @return
     */
    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    /**
     * 创建一个位移动画
     *
     * @return
     */
    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        if (mTranslateOutAnimation == null) {
            mTranslateOutAnimation = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1);
            mTranslateOutAnimation.setDuration(TRANSLATE_DURATION);
            mTranslateOutAnimation.setFillAfter(true);
            mTranslateOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismiss();
                    switch (mCurrentClickTag) {
                        case TAG_BG_CLICK: {
                            if (mListener != null) {
                                mListener.onCancelClickAfterAnimation(mTag);
                            }
                            break;
                        }
                        case TAG_CANCEL_CLICK: {
                            if (mListener != null) {
                                mListener.onCancelClickAfterAnimation(mTag);
                            }
                            break;
                        }
                        default: {
                            if (mListener != null) {
                                mListener.onItemClickAfterAnimation(mTag, mCurrentClickTag);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return mTranslateOutAnimation;
    }

    /**
     * 工具方法，dp转换成px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
                .getDisplayMetrics());
    }


    /***********************************************
     * 事件
     ***********************************************/

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getTag().equals(BG_VIEW_TAG) && !mCancelableOnTouchOutside)
            return;
        dismissMenu();

        if (v.getTag().equals(CANCEL_BTN_TAG)) {
            //点击取消按钮
            mCurrentClickTag = TAG_CANCEL_CLICK;
            if (mListener != null) {
                mListener.onCancelClick(mTag);
            }
        } else if (v.getTag().equals(BG_VIEW_TAG)) {
            mCurrentClickTag = TAG_BG_CLICK;
            if (mListener != null) {
                mListener.onCancelClick(mTag);
            }
        } else if (v.getTag().equals(BG_VIEW_TAG)) {
            mCurrentClickTag = TAG_BG_CLICK;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismissMenu();
        mCurrentClickTag = position;
        if (mListener != null) {
            mListener.onItemClick(mTag, position);
        }
    }


    /***********************************************
     * 自定义内部类
     ***********************************************/


    /**
     * 菜单点击的接口
     */
    public static interface MenuItemClickListener {

        /**
         * item点击
         *
         * @param tag
         * @param itemPosition
         */
        void onItemClick(String tag, int itemPosition);

        /**
         * 取消点击
         *
         * @param tag
         */
        void onCancelClick(String tag);


        /**
         * 点击操作完成，在关闭动画结束时候调用
         *
         * @param tag
         * @param itemPosition
         */
        void onItemClickAfterAnimation(String tag, int itemPosition);

        /**
         * 取消按钮点击后，关闭动画完成时候调用
         *
         * @param tag
         */
        void onCancelClickAfterAnimation(String tag);
    }

    /**
     * 视图类
     */
    private static class ViewHolder {

        /**
         * 名称
         */
        private TextView mItemNameTV;

    }

    /**
     * 自定义属性的控件主题
     */
    private class Attributes {
        private Context mContext;

        private Drawable background;
        private Drawable cancelButtonBackground;
        private Drawable otherButtonTopBackground;
        private Drawable otherButtonMiddleBackground;
        private Drawable otherButtonBottomBackground;
        private Drawable otherButtonSingleBackground;
        private int cancelButtonTextColor;
        private int otherButtonTextColor;
        private int padding;
        private int otherButtonSpacing;
        private int cancelButtonMarginTop;
        private float actionSheetTextSize;

        public Attributes(Context context) {
            mContext = context;
            this.background = new ColorDrawable(Color.TRANSPARENT);
            this.cancelButtonBackground = new ColorDrawable(Color.BLACK);
            ColorDrawable gray = new ColorDrawable(Color.GRAY);
            this.otherButtonTopBackground = gray;
            this.otherButtonMiddleBackground = gray;
            this.otherButtonBottomBackground = gray;
            this.otherButtonSingleBackground = gray;
            this.cancelButtonTextColor = Color.WHITE;
            this.otherButtonTextColor = Color.BLACK;
            this.padding = dp2px(20);
            this.otherButtonSpacing = dp2px(2);
            this.cancelButtonMarginTop = dp2px(10);
            this.actionSheetTextSize = dp2px(16);
        }

        public Drawable getOtherButtonMiddleBackground() {
            if (otherButtonMiddleBackground instanceof StateListDrawable) {
                TypedArray a = mContext.getTheme().obtainStyledAttributes(null, R.styleable.ActionSheet,
                        R.attr.actionSheetStyle, 0);
                otherButtonMiddleBackground = a
                        .getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
                a.recycle();
            }
            return otherButtonMiddleBackground;
        }

    }

    /**
     * listView的适配器
     */
    private class MyActionSheetAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.list_cell_actionsheet, null);
                holder.mItemNameTV = (TextView) convertView.findViewById(R.id.tv_cell_actionsheet_label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mItemNameTV.setText(mItems.get(position));
            return convertView;
        }
    }


}
