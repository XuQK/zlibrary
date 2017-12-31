package xuqk.github.zlibrary;

import xuqk.github.zlibrary.basekit.zlog.ZLog;

/**
 * ClassName: ZConfig <br/>
 * PackageName: xuqk.github.zlibrary <br/>
 * Create On: 12/24/17 4:28 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */


public class ZConfig {

    private ZConfig() {}

    public void initLog(boolean isLog, String logTag) {
        ZLog.init(isLog, logTag);
    }
}
