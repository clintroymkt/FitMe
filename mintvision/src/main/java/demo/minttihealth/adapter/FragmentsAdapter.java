package demo.minttihealth.adapter;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import demo.minttihealth.fmt.BaseFragment;

/**
 * Created by ccl on 2016/4/29.
 */
public class FragmentsAdapter extends FragmentPagerAdapter {

    private final Context context;
    private SparseArray<BaseFragment> fragmentSparseArr;

    public FragmentsAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    public void setFragments(SparseArray<BaseFragment> fragmentSparseArray) {
        this.fragmentSparseArr = fragmentSparseArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentSparseArr.get(position);
    }

    @Override
    public int getCount() {
        if (null != fragmentSparseArr) return fragmentSparseArr.size();
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragmentSparseArr == null)
            return "";
        if (fragmentSparseArr.get(position) == null)
            return "";
        return context.getResources().getString(fragmentSparseArr.get(position).getTitle());
    }
}
