package xuqk.github.zlibrary.basekit.dialog.base;

import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.KeyEvent;

/**
 * ClassName: onBackPressedListener <br/>
 * PackageName: xuqk.github.zlibrary.basekit.dialog.base <br/>
 * ProjectName: app <br/>
 * Create On: 1/1/18 1:53 AM <br/>
 * Site: http://www.handongkeji.com <br/>
 *
 * @author: xuqk <br/>
 */


public abstract class OnBackPressedListener implements Parcelable {

    protected abstract void backPressedListener(DialogInterface dialog, int keyCode, KeyEvent event);

    protected OnBackPressedListener(Parcel in) {}

    public OnBackPressedListener() {

    }

    public static final Creator<OnBackPressedListener> CREATOR = new Creator<OnBackPressedListener>() {
        @Override
        public OnBackPressedListener createFromParcel(Parcel in) {
            return new OnBackPressedListener(in) {
                @Override
                protected void backPressedListener(DialogInterface dialog, int keyCode, KeyEvent event) {

                }
            };
        }

        @Override
        public OnBackPressedListener[] newArray(int size) {
            return new OnBackPressedListener[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }


}
