package com.kl.minttisdkdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kl.minttisdk.ble.BleUtils;
import com.kl.minttisdk.ble.callback.IUpgradeFirmListener;
import com.kl.minttisdk.ble.dfu.MinttiDfuManager;
import com.kl.minttisdkdemo.base.BaseActivity;
import com.kl.minttisdkdemo.base.utils.ZipUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirmwareListActivity extends BaseActivity implements IUpgradeFirmListener {

    private static final int HANDLER_CUR_PROGRESS = 1;
    private static final int HANDLER_MAX_PROGRESS = 2;
    private static final int HANDLER_TRANSFER_OVER = 3;
    private static final int HANDLER_ENTER_DFU = 4;
    private static final int HANDLER_SEND_INIT_PKG = 5;
    private static final int HANDLER_SEND_FIRMWARE = 6;
    private static final int HANDLER_UPDATE_SUCCESS = 7;

    private FirmwareAdapter firmwareAdapter;
    private TextView tvMsg;
    private RecyclerView rvFirmware;
    private List<File> fileList= new ArrayList<>();
    private MinttiDfuManager minttiDfuManager;
    private File zipDir;
    private File unZipDir;
    private String bleAddress;
    private ProgressDialog mUpdateProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmwate_list);
        bleAddress=getIntent().getStringExtra("BLE_ADDRESS");
        initView();


    }

    private void initView() {

        tvMsg = findViewById(R.id.tv_msg);
        rvFirmware = findViewById(R.id.rv_firmware_list);
        zipDir = new File(Environment.getExternalStorageDirectory(),"MinttiSdkDemo/zip");
        if ( !zipDir.exists() ){
            zipDir.mkdirs();
        }
        File[] files = zipDir.listFiles();

        if ( files == null || files.length == 0){
            tvMsg.setVisibility(View.VISIBLE);
        }else {
            for (File file:files){
                if (file.getName().endsWith(".zip")){
                    fileList.add(file);
                }
            }
            firmwareAdapter = new FirmwareAdapter(fileList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvFirmware.setLayoutManager(layoutManager);
            rvFirmware.setAdapter(firmwareAdapter);
            rvFirmware.addItemDecoration(new DividerItemDecoration(FirmwareListActivity.this,DividerItemDecoration.VERTICAL));
            firmwareAdapter.setOnItemClickListener(itemClickListener);
        }
    }


    private BaseQuickAdapter.OnItemClickListener itemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            File file = fileList.get(position);
            Log.d(TAG, "onItemClick: "+file.getName());
            unZipDir = new File(zipDir,getFileNameNoEx(file.getName()));
            if (unZipDir.exists()){
                for (File subFile : unZipDir.listFiles()){
                    Log.d(TAG, "delete "+subFile.getName()+" "+subFile.delete());
                }
                unZipDir.delete();
            }
            unZipDir.mkdirs();
            Log.d(TAG, "onItemClick: "+file.getAbsolutePath()+"  unZipDir = "+unZipDir.getAbsolutePath());
            ZipUtils.unZip(file.getAbsolutePath(),unZipDir.getAbsolutePath());
            //传入当前要更新设备的mac地址和已解压的固件文件夹路径
            minttiDfuManager = new MinttiDfuManager(FirmwareListActivity.this,bleAddress,unZipDir.getAbsolutePath());
            minttiDfuManager.setUpgradeFirmListener(FirmwareListActivity.this);
            BleUtils.getInstance().disconnect();
            if (minttiDfuManager.isCancel()){
                minttiDfuManager.setCancel(false);
            }
            minttiDfuManager.scanAndConnect();
            mUpdateProgressDialog = new ProgressDialog(FirmwareListActivity.this);
            mUpdateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mUpdateProgressDialog.setCanceledOnTouchOutside(false);
            mUpdateProgressDialog.setCancelable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                mUpdateProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (minttiDfuManager != null){
                            minttiDfuManager.setCancel(true);
                            minttiDfuManager.disconnect();
                        }
                        dialog.cancel();
                    }
                });
            }
            mUpdateProgressDialog.setTitle("固件更新中");
            mUpdateProgressDialog.setMessage("等待更新中。。。");
            mUpdateProgressDialog.show();
        }
    };

    @Override
    public void curProgress(int i) {
        Message msg = Message.obtain();
        msg.what = HANDLER_CUR_PROGRESS;
        msg.arg1 = i;
        handler.sendMessage(msg);
    }

    @Override
    public void maxProgress(int i) {
        Message msg = Message.obtain();
        msg.what = HANDLER_MAX_PROGRESS;
        msg.arg1 = i;
        handler.sendMessage(msg);
    }

    @Override
    public void enterDfu() {
        handler.sendEmptyMessage(HANDLER_ENTER_DFU);
    }

    @Override
    public void sendInitPkg() {
        handler.sendEmptyMessage(HANDLER_SEND_INIT_PKG);
    }

    @Override
    public void sendFirmware() {
        handler.sendEmptyMessage(HANDLER_SEND_FIRMWARE);
    }

    @Override
    public void updateSuccess() {
        handler.sendEmptyMessage(HANDLER_UPDATE_SUCCESS);
    }


    @Override
    public void transferOver() {
        handler.sendEmptyMessage(HANDLER_TRANSFER_OVER);
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case HANDLER_ENTER_DFU:
                    mUpdateProgressDialog.setMessage("设备正在进入DFU模式,会黑屏");
                    break;
                case HANDLER_SEND_INIT_PKG:
                    mUpdateProgressDialog.setMessage("发送初始化包");
                    break;
                case HANDLER_SEND_FIRMWARE:
                    mUpdateProgressDialog.setMessage("发送固件包中");
                    break;
                case HANDLER_MAX_PROGRESS:
                    mUpdateProgressDialog.setMax(msg.arg1);
                    break;
                case HANDLER_CUR_PROGRESS:
                    mUpdateProgressDialog.setProgress(msg.arg1);
                    break;
                case HANDLER_UPDATE_SUCCESS:
                    mUpdateProgressDialog.setMessage("更新成功");
                    mUpdateProgressDialog.cancel();
                    showToast("更新成功，等待设备重启");
                    break;
                case HANDLER_TRANSFER_OVER:
                    mUpdateProgressDialog.cancel();
                    break;



            }
        }
    };


    class FirmwareAdapter extends BaseQuickAdapter<File, BaseViewHolder>{

        public FirmwareAdapter(@Nullable List<File> data) {
            super(R.layout.item_firmware_list,data);
        }
        @Override
        protected void convert(BaseViewHolder helper, File item) {
            helper.setText(R.id.tv_firmware_file_name,item.getName());
        }
    }

    //获取文件名不带后缀
    private  String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    @Override
    protected void onDestroy() {
        if (mUpdateProgressDialog != null && mUpdateProgressDialog.isShowing()){
            mUpdateProgressDialog.cancel();
        }
        super.onDestroy();

    }
}
