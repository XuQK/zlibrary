package xuqk.github.zlibrary.baseui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * ClassName: ViewModelHolder <br/>
 * PackageName: xuqk.github.zlibrary.baseui <br/>
 * ProjectName: ZhenHaoApp <br/>
 * Create On: 12/30/17 5:51 PM <br/>
 * Site: http://www.handongkeji.com <br/>
 *
 * @author: xuqk <br/>
 * 管理ViewModel的无UI fragment容器
 */


public class ViewModelHolder<VM> extends Fragment {

    private VM mViewModel;

    public ViewModelHolder() {}

    public static <M> ViewModelHolder createContainer(@NonNull M viewModel) {
        ViewModelHolder<M> viewModelContainer = new ViewModelHolder<>();
        viewModelContainer.setViewModel(viewModel);
        return viewModelContainer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    public VM getViewModel() {
        return mViewModel;
    }

    public void setViewModel(@NonNull VM viewModel) {
        mViewModel = viewModel;
    }
}
