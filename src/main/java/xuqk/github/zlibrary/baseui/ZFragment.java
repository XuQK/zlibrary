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
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * @ClassName: ZFragment
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:48 PM
 * @Site:xuqk.top
 * @author: xuqk
 */


public abstract class ZFragment<D extends ViewDataBinding, VM extends ZViewModel>
        extends Fragment implements UiCallback {

    protected AppCompatActivity mActivity;
    private View mRootView;
    private LayoutInflater mLayoutInflater;
    private VM mViewModel;
    private D mBinding;
    private BehaviorSubject<LifecycleEvent> subject = BehaviorSubject.create();
    private BaseNiceDialog mLoadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

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
        subject.onNext(LifecycleEvent.CREATE_VIEW);
        return mRootView;
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
            mViewModel.onDestroy();
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
        mActivity.onBackPressed();
    }

    @Override
    public void finish() {
        mActivity.finish();
    }

    @Override
    public void setResult(int result) {
        mActivity.setResult(result);
    }

    @Override
    public void setResult(int result, Intent data) {
        mActivity.setResult(result, data);
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
        CommonDialog.showConfirmDialog(mActivity, listener);
    }

    @Override
    public void showCommonDialog(BaseNiceDialog dialog) {
        dialog.show(mActivity.getSupportFragmentManager());
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
        ViewModelHolder<VM> retainedViewModel = (ViewModelHolder<VM>) mActivity.getSupportFragmentManager()
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
                        .newInstance(mActivity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert viewModel != null;
            ActivityUtils.addFragmentToActivity(mActivity.getSupportFragmentManager(),
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
