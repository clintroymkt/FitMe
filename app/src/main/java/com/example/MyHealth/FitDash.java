package com.example.MyHealth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.crrepa.ble.conn.bean.CRPStepInfo;
import com.crrepa.ble.conn.bean.CRPStepsCategoryInfo;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.conn.listener.CRPBleECGChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.listener.CRPSleepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepsCategoryChangeListener;
import com.crrepa.ble.conn.type.CRPEcgMeasureType;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FitDash extends AppCompatActivity {
    private static final String TAG = "DeviceActivity";
    public static final String DEVICE_MACADDR = "device_macaddr";


    Handler stephandler = new Handler();
    Handler ringhandler = new Handler();
    ProgressDialog mProgressDialog;
    CRPBleClient mBleClient;
    CRPBleDevice mBleDevice;
    CRPBleConnection mBleConnection;
    boolean isUpgrade = false;
    private RelativeLayout calorieCounter, bpCalculator, heartRateScanner, FatCalculator, bmiCalculator, drinkWater, pedoMeter;
    private TextView todayDate;
    private String currentDate;
    private TextView dailyProteinReq, bpm, bmi, fatPercentage, Calories, walk, water;
    DBHelper DB = new DBHelper(this);

    @BindView(R.id.fittv_connect_state)
    TextView tvConnectState;
    @BindView(R.id.steps)
    TextView txtStep;
    @BindView(R.id.distance)
    TextView tvDistance;
    @BindView(R.id.calories)
    TextView tvCalorie;

    @BindView(R.id.bpm)
    TextView tvHeartRate;
    @BindView(R.id.tv_blood_pressure)
    TextView tvBloodPressure;
    @BindView(R.id.tvSPO2)
    TextView tvBloodOxygen;
    @BindView(R.id.tv_watchrestful)
    TextView tvwatchrestful;
    @BindView(R.id.tv_watchlight)
    TextView tvwatchlight;
    @BindView(R.id.btn_ble_connect_state)
    Button btnBleDisconnect;
    @BindView(R.id.stepdateinv)
    TextView stepdateinv;
    @BindView(R.id.stepgraphdateinv)
    TextView stepgraphdateinv;
    @BindView(R.id.bptimeinv)
    TextView bptimeinv;
    @BindView(R.id.sp02timeinv)
    TextView spo2timeinv;
    @BindView(R.id.ringnumberinv)
    TextView stepringtext;

    @BindView(R.id.tvringscompleted)
    TextView ringcounter;

    @BindView(R.id.lightpercinv)
    TextView lightpercentagetxt;
    @BindView(R.id.restfulpercinv)
    TextView restfulpercentagetxt;
    @BindView(R.id.totaltimeinv)
    TextView totaltimetxt;
    @BindView(R.id.txtSleepScore)
    TextView sleepscoreTXT;
    @BindView(R.id.tvSleepState)
    TextView sleepstatelabel;
    @BindView(R.id.tvwarnings)
    TextView warningTXT;

    TextView textgoal;
    TextView textsteps;

    TextView steppercentage;

    TextView textcaloriegoal;
    TextView textcalories;
    TextView calopercentage;



    int ringnumber = 0;
    float totalTime = 0;
    float lightperc = 0;
    float restfulperc = 0;
    float timeperc= 0;
    float qualscore = 0;
    float sleepscore = 0;
    float idealTime = 480;
    int warningcounter = 0;
    int ringwarning = 0;
    int sleepwarning = 0;
    int spo2warning = 0;

    RelativeLayout SPO2card;
    RelativeLayout BPcard;
    RelativeLayout Backupcard;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_dash);


        todayDate= findViewById(R.id.date);
        ButterKnife.bind(this);
        initView();
        mProgressDialog = new ProgressDialog(this);
        String macAddr = getIntent().getStringExtra(DEVICE_MACADDR);
        if (TextUtils.isEmpty(macAddr)) {
            finish();
            return;
        }
        mBleClient = SampleApplication.getBleClient(this);
        mBleDevice = mBleClient.getBleDevice(macAddr);
        if (mBleDevice != null) {
            connect();
        }

        spo2timeinv = findViewById(R.id.sp02timeinv);
        bptimeinv =findViewById(R.id.bptimeinv);
        stepdateinv = findViewById(R.id.stepdateinv);
        stepgraphdateinv = findViewById(R.id.stepgraphdateinv);
        calorieCounter = findViewById(R.id.card1);
        heartRateScanner = findViewById(R.id.card2);
        //  bpCalculator = findViewById(R.id.card3);
        drinkWater = findViewById(R.id.card6);
    //    bmiCalculator = findViewById(R.id.card7);
    //    FatCalculator = findViewById(R.id.card10);
        pedoMeter = findViewById(R.id.card5);

        dailyProteinReq = findViewById(R.id.tv_blood_pressure);
        bpm = findViewById(R.id.bpm);
        //  bmi = findViewById(R.id.bmi);
      //  fatPercentage = findViewById(R.id.fatPercentage);
        Calories = findViewById(R.id.calories);
        walk = findViewById(R.id.steps);
        //  water = findViewById(R.id.litre);

        SimpleDateFormat formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("MMMM d,YYYY");
        }
        Date date = new Date();
        currentDate = formatter.format(date);

        todayDate.setText(currentDate);
        repeatStepSave();

        runringrunnable();

        textgoal = findViewById(R.id.goal);
        textsteps = findViewById(R.id.steps);
        textcaloriegoal = findViewById(R.id.calGoal);
        textcalories = findViewById(R.id.calories);

        steppercentage = findViewById(R.id.steppercentageinv);
        calopercentage = findViewById(R.id.calopercentageinv);



        ringcounter.setText(DB.ringSum());

        SPO2card = findViewById(R.id.card8);
        BPcard = findViewById(R.id.card3);
        Backupcard =findViewById(R.id.cardbackup);

        BPcard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                OpenBP();
                return false;
            }
        });

        SPO2card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                OpenSPO2();
                return false;
            }
        });

        Backupcard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                OpenBackup();
                return false;
            }
        });







        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String goalstring = textgoal.getText().toString();
                String stepstring = textsteps.getText().toString();
                String calogoalstring = textcaloriegoal.getText().toString();
                String calostring = textcalories.getText().toString();
                String reststring = tvwatchrestful.getText().toString();
                String lightstring = tvwatchlight.getText().toString();




                float goalValue = Float.parseFloat(goalstring);
                float stepValue = Float.parseFloat(stepstring);
                float calogoalValue = Float.parseFloat(calogoalstring);
                float caloValue = Float.parseFloat(calostring);
                float restValue =Float.parseFloat(reststring);
                float lightValue = Float.parseFloat(lightstring);

                totalTime = restValue + lightValue;
                timeperc = (totalTime/idealTime) * 65;

                restfulperc = (restValue/totalTime) * 100;

                lightperc = (lightValue/totalTime) * 100;

                qualscore = ((restfulperc + lightperc)/200) * 35;
                sleepscore = timeperc + qualscore;



                float caloprogress = (caloValue/calogoalValue) * 100;

                float stepprogress = (stepValue/goalValue) * 100;


                int timefinal = Math.round(totalTime);
                int lighfinal = Math.round(restfulperc);
                int restfinal = Math.round(lightperc);
                int sleepscorefinal = Math.round(sleepscore);

                int caloperfinal = Math.round(caloprogress);
                int perfinal = Math.round(stepprogress);

                sleepscoreTXT.setText(String.valueOf(sleepscorefinal));
                totaltimetxt.setText(String.valueOf(timefinal));
                lightpercentagetxt.setText(String.valueOf(lighfinal));
                restfulpercentagetxt.setText(String.valueOf(restfinal));
                sleepstatelabel.setText(String.valueOf(sleepscorefinal));


                steppercentage.setText(String.valueOf(perfinal));
                calopercentage.setText(String.valueOf(caloperfinal));

                String calopercentstring = calopercentage.getText().toString();

                String percentstring = steppercentage.getText().toString();

                String timestring = totaltimetxt.getText().toString();

                String lightpercstring = lightpercentagetxt.getText().toString();
                String restfulperstring = restfulpercentagetxt.getText().toString();

                if (sleepscorefinal < 60){
                    sleepwarning++;
                } else if(sleepwarning >=1 && sleepscorefinal >=60) {
                    sleepwarning--;
                }else {

                }

                if(ringnumber < 3){
                    ringwarning++;
                } else if(ringwarning >=1 && ringnumber >=1) {
                    ringnumber--;
                }else {

                }
                warningcounter = sleepwarning + spo2warning + ringwarning;
                warningTXT.setText(String.valueOf(warningcounter));




                CircularProgressBar progressBar = findViewById(R.id.totalStepProgressBar);
                progressBar.setProgress(
                        Float.parseFloat(percentstring)
                );

                CircularProgressBar caloprogressBar = findViewById(R.id.caloprogress_bar);
                caloprogressBar.setProgress(
                        Float.parseFloat(calopercentstring)
                );

                if (perfinal >= 100) {
                    ringnumber = 1;
                    Toast.makeText(FitDash.this, "You Have Completed 10000 Steps!)", Toast.LENGTH_LONG).show();
                }else{
                    ringnumber = 0;
                }
            }
        }, 10000);







        //new Handler().postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
         //       saveBPinfo();
        //    }
       // }, 25000);

        //   DB = new DBHelper(this);









        //bpCalculator.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //    public void onClick(View view) {
        //         mBleConnection.startMeasureDynamicRate();
        //     }
        //  });


    }

    private void OpenBackup() {
        startActivity(new Intent(FitDash.this, Backupactivity.class));
    }

    private void OpenBP() {
        saveBPinfo();
        startActivity(new Intent(FitDash.this, BPpage.class));

    }

    private void OpenSPO2() {
        saveSPOinfo();
        startActivity(new Intent(FitDash.this, SPO2page.class));

    }

    private void runringrunnable() {
        ringRunnable.run();
    }




    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e(TAG, "ORIENTATION_LANDSCAPE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e(TAG, "ORIENTATION_PORTRAIT");
        }
    }

    //  @Override
    //  protected void onDestroy() {
    //      super.onDestroy();
    //      if (mBleDevice != null) {
    //          mBleDevice.disconnect();
    //      }
    //   }

    void initView() {
        updateStepInfo(0, 0, 0);
        updateSleepInfo(0, 0);
        //  saveStepInfo();
    }



    void connect() {
        mProgressDialog.show();
        mBleConnection = mBleDevice.connect();
        mBleConnection.setConnectionStateListener(new CRPBleConnectionStateListener() {
            @Override
            public void onConnectionStateChange(int newState) {
                Log.d(TAG, "onConnectionStateChange: " + newState);
                int state = -1;
                switch (newState) {
                    case CRPBleConnectionStateListener.STATE_CONNECTED:
                        state = R.string.state_connected;
                        mProgressDialog.dismiss();

                        updateTextView(btnBleDisconnect, getString(R.string.disconnect));
                        testSet();




                        break;
                    case CRPBleConnectionStateListener.STATE_CONNECTING:
                        state = R.string.state_connecting;
                        break;
                    case CRPBleConnectionStateListener.STATE_DISCONNECTED:
                        state = R.string.state_disconnected;
                        mProgressDialog.dismiss();
                        updateTextView(btnBleDisconnect, getString(R.string.connect));
                        break;
                }
                updateConnectState(state);

            }
        });
        mBleConnection.setStepChangeListener(mStepChangeListener);
        mBleConnection.setSleepChangeListener(mSleepChangeListener);
        mBleConnection.setHeartRateChangeListener(mHeartRateChangListener);
        mBleConnection.setBloodPressureChangeListener(mBloodPressureChangeListener);
        mBleConnection.setBloodOxygenChangeListener(mBloodOxygenChangeListener);
        // mBleConnection.setFindPhoneListener(mFindPhoneListener);
        //  mBleConnection.setECGChangeListener(mECGChangeListener, CRPEcgMeasureType.TYHX);
        mBleConnection.setStepsCategoryListener(mStepsCategoryChangeListener);



    }

    private void testSet() {
        Log.d(TAG, "testSet");
        mBleConnection.syncTime();
        mBleConnection.queryPastHeartRate();
        mBleConnection.syncSleep();
        mBleConnection.syncStep();


//        sendFindBandMessage();


    }

    Runnable ringRunnable = new Runnable() {
        @Override
        public void run() {
            ringcounter.setText(DB.ringSum());

            textgoal = findViewById(R.id.goal);
            textsteps = findViewById(R.id.steps);
            textcaloriegoal = findViewById(R.id.calGoal);
            textcalories = findViewById(R.id.calories);

            steppercentage = findViewById(R.id.steppercentageinv);
            calopercentage = findViewById(R.id.calopercentageinv);

            String goalstring = textgoal.getText().toString();
            String stepstring = textsteps.getText().toString();
            String calogoalstring = textcaloriegoal.getText().toString();
            String calostring = textcalories.getText().toString();
            String reststring = tvwatchrestful.getText().toString();
            String lightstring = tvwatchlight.getText().toString();




            float goalValue = Float.parseFloat(goalstring);
            float stepValue = Float.parseFloat(stepstring);
            float calogoalValue = Float.parseFloat(calogoalstring);
            float caloValue = Float.parseFloat(calostring);
            float restValue =Float.parseFloat(reststring);
            float lightValue = Float.parseFloat(lightstring);

            totalTime = restValue + lightValue;
            timeperc = (totalTime/idealTime) * 65;

            restfulperc = (restValue/totalTime) * 100;

            lightperc = (lightValue/totalTime) * 100;

            qualscore = ((restfulperc + lightperc)/200) * 35;
            sleepscore = timeperc + qualscore;



            float caloprogress = (caloValue/calogoalValue) * 100;

            float stepprogress = (stepValue/goalValue) * 100;


            int timefinal = Math.round(timeperc);
            int lighfinal = Math.round(restfulperc);
            int restfinal = Math.round(lightperc);
            int sleepscorefinal = Math.round(sleepscore);

            int caloperfinal = Math.round(caloprogress);
            int perfinal = Math.round(stepprogress);

            sleepscoreTXT.setText(String.valueOf(sleepscorefinal));
            totaltimetxt.setText(String.valueOf(timefinal));
            lightpercentagetxt.setText(String.valueOf(lighfinal));
            restfulpercentagetxt.setText(String.valueOf(restfinal));
            sleepstatelabel.setText(String.valueOf(sleepscorefinal));

            steppercentage.setText(String.valueOf(perfinal));
            calopercentage.setText(String.valueOf(caloperfinal));

            String calopercentstring = calopercentage.getText().toString();

            String percentstring = steppercentage.getText().toString();

          //  String timestring = totaltimetxt.getText().toString();

        //    String lightpercstring = lightpercentagetxt.getText().toString();
         //   String restfulperstring = restfulpercentagetxt.getText().toString();





            CircularProgressBar progressBar = findViewById(R.id.totalStepProgressBar);
            progressBar.setProgress(
                    Float.parseFloat(percentstring)
            );

            CircularProgressBar caloprogressBar = findViewById(R.id.caloprogress_bar);
            caloprogressBar.setProgress(
                    Float.parseFloat(calopercentstring)
            );
            if (perfinal >= 100) {
                ringnumber = 1;
                Toast.makeText(FitDash.this, "You Have Completed 10000 Steps!)", Toast.LENGTH_LONG).show();
            }else{
                ringnumber = 0;
            }

            ringhandler.postDelayed(this, 60000);

        }
    };

    private void sendFindBandMessage() {
        handler.sendEmptyMessageDelayed(1, 5000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mBleConnection.findDevice();
                sendFindBandMessage();
            }
        }
    };

    @OnClick(R.id.btn_ble_connect_state)
    public void onConnectStateClick() {
        //   if (mBleConnection ) {
        //      mBleDevice.disconnect();
        //    } else {
        connect();
        //   }
    }

    @OnClick({R.id.calorielayout,R.id.steplayout, R.id.heartratelayout,
            R.id.card3, R.id.card8, R.id.cardclose, R.id.card6,
            R.id.card5, R.id.cardbackup
    })
    public void onViewClicked(View view) {
        //     if (!mBleConnection.isConnected()) {
        //     return;
        //    }
        switch (view.getId()) {
            case R.id.steplayout:
                mBleConnection.syncStep();
                testSet();
                saveStepInfo();
                openSteps();
                break;
            case R.id.card3:
                mBleConnection.startMeasureBloodPressure();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveBPinfo();
                    }
                }, 25000);
                break;
            case R.id.card8:
                mBleConnection.startMeasureBloodOxygen();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveSPOinfo();
                    }
                }, 25000);
                break;
            case R.id.card6:
                mBleConnection.syncSleep();
                testSet();
                saveSleepInfo();
                openSleep();

                break;
            case R.id.card5:
                mBleConnection.syncStep();
                testSet();
                saveStepInfo();
                openSteps();
                break;
            case R.id.cardbackup:
                Toast.makeText(FitDash.this, "You're not logged in, please log into your fitme account", Toast.LENGTH_SHORT).show();
                break;


            case R.id.cardclose:

                //  super.onDestroy();
                if (mBleDevice != null) {
                    mBleDevice.disconnect();
                }
                finish();
                break;


            default:
                break;
        }
    }

    private void openSleep() {
        Intent intent = new Intent(FitDash.this, SleepPage.class);
        startActivity(intent);
    }

    private void openSteps() {
        Intent intent = new Intent(FitDash.this, StepsPage.class);
        startActivity(intent);
    }


    private void openBMI() {
        Intent intent = new Intent(FitDash.this, BmiActivity.class);
        startActivity(intent);
    }

    CRPStepsCategoryChangeListener mStepsCategoryChangeListener = new CRPStepsCategoryChangeListener() {
        @Override
        public void onStepsCategoryChange(CRPStepsCategoryInfo info) {
            List<Integer> stepsList = info.getStepsList();
            Log.d(TAG, "onStepsCategoryChange size: " + stepsList.size());
            for (int i = 0; i < stepsList.size(); i++) {
                Log.d(TAG, "onStepsCategoryChange: " + stepsList.get(i).intValue());
            }
        }
    };
    CRPStepChangeListener mStepChangeListener = new CRPStepChangeListener() {
        @Override
        public void onStepChange(CRPStepInfo info) {
            Log.d(TAG, "step: " + info.getSteps());
            updateStepInfo(info.getSteps(), info.getDistance(), info.getCalories());

        }

        @Override
        public void onPastStepChange(int timeType, CRPStepInfo info) {
            Log.d(TAG, "onPastStepChange: " + info.getSteps());

        }


    };

    CRPSleepChangeListener mSleepChangeListener = new CRPSleepChangeListener() {
        @Override
        public void onSleepChange(CRPSleepInfo info) {
            List<CRPSleepInfo.DetailBean> details = info.getDetails();
            if (details == null) {
                return;
            }
            Log.d(TAG, "soberTime: " + info.getSoberTime());
            updateSleepInfo(info.getRestfulTime(), info.getLightTime());
        }

        @Override
        public void onPastSleepChange(int timeType, CRPSleepInfo info) {
            Log.d(TAG, "onPastSleepChange: " + info.getTotalTime());
        }
    };

    CRPHeartRateChangeListener mHeartRateChangListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int rate) {
            Log.d(TAG, "onMeasuring: " + rate);
            updateTextView(tvHeartRate, String.format(getString(R.string.string_heart_rate), rate));
        }

        @Override
        public void onOnceMeasureComplete(int rate) {
            Log.d(TAG, "onOnceMeasureComplete: " + rate);
        }

        @Override
        public void onMeasureComplete(CRPHeartRateInfo info) {
            if (info != null && info.getMeasureData() != null) {
                for (Integer integer : info.getMeasureData()) {
                    Log.d(TAG, "onMeasureComplete: " + integer);
                }
            }
        }

        @Override
        public void on24HourMeasureResult(CRPHeartRateInfo info) {
            List<Integer> data = info.getMeasureData();
            Log.d(TAG, "on24HourMeasureResult: " + data.size());
        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {
            for (CRPMovementHeartRateInfo info : list) {
                if (info != null) {
                    Log.d(TAG, "onMovementMeasureResult: " + info.getStartTime());
                }
            }
        }

    };

    CRPBloodPressureChangeListener mBloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int sbp, int dbp) {
            Log.d(TAG, "sbp:" + sbp + "\n dbp: " + dbp);
            updateTextView(tvBloodPressure,
                    String.format(getString(R.string.watch_blood_pressure), sbp, dbp));

        }

    };

    public void repeatStepSave(){
       //stephandler.postDelayed(stepRunnable, 300000);
        stepRunnable.run();
    }

    private  Runnable stepRunnable = new Runnable() {
        @Override
        public void run() {
            saveStepInfo();
            saveSleepInfo();
            stephandler.postDelayed(this, 300000);
        }
    };



    private void saveBPinfo() {

        Date timedate = Calendar.getInstance().getTime();
        SimpleDateFormat formatted =  new SimpleDateFormat("hh:mm a", Locale.getDefault());



        String TIMEDATE = formatted.format((timedate));
        bptimeinv.setText(TIMEDATE);
        String timedateTXT = bptimeinv.getText().toString();
        String dateBPTXT = stepdateinv.getText().toString();
        String sysdialTXT = (String) tvBloodPressure.getText();


        Boolean checkinsertdata = DB.insertBPdata(timedateTXT,dateBPTXT,sysdialTXT
        );

        if (checkinsertdata == true) {
            Toast.makeText(FitDash.this, "BP Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(FitDash.this, "BP Update failed", Toast.LENGTH_SHORT).show();
        }

    }

    CRPBloodOxygenChangeListener mBloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onBloodOxygenChange(int bloodOxygen) {
            updateTextView(tvBloodOxygen,
                    String.format(getString(R.string.blood_oxygen), bloodOxygen));
        }
    };



    void updateConnectState(final int state) {
        if (state < 0) {
            return;
        }
        updateTextView(tvConnectState, getString(state));

    }
    void updateTextView(final TextView view, final String con) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(con);

            }

        });

    }

    void updateStepInfo(int step, int distance, int calories) {
        updateTextView(txtStep, String.format(getString(R.string.step), step));
        updateTextView(tvDistance, String.format(getString(R.string.distance), distance));
        updateTextView(tvCalorie, String.format(getString(R.string.calorie), calories));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveStepInfo();
            }
        }, 15000);





    }

    private void saveStepInfo() {

        Date stepdate = new Date();
        DateFormat formatted = new SimpleDateFormat("yyyy/MM/dd");

        String DATE = formatted.format(stepdate);
        stepdateinv.setText(DATE);
        String dateTXT = stepdateinv.getText().toString();

        Date stepgraphdate = new Date();
        DateFormat formatted2 = new SimpleDateFormat("dd-MM-yyyy");

        String DATE2 = formatted2.format(stepgraphdate);
        stepgraphdateinv.setText(DATE2);
        String dateGraphTXT = stepgraphdateinv.getText().toString();

        String stepTXT = (String) txtStep.getText();
        String distanceTXT = (String) tvDistance.getText();
        String calorieTXT = (String) tvCalorie.getText();
        stepringtext.setText(String.valueOf(ringnumber));
        String ringTXT =(String) stepringtext.getText();


        Boolean checkinsertdata = DB.insertstepsdata(dateTXT, stepTXT,
                dateGraphTXT, distanceTXT,ringTXT, calorieTXT
        );

        if (checkinsertdata == true) {
            Toast.makeText(FitDash.this, "Data Updated", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(FitDash.this, "data failed", Toast.LENGTH_SHORT).show();
            Boolean checkupdatedata = DB.updatestepsdata(dateTXT, stepTXT,
                    dateGraphTXT, distanceTXT,ringTXT, calorieTXT
            );
            if (checkupdatedata == true) {

            } else {
                Toast.makeText(FitDash.this, "Failed to update steps", Toast.LENGTH_SHORT).show();
            }


        }
    }
    //  }


    void updateSleepInfo(int restful, int light) {
        updateTextView(tvwatchrestful, String.format(getString(R.string.restful), restful));
        updateTextView(tvwatchlight, String.format(getString(R.string.light), light));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveSleepInfo();
            }
        }, 15000);
    }


    private void saveSleepInfo() {
//  Date dateTXT = currentDate.ge

        //stepdateinv.setText(R.id.stepdateinv);
        Date stepdate = new Date();
        DateFormat formatted = new SimpleDateFormat("yyyy/MM/dd");

        String DATE = formatted.format(stepdate);
        stepdateinv.setText(DATE);
        String dateTXT = stepdateinv.getText().toString();

        Date stepgraphdate = new Date();
        DateFormat formatted2 = new SimpleDateFormat("dd-MM-yyyy");

        String DATE2 = formatted2.format(stepgraphdate);
        stepgraphdateinv.setText(DATE2);
        String dateGraphTXT = stepgraphdateinv.getText().toString();

        String sleeptotalTXT = (String) totaltimetxt.getText();
        String lightTXT = (String) tvwatchlight.getText();
        String restfulTXT = (String) tvwatchrestful.getText();
        String lightperTXT = (String) lightpercentagetxt.getText();
        String restfulperTXT = (String) restfulpercentagetxt.getText();
        String sleepscore = (String) sleepscoreTXT.getText();




        //  Cursor res = DB.getdate();
        //  if (DB.getdate() == null) {
        //      Toast.makeText(FitDash.this, "No date in db", Toast.LENGTH_SHORT).show();
        //  }else {

        //      String DATE = res.getString(0);
        //      stepdateinv.setText(DATE);
        //   }


        Boolean checkinsertdata = DB.insertsleepdata(dateTXT,dateGraphTXT, lightTXT,restfulTXT,
                sleeptotalTXT, lightperTXT,restfulperTXT, sleepscore
        );

        if (checkinsertdata == true) {
            Toast.makeText(FitDash.this, "Sleep Data Updated", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(FitDash.this, "data failed", Toast.LENGTH_SHORT).show();
            Boolean checkupdatedata = DB.updatesleepdata(dateTXT,dateGraphTXT, lightTXT,restfulTXT,
                    sleeptotalTXT, lightperTXT,restfulperTXT, sleepscore
            );
            if (checkupdatedata == true && restfulperc !=0 && lightperc != 0) {

            } else {
                Toast.makeText(FitDash.this, "Failed to update sleep", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void saveSPOinfo() {

        Date timedate = Calendar.getInstance().getTime();
        SimpleDateFormat formatted =  new SimpleDateFormat("hh:mm a", Locale.getDefault());



        String TIMEDATE = formatted.format((timedate));
        spo2timeinv.setText(TIMEDATE);
        String timedateTXT = spo2timeinv.getText().toString();
        String dateSPOTXT = stepdateinv.getText().toString();
        String SPOTXT = (String) tvBloodOxygen.getText();

        String SPOstring = tvBloodOxygen.getText().toString();
        float SPOvalue = Float.parseFloat(SPOstring);

        if(SPOvalue < 95){
            spo2warning++;
        } else if(spo2warning >=1 && spo2warning >= 95) {
            spo2warning--;
        }else {

        }

        warningcounter = sleepwarning + spo2warning + ringwarning;
        warningTXT.setText(String.valueOf(warningcounter));


        Boolean checkinsertdata = DB.insertSPOdata(timedateTXT,dateSPOTXT,SPOTXT
        );

        if (checkinsertdata == true) {
            Toast.makeText(FitDash.this, "SPO2 Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(FitDash.this, "SP02 Update failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}