package xuqk.github.zlibrary.baseui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import xuqk.github.zlibrary.R;

/**
 * @ClassName: LoadingDialog
 * @PackageName: xuqk.github.zlibrary.baseui.widget
 * @Create On 12/7/17 5:55 PM
 * @Site:http://xuqk.top
 * @author: 徐乾琨
 */

public class LoadingDialog extends DialogFragment {
    private String msg;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView textView = dialog.findViewById(R.id.txt_wait);
        if (TextUtils.isEmpty(msg)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(msg);
        }

        return dialog;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    public static LoadingDialog show(Context context){
        return show(context,"");
    }

    public static LoadingDialog show(Context context, String msg){
        LoadingDialog dialog = new LoadingDialog();
        dialog.show(((FragmentActivity)context).getSupportFragmentManager(),"");
        dialog.setMsg(msg);
        return dialog;
    }

}
