package xuqk.github.zlibrary.basekit.cache;

/**
 * ClassName: ICache <br/>
 * PackageName: xuqk.github.zlibrary.basekit.cache <br/>
 * Create On: 12/24/17 4:25 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */

public interface ICache {
    void put(String key, Object value);

    Object get(String key);

    void remove(String key);

    boolean contains(String key);

    void clear();

}
