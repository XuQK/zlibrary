package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import xuqk.github.zlibrary.basekit.RxBusFactory;

/**
 * @ClassName: BaseViewModel
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 3:04 PM
 * @Site:xuqk.top
 * @author: xuqk
 *
 * 注意：若想在ViewModel里调用Activity或Fragment里实现的UiCallback接口的快捷方法，
 * 必须引入BaseNavigator的子接口，即使是个空接口，并且需要在Activity或Fragment里手动设置接口
 */


public class BaseViewModel<N extends BaseNavigator> extends BaseObservable {

    protected BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    protected Context mContext;
    protected N mNavigator;
    ObservableBoolean mShowLoading = new ObservableBoolean(false);
    String mLoadingContent = "";
    private boolean mLoadData = true;

    public BaseViewModel(Context context) {
        mContext = context.getApplicationContext();
        if (useRxBus()) {
            RxBusFactory.getBus().register(this);
        }
    }

    /**
     * 只在创建时初始化一次数据
     */
    void initOnCreate() {
        if (mLoadData) {
            mLoadData = false;
            init();
        }
    }

    /**
     * 初始化方法，默认只在创建的时候调用一次
     */
    public void init() {

    }

    public void setNavigator(N navigator) {
        mNavigator = navigator;
    }

    /**
     * 显示Loading
     * @param content
     */
    protected void showLoading(@Nullable String content) {
        mLoadingContent = content;
        mShowLoading.set(true);
    }
    protected void showLoading() {
        mShowLoading.set(true);
    }

    /**
     * 隐藏Loading
     */
    protected void dismissLoading() {
        mLoadingContent = "";
        mShowLoading.set(false);
    }

    /**
     * 一般用来将回调接口实例置空，防止Fragment DestroyView后仍然调用
     */
    @CallSuper
    public void onDestroyView() {
        subject.onNext(LifecycleEvent.DETACH);
        mNavigator = null;
    }

    @CallSuper
    public void onDestroy() {
        if (useRxBus()) {
            RxBusFactory.getBus().unregister(this);
        }
        subject.onNext(LifecycleEvent.DESTROY);
        mNavigator = null;
    }

    protected boolean useRxBus() {
        return false;
    }

    protected <T> ObservableTransformer<T, T> bindToLifecycle() {
        return upstream -> upstream.takeUntil(subject.skipWhile(event ->
                event != LifecycleEvent.DESTROY && event != LifecycleEvent.DETACH));
    }
}
