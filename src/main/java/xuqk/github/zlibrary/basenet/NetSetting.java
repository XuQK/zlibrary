package xuqk.github.zlibrary.basenet;

import okhttp3.Headers;
import okhttp3.Interceptor;
import xuqk.github.zlibrary.basekit.zlog.ZLog;
import xuqk.github.zlibrary.basenet.https.HttpsManager;
import xuqk.github.zlibrary.basenet.https.SSLParams;

/**
 * ClassName: NetSetting <br/>
 * PackageName: xuqk.github.zlibrary.basenet <br/>
 * Create On: 12/24/17 4:25 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 * okhttp 的网络设置 <br/>
 */


class NetSetting {
    /**
     * 设置是否重试
     *
     * @return true重试，false不重试
     */
    boolean configIsRetryOnConnectionFailure() {
        return true;
    }

    /**
     * 添加公共的header
     *
     * @return 需添加的headers
     */
    Headers configCommonHeaders() {
        return null;
    }

    /**
     * 为OkHttpClient添加NetworkInterceptors
     *
     * @return 设置的网络拦截器
     */

    Interceptor[] configNetworkInterceptors() {
        return null;
    }

    /**
     * 为OkHttpClient添加Interceptors
     *
     * @return 设置的拦截器
     */
    Interceptor[] configInterceptors() {
        return null;
    }

    /**
     * 配置https
     *
     * @return 默认忽略验证，可访问所有https
     */
    SSLParams configHttps() {
        return HttpsManager.getSslSocketFactory(null, null, null);
    }

    /**
     * 日志输出等级 {@link HttpLoggingInterceptor.Level}
     *
     * @return 日志输出的等级
     * @see HttpLoggingInterceptor.Level
     */

    HttpLoggingInterceptor.Level configLogLevel() {
        if (ZLog.LOG) {
            return HttpLoggingInterceptor.Level.BODY;
        } else {
            return HttpLoggingInterceptor.Level.NONE;
        }
    }

    /**
     * 网络请求日志输出，需要使用自己的日志输出工具打印的，重写该方法
     *
     * @param message 待输出日志
     * @return 返回true当前日志已处理，内置输出工具不再打印
     */
    boolean log(String message) {
        return false;
    }

    /**
     * 连接超时时间，默认10s
     *
     * @return 连接超时的时间
     */
    long configConnectTimeoutMills() {
        return 1000 * 10;
    }

    /**
     * 读取超时时间，默认20s
     *
     * @return 读取超时的时间
     */
    long configReadTimeoutMills() {
        return 1000 * 2000;
    }
}
