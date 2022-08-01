package demo.minttihealth.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import demo.minttihealth.adapter.DataBindingAdapter;
import demo.minttihealth.bean.Bp;
import demo.minttihealth.bean.Bt;
import demo.minttihealth.bean.ECG;
import demo.minttihealth.bean.SpO2;
import demo.minttihealth.health.R;
import lib.linktop.common.CssSubscriber;
import lib.linktop.common.ResultPair;
import lib.linktop.obj.DataFile;
import lib.linktop.obj.LoadBean;
import lib.linktop.sev.HmLoadDataTool;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ccl on 2017/2/24.
 */

public class ShowDownloadDataActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener {

    private final static DataFile[] dataFiles = new DataFile[]{
            DataFile.DATA_BP, DataFile.DATA_BT, DataFile.DATA_SPO2, DataFile.DATA_ECG
    };
    public DataFile dataFile = dataFiles[0];
    private DataBindingAdapter<LoadBean> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_download_data);
        setUpActionBar("数据下载", "Data Download", true);
        AppCompatSpinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        mAdapter = new DataBindingAdapter<>(this, R.layout.item_hm_load_data);
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dataFile = dataFiles[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void clickDownload(View v) {
        mAdapter.clearItems();
        /*
         * @param dataFile 数据类型
         * @param target 账号
         * @param ts 请求的日期时间戳
         *
         * @param days 代表需要获取多少天的数据，days >= 1,不能为0，但也不要太大，数值越大请求越多，效率越低。
         *  例如以下downloadData1()，请求意思是，获取包括今天在内前一周（一共7天）的数据
         *     or
         * @param offset 代表若当天没数据，一直往前一天查询直到有数据的那一天，最多往前查几次。offset >= 0。
         * 例如以下downloadData2()，若一直往前查询直到第7天都没数据，则不再往前查询；否则，只要期间某一天有数据，直接返回那天的数据，不再往前查询。
         *
         * 由此，可以得出结论，函数downloadData1适合多天加载的情况（比如更新多天数据）
         *                 函数downloadData2适合数据列表加载更多历史数据的情况
         *
         * 另外，设备解绑后，服务器将清除设备对应的所有测量数据。
         * */
        Observable<ResultPair<List<LoadBean>>> observable = null;
        switch (dataFile) {
            case DATA_BP:
                observable = HmLoadDataTool.getInstance().downloadData1(dataFile, Bp.class, System.currentTimeMillis(), 7);
//                observable = HmLoadDataTool.getInstance().downloadData2(dataFile, Bp.class, System.currentTimeMillis(), 7);
                break;
            case DATA_BT:
                observable = HmLoadDataTool.getInstance().downloadData1(dataFile, Bt.class, System.currentTimeMillis(), 7);
//                observable = HmLoadDataTool.getInstance().downloadData2(dataFile, Bt.class, System.currentTimeMillis(), 7);
                break;
            case DATA_ECG:
                observable = HmLoadDataTool.getInstance().downloadData1(dataFile, ECG.class, System.currentTimeMillis(), 7);
//                observable = HmLoadDataTool.getInstance().downloadData2(dataFile, ECG.class, System.currentTimeMillis(), 7);
                break;
            case DATA_SPO2:
                observable = HmLoadDataTool.getInstance().downloadData1(dataFile, SpO2.class, System.currentTimeMillis(), 7);
//                observable = HmLoadDataTool.getInstance().downloadData2(dataFile, SpO2.class, System.currentTimeMillis(), 7);
                break;
        }
        if (observable != null)
            observable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CssSubscriber<List<LoadBean>>() {
                        @Override
                        public void onNextRequestSuccess(List<LoadBean> list) {
                            if (list == null || list.isEmpty()) {
                                toast("没有历史数据");
                            } else {
                                mAdapter.addItems(list);
                            }
                        }

                        @Override
                        public void onNextRequestFailed(int status) {
                            switch (status) {
                                case -1:
                                    toast("网络连接异常");
                                    break;
                                case 400:
                                    toast("请求失敗");
//                                toast("没有更多数据");
//                                    可能情况：1.参数错误
//                                            2.若检查后发现参数都没错，那可能是这种参数错误：
//                                              所请求的日期超过设备绑定日期的时间点。
//                                             （比如 2017.1.1绑定的设备，但请求的都是2017.1.1（不包含）之前的日期，因服务器把从绑定之日开始的日期当做有效参数
//                                               ，认为日期参数错误，若出现这种情况可认为是数据查询到尽头，没有更多历史数据）
                                    break;
                            }
                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("clickDownload", "onError:" + e.getMessage());
                        }
                    });
    }
}
