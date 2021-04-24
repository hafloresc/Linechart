package com.rbtechnology3.linechart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText xValue, yValue;
    Button buttonIngresar;
    LineChart lineChart;
    FirebaseDatabase database;
    DatabaseReference reference;
    LineDataSet lineDataSet=new LineDataSet(null,null);
    ArrayList<ILineDataSet> iLineDataSets=new ArrayList<>();
    LineData lineData;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xValue=(EditText)findViewById(R.id.EditTextx);
        yValue=(EditText)findViewById(R.id.EditTexty);
        buttonIngresar=(Button)findViewById(R.id.ButtonIngresar);
        lineChart=(LineChart)findViewById(R.id.Linechart);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Linechart").child("SENSOR");
        insertar();





    }

    private void insertar() {
        buttonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=reference.push().getKey();
                int x=Integer.parseInt(xValue.getText().toString());
                int y=Integer.parseInt(yValue.getText().toString());
                DataPoint dataPoint=new DataPoint(x,y);
                reference.child(id).setValue(dataPoint);
                recuperar();
            }
        });

    }

    private void recuperar() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> datavals=new ArrayList<Entry>();

                if (snapshot.hasChildren()){
                    for (DataSnapshot mydataSnaphot:snapshot.getChildren()){
                        DataPoint dataPoint=mydataSnaphot.getValue(DataPoint.class);

                        datavals.add(new Entry(dataPoint.getxValue(),dataPoint.getyValue()));





                    }
                    Log.i(TAG, "onDataChange: "+datavals);
                    graficar(datavals);
                }else {
                    lineChart.clear();
                    lineChart.invalidate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void graficar(ArrayList<Entry> datavals) {
        lineDataSet.setValues(datavals);
        lineDataSet.setLabel("Ejemplo 1");
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData=new LineData(iLineDataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();



    }
}