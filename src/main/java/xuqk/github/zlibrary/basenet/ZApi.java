package xuqk.github.zlibrary.basenet;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ClassName: ZApi <br/>
 * PackageName: xuqk.github.zlibrary.basenet <br/>
 * Create On: 12/24/17 4:26 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */


public class ZApi {

    private static ZApi instance;

    private ZApi() {}

    public static ZApi getInstance() {
        if (instance == null) {
            synchronized (ZApi.class) {
                instance = new ZApi();
            }
        }
        return instance;
    }

    public Retrofit getRetrofit(String baseUrl) {
        return getRetrofit(baseUrl, new NetSetting());
    }

    private Retrofit getRetrofit(String baseUrl, NetSetting setting) {
        return getRetrofit(baseUrl, getOkHttpClient(setting));
    }

    private Retrofit getRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient);
        return builder.build();
    }

    private OkHttpClient getOkHttpClient(final NetSetting setting) {
        if (setting == null) {
            throw new IllegalStateException("NetSetting can not be null");
        }

        // 设置日志拦截
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            if (!setting.log(message)) {
                Log.d("ZApi", message);
            }
        });
        loggingInterceptor.setLevel(setting.configLogLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(setting.configConnectTimeoutMills(), TimeUnit.MILLISECONDS)
                .readTimeout(setting.configReadTimeoutMills(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(setting.configIsRetryOnConnectionFailure())
                .addNetworkInterceptor(loggingInterceptor);
        // 设置支持 Https
        if (setting.configHttps() != null) {
            builder.sslSocketFactory(setting.configHttps().sSLSocketFactory, setting.configHttps().trustManager);
        }
        // 添加应用拦截
        if (setting.configInterceptors() != null) {
            for (Interceptor interceptor: setting.configInterceptors()) {
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }
        // 添加网络拦截
        if (setting.configNetworkInterceptors() != null) {
            for (Interceptor interceptor : setting.configNetworkInterceptors()) {
                if (interceptor != null) {
                    builder.addNetworkInterceptor(interceptor);
                }
            }
        }
        //添加统一的header
        if (setting.configCommonHeaders() != null) {
            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .headers(setting.configCommonHeaders())
                        .build();
                return chain.proceed(request);
            });
        }
        return builder.build();
    }

    /**
     * 统一线程切换
     * @param <T> 被观察者
     * @return 切换线程后的被观察者
     */
    public static <T> ObservableTransformer<T, T> getObservableScheduler() {

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    public static <T>FlowableTransformer<T, T> getFlowableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
