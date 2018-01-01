package xuqk.github.zlibrary.basekit.dialog;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import xuqk.github.zlibrary.R;
import xuqk.github.zlibrary.basekit.dialog.base.BaseNiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.NiceDialog;
import xuqk.github.zlibrary.basekit.dialog.base.ViewConvertListener;
import xuqk.github.zlibrary.basekit.dialog.base.ViewHolder;

/**
 * ClassName: CommonDialog <br/>
 * PackageName: xuqk.github.zlibrary.basekit <br/>
 * ProjectName: ZhenHaoApp <br/>
 * Create On: 12/31/17 1:47 PM <br/>
 * Site: http://www.handongkeji.com <br/>
 *
 * @author: xuqk <br/>
 */


public class CommonDialog {

    public static void showConfirmDialog(FragmentActivity activity, ViewConvertListener listener) {
        NiceDialog.init()
                .setLayoutId(R.layout.confirm_dialog)
                .setConvertListener(listener)
                .setDimAmount(0.3f)
                .setMargin(24)
                .show(activity.getSupportFragmentManager());

    }

    public static BaseNiceDialog getLoadingDialog(String message) {
        return NiceDialog.init()
                .setLayoutId(R.layout.progress_dialog)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        if (TextUtils.isEmpty(message)) {
                            holder.getView(R.id.loading_message).setVisibility(View.GONE);
                        } else {
                            holder.setText(R.id.loading_message, message);
                        }
                    }
                })
                .setWidth(-1)
                .setOutCancel(false)
                .setDimAmount(0.3f);
    }
}
