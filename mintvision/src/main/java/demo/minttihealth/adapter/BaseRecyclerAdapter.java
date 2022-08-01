package demo.minttihealth.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import demo.minttihealth.widget.CustomRecyclerView;

/**
 * Created by ccl on 2016/8/5.
 * BaseRecyclerAdapter
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements View.OnClickListener, View.OnLongClickListener {

    protected final Context mContext;
    private final ArrayList<T> mList = new ArrayList<>();

    private CustomRecyclerView rv;
    private OnRecyclerSizeListener listener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public boolean isFooter(int position) {
        return position == getItemCount() - 1;
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public void addItem(T item) {
        int pos = getItemCount();
        mList.add(item);
        notifyItemInserted(pos);
    }

    public void addItemToFirst(T item) {
        mList.add(0, item);
        notifyItemInserted(0);
    }

    public void addItemToFirstAndNotifyAll(T item) {
        mList.add(0, item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mList.remove(position);
        if (mList.isEmpty()) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }
    }

    public void addItems(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setItems(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mList.clear();
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return mList;
    }

    @Override
    public int getItemCount() {
        final int size = mList.size();
        if (listener != null) listener.onRecyclerSize(size == 0);
        return size;
    }

    protected View getLayoutInflate(int layoutResId, ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        inflate.setOnClickListener(this);
        inflate.setOnLongClickListener(this);
        return inflate;
    }


    protected ViewDataBinding getViewDataBinding(int layoutResId, ViewGroup viewGroup) {
        final ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutResId, viewGroup, false);
        final View root = binding.getRoot();
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        return binding;
    }

    @Override
    public void onClick(View v) {
        if (rv.getItemClickListener() != null) {
            int layoutPosition = rv.getChildViewHolder(v).getLayoutPosition();
            rv.getItemClickListener().onItemClick(v, layoutPosition);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (rv.getItemLongClickListener() != null) {
            int layoutPosition = rv.getChildViewHolder(v).getLayoutPosition();
            rv.getItemLongClickListener().onItemLongClick(v, layoutPosition);
        }
        return true;
    }

    public void setContent(CustomRecyclerView recyclerView) {
        this.rv = recyclerView;
    }

    protected String getStringFormat(int formatResId, Object... args) {
        return String.format(mContext.getString(formatResId), args);
    }

    public void setOnRecyclerSizeListener(OnRecyclerSizeListener listener) {
        this.listener = listener;
    }

    public void updateItem(int position, T t) {
        mList.remove(position);
        mList.add(position, t);
        notifyItemChanged(position);
    }

    public interface OnRecyclerSizeListener {

        void onRecyclerSize(boolean isEmpty);
    }
}
