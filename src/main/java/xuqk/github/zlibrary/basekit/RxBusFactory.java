package xuqk.github.zlibrary.basekit;

import com.hwangjr.rxbus.Bus;

/**
 * ClassName: RxBusFactory <br/>
 * PackageName: xuqk.github.zlibrary.basekit <br/>
 * Create On: 12/24/17 4:25 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */


public class RxBusFactory {

    private static Bus sBus;

    public static Bus getBus() {
        if (sBus == null) {
            synchronized (RxBusFactory.class) {
                if (sBus == null) {
                    sBus = new Bus();
                }
            }
        }
        return sBus;
    }
}
