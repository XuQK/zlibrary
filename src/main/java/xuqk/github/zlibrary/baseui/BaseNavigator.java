package xuqk.github.zlibrary.baseui;

import android.content.Intent;

import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

/**
 * @ClassName: BaseNavigator
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 3:17 PM
 * @Site:xuqk.top
 * @author: xuqk
 * 联系 ViewModel 和 Activity 及 Fragment 行为的接口
 */


public interface BaseNavigator {

    /**
     * 调用Activity的Finish方法
     */
    void finish();

    /**
     * Activity回退键
     */
    void onBackPressed();

    /**
     * 设置Activity返回结果
     * @param result
     */
    void setResult(int result);

    /**
     * 设置Activity返回结果
     * @param result
     * @param data
     */
    void setResult(int result, Intent data);

    /**
     * 启动Activity
     * @param clazz
     */
    void goActivity(Class clazz);

    /**
     * 启动带参数的Activity
     * @param clazz
     * @param intent
     */
    void goActivity(Class clazz, Intent intent);

    /**
     * 启动带参数的Activity并获取结果
     * @param clazz
     * @param intent
     * @param requestCode
     */
    void goActivityForResult(Class clazz, Intent intent, int requestCode);

    /**
     * 显示确认选项框
     * @param listener
     */
    void showConfirmDialog(ViewConvertListener listener);

    /**
     * 显示弹窗
     * @param dialog
     */
    void showCommonDialog(BaseNiceDialog dialog);

    /**
     * 显示LoadingView
     * @param message 信息
     */
    void showLoadingDialog(String message);

    /**
     * 隐藏LoadingDialog
     */
    void dismissLoadingDialog();
}
