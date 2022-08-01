package demo.minttihealth.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import demo.minttihealth.adapter.BaseRecyclerAdapter;

/**
 * Created by ccl on 2016/4/28.
 * CustomRecyclerView
 */
public class CustomRecyclerView extends RecyclerView {

    private RecyclerItemClickListener itemClickListener;
    private RecyclerItemLongClickListener itemLongClickListener;
    private OnRecyclerLoadMore loadMoreListener;
    private boolean isLoadMore;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        addOnScrollListener(new AutoLoadScrollListener());
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        addOnScrollListener(new AutoLoadScrollListener());
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        try {
            ((BaseRecyclerAdapter<?, ?>) adapter).setContent(this);
        } catch (ClassCastException e) {
            e.fillInStackTrace();
        }
    }



    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(RecyclerItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public final RecyclerItemClickListener getItemClickListener() {
        return this.itemClickListener;
    }

    public final RecyclerItemLongClickListener getItemLongClickListener() {
        return this.itemLongClickListener;
    }

    public interface RecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface RecyclerItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.onInterceptTouchEvent(e);
    }

    public void setLoadMoreListener(OnRecyclerLoadMore loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public interface OnRecyclerLoadMore {

        void onLoadMore(int lastPosition);
    }

    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private LinearLayoutManager llManager;
        private boolean isSlidingDown;

        private AutoLoadScrollListener() {
        }


        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isSlidingDown = dy > 0;
        }

        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；由于用户的操作，屏幕产生惯性滑动时为2
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //根据newState状态做处理
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (isLoadMore)
                        return;
                    if (llManager == null)
                        llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItem = llManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = llManager.getItemCount();
                    if (lastVisibleItem == totalItemCount - 1 && isSlidingDown) {
                        isLoadMore = true;
                        if (loadMoreListener != null)
                            loadMoreListener.onLoadMore(totalItemCount);
                    }
                    break;
            }
        }
    }
}
