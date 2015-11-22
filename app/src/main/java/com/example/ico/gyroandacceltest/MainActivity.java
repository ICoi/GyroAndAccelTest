package com.example.ico.gyroandacceltest;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    int accelXValue;
    int accelYValue;
    int accelZValue;

    int gyroX;
    int gyroY;
    int gyroZ;

    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private Sensor accSensor;
    private Sensor graSensor;
    private Sensor mPressure;


    private static final int GYLO = 0;
    private static final int ACCEL = 1;

    private static final String GYLOFILEPATH =  "gyroLog.txt";
    private static final String ACCELFILEPATH = "accelLog.txt";
    private static final String PRESSUREPATH = "pressureLog.txt";
    private static final String GRAVITYPATH = "gravityLog.txt";
    private static final String TAG = "DaunFile";

    private TextView pressureTV;
    private TextView gravitiyTV;
    private FileOutputStream gyroFos;
    private FileOutputStream accelFos;

    private FileOutputStream pressureFos;
    private FileOutputStream gravitiyFos;
    FileOutputStream fos = null;



    private int accCalibrationIndex = 0;
    private int sumAcc = 0;
    private int accCalibrationValue = 0;
    private float[] velocityx = new float[240];
    private float[] accelerationx = new float[240];
    private float[] positionx = new float[240];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pressureTV = (TextView)findViewById(R.id.pressureTV);
        gravitiyTV = (TextView)findViewById(R.id.gravityTV);

        //센서 매니저 얻기
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //자이로스코프 센서(회전)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //엑셀러로미터 센서(가속)
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // 압력 센서
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        graSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(MainActivity.this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(MainActivity.this, graSensor, SensorManager.SENSOR_DELAY_NORMAL);
        try {

            gyroFos = openFileOutput(GYLOFILEPATH, Context.MODE_PRIVATE);
            accelFos = openFileOutput(ACCELFILEPATH, Context.MODE_PRIVATE);
            pressureFos = openFileOutput(PRESSUREPATH, Context.MODE_PRIVATE);
            gravitiyFos = openFileOutput(GRAVITYPATH, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            Toast.makeText(MainActivity.this, "실행 불가",Toast.LENGTH_SHORT);
        }

        onClickListener();

    }


    public void onClickListener(){
        Button btn1 = (Button)findViewById(R.id.btn_first);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Check Point 1 기록",Toast.LENGTH_SHORT).show();
                writeGyroFos("CheckPoint1");
                writeAcelFos("CheckPoint1");
                writePressFos("CheckPoint1\n");
                writeGravitiyFos("CheckPoint1\n");

            }
        });
        Button btn2 = (Button)findViewById(R.id.btn_second);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Check Point 2 기록", Toast.LENGTH_SHORT).show();
                writeGyroFos("CheckPoint2");
                writeAcelFos("CheckPoint2");
                writePressFos("CheckPoint2\n");
                writeGravitiyFos("CheckPoint2\n");

            }
        });
        Button btn3 = (Button)findViewById(R.id.btn_third);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Check Point 3 기록", Toast.LENGTH_SHORT).show();
                writeGyroFos("CheckPoint3");
                writeAcelFos("CheckPoint3");
                writePressFos("CheckPoint3\n");
                writeGravitiyFos("CheckPoint3\n");

            }
        });
        Button btn4 = (Button)findViewById(R.id.btn_forth);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Check Point 4 기록",Toast.LENGTH_SHORT).show();
                writeGyroFos("CheckPoint4");
                writeAcelFos("CheckPoint4");
                writePressFos("CheckPoint4\n");
                writeGravitiyFos("CheckPoint4\n");

            }
        });

        Button btnStart = (Button)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSensorManager.registerListener(MainActivity.this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(MainActivity.this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(MainActivity.this,"Log 기록 시작",Toast.LENGTH_SHORT).show();
            }
        });

        Button btnFinish = (Button)findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(MainActivity.this);
                Toast.makeText(MainActivity.this, "Log 기록 종료", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSend2 = (Button)findViewById(R.id.button_send2);
        btnSend2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                // Read data from files
                String gyroStr = "";
                String acelStr = "";
                FileInputStream fisPres = null;
                FileInputStream fisGravity = null;
                try {
                    fisPres = openFileInput(PRESSUREPATH);
                    byte[] data = new byte[fisPres.available()];
                    while (fisPres.read(data) != -1) {
                        gyroStr += new String(data, StandardCharsets.UTF_8);
                    }
                    fisPres.close();

                    fisGravity = openFileInput(GRAVITYPATH);
                    data = new byte[fisGravity.available()];
                    while (fisGravity.read(data) != -1) {
                        acelStr += new String(data, StandardCharsets.UTF_8);
                    }
                    fisGravity.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Send Email
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ICo1022@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "[캡스톤설계] Move Log Test - pressure and gravity");
                i.putExtra(Intent.EXTRA_TEXT, "pressure \n" + gyroStr.toString() + "\n gravity \n" + acelStr.toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button btnSend = (Button)findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                // Read data from files
                String gyroStr = "";
                String acelStr = "";
                FileInputStream fisGyro = null;
                FileInputStream fisAccel = null;
                try {
                    fisGyro = openFileInput(GYLOFILEPATH);
                    byte[] data = new byte[fisGyro.available()];
                    while (fisGyro.read(data) != -1) {
                        gyroStr += new String(data, StandardCharsets.UTF_8);
                    }
                    fisGyro.close();

                    fisAccel = openFileInput(ACCELFILEPATH);
                    data = new byte[fisAccel.available()];
                    while (fisAccel.read(data) != -1) {
                        acelStr += new String(data, StandardCharsets.UTF_8);
                    }
                    fisAccel.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Send Email
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ICo1022@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "[캡스톤설계] Move Log Test");
                i.putExtra(Intent.EXTRA_TEXT, "gyro \n" + gyroStr.toString() + "\n Accel \n" + acelStr.toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //정확도에 대한 메소드 호출 (사용안함)
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensor Accuracy가 변화할때 사용하는 것!!
    }


    //센서값 얻어오기
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = Math.round(event.values[0] * 1000);
            gyroY = Math.round(event.values[1] * 1000);
            gyroZ = Math.round(event.values[2] * 1000);
          //  System.out.println("gyroX ="+gyroX);
          //  System.out.println("gyroY ="+gyroY);
          //  System.out.println("gyroZ ="+gyroZ);
          writeGyroFos("{" + gyroX + "," + gyroY+","+gyroZ+"},");

        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            accelXValue = (int) event.values[0];
            accelYValue = (int) event.values[1];
            accelZValue = (int) event.values[2];
            // System.out.println("accelXValue="+accelXValue);
            // System.out.println("accelYValue="+accelYValue);
            // System.out.println("accelZValue="+accelZValue);
            writeAcelFos("{" + accelXValue + "," + accelYValue + "," + accelZValue + "},");
        }
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
            float millibars_of_pressure = event.values[0];
            pressureTV.setText("" + millibars_of_pressure);
            writePressFos(millibars_of_pressure+"\n");
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float zValue = (float)event.values[2];
            gravitiyTV.setText("" + zValue);
            writeGravitiyFos(zValue + "\n");

            if(accCalibrationIndex == 240){
                accCalibrationValue = sumAcc / 240;
                sumAcc = 0;
                accelerationx[0] = 0;
                velocityx[0] = 0;
                positionx[0] = 0;
            }
            accCalibrationIndex++;

            if (curtime == 0) {
                curtime = System.currentTimeMillis();
            }else{
                lastime = curtime;
                curtime = System.currentTimeMillis();
                deltaT = curtime - lastime;
                float time = (float)deltaT/1000;
                accelerationx[1] = (float) (event.values[0] - accCalibrationValue);
                FilteringWindow();
                sumAcc += accelerationx[1];
                velocityx[1]= CalcIntegration(velocityx[0],
                        accelerationx[0],accelerationx[1],time);
                positionx[1]= CalcIntegration(positionx[0], velocityx[0], velocityx[1], time);
                accelerationx[0] = accelerationx[1]; velocityx[0] = velocityx[1]; positionx[0] = positionx[1];
                this.movement_end_check();
            }
        }

    }
    private long curtime = 0;
    private long lastime = 0;
    private long deltaT = 0;
    public final static float window = 0.06f;
    void FilteringWindow(){
        if(((accelerationx[1] <= window) && (accelerationx[1] >= -window))){
            accelerationx[1] = 0;
        }
    }
    private int movementEndCheckVariable = 0;
    private int countx = 0;
    void movement_end_check(){
        if(accelerationx[1] < movementEndCheckVariable &&
                accelerationx[1] > -movementEndCheckVariable){
            countx++;
        }else{
            countx = 0;
        }

        if(countx >= 5){
            velocityx[1] = 0;
            velocityx[0] = 0;
        }
    }
    float CalcIntegration(float base, float diff0, float diff1, float time){
       // outputX.setText("x : " + Float.toString(diff1));
        Log.i("hahahohoTest",Float.toString(diff1));
        float result = 0;
        result = base + (diff0 + ((diff1 - diff0)/2)) * (time/1000);
        return result;
    }
    private void writeGyroFos(String str) {
        try {
            gyroFos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void writeAcelFos(String str) {
        try {
            accelFos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePressFos(String str){
        try{
            pressureFos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeGravitiyFos(String str){
        try{
            gravitiyFos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 주기 설명
    // SENSOR_DELAY_UI 갱신에 필요한 정도 주기
    // SENSOR_DELAY_NORMAL 화면 방향 전환 등의 일상적인  주기
    // SENSOR_DELAY_GAME 게임에 적합한 주기
    // SENSOR_DELAY_FASTEST 최대한의 빠른 주기


    //리스너 등록
    protected void onResume() {
        super.onResume();
       // mSensorManager.registerListener(this, mGyroscope,SensorManager.SENSOR_DELAY_FASTEST);
        //mSensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    //리스너 해제
    protected void onPause() {
        super.onPause();
    }



}
