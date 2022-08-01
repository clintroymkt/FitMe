package demo.minttihealth.adapter.vh;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ccl on 2016/9/30.
 */

public class BindingViewHolder extends RecyclerView.ViewHolder {

    public final ViewDataBinding binding;

    public BindingViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
