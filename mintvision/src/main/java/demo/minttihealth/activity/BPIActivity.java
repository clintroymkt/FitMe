//package demo.minttihealth.activity;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import androidx.annotation.NonNull;
//import android.util.Log;
//import android.util.Pair;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.minttihealth.MonitorDataTransmissionManager;
//import com.minttihealth.constant.UserDataType;
//import com.minttihealth.infs.OnBpiResultListener;
//import com.minttihealth.whealthService.MeasureType;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//import demo.minttihealth.fmt.SetUserInfoDialogFragment;
//import demo.minttihealth.health.R;
//import demo.minttihealth.widget.WaveSurfaceView;
//import demo.minttihealth.widget.wave.ECG$PPGDrawWave;
//
////104 57 76
////110 60 77
//
//public class BPIActivity extends BaseActivity {
//
//    private WaveSurfaceView waveSurfaceView;
//    private ECG$PPGDrawWave ppgWave;
//    private TextView tvGender;
//    private TextView tvAge;
//    private TextView tvHeight;
//    private TextView tvWeight;
//    private TextView tvTimer;
//    private TextView tvHz;
//    private TextView tvPPGSq;
//    private TextView tvECGSq;
//    private TextView tvResult;
//    private Button btnDone;
//
//
//    private boolean isStart = false;
//    private int[] userHealthInfo;
//    //    private int count;
//    private final StringBuilder sbECG = new StringBuilder();
//    private final StringBuilder sbPPG = new StringBuilder();
//    private List<int[]> originalDataList;
//    private int index = 0;
//    private OnBpiResultListener listenerPPG = new OnBpiResultListener() {
//        @Override
//        public void onWaveData(Pair<Integer, Float> data) {
//            ppgWave.addData(data);
//            index++;
//        }
//
//
//        /**
//         * @param validRRICount ???????????????????????????????????????60?????????????????????????????????????????????????????????
//         * @param sbp ?????????
//         * @param dbp ?????????
//         * @param hr ??????
//         * */
//        @Override
//        public void onResult(int validRRICount, int sbp, int dbp, int hr) {
//            setResult(validRRICount, sbp, dbp, hr);
//        }
//
//        @Override
//        public void onFinish() {
//            Toast.makeText(getBaseContext(), "????????????", Toast.LENGTH_SHORT).show();
//            resetUi();
//        }
//
//        /**
//         * @param ecgSq ECG???????????????ecgSq = 0 ??????????????????ecgSq = 1 ??????????????????
//         * @param ppgSq PPG??????????????????????????????
//         * <p>???????????????????????????????????????????????????????????????
//         *    ??????????????????????????????????????????????????????</p>
//         * */
//        @Override
//        public void onDataSignQuality(int ecgSq, int ppgSq) {
//            // ??????????????????????????????????????????ECG?????????PPG?????????????????????????????????????????????
//            tvECGSq.setTextColor(ecgSq == 0 ? Color.GREEN : Color.RED);
//            tvPPGSq.setTextColor(ppgSq == 0 ? Color.GREEN : Color.RED);
//        }
//
//        @Override
//        public void onException(String excStr) {
//            Toast.makeText(getBaseContext(), "?????????????????????????????????\n" + excStr, Toast.LENGTH_SHORT).show();
//            resetUi();
//        }
////
////        @Override
////        public void onDebugData(final List<int[]> originalDataList) {
////            MainActivity.this.originalDataList = originalDataList;
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                final int i = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
////                if (i != PackageManager.PERMISSION_GRANTED) {
////                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);
////                    return;
////                }
////            }
////            saveOriginalData();
////        }
//
//    };
//    //    private Timer timer;
//    private Handler timerHandler;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bpi);
//        /*
//         * ????????????debug??????
//         * ????????????{@link OnPPGResultListener#onDebugData}???????????????PPG?????????????????????
//         * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
//         * */
////        HLog.DEBUG = true;
//        waveSurfaceView = findViewById(R.id.waveSurfaceView);
//        //??????PPG????????????????????????WaveSurfaceView??????
//        // WaveSurfaceView????????????PPGDrawWave?????????????????????
//        ppgWave = new ECG$PPGDrawWave();
//        waveSurfaceView.setDrawWave(ppgWave);
//
//        tvGender = findViewById(R.id.tv_gender);
//        tvAge = findViewById(R.id.tv_age);
//        tvHeight = findViewById(R.id.tv_height);
//        tvWeight = findViewById(R.id.tv_weight);
//        tvTimer = findViewById(R.id.tv_time);
//        tvHz = findViewById(R.id.tv_hz);
//        tvPPGSq = findViewById(R.id.tv_ppg_sq);
//        tvECGSq = findViewById(R.id.tv_ecg_sq);
//        tvResult = findViewById(R.id.tv_result);
//        btnDone = findViewById(R.id.btn_done);
//        userHealthInfo = new int[4];
//        userHealthInfo[UserDataType.SEX.index] = 1;
//        userHealthInfo[UserDataType.AGE.index] = 27;
//        userHealthInfo[UserDataType.HEIGHT.index] = 170;
//        userHealthInfo[UserDataType.WEIGHT.index] = 65;
//        setUsrHealthInfo();
//        setResult(0, 0, 0, 0);
//        MonitorDataTransmissionManager.getInstance().setOnPPGDataCallback(listenerPPG);
//    }
//
//    @Override
//    protected void onResume() {
//        //??????SurfaceView?????????????????????
//        waveSurfaceView.reply();
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //??????SurfaceView?????????????????????
//        waveSurfaceView.pause();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 666) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
//                    final int grantResult = grantResults[i];
//                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                        saveOriginalData();
//                    }
//                }
//            }
//        }
//    }
//
//    private void saveOriginalData() {
//        if (originalDataList == null || originalDataList.isEmpty()) return;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final String gender = tvGender.getText().toString();
//                final String age = tvAge.getText().toString();
//                final String Height = tvHeight.getText().toString();
//                final String Weight = tvWeight.getText().toString();
//                final String result = tvResult.getText().toString();
//                final int size = originalDataList.size();
//                StringBuilder sb = new StringBuilder();
//                sb.append("???????????????\n")
//                        .append(gender).append("\n")
//                        .append(age).append("\n")
//                        .append(Height).append("\n")
//                        .append(Weight).append("\n\n");
//                sb.append("????????????\n")
//                        .append(result).append("\n");
//                for (int i = 0; i < size; i++) {
//                    sb.append(i).append(",");
//                    final int[] data = originalDataList.get(i);
//                    try {
//                        sb.append(data[0]);
//                    } catch (IndexOutOfBoundsException e) {
//                        sb.append(" - ");
//                    }
//                    sb.append(",");
//                    try {
//                        sb.append(data[1]);
//                    } catch (IndexOutOfBoundsException e) {
//                        sb.append(" - ");
//                    }
//                    sb.append("\n");
//                }
//                final String string = sb.toString();
//                try {
//                    final String filePath = save("PpgData_" + System.currentTimeMillis() + ".txt", string);
//                    Log.e("TestPPGActivity", "File save successful. - " + filePath);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getBaseContext(), "????????????????????????\n?????????" + filePath, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    public void clickModifyUserInfo(final View v) {
//        final SetUserInfoDialogFragment dialogFragment = new SetUserInfoDialogFragment();
//        dialogFragment.setUserHealthInfo(userHealthInfo);
//        dialogFragment.setOnPositiveListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                userHealthInfo = dialogFragment.getUserHealthInfo();
//                setUsrHealthInfo();
//            }
//        });
//        dialogFragment.show(getFragmentManager(), dialogFragment.getClass().getSimpleName());
//    }
//
//    private void resetUi() {
//        if (timerHandler != null) {
//            timerHandler.removeCallbacksAndMessages(null);
//            timerHandler = null;
//        }
//        isStart = false;
//        btnDone.setText("??????");
//        tvECGSq.setTextColor(Color.GRAY);
//        tvPPGSq.setTextColor(Color.GRAY);
//        index = 0;
//    }
//
//    public void clickTest(View v) {
//
//        if (isStart) {
//            MonitorDataTransmissionManager.getInstance().stopMeasure();
//            resetUi();
//        } else {
//            switch (canStartTest()) {
//                case 0:
//                    clickModifyUserInfo(null);
//                    break;
//                case 2:
//                    startTest();
//                    break;
//            }
//        }
//    }
//
//    private void startTest() {
//        sbECG.setLength(0);
//        sbPPG.setLength(0);
//        isStart = true;
//        btnDone.setText("??????");
//        ppgWave.clear();
//        setResult(0, 0, 0, 0);
//        MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.BPI);
//        timerHandler = new Handler();
//        timerHandler.post(new Runnable() {
//
//            private int i;
//
//            @Override
//            public void run() {
//                tvHz.setText(String.format(Locale.getDefault(), "???????????????%1$.2f Hz", this.i == 0 ? 0 : (index / (float) this.i)));
//                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", this.i / 60, this.i % 60));
//                this.i++;
//                timerHandler.postDelayed(this, 1000L);
//            }
//        });
//    }
//
//    private void setUsrHealthInfo() {
//        //??????PPG????????????????????????????????????????????????????????????????????????????????????????????????
//        tvGender.setText(getString(R.string.gender_value, (userHealthInfo == null ? "??????" : (userHealthInfo[0] == 1 ? "??????" : "??????"))));
//        tvAge.setText(getString(R.string.age_value, userHealthInfo == null ? 0 : userHealthInfo[1]));
//        tvHeight.setText(getString(R.string.height_value, userHealthInfo == null ? 0 : userHealthInfo[2]));
//        tvWeight.setText(getString(R.string.weight_value, userHealthInfo == null ? 0 : userHealthInfo[3]));
////        MonitorDataTransmissionManager.getInstance().setPPGUserInfo(userHealthInfo);
//    }
//
//    private int canStartTest() {
//        int x = 0;
//        if (userHealthInfo != null) {
//            boolean b1 = userHealthInfo[1] == 0 || userHealthInfo[2] == 0 || userHealthInfo[3] == 0;
//            x = b1 ? 0 : 2;
//        }
//        return x;
//    }
//
//
//    /**
//     * @param sbp ?????????
//     * @param dbp ?????????
//     * @param hr  ??????
//     */
//    private void setResult(int validRRICount, int sbp, int dbp, int hr) {
//        String result = String.format(Locale.getDefault(), "???????????????%d ???"
//                        + "\n????????????SBP??????%d mmHg"
//                        + "\n????????????DBP??????%d mmHg"
//                        + "\n?????????HR??????%d BPM",
//                validRRICount, sbp, dbp, hr);
//        tvResult.setText(result);
//    }
//
//
//    public static String save(String fileName, final String content) throws IOException {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            // ??????SD????????????
//            File sdDire = Environment.getExternalStorageDirectory();
//            final String filePath = sdDire.getCanonicalPath() + File.separator + "PPGData" + File.separator + fileName;
//            File file = new File(filePath);
//            final File parentFile = file.getParentFile();
//            if (!parentFile.exists()) //noinspection ResultOfMethodCallIgnored
//                parentFile.mkdir();
//            FileOutputStream outFileStream = new FileOutputStream(file);
//            outFileStream.write(content.getBytes());
//            outFileStream.close();
//            return filePath;
//        }
//        return "null";
//    }
//}
