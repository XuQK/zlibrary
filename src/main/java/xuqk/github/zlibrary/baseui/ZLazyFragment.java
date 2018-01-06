package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;

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
 * Created by shihao on 2017/1/26.
 */

public abstract class ZLazyFragment<D extends ViewDataBinding, VM extends ZViewModel>
        extends LazyFragment implements UiCallback {

    private D mBinding;
    private VM mViewModel;
    private BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    private BaseNiceDialog mLoadingDialog;

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject.onNext(LifecycleEvent.CREATE);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            mBinding = DataBindingUtil.bind(getRealRootView());
        }
        if (useRxBus()) {
            RxBusFactory.getBus().register(this);
        }

        mViewModel = findOrCreateViewModel();
        setListener();
        init(savedInstanceState);
        mViewModel.initOnCreate();
        subject.onNext(LifecycleEvent.CREATE_VIEW);
    }

    @Override
    @CallSuper
    protected void onStartLazy() {
        subject.onNext(LifecycleEvent.START);
    }

    @Override
    @CallSuper
    protected void onResumeLazy() {
        subject.onNext(LifecycleEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPauseLazy() {
        subject.onNext(LifecycleEvent.PAUSE);
    }

    @Override
    @CallSuper
    protected void onStopLazy() {
        subject.onNext(LifecycleEvent.STOP);
    }

    /**
     * 解除RxBus订阅，清理ViewModel与该Fragment关联
     */
    @Override
    @CallSuper
    protected void onDestroyViewLazy() {
        if (useRxBus()) {
            RxBusFactory.getBus().unregister(this);
        }
        if (mViewModel != null) {
            mViewModel.onDestroy();
        }
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        subject.onNext(LifecycleEvent.DESTROY);
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        subject.onNext(LifecycleEvent.DETACH);
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
    public void onBackPressed() {
        if (mLoadingDialog != null && mLoadingDialog.isResumed()) {
            mLoadingDialog.dismiss();
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void setResult(int result) {
        getActivity().setResult(result);
    }

    @Override
    public void setResult(int result, Intent data) {
        getActivity().setResult(result, data);
    }

    @Override
    public void goActivity(Class clazz) {
        startActivity(new Intent(getActivity(), clazz));
    }

    @Override
    public void goActivity(Class clazz, Intent intent) {
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
    }

    @Override
    public void goActivityForResult(Class clazz, Intent intent, int requestCode) {
        intent.setClass(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void showConfirmDialog(ViewConvertListener listener) {
        CommonDialog.showConfirmDialog(getActivity(), listener);
    }

    @Override
    public void showCommonDialog(BaseNiceDialog dialog) {
        dialog.show(getActivity().getSupportFragmentManager());
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
                .show(mActivity.getSupportFragmentManager());
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
        ViewModelHolder<VM> retainedViewModel = (ViewModelHolder<VM>) getActivity().getSupportFragmentManager()
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
                        .newInstance(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert viewModel != null;
            ActivityUtils.addFragmentToActivity(getActivity().getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    getViewModelTag());
            return viewModel;
        }
    }

    @Override
    public String getViewModelTag() {
        return getClass().getCanonicalName();
    }

    public D getBinding() {
        return mBinding;
    }

    public BehaviorSubject<LifecycleEvent> getSubject() {
        return subject;
    }

    public BaseNiceDialog getLoadingDialog() {
        return mLoadingDialog;
    }

    public VM getViewModel() {
        return mViewModel;
    }

    public LayoutInflater getLayoutInflate() {
        return mLayoutInflater;
    }

    protected <T> ObservableTransformer<T, T> bindToLifecycle() {
        return upstream -> upstream.takeUntil(subject.skipWhile(event ->
                event != LifecycleEvent.DESTROY && event != LifecycleEvent.DETACH));
    }

    protected <T> ObservableTransformer<T, T> bindUntilEvent(LifecycleEvent lifecycle) {
        return upstream -> upstream.takeUntil(subject.skipWhile(event -> event != lifecycle));
    }
}
