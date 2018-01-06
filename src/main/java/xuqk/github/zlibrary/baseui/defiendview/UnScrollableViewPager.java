package xuqk.github.zlibrary.baseui.defiendview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ClassName: UnScrollableViewPager <br/>
 * PackageName: com.yueertang.app.ui.definedview <br/>
 * Create On: 12/26/17 9:39 AM <br/>
 * Site:http://www.handongkeji.com <br/>
 * @author 徐乾琨 <br/>
 */


public class UnScrollableViewPager extends ViewPager {

    public UnScrollableViewPager(Context context) {
        super(context);
    }

    public UnScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}
