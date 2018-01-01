package xuqk.github.zlibrary.baseui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

/**
 * @ClassName: UiCallback
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:35 PM
 * @Site:xuqk.top
 * @author: xuqk
 * Activity 和 Fragment 统一的初始化行为，方便根据需求在 Activity 和 Fragment 之间进行切换
 */


public interface UiCallback<VM> {
    /**
     * 初始化方法
     * @param savedInstanceState
     */
    void init(Bundle savedInstanceState);

    /**
     * 统一设置监听方法
     */
    void setListener();

    /**
     * 获取布局
     * @return
     */
    int getLayoutId();

    /**
     * RxBus 开关
     * @return
     */
    boolean useRxBus();

    /**
     * 用FragmentManager来保存ViewModel，使其不会由于Activity或Fragment的生命周期变化影响到数据持有情况
     * ViewModel的标签名为持有类类名
     * 通过反射来获取实际建立的BaseViewModel的子类
     * @return VM
     */
    VM findOrCreateViewModel();

    /**
     * ViewModel唯一标识符，如有必要可重写该方法指定ViewModel标识
     * @return VM标识
     */
    String getViewModelTag();

    /**
     * 回退键
     */
    void onBackPressed();

    /**
     * 结束当前Activity
     */
    void finish();

    /**
     * 设置返回结果
     * @param result
     */
    void setResult(int result);

    /**
     * 设置带数据的返回结果
     * @param result
     * @param data
     */
    void setResult(int result, Intent data);

    /**
     * 启动空Activity
     * @param clazz
     */
    void goActivity(Class clazz);

    /**
     * 启动带参数的Activity
     * @param clazz
     * @param intent
     */
    void goActivity(Class clazz, @Nullable Intent intent);

    /**
     * 启动带参数的Activity并获取结果
     * @param clazz
     * @param intent
     * @param requestCode
     */
    void goActivityForResult(Class clazz, @Nullable Intent intent, int requestCode);

    /**
     * 展示确认弹窗
     * @param listener
     */
    void showConfirmDialog(ViewConvertListener listener);

    /**
     * 显示弹窗
     * @param dialog
     */
    void showCommonDialog(BaseNiceDialog dialog);

    /**
     * 显示LoadingDialog
     * @param message
     */
    void showLoadingDialog(@Nullable String message);

    /**
     * 隐藏LoadingDialog
     */
    void dismissLoadingDialog();

    /**
     * 请求授权
     * @param requestPermission
     * @return 授权结果，0授权，1拒绝，2永久拒绝不再提示
     */
    int requestPermission(String requestPermission);
}
