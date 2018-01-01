package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import xuqk.github.zlibrary.basekit.RxBusFactory;
import xuqk.github.zlibrary.basekit.dialog.CommonDialog;
import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.OnBackPressedListener;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

/**
 * @ClassName: ZFragment
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:48 PM
 * @Site:xuqk.top
 * @author: xuqk
 */


public abstract class ZFragment<D extends ViewDataBinding, VM extends BaseViewModel>
        extends Fragment implements UiCallback {

    private View mRootView;
    private LayoutInflater mLayoutInflater;
    private VM mViewModel;
    private D mBinding;
    private BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    private BaseNiceDialog mLoadingDialog;

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject.onNext(LifecycleEvent.CREATE);
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mLayoutInflater = inflater;
        if (mRootView == null && getLayoutId() > 0) {
            mRootView = inflater.inflate(getLayoutId(), null);
            mBinding = DataBindingUtil.bind(mRootView);
        } else {
            ViewGroup viewGroup = (ViewGroup) mRootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mRootView);
            }
        }

        if (useRxBus()) {
            RxBusFactory.getBus().register(this);
        }

        mViewModel = findOrCreateViewModel();
        setListener();
        init(savedInstanceState);
        mViewModel.initOnCreate();
        initLoadingDialog();
        subject.onNext(LifecycleEvent.CREATE_VIEW);
        return mRootView;
    }

    private void initLoadingDialog() {
        if (mViewModel != null) {
            mViewModel.mShowLoading.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable observable, int i) {
                    if (mViewModel.mShowLoading.get()) {
                        mLoadingDialog = CommonDialog.getLoadingDialog(getViewModel().mLoadingMessage)
                                .setOnBackPressedListener(new OnBackPressedListener() {
                                    @Override
                                    protected void backPressedListener(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        getViewModel().dismissLoading();
                                    }
                                });

                        mLoadingDialog.show(getActivity().getSupportFragmentManager());
                    } else {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        subject.onNext(LifecycleEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        subject.onNext(LifecycleEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
        subject.onNext(LifecycleEvent.PAUSE);
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();
        subject.onNext(LifecycleEvent.STOP);
    }

    /**
     * 解除RxBus订阅，清理ViewModel与该Fragment关联
     */
    @Override
    @CallSuper
    public void onDestroyView() {
        if (useRxBus()) {
            RxBusFactory.getBus().unregister(this);
        }
        if (mViewModel != null) {
            mViewModel.onDestroyView();
        }
        super.onDestroyView();
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
        mViewModel.showLoading(message);
    }

    @Override
    public void dismissLoadingDialog() {
        mViewModel.dismissLoading();
    }

    @Override
    public int requestPermission(String requestPermission) {
        int[] result = new int[1];
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        Observable.create(e -> e.onNext(new Object()))
                .compose(rxPermissions.ensureEach(requestPermission))
                .compose(bindToLifecycle())
                .subscribe(permission -> {
                    if (permission.granted) {
                        result[0] = 0;
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        result[0] = 1;
                    } else {
                        result[0] = 2;
                    }
                });
        return result[0];
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
