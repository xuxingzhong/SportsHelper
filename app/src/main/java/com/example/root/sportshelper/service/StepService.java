package com.example.root.sportshelper.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.SportMain;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.CountDownTimer;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyNotification;
import com.example.root.sportshelper.utils.MyTime;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//计步服务

@TargetApi(Build.VERSION_CODES.CUPCAKE)         //实现高低API版本兼容    1.5版
public class StepService extends Service implements SensorEventListener {
    private final String TAG="TAG_StepService";   //"StepService";
    //默认为30秒进行一次存储
    private static int duration=30000;
    private static String CURRENTDATE="";   //当前的日期
    private SensorManager sensorManager;    //传感器管理者
    private StepDetector stepDetector;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Messenger messenger=new Messenger(new MessengerHandler());
    //广播
    private BroadcastReceiver mBatInfoReceiver;
    private PowerManager.WakeLock mWakeLock;
    private TimeCount time;

    //计步传感器类型 0-counter 1-detector 2-加速度传感器
    private static int stepSensor = -1;
    private List<SportsRecord> mStepData;

    private int num=1;
    private int numOther=1;         //避免两次实例化
    //用于计步传感器
    private int previousStep;    //用于记录之前的步数
    private boolean isNewDay=false;    //用于判断是否是新的一天，如果是新的一天则将之前的步数赋值给previousStep

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case Constant.MSG_FROM_CLIENT:
                    try{
                        Messenger messenger=msg.replyTo;
                        Message replyMsg=Message.obtain(null,Constant.MSG_FROM_SERVER);
                        Bundle bundle=new Bundle();
                        //将现在的步数以消息的形式进行发送
                        bundle.putInt("step",StepDetector.CURRENT_STEP);
                        bundle.putFloat("Kilometer",StepDetector.mileage);
                        bundle.putInt("calorie",StepDetector.calorie);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);  //发送要返回的消息
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(){     //服务创建时调用
        super.onCreate();
        //初始化广播
        initBroadcastReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //启动步数监测器
                startStepDetector();
            }
        }).start();
        startTimeCount();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){       //每次服务启动时调用
        initTodayData();
        updateNotification("今日步数:"+StepDetector.CURRENT_STEP+" 步");

        MyNotification.getInstance().init(this);
        MyNotification.getInstance().initOne(this);
        MyNotification.getInstance().initTwo(this);
        return START_STICKY;
    }


    /**
     * 初始化当天的日期
     */
    private void initTodayData(){
        CURRENTDATE= MyTime.getTodayDate();

        Log.i(TAG, "今天日期为"+CURRENTDATE);
        //获取当天的数据
        List<SportsRecord> list= DbHelper.queryByDateToStepCount(CURRENTDATE);
        if(list.size()==0||list.isEmpty()){
            //如果获取当天数据为空，则步数为0
            StepDetector.CURRENT_STEP=0;
            StepDetector.mileage=0.00f;
            StepDetector.calorie=0;
            isNewDay=true;  //用于判断是否存储之前的数据，后面会用到
        }else if(list.size()==1){
            isNewDay=false;
            //如果数据库中存在当天的数据那么获取数据库中的步数
            StepDetector.CURRENT_STEP=list.get(0).getRealStep();
        }else{
            Log.e(TAG, "出错了！");
        }
    }

    /**
     * 初始化广播
     */
    private void initBroadcastReceiver(){
        //定义意图过滤器
        final IntentFilter filter=new IntentFilter();
        //屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //日期修改
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        //关闭广播,关机
        filter.addAction(Intent.ACTION_SHUTDOWN);
        //屏幕高亮广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        //屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        //当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        //example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        //所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_TIME_TICK);

        mBatInfoReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();

                switch (action){
                    case Intent.ACTION_SCREEN_OFF:
                        Log.v(TAG,"screen off");
                        save();
                        //改为10秒一存储
                        duration=10000;
                        break;
                    case Intent.ACTION_SHUTDOWN:
                        //关机关播
                        Log.v(TAG,"receive ACTION_SHUTDOWN");
                        save();
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        Log.v(TAG,"screen unlock");
                        save();
                        //改为30秒一存储
                        duration=30000;
                        break;
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        Log.v(TAG,"receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS  出现系统对话框");
                        //保存一次
                        save();
                        break;
                    //监听日期变化
                    case Intent.ACTION_DATE_CHANGED:
                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIME_TICK:
                        save();
                        isNewDay();
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mBatInfoReceiver,filter);
    }

    private void startTimeCount(){
        time=new TimeCount(duration,1000);
        time.start();
    }

    /**
     * 监听晚上0点变化初始化数据
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) ||
                !CURRENTDATE.equals(MyTime.getTodayDate())) {
            Log.i(TAG, "isNewDay: 新的一天");
            initTodayData();
            //发送广播去更新计步记录中的数据
            Intent msg = new Intent(Constant.UPDATESTEPCOUNT);
            sendOrderedBroadcast(msg,null);
        }
    }

    /**
     * 更新通知(显示通知栏信息)
     * @param content
     */
    private void updateNotification(String content){
//        builder=new NotificationCompat.Builder(this);
//        builder.setPriority(Notification.PRIORITY_MIN);
//        PendingIntent contentIntent=PendingIntent.getActivity(this,0,
//                new Intent(this, SportMain.class),0);
//        builder.setContentIntent(contentIntent);
//        builder.setSmallIcon(R.mipmap.ic_notification);
//        builder.setTicker("BasePedo");
//        builder.setContentTitle("BasePedo");
//        //设置不可清除
//        builder.setOngoing(true);
//        builder.setContentText(content);
//        Notification notification=builder.build(); //上面均为构造Notification的构造器中设置的属性
//
//        startForeground(0,notification);
//        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        nm.notify(R.string.app_name,notification);


//        PendingIntent contentIntent=PendingIntent.getActivity(this,0,
//                new Intent(this, SportMain.class),0);
//        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Notification notification=new NotificationCompat.Builder(this)
//                .setContentTitle("BasePedo")
//                .setContentText(content)
//                .setSmallIcon(R.mipmap.ic_notification)
//                .setTicker("BasePedo")
//                .setOngoing(true)           //设置不可清除
//                .setPriority(Notification.PRIORITY_MIN)
//                .setContentIntent(contentIntent)
//                .build();
//        startForeground(0,notification);
//        nm.notify(R.string.app_name,notification);
        if(StepDetector.CURRENT_STEP>DbHelper.getRTStepTarget()&&Math.abs(StepDetector.CURRENT_STEP-DbHelper.getRTStepTarget())<10){
            //达到要求,10步之内通知，已经通知过了，不在通知
            if(num==1){
                MyNotification.getInstance().init(this);
                MyNotification.getInstance().showSuccessNotification();
                num++;
            }
        }else {

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void startStepDetector(){
        if(sensorManager!=null&& stepDetector !=null){
            sensorManager.unregisterListener(stepDetector);
            sensorManager=null;
            stepDetector =null;
        }
        //得到休眠锁，目的是为了当手机黑屏后仍然保持CPU运行，使得服务能持续运行
        getLock(this);
        sensorManager=(SensorManager)this.getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if(VERSION_CODES>=19){
            addCountStepListener();
        }else{
            addBasePedoListener();
        }
    }

    /**
     * 使用自带的计步传感器
     *
     * 说明：
     *     开始使用这个传感器的时候很头疼，虽然它计步很准确，然而计步一开始就是5w多步，搞不懂它是怎么计算的，而且每天
     * 都在增长，不会因为日期而清除。今天灵光一闪，我脑海中飘过一个想法，会不会手机上的计步传感器是以一个月为计算周期
     * 呢？     于是乎，我打开神器QQ，上面有每天的步数，我把这个月的步数加到了一起在和手机上显示的步数进行对比，呵，
     * 不比不知道，一比吓一跳哈。就差了几百步，我猜测误差是因为QQ定期去得到计步传感器的步数，而我的引用是实时获取。要不然
     * 就是有点小误差。不过对于11W多步的数据几百步完全可以忽略。从中我可以得到下面两个信息：
     * 1.手机自带计步传感器存储的步数信息是以一个月为周期进行清算
     * 2.QQ计步使用的是手机自带的计步传感器   啊哈哈哈
     *
     *
     * 后来当我要改的时候又发现问题了
     * 我使用了StepDetector.CURRENT_STEP = (int)event.values[0];
     * 所以它会返回这一个月的步数，当每次传感器发生改变时，我直接让CURRENT_STEP++；就可以从0开始自增了
     * 不过上面的分析依然正确。不过当使用CURRENT_STEP++如果服务停掉计步就不准了。如果使用计步传感器中
     * 统计的数据减去之前的数据就是当天的数据了，这样每天走多少步就能准确的显示出来
     */
    private void addCountStepListener(){
        Sensor detectorSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);          //计步传感器
        if(countSensor!=null){
            stepSensor = 0;
            Log.v(TAG, "countSensor 步数传感器");
            sensorManager.registerListener(StepService.this,countSensor,SensorManager.SENSOR_DELAY_UI);
        }else if(detectorSensor!=null){
            stepSensor = 1;
            Log.v("base", "detector");
            sensorManager.registerListener(StepService.this,detectorSensor,SensorManager.SENSOR_DELAY_UI);
        }else{
            stepSensor = 2;
            Log.e(TAG,"Count sensor not available! 没有可用的传感器，只能用加速传感器了");
            addBasePedoListener();
        }
    }


    /**
     * 使用加速度传感器
     */
    private void addBasePedoListener(){
        //只有在使用加速传感器的时候才会调用StepDetector这个类
        stepDetector =new StepDetector(this);
        //获得传感器类型，这里获得的类型是加速度传感器
        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(stepDetector,sensor,SensorManager.SENSOR_DELAY_UI);
        stepDetector.setOnSensorChangeListener(new StepDetector.OnSensorChangeListener() {
            @Override
            public void onChange() {
                updateNotification("今日步数:"+StepDetector.CURRENT_STEP+" 步");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(stepSensor == 0){   //使用计步传感器
            if(isNewDay) {
                //用于判断是否为新的一天，如果是那么记录下计步传感器统计步数中的数据
                // 今天走的步数=传感器当前统计的步数-之前统计的步数
                previousStep = (int) event.values[0];    //得到传感器统计的步数
                isNewDay = false;
                save();
                //为防止在previousStep赋值之前数据库就进行了保存，我们将数据库中的信息更新一下
                List<SportsRecord> list=DbHelper.queryByDateToStepCount(CURRENTDATE);
                //修改数据
                SportsRecord data=list.get(0);
                data.setPreviousStep(previousStep);
                try {
                    DbHelper.saveByDateToStepCount(data);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                //取出之前的数据
                List<SportsRecord> list=DbHelper.queryByDateToStepCount(CURRENTDATE);
                SportsRecord data=list.get(0);
                this.previousStep = Integer.valueOf(data.getPreviousStep());
            }
            StepDetector.CURRENT_STEP=(int)event.values[0]-previousStep;

            //或者只是使用下面一句话，不过程序关闭后可能无法计步。根据需求可自行选择。
            //如果记录程序开启时走的步数可以使用这种方式——StepDetector.CURRENT_STEP++;
            //StepDetector.CURRENT_STEP++;
        }else if(stepSensor == 1){
            StepDetector.CURRENT_STEP++;
        }
        //更新状态栏信息
        updateNotification("今日步数：" + StepDetector.CURRENT_STEP + " 步");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 保存数据
     */
    private void save(){
        int targetStep= DbHelper.getRTStepTarget();
        int tempStep=StepDetector.CURRENT_STEP;
        SportsRecord sportsRecord=new SportsRecord();
        sportsRecord.setDate(CURRENTDATE);
        sportsRecord.setRealStep(tempStep);
        sportsRecord.setTargetStep(targetStep);
        sportsRecord.setPreviousStep(previousStep);
        try {
            DbHelper.saveByDateToStepCount(sportsRecord);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy(){                    //服务销毁时调用
        //取消前台进程
        stopForeground(true);

        unregisterReceiver(mBatInfoReceiver);
        Intent intent=new Intent(this,StepService.class);
        startService(intent);
        super.onDestroy();
    }

    //  同步方法   得到休眠锁
    synchronized private PowerManager.WakeLock getLock(Context context){
        if(mWakeLock!=null){
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
                Log.v(TAG,"释放锁");
            }

            mWakeLock=null;
        }

        if(mWakeLock==null){
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour =c.get(Calendar.HOUR_OF_DAY);
            if(hour>=23||hour<=6){
                mWakeLock.acquire(5000);
            }else{
                mWakeLock.acquire(300000);
            }
        }
        Log.v(TAG,"得到了锁");
        return (mWakeLock);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture,long countDownInterval){
            super(millisInFuture,countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            //如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }
    }


}

