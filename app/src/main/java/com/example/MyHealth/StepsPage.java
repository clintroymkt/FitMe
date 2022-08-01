package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.google.gson.internal.bind.util.ISO8601Utils.format;

public class StepsPage extends AppCompatActivity {
    BarChart stepBarChart;
    BarChart distanceBarChart;
    DBHelper DB = new DBHelper(this);
    SQLiteDatabase sqLiteDatabase;
  //  DatabaseAdapter databaseAdapter;

    ArrayList<String> stepslistitem;
    ArrayList<String> distancelistitem;
    SimpleCursorAdapter stepsadapter;
    SimpleCursorAdapter distanceadapter;



    ListView lvStepdata;
    ListView lvdistance;
    ListView lvstepinv;

    final  String[] from = new String[]{DB.GRAPHDATE,DB.STEPS,};
    final  int[] to = new int[]{R.id.txtDate1,R.id.txtstepcount1};

    final String[] distfrom = new String[]{DB.GRAPHDATE,DB.DISTANCE};
    final int[] distto = new int[]{R.id.txtDate2,R.id.txtdistancecount1};
    TextView totaltext;
  //  final ArrayList<BarEntry> dataVals = new ArrayList<>();
    int steptotal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_page);


        stepslistitem = new ArrayList<>();
        distancelistitem = new ArrayList<>();



        Cursor cursor = DB.getstepdata();



        //stepschartlist = new ArrayList<>();

        stepBarChart = findViewById(R.id.stepsChart);
        distanceBarChart = findViewById(R.id.distanceChart);
         lvStepdata = findViewById(R.id.listvstepdata);
         lvdistance = findViewById(R.id.listvdistancedata);
         totaltext = findViewById(R.id.totaltest);

         stepsadapter = new SimpleCursorAdapter(this, R.layout.stepsdata_item,cursor,from, to, 0);
         distanceadapter = new SimpleCursorAdapter(this,R.layout.distancedata_item,cursor,distfrom,distto,0);



         lvStepdata.setAdapter(stepsadapter);
         lvdistance.setAdapter(distanceadapter);
         showStepsGraph();
         showDistanceGraph();
      /*   for (int i= 0; i<graphlist.size(); i++){
             float _id=Float.parseFloat(graphlist.get(i).get_ID());
             float steps = Float.parseFloat(graphlist.get(i).getSTEPS());
             stepschartlist.add(new BarEntry (_id, steps));
         } */

        BarDataSet barDataSet = new BarDataSet(showStepsGraph(), "Steps");
         barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
         barDataSet.setValueTextColor(Color.BLUE);
         barDataSet.setValueTextSize(16f);

         BarData barData = new BarData(barDataSet);


        XAxis xAxis = stepBarChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);
      //  xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setGranularity(1f); // only intervals of 1 day
       // xAxis.setLabelCount(30);
        xAxis.setGranularityEnabled(true);
      //  xAxis.setValueFormatter(new xAxisFormatter(stepBarChart));
       // stepBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter());


       // xAxis.setValueFormatter(new IndexAxisValueFormatter(getDate()));





     //   float groupSpace = 0.06f;
     //   float barSpace = 0.02f;
      //

        BarDataSet distancebarDataSet = new BarDataSet(showDistanceGraph(), "Distance(m)");
        distancebarDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        distancebarDataSet.setValueTextColor(Color.BLUE);
        distancebarDataSet.setValueTextSize(16f);

        BarData distancebarData = new BarData(distancebarDataSet);



        XAxis distancexAxis = distanceBarChart.getXAxis();
        distancexAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        distancexAxis.setDrawGridLines(false);
        distancexAxis.setGranularity(1f); // only intervals of 1 day
        distancexAxis.setLabelCount(30);
        distancexAxis.setGranularityEnabled(true);
        distancexAxis.setValueFormatter(new xAxisFormatter(distanceBarChart));




       YAxis leftAxis = stepBarChart.getAxisLeft();

      //  leftAxis.setLabelCount(11);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
       // leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawLabels(false);

        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = stepBarChart.getAxisRight();
        rightAxis.setLabelCount(11);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);

        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);


        YAxis distanceleftAxis = distanceBarChart.getAxisLeft();

        //  leftAxis.setLabelCount(11);
        distanceleftAxis.setDrawGridLines(false);
        distanceleftAxis.setDrawAxisLine(false);
        // leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        distanceleftAxis.setSpaceTop(15f);
        distanceleftAxis.setDrawLabels(false);

        distanceleftAxis.setAxisMinimum(0f);

        YAxis distancerightAxis = distanceBarChart.getAxisRight();
        distancerightAxis.setLabelCount(11);
        distancerightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        distancerightAxis.setDrawGridLines(false);
        distancerightAxis.setDrawAxisLine(false);
        distancerightAxis.setDrawLabels(false);

        distancerightAxis.setSpaceTop(15f);
        distancerightAxis.setAxisMinimum(0f);



        stepBarChart.getAxisLeft().setDrawAxisLine(false);

         stepBarChart.setFitBars(true);

      //  stepBarChart.groupBars(0f, groupSpace, barSpace);


        stepBarChart.notifyDataSetChanged();
        stepBarChart.setData(barData);

         stepBarChart.invalidate();
         stepBarChart.animateY(1000);
        // stepBarChart.setData(stepbarData);




        distanceBarChart.getAxisLeft().setDrawAxisLine(false);

        distanceBarChart.setFitBars(true);

        //  stepBarChart.groupBars(0f, groupSpace, barSpace);


        distanceBarChart.notifyDataSetChanged();
        distanceBarChart.setData(distancebarData);

        distanceBarChart.invalidate();
        distanceBarChart.animateY(1000);


        totaltext.setText(DB.ringSum());




//         float xVal = Float.parseFloat(String.valueOf(DB._ID));
       //  float yVal = Float.parseFloat(String.valueOf(stepno));

       /*  lineDataSet.setValues(showStepsGraph());
         lineDataSet.setLabel("DataSet 1");
         dataSets.clear();
         dataSets.add(lineDataSet);
         lineData = new LineData((ILineDataSet) dataSets);
         stepschart.clear();
         stepschart.setData(lineData);
         stepschart.invalidate();
         showStepsGraph(); */

        // loadData();

      //  viewstepsdata();
       // SimpleCursorAdapter simpleCursorAdapter = databaseAdapter.populateListViewFromDB();
       // lvStepdata.setAdapter(simpleCursorAdapter);
    }


    private ArrayList<BarEntry> showDistanceGraph() {
        final ArrayList<BarEntry> dataVals = new ArrayList<>();
        Cursor cursor =DB.distanceChartdata();

        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataVals.add(new BarEntry(cursor.getInt(0),cursor.getInt(1)));

        }

        cursor.close();
        DB.close();

        return  dataVals;
    }


  /*  public void showStepsGraph() {
        final ArrayList<BarEntry> stepVals = new ArrayList<BarEntry>();

        final  ArrayList<String> stepdata  = DB.stepChartdata();

        for (int i = 0; i < DB.stepChartdata().size(); i++){
            BarEntry newBarEntry = new BarEntry(i, Float.parseFloat(DB.stepChartdata().get(i)));
            stepVals.add(newBarEntry);
        }

        final ArrayList<String> dateVals = new ArrayList<String>();
        final ArrayList<String> datedata = DB.dateChartdata();


        for (int i = 0; i < DB.dateChartdata().size(); i++){
            dateVals.add(datedata.get(i));
        }

        BarDataSet dataSet =  new BarDataSet(stepVals, "Steps (Weekly)");

        ArrayList<IBarDataSet> dataSets1 = new ArrayList<>();
        dataSets1.add(dataSet);

        BarData data = new BarData(dataSets1);

        stepBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dateVals));

        stepBarChart.setData(data);




    } */


    private ArrayList<BarEntry> showStepsGraph() {

        final ArrayList<BarEntry> dataVals = new ArrayList<>();
        //ValueFormatter xAxisFormatter = new xAxisValueFormatter(dataVals);
        Cursor cursor = DB.stepChartdata();


        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataVals.add(new BarEntry(cursor.getInt(0), cursor.getInt(1)));

        }


       //  String[] dateStringfrom = new String[]{DB.GRAPHDATE};

       // String[] dateStringto = new int []{R.};
      //  stepBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dateStringfrom));
        cursor.close();
        DB.close();

        return dataVals;

    }

      /*  for (int i = 0; i<DB.querysteps().size(); i++){
            BarEntry newBarEntry = new BarEntry(i, Float.parseFloat(DB.querysteps().get(i)));
            yVals.add(newBarEntry);
        }

        final ArrayList<String> xVals = new ArrayList<String>();
        final  ArrayList<String> xdata = DB.querysteps();

        for (int i = 0; i<DB.querydate().size(); i++){
            xVals.add(xdata.get(i));
        }

        BarDataSet dataSet = new BarDataSet(yVals, "Steps");
        ArrayList<IBarDataSet> dataSets1 = new ArrayList<>();

        dataSets1.add(dataSet);

        BarData data = new BarData(dataSets1);

        stepBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xVals));

        stepBarChart.setData(data);

        XAxis xAxis = stepBarChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setAvoidFirstLastClipping(true);

        xAxis.setDrawLabels(true);
        xAxis.isCenterAxisLabelsEnabled();
        xAxis.setGranularityEnabled(true);

        YAxis rightAxis = stepBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        stepBarChart.setMaxVisibleValueCount(7);
        stepBarChart.setFitBars(true);


    } */





 /*   public class xAxisFormatter extends ValueFormatter {



        @Override

        public String getFormattedValue(float value) {

            String datestring = new String(""+value);

                return datestring;
            }
    } */

    public class xAxisFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public xAxisFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }
        @Override
        public String getFormattedValue(float value) {
            return "" + value;
        }
    }




  //  private void loadData() {
 //       lineDataSet.setValues(g);
  //  }


// private ArrayList<>
  /*  private void viewstepsdata() {

        Cursor cursor = DB.getstepdata();



        if (cursor.getCount()==0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                stepslistitem.add(cursor.getString(0));
                stepslistitem.add(cursor.getString(1));
                stepslistitem.add(cursor.getString(2));
            }

            stepsadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,stepslistitem);
            lvStepdata.setAdapter(stepsadapter);
        }
    } */
}