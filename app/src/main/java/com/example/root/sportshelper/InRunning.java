package com.example.root.sportshelper;

//跑步中,使用高德地图来更新位置
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.LatLng;
import com.example.root.sportshelper.database.BadgeRecord;
import com.example.root.sportshelper.database.GpsRecord;
import com.example.root.sportshelper.database.RunningRecord;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.ruler.LongpressCircle;
import com.example.root.sportshelper.ruler.SlideLockView;
import com.example.root.sportshelper.utils.AudioUtils;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MiscUtil;
import com.example.root.sportshelper.utils.MyTime;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InRunning extends AppCompatActivity implements View.OnClickListener,AMapLocationListener,LocationSource {
    private String TAG="InRunning";
    private static final int GpsprecisionLow= 1;
    private static final int SHOWMESSAGE= 2;
    private static final int TIMECOUNT=3;               //计时
    private static final int AUTOPAUSE=4;               //自动暂停
    private static final int OPENVOICEANNOUCEMENTSOFDISTANCE=5;   //语音播报(按距离)
    private static final int OPENVOICEANNOUCEMENTSOFTIME=6;   //语音播报(按时间)

    ImageView showMap;
    LongpressCircle longpress_suspend;
    LinearLayout ContinueOrend;
    TextView Continue;
    TextView end;
    TextView distance;
    TextView speed;
    TextView UseTime;
    TextView ExpendCalorie;

    SlideLockView slideLockView;        //滑动解锁

    Boolean stop = true;
    Boolean saved = true;
    Boolean firstStart = true;
    public static float curr_speed = 0;
    long total_time = 0;
    long last_time = 0;
    public static float curr_distance = 0;
    public static String timeStr;                   //计时时间
    int calorie=0;            //卡路里
    Date startTime;
    ArrayList<GpsRecord> locations = new ArrayList<>();

    private Boolean AutoPause;                          //是否开启自动暂停
    private Timer timer;
    private float lastDistance;                         //上一次距离

    private Boolean ScreenOn;                           //屏幕常亮是否打开

    private Boolean VoiceAnnouncements;                 //语音播报是否打开
    private int maleVoiceOrfemaleVoice;                 //男声或女声
    private int frequency;                              //频率
    private int number;                                 //次数，如每隔500米提醒一次
    private int frequencyOfDistance;                    //距离频率
    private int frequencyOfTime;                        //时间频率

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    private AMapLocation lastAMapLocation=null;                          //上一个位置

    private IntentFilter mScreenOnFilter = new IntentFilter();

    private String lastTime;
    private float frontTotalKm=0;                 //今天之前的公里

    // Handler gets created on the UI-thread
    private Handler mHandler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GpsprecisionLow:
                    Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.gps_precision_low2), Constant.LOCATION_ACCURACY), Toast.LENGTH_LONG).show();
                    break;
                case SHOWMESSAGE:
                    speed.setText(String.format("%s", MiscUtil.getSpeedString(curr_speed)));           //速度
                    distance.setText(String.format("%.2f", MiscUtil.getDistance(curr_distance)));          //距离
                    ExpendCalorie.setText(calorie+"");          //卡路里
                    break;
                case TIMECOUNT:
                    UseTime.setText(message.getData().getString("myTime"));
                    break;
                case AUTOPAUSE:
                    stop = true;
                    longpress_suspend.setVisibility(View.GONE);
                    ContinueOrend.setVisibility(View.VISIBLE);
                    break;
                case OPENVOICEANNOUCEMENTSOFDISTANCE:
                    if(VoiceAnnouncements){
                        String myDistace="你已经运动了"+frequencyOfDistance*number+"米";
                        AudioUtils.getInstance().speakText(myDistace);
                    }
                    break;
                case OPENVOICEANNOUCEMENTSOFTIME:
                    if(VoiceAnnouncements){
                        String myTime="你已经运动了"+frequencyOfTime*number+"分钟";
                        AudioUtils.getInstance().speakText(myTime);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解除系统锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.in_running);

        initView();
        setDefaultView();
        initConstant();
        startTimeCount();
        initMonitor();
        AudioUtils.getInstance().init(this,maleVoiceOrfemaleVoice);

        longpress_suspend.setOnLongClickListener(new LongpressCircle.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                stop = true;
                longpress_suspend.setVisibility(View.GONE);
                ContinueOrend.setVisibility(View.VISIBLE);
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("运动暂停");
                }
                return false;
            }
        });

        slideLockView.setmLockListener(new SlideLockView.OnLockListener() {
            @Override
            public void onOpenLockSuccess() {
                showMap.setVisibility(View.VISIBLE);
                if(stop){
                    longpress_suspend.setVisibility(View.GONE);
                    ContinueOrend.setVisibility(View.VISIBLE);
                    slideLockView.setVisibility(View.GONE);
                }else {
                    longpress_suspend.setVisibility(View.VISIBLE);
                    ContinueOrend.setVisibility(View.GONE);
                    slideLockView.setVisibility(View.GONE);
                }
            }
        });
        initLoc();
    }

    private void initView(){
        showMap=(ImageView)findViewById(R.id.showMap);
        longpress_suspend=(LongpressCircle)findViewById(R.id.longpress_suspend);
        showMap.setOnClickListener(this);
        ContinueOrend=(LinearLayout)findViewById(R.id.ContinueOrend);
        Continue=(TextView)findViewById(R.id.Continue);
        end=(TextView)findViewById(R.id.end);
        Continue.setOnClickListener(this);
        end.setOnClickListener(this);

        slideLockView=(SlideLockView)findViewById(R.id.SlideLock);

        distance=(TextView)findViewById(R.id.distance);
        speed=(TextView)findViewById(R.id.speed);
        UseTime=(TextView)findViewById(R.id.UseTime);
        ExpendCalorie=(TextView)findViewById(R.id.ExpendCalorie);

        speed.setText(String.format("%s", MiscUtil.getSpeedString(curr_speed)));           //速度
        distance.setText(String.format("%.2f", MiscUtil.getDistance(curr_distance)));          //距离
        ExpendCalorie.setText(0+"");
        UseTime.setText("0:00:00");
    }

    private void setDefaultView()
    {
        longpress_suspend.setVisibility(View.VISIBLE);
        ContinueOrend.setVisibility(View.GONE);
        slideLockView.setVisibility(View.GONE);
    }

    //初始化监听
    private void initMonitor(){
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenActionReceiver,mScreenOnFilter);
    }

    //初始化常量
    private void initConstant(){
        last_time = System.currentTimeMillis();
        if (stop) {
            stop = false;
        }
        if (firstStart) {
            firstStart = false;
            locations.clear();
            curr_distance = 0;
            curr_speed = 0;
            total_time = 0;
            saved = false;
            startTime = new Date();
            AutoPause=DbHelper.getOpenAutoPause();
            if(AutoPause){
                OpenAutoPause();
            }
            ScreenOn=DbHelper.getOpenScreenOn();
            if(ScreenOn){
                OpenScreenOn();
            }

            VoiceAnnouncements=DbHelper.getOpenVoiceAnnouncements();            //是否开启语音播报
            maleVoiceOrfemaleVoice=DbHelper.getmaleVoiceOrfemaleVoice();
            frequency=DbHelper.getfrequency();                                  //获得频率
            if(frequency==1){
                frequencyOfDistance=500;
            }else if(frequency==2){
                frequencyOfDistance=1000;
            }else if(frequency==3){
                frequencyOfDistance=2000;
            }else if(frequency==4){
                frequencyOfDistance=3000;
            }else if(frequency==5){
                frequencyOfTime=5;
            }else if(frequency==6){
                frequencyOfTime=10;
            }else if(frequency==7){
                frequencyOfTime=20;
            }else if(frequency==8){
                frequencyOfTime=30;
            }
            if(frequency==1||frequency==2||frequency==3||frequency==4){
                MonitorDistance(frequencyOfDistance);
            }else if(frequency==5||frequency==6||frequency==7||frequency==8){

            }
            number=1;
            lastTime=MyTime.getTodayTime();
        }
        getFrontRunningKm();
    }

    //开始计时
    private void startTimeCount(){
        // update the time
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(1000);
                        if (stop) continue;
                        total_time += System.currentTimeMillis() - last_time;
                        last_time = System.currentTimeMillis();
                        long total_time_tmp = total_time / 1000;
                        timeStr = String.format("%d:%02d:%02d", total_time_tmp / 3600, total_time_tmp % 3600 / 60, total_time_tmp % 3600 % 60);
                        Message message=new Message();
                        message.what=TIMECOUNT;
                        Bundle bundle=new Bundle();
                        bundle.putString("myTime",timeStr);
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (InterruptedException e) {}
            }
        };
        thread.start();
    }

    //监听距离
    private void MonitorDistance(final int distace){
        // update the time
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        if (stop) continue;
                        if(curr_distance>=distace*number&&curr_distance<=distace*number+100){
                            number++;
                            Message message=new Message();
                            message.what=OPENVOICEANNOUCEMENTSOFDISTANCE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {}
            }
        };
        thread.start();
    }

    //监听时间
    private void MonitorTime(final int time){
        // update the time
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        if (stop) continue;
                        if(MyTime.getOccupyMinute(timeStr)>=time*number&&MyTime.getOccupyMinute(timeStr)<=time*number+1){
                            number++;
                            Message message=new Message();
                            message.what=OPENVOICEANNOUCEMENTSOFTIME;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {}
            }
        };
        thread.start();
    }

    //开启自动暂停
    private void OpenAutoPause(){
        lastDistance=-1;
        timer=new Timer(true);
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                if(!stop){
                    if(lastDistance==curr_distance){
                        Log.i(TAG, "run: 自动暂停");
                        timer.cancel();
                        Message message=new Message();
                        message.what=AUTOPAUSE;
                        mHandler.sendMessage(message);
                    }else {
                        lastDistance=curr_distance;
                    }
                }
            }
        };
        timer.schedule(task,0,5000);
    }

    //开启屏幕常亮
    private void OpenScreenOn(){
        Log.i(TAG, "OpenScreenOn: 开启屏幕常亮");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //定位
    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                if(lastAMapLocation==null){
                    lastAMapLocation=amapLocation;
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +"" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "lastAMapLocation: 第一次定位");
                    saveLocation(amapLocation);
                }else if(Math.abs(MiscUtil.getDistance(MiscUtil.getLatLng(lastAMapLocation),MiscUtil.getLatLng(amapLocation)))>Constant.INTERVAL_LOCATION){
                    //经纬度不同表示不同的位置
                    Log.i(TAG, "onLocationChanged: 与上一个距离超过10米");
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +"" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    lastAMapLocation=amapLocation;
                    saveLocation(amapLocation);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    private void saveLocation(AMapLocation aMapLocation){
        Log.i(TAG, "saveLocation: 调用了");
        float dist = 0, speed = 0;
        double alt=0;               //高度
        Date date = new Date(aMapLocation.getTime());
        final GpsRecord gpsRecord=new GpsRecord(date,aMapLocation);

        if (locations.size() > 0) {
            GpsRecord pre = locations.get(locations.size() - 1);        //获得最后的记录
            dist=MiscUtil.getDistance(MiscUtil.getLatLng(pre.loc),MiscUtil.getLatLng(aMapLocation));
            dist = Math.abs(dist);

            //get a temporal speed, and it will be corrected by a average speed.
            speed = dist / (1.0f * (date.getTime() - pre.getDate().getTime()) / 1000);
            if (aMapLocation.hasAltitude()){
                alt = aMapLocation.getAltitude();
            }else{
                alt = Constant.INVALID_ALT;
            }
        }

        // speed: get the avg speed of SPEED_AVG points
        if (Constant.SPEED_AVG > 0 && locations.size()>=Constant.SPEED_AVG-1) {
            GpsRecord preN = locations.get(locations.size() - (Constant.SPEED_AVG - 1));
            float dist_avg=MiscUtil.getDistance(MiscUtil.getLatLng(aMapLocation),MiscUtil.getLatLng(preN.loc));
            dist_avg = Math.abs(dist_avg);
            speed = dist_avg / (1.0f * (date.getTime() - preN.getDate().getTime()) / 1000);
        }


        gpsRecord.setDistance(dist);
        gpsRecord.setSpeed(speed);
        gpsRecord.setAlt(alt);
        locations.add(gpsRecord);
        if(!stop){
            curr_speed = speed;
            curr_distance += dist;
            total_time += date.getTime() - last_time;
            last_time = date.getTime();
            calorie=getCalorie();
        }

        SpeedFivekilometers(MiscUtil.getDistance(curr_distance));
        SpeedEightkilometers(MiscUtil.getDistance(curr_distance));
        SpeedTenkilometers(MiscUtil.getDistance(curr_distance));

        ChallengeFivekilometers(MiscUtil.getDistance(curr_distance));
        ChallengeTenkilometers (MiscUtil.getDistance(curr_distance));
        ChallengeTwentykilometers(MiscUtil.getDistance(curr_distance));
        ChallengeThirtykilometers(MiscUtil.getDistance(curr_distance));
        ChallengeFortykilometers(MiscUtil.getDistance(curr_distance));
        ChallengeFiftykilometers(MiscUtil.getDistance(curr_distance));

        TotalRunFiftykilometers(MiscUtil.getDistance(curr_distance));
        TotalRunOneHundredkilometers(MiscUtil.getDistance(curr_distance));
        TotalRunThreeHundredkilometers(MiscUtil.getDistance(curr_distance));
        TotalRunFiveHundredkilometers(MiscUtil.getDistance(curr_distance));
        TotalRunEightHundredkilometers(MiscUtil.getDistance(curr_distance));
        TotalRunThousandkilometers(MiscUtil.getDistance(curr_distance));

        Log.i(TAG, String.format("%s", gpsRecord.toString()));

        Message message=new Message();
        message.what=SHOWMESSAGE;
        mHandler.sendMessage(message);
    }

    private int getCalorie(){
        //运动多少千米，现将步数转换为千米
        double costcalorie= DbHelper.getbodyWeight()*1.036*MiscUtil.getDistance(curr_distance);
        double finalcalorie=Math.rint(costcalorie);
        return (int)finalcalorie;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.showMap:
                if(ContextCompat.checkSelfPermission(InRunning.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(InRunning.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else {
                    Intent intent=new Intent(InRunning.this,ShowMap.class);
                    intent.putExtra("myLocations", locations);
                    startActivity(intent);
                }
                break;
            case R.id.Continue:
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("继续运动");
                }
                last_time = System.currentTimeMillis();
                if (stop) {
                    stop = false;
                }
                longpress_suspend.setVisibility(View.VISIBLE);
                ContinueOrend.setVisibility(View.GONE);
                if(AutoPause){
                    OpenAutoPause();
                }
                break;
            case R.id.end:
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("运动结束");
                }
                if(curr_distance<100){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.NoBackGroundDialog);
                    builder.setMessage(getResources().getString(R.string.isQuit))
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    Log.i(TAG, "onClick: 记录位保存");
                                    finish();
                                    return;
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    RunningRecord runningRecord=new RunningRecord();
                    runningRecord.setDate(MyTime.getTodayDate());
                    runningRecord.setCalorie(calorie);
                    runningRecord.setStartTime(MyTime.getTimeFromDate(startTime));
                    runningRecord.setEndTime(MyTime.getTodayTime());
                    runningRecord.setMileage(MiscUtil.getDistance(curr_distance));
                    runningRecord.setSpeed(MiscUtil.getSpeedString(curr_speed));
                    runningRecord.setPersistTime(timeStr);

                    for(GpsRecord gpsRecord:locations){
                        gpsRecord.save();
                        runningRecord.getGpsRecords().add(gpsRecord);
                    }
                    //runningRecord.setGpsRecords(locations);
                    runningRecord.save();

                    //broadcast a message
                    Intent msg = new Intent(Constant.BC_INTENT);
                    sendOrderedBroadcast(msg,null);

                    finish();
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent intent=new Intent(InRunning.this,ShowMap.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
        //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
        //销毁前注销广播
        unregisterReceiver(mScreenActionReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

        }
        return false;
    }


    private BroadcastReceiver mScreenActionReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)||action.equals(Intent.ACTION_SCREEN_OFF)) {
                longpress_suspend.setVisibility(View.GONE);
                ContinueOrend.setVisibility(View.GONE);
                showMap.setVisibility(View.GONE);
                slideLockView.setVisibility(View.VISIBLE);
            }
        }
    };

    //极速5公里
    public void SpeedFivekilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.SpeedFivekilometers))){
            if(Math.abs(kilometre-5)<1&&kilometre>5){
                Log.i(TAG, "极速5公里");
                if(!MyTime.getTimeDifferenceGreaterThanHour(lastTime,MyTime.getTodayTime())){
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.SpeedFivekilometers));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(this, ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.SpeedFivekilometers));
                    startActivity(intent);
                }
            }
        }
    }

    //极速8公里
    public void SpeedEightkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.SpeedEightkilometers))){
            if(Math.abs(kilometre-8)<1&&kilometre>8){
                Log.i(TAG, "极速8公里");
                if(!MyTime.getTimeDifferenceGreaterThanHour(lastTime,MyTime.getTodayTime())){
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.SpeedEightkilometers));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(this, ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.SpeedEightkilometers));
                    startActivity(intent);
                }
            }
        }
    }

    //极速10公里
    public void SpeedTenkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.SpeedTenkilometers))){
            if(Math.abs(kilometre-10)<1&&kilometre>10){
                Log.i(TAG, "极速10公里");
                if(!MyTime.getTimeDifferenceGreaterThanHour(lastTime,MyTime.getTodayTime())){
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.SpeedTenkilometers));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(this, ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.SpeedTenkilometers));
                    startActivity(intent);
                }
            }
        }
    }


    //挑战5公里
    public void ChallengeFivekilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeFivekilometers))){
            if(Math.abs(kilometre-5)<1&&kilometre>5){
                Log.i(TAG, "挑战5公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeFivekilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeFivekilometers));
                startActivity(intent);
            }
        }
    }

    //挑战10公里
    public void ChallengeTenkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeTenkilometers))){
            if(Math.abs(kilometre-10)<1&&kilometre>10){
                Log.i(TAG, "挑战10公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeTenkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeTenkilometers));
                startActivity(intent);
            }
        }
    }

    //挑战20公里
    public void ChallengeTwentykilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeTwentykilometers))){
            if(Math.abs(kilometre-20)<1&&kilometre>20){
                Log.i(TAG, "挑战20公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeTwentykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeTwentykilometers));
                startActivity(intent);
            }
        }
    }

    //挑战30公里
    public void ChallengeThirtykilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeThirtykilometers))){
            if(Math.abs(kilometre-30)<1&&kilometre>30){
                Log.i(TAG, "挑战30公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeThirtykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeThirtykilometers));
                startActivity(intent);
            }
        }
    }

    //挑战40公里
    public void ChallengeFortykilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeFortykilometers))){
            if(Math.abs(kilometre-40)<1&&kilometre>40){
                Log.i(TAG, "挑战40公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeFortykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeFortykilometers));
                startActivity(intent);
            }
        }
    }

    //挑战50公里
    public void ChallengeFiftykilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeFiftykilometers))){
            if(Math.abs(kilometre-50)<1&&kilometre>50){
                Log.i(TAG, "挑战50公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeFiftykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeFiftykilometers));
                startActivity(intent);
            }
        }
    }


    //获得以前跑步的所有公里
    public void getFrontRunningKm(){
        List<RunningRecord> runningRecordList=DataSupport.offset(1).order("id desc").find(RunningRecord.class);
        if(!(runningRecordList.size()==0)||!runningRecordList.isEmpty()){
            for (RunningRecord myRunningRecord:runningRecordList){
                frontTotalKm+=myRunningRecord.getMileage();
            }
        }
    }

    //累计50公里
    public void TotalRunFiftykilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunFiftykilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-50)<1&&totalKm>=50){
                Log.i(TAG, "累计50公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunFiftykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunFiftykilometers));
                startActivity(intent);
            }
        }
    }

    //累计100公里
    public void TotalRunOneHundredkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunOneHundredkilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-100)<1&&totalKm>=100){
                Log.i(TAG, "累计100公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunOneHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunOneHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计300公里
    public void TotalRunThreeHundredkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunThreeHundredkilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-300)<1&&totalKm>=300){
                Log.i(TAG, "累计300公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunThreeHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunThreeHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计500公里
    public void TotalRunFiveHundredkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunFiveHundredkilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-500)<1&&totalKm>=500){
                Log.i(TAG, "累计500公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunFiveHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunFiveHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计800公里
    public void TotalRunEightHundredkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunEightHundredkilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-50)<1&&totalKm>=50){
                Log.i(TAG, "累计800公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunEightHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunEightHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计1000公里
    public void TotalRunThousandkilometers(float kilometre){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalRunThousandkilometers))){
            float totalKm=0;
            totalKm=frontTotalKm+kilometre;
            if(Math.abs(totalKm-50)<1&&totalKm>=50){
                Log.i(TAG, "累计1000公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalRunThousandkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(this, ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalRunThousandkilometers));
                startActivity(intent);
            }
        }
    }

}
