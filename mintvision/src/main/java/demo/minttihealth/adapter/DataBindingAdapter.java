package demo.minttihealth.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import android.view.ViewGroup;

import demo.minttihealth.adapter.vh.BindingViewHolder;
import demo.minttihealth.health.BR;

/**
 * Created by ccl on 2016/9/30.
 * DataBindingAdapter
 */

public class DataBindingAdapter<T> extends BaseRecyclerAdapter<T, BindingViewHolder> {

    private final int layoutId;
    private ObservableInt listSize = new ObservableInt(0);

    public DataBindingAdapter(Context context, int layoutId) {
        super(context);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getBindingViewHolder(layoutId, parent);
    }

    private BindingViewHolder getBindingViewHolder(int layoutResId, ViewGroup viewGroup) {
        return new BindingViewHolder(getViewDataBinding(layoutResId, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        holder.binding.setVariable(BR.item, getItem(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        listSize.set(itemCount);
        return itemCount;
    }

    public ObservableInt getListSize() {
        return listSize;
    }
}
