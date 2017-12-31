package xuqk.github.zlibrary.baseui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import xuqk.github.zlibrary.basekit.RxBusFactory;
import xuqk.github.zlibrary.basekit.dialog.ConfirmDialog;
import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;
import xuqk.github.zlibrary.baseui.widget.LoadingDialog;

/**
 * @ClassName: ZActivity
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:31 PM
 * @Site:xuqk.top
 * @author: xuqk
 */


public abstract class ZActivity<D extends ViewDataBinding, VM extends BaseViewModel>
        extends AppCompatActivity implements UiCallback<BaseViewModel> {

    private AppCompatActivity mActivity;
    private D mBinding;
    private VM mViewModel;
    private BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    private LoadingDialog mLoadingDialog;
    protected ImmersionBar mImmersionBar;

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

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.fitsSystemWindows(true)
                .statusBarColor("#ffffff")
                .statusBarDarkFont(true, 0.2f)
                .keyboardEnable(true);

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
        if (mViewModel != null) {
            mViewModel.mShowLoading.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable observable, int i) {
                    if (!mViewModel.mShowLoading.get()) {
                        mLoadingDialog.dismiss();
                    } else {
                        mLoadingDialog = LoadingDialog.show(mActivity, mViewModel.mLoadingContent);
                    }
                }
            });
        }
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
        ConfirmDialog.show(getActivity(), listener);
    }

    @Override
    public void showCommonDialog(BaseNiceDialog dialog) {
        dialog.show(getSupportFragmentManager());
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

    public LoadingDialog getLoadingDialog() {
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
