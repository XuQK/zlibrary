package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import xuqk.github.zlibrary.basekit.RxBusFactory;
import xuqk.github.zlibrary.basekit.dialog.CommonDialog;
import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.OnBackPressedListener;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

/**
 * @ClassName: ZActivity
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:31 PM
 * @Site:xuqk.top
 * @author: xuqk
 */


public abstract class ZActivity<D extends ViewDataBinding, VM extends ZViewModel>
        extends AppCompatActivity implements UiCallback<ZViewModel> {

    private AppCompatActivity mActivity;
    private D mBinding;
    private VM mViewModel;
    private BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    private BaseNiceDialog mLoadingDialog;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        if (getLayoutId() > 0) {
            mBinding = DataBindingUtil.setContentView(mActivity, getLayoutId());
        }

        if (useRxBus()) {
            RxBusFactory.getBus().register(this);
        }

        mViewModel = findOrCreateViewModel();
        setListener();
        init(savedInstanceState);
        mViewModel.initOnCreate();
        subject.onNext(LifecycleEvent.CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        subject.onNext(LifecycleEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        subject.onNext(LifecycleEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        subject.onNext(LifecycleEvent.PAUSE);
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        subject.onNext(LifecycleEvent.STOP);
    }

    /**
     * 解除RxBus订阅，清理ViewModel
     */
    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        subject.onNext(LifecycleEvent.DESTROY);
        if (useRxBus()) {
            RxBusFactory.getBus().unregister(this);
        }
        if (mViewModel != null) {
            mViewModel.onDestroy();
        }
    }

    /**
     * 重写此方法，设置回调接口
     */
    @Override
    public void setListener() {

    }

    @Override
    public boolean useRxBus() {
        return false;
    }

    @Override
    public void goActivity(Class clazz) {
        startActivity(new Intent(mActivity, clazz));
    }

    @Override
    public void goActivity(Class clazz, Intent intent) {
        intent.setClass(mActivity, clazz);
        startActivity(intent);
    }

    @Override
    public void goActivityForResult(Class clazz, Intent intent, int requestCode) {
        intent.setClass(mActivity, clazz);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void showConfirmDialog(ViewConvertListener listener) {
        CommonDialog.showConfirmDialog(getActivity(), listener);
    }

    @Override
    public void showCommonDialog(BaseNiceDialog dialog) {
        dialog.show(getSupportFragmentManager());
    }

    @Override
    public void showLoadingDialog(@Nullable String message) {
        mLoadingDialog = CommonDialog.getLoadingDialog(message)
                .setOnBackPressedListener(new OnBackPressedListener() {
                    @Override
                    protected void backPressedListener(DialogInterface dialog, int keyCode, KeyEvent event) {
                        dialog.dismiss();
                    }
                })
                .show(getSupportFragmentManager());
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public VM findOrCreateViewModel() {
        // 先去FragmentManager里寻找对应的VM
        ViewModelHolder<VM> retainedViewModel = (ViewModelHolder<VM>) getSupportFragmentManager()
                .findFragmentByTag((getViewModelTag()));

        if (retainedViewModel != null && retainedViewModel.getViewModel() != null) {
            return retainedViewModel.getViewModel();
        } else {
            // 用反射创建子类实例
            Type type = getClass().getGenericSuperclass();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            VM viewModel = null;
            try {
                viewModel = (VM) ((Class) types[1]).getConstructor(Context.class)
                        .newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert viewModel != null;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    getViewModelTag());
            return viewModel;
        }
    }

    @Override
    public String getViewModelTag() {
        return getClass().getCanonicalName();
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public D getBinding() {
        return mBinding;
    }

    public BehaviorSubject<LifecycleEvent> getSubject() {
        return subject;
    }

    /**
     * 获取页面唯一加载弹窗
     * @return
     */
    public BaseNiceDialog getLoadingDialog() {
        return mLoadingDialog;
    }

    public VM getViewModel() {
        return mViewModel;
    }

    public <T> ObservableTransformer<T, T> bindToLifecycle() {
        return upstream -> upstream.takeUntil(subject.skipWhile(event ->
                event != LifecycleEvent.DESTROY && event != LifecycleEvent.DETACH));
    }

    public <T> ObservableTransformer<T, T> bindUntilEvent(LifecycleEvent lifecycle) {
        return upstream -> upstream.takeUntil(subject.skipWhile(event -> event != lifecycle));
    }
}
