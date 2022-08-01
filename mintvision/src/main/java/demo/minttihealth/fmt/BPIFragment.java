//package demo.minttihealth.fmt;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.databinding.ViewDataBinding;
//
//import demo.minttihealth.health.R;
//
///**
// * Created by ccl on 2017/11/2.
// * BP measurement by ECG & PPG.
// */
//
//public class BPIFragment extends BaseFragment implements View.OnClickListener {
//
//    @Override
//    public int getTitle() {
//        return 0;
//    }
//
//    @Override
//    protected int onLayoutRes() {
//        return 0;
//    }
//
//    @Override
//    public void reset() {
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final View inflate = inflater.inflate(R.layout.fragment_bpi, container, false);
//        inflate.findViewById(R.id.btn).setOnClickListener(this);
//        return inflate;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }
//
//    @Override
//    protected void onViewBindingCreated(ViewDataBinding viewDataBinding, @Nullable Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public void onClick(View v) {
////        startActivity(new Intent(getContext(), BPIActivity.class));
//    }
//}
