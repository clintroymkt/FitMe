package demo.minttihealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import android.view.View;

import demo.minttihealth.adapter.vh.BindingViewHolder;
import demo.minttihealth.health.BR;
import demo.minttihealth.health.R;
import demo.minttihealth.utils.UToast;
import lib.linktop.common.CssSubscriber;
import lib.linktop.obj.Device;
import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/3/9.
 */

public class BindDevListAdapter extends DataBindingAdapter<Device> {

    private final ObservableField<String> id;

    public BindDevListAdapter(Context context, ObservableField<String> id) {
        super(context, R.layout.item_bind_dev);
        this.id = id;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        super.onBindViewHolder(holder, position);
        holder.binding.setVariable(demo.minttihealth.health.BR.id, id);
        holder.binding.setVariable(BR.clickGetDevActiveInfo, (View.OnClickListener) v -> {
            final String devId = getItem(position).getDevId();
            CssServerApi.getDevActiveInfo(devId)
                    .subscribe(new CssSubscriber<String[]>() {
                        @Override
                        public void onNextRequestSuccess(String[] strings) {
                            final String s = "Device key:" + strings[0] + ",\nQR code:" + strings[1];
                            UToast.show(v.getContext(), s);
                        }

                        @Override
                        public void onNextRequestFailed(int status) {

                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        });
    }
}
