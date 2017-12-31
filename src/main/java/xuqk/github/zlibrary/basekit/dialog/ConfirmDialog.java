package xuqk.github.zlibrary.basekit.dialog;

import android.support.v4.app.FragmentActivity;

import xuqk.github.zlibrary.R;
import xuqk.github.zlibrary.basekit.dialog.base.NiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;

/**
 * ClassName: ConfirmDialog <br/>
 * PackageName: xuqk.github.zlibrary.basekit <br/>
 * ProjectName: ZhenHaoApp <br/>
 * Create On: 12/31/17 1:47 PM <br/>
 * Site: http://www.handongkeji.com <br/>
 *
 * @author: xuqk <br/>
 */


public class ConfirmDialog {

    public static void show(FragmentActivity activity, ViewConvertListener listener) {
        NiceDialog.init()
                .setLayoutId(R.layout.confirm_dialog)
                .setConvertListener(listener)
                .setDimAmount(0.4f)
                .setMargin(24)
                .show(activity.getSupportFragmentManager());

    }
}
