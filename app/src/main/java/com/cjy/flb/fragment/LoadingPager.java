package com.cjy.flb.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.cjy.flb.manager.ThreadManager;
import com.cjy.flb.utils.UIUtils;

/**
 * @author <p/>
 *         在子类中 耗时操作放到 load中，然后load返回一个状态，在showPagerView中根据状态选择 显示的页面
 *         如果装在是成功的。那么久显示 createSuccessView
 *         加载中 ：就是滚动页面，后台获取加载的数据，每个页面的数据不同所以就让子类来实现，直接抽象abstract了。
 */
public abstract class LoadingPager extends FrameLayout {

    // 加载默认的状态
    private static final int STATE_UNLOADED = 1;
    // 数据库读取完成
    private static final int STATE_READ_DATABASE = 2;
    // 数据库写入完成,下载完成
    private static final int STATE_WRITE_DATABASE = 3;
    // 加载成功的状态
    private static final int STATE_SUCCEED = 5;

    // 转圈的view
    private View mLoadingView;
    // 成功的view
    private View mSucceedView;

    // 默认的状态
    private int mState;

    private int load_page_loading;

    public LoadingPager(Context context) {
        super(context);
        mState = STATE_SUCCEED;
        showSafePagerView();
    }

    public LoadingPager(Context context, int loading, int error, int empty) {
        this(context, null, 0, loading, error, empty);
    }

    public LoadingPager(Context context, AttributeSet attrs, int loading, int error, int empty) {
        this(context, attrs, 0, loading, error, empty);
    }

    public LoadingPager(Context context, AttributeSet attrs, int defStyle, int loading, int error, int empty) {
        super(context, attrs, defStyle);

        load_page_loading = loading;
        init();
    }

    private void init() {
        // 初始化状态 为1 无加载
        mState = STATE_UNLOADED;
        // 初始化 load状态的view 这个时候 三个状态的view叠加在一起了
        mLoadingView = createLoadingView();
        if (null != mLoadingView) {
            addView(mLoadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        mSucceedView = createSuccessView();
        if (null != mSucceedView) {
            addView(mSucceedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        showSafePagerView();
    }

    private void showSafePagerView() {
        // 直接运行到主线程
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                showPagerView();
            }
        });
    }

    /**
     * 判断显示的页面为那个
     */
    private void showPagerView() {
        // 這個時候 都不為空 mState默認是STATE_UNLOADED狀態所以只顯示 lodaing 下面的 error和empty暂时不显示
        if (null != mLoadingView) {
            mLoadingView.setVisibility(mState == STATE_UNLOADED ? View.VISIBLE : View.GONE);
        }

        if (null != mSucceedView && mState == STATE_WRITE_DATABASE) {//完成了下载
            mState = readDatabase().getValue();
        }

        if (null != mSucceedView) {
            mSucceedView.setVisibility(mState == STATE_READ_DATABASE ? View.VISIBLE : View.INVISIBLE);
        }

        if (mState == STATE_UNLOADED) {
            //位于线程池中线程运行
            LoadingRunnable task = new LoadingRunnable();
            ThreadManager.getLongPool().execute(task);
        }
    }

    //onResume
    public void show() {
        // 如果是unload 就把状态 变为 loading了 这时候从服务器拿数据
        if (mState == STATE_READ_DATABASE) {
            LoadingRunnable task = new LoadingRunnable();
            ThreadManager.getLongPool().execute(task);
        }
    }

    public int getCurrentState() {
        return mState;
    }

    /**
     * 正在旋转的页面
     *
     * @return
     */
    public View createLoadingView() {
        if (load_page_loading != 0) {
            return UIUtils.inflate(load_page_loading);
        }
        return null;
    }

    /**
     * 处理下载 耗时操作
     *
     * @return
     */
    protected abstract LoadResult loadingData();

    protected abstract LoadResult readDatabase();

    /**
     * 制作成功界面
     *
     * @return
     */
    protected abstract View createSuccessView();


    class LoadingRunnable implements Runnable {
        @Override
        public void run() {
            final LoadResult loadResult = loadingData();//下载,写入数据库
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    mState = loadResult.getValue();//STATE_WRITE_DATABASE
                    showPagerView();
                }
            });
        }
    }

    /**
     * 枚举设计
     */
    public enum LoadResult {
        READ(2), WRITE(3), SUCCESS(5);
        int value;

        LoadResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
