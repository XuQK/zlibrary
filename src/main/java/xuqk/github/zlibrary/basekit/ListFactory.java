package xuqk.github.zlibrary.basekit;

import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;

/**
 * ClassName: ListFactory <br/>
 * PackageName: xuqk.github.zlibrary.basekit <br/>
 * ProjectName: app <br/>
 * Create On: 1/7/18 4:15 PM <br/>
 * Site: http://www.handongkeji.com <br/>
 *
 * @author: xuqk <br/>
 */


public class ListFactory {

    public static ObservableList.OnListChangedCallback getListChangedCallback(RecyclerView.Adapter adapter) {
        return new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList observableList) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList observableList, int i, int i1) {
                adapter.notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList observableList, int i, int i1) {
                adapter.notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList observableList, int i, int i1, int i2) {
                if (i2 == 1) {
                    adapter.notifyItemMoved(i, i1);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemRangeRemoved(ObservableList observableList, int i, int i1) {
                adapter.notifyItemRangeRemoved(i, i1);
            }
        };
    }
}
