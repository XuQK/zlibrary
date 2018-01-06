package xuqk.github.zlibrary.baseui;

import android.os.Bundle;

/**
 * @ClassName: UiCallback
 * @PackageName: xuqk.github.zlibrary.baseui
 * @ProjectName: yidcode
 * @Create On 11/20/17 2:35 PM
 * @Site:xuqk.top
 * @author: xuqk
 * Activity 和 Fragment 统一的初始化行为，方便根据需求在 Activity 和 Fragment 之间进行切换
 */


public interface UiCallback<VM> extends BaseNavigator {
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
}
