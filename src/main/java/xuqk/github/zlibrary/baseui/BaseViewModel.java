package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import xuqk.github.zlibrary.basekit.RxBusFactory;
import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

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

    /**
     * 这是将RxJava流与Activity/Fragment的生命周期绑定的办法
     */
    protected BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    /**
     * 该Context必须是ApplicationContext，防止内存泄漏
     */
    protected Context mContext;
    /**
     * Activity/Fragment行为接口，通过该接口调用宿主Activity/Fragment中的通用方法
     */
    protected N mNavigator;
    ObservableBoolean mShowLoading = new ObservableBoolean(false);
    String mLoadingMessage = "";
    private boolean mLoadData = true;

    ObservableBoolean mShowConfirmDialog = new ObservableBoolean(false);
    ViewConvertListener mViewConvertListener;

    ObservableBoolean mShowCommonDialog = new ObservableBoolean(false);
    BaseNiceDialog mBaseNiceDialog;


    public BaseViewModel(Context context) {
        mContext = context.getApplicationContext();
        if (useRxBus()) {
            RxBusFactory.getBus().register(this);
        }
    }

    /**
     * 只在首次创建时初始化一次数据，防止宿主Activity/Fragment被摧毁重建后重新申请数据
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

    /**
     * 设置宿主Activity/Fragment中的回调接口
     * @param navigator
     */
    public void setNavigator(N navigator) {
        mNavigator = navigator;
    }

    /**
     * 显示Loading
     * @param content
     */
    protected void showLoading(@Nullable String content) {
        mLoadingMessage = content;
        mShowLoading.set(true);
    }
    protected void showLoading() {
        mShowLoading.set(true);
    }

    /**
     * 隐藏Loading
     */
    protected void dismissLoading() {
        mLoadingMessage = "";
        mShowLoading.set(false);
    }

    protected void showConfirmDialog(ViewConvertListener listener) {
        mViewConvertListener = listener;
        mShowConfirmDialog.set(true);
    }

    protected void setShowCommonDialog(BaseNiceDialog dialog) {
        mBaseNiceDialog = dialog;
        mShowCommonDialog.set(true);
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
