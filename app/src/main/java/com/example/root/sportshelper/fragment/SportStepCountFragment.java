package com.example.root.sportshelper.fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.ShowBadgeInfo;
import com.example.root.sportshelper.SportMain;
import com.example.root.sportshelper.StepCount;
import com.example.root.sportshelper.adapter.HStepCountAdapter;
import com.example.root.sportshelper.adapter.HStepRecordAdapter;
import com.example.root.sportshelper.database.BadgeRecord;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.ruler.CircleProgress;
import com.example.root.sportshelper.service.SedentaryReminderService;
import com.example.root.sportshelper.service.StepService;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MiscUtil;
import com.example.root.sportshelper.utils.MyTime;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 计步
 * Created by root on 17-8-29.
 */

public class SportStepCountFragment extends Fragment implements Handler.Callback{
    private String TAG="SportStepCountFragment";
    private View rootView;
    //循环取当前时刻的步数中间的时间间隔
    private long TIME_INTERVAL = 500;

    private List<SportsRecord> sportsRecordsList=new ArrayList<>();         //运动记录
    private CircleProgress mCircleProgress;
    private int stepTarget;         //步数目标
    private TextView Kilometer;     //公里数
    private TextView calorie;       //销耗的卡路里
    RecyclerView HStepRecord;       //
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private int lastStep=0;         //记录上一次步数
    private String continueOrStop="myContinue";  //持续或者停止
    private boolean isBound = false;               //客户端与服务端是否连接

    private boolean isBind = false;

    private UpdateRecordReceiver updateRecordReceiver=new UpdateRecordReceiver();
    private IntentFilter intentFilter=new IntentFilter(Constant.UPDATESTEPCOUNT);

    private UpdateStepTarget updateStepTarget=new UpdateStepTarget();
    private IntentFilter updateStepTargetIntentFilter=new IntentFilter(Constant.UPDATESTEPTARGET);

    private float frontTotalKm=0;                 //今天之前的公里
    private int step=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //DataSupport.deleteAll(SportsRecord.class);
        if(rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent!=null){
                parent.removeView(rootView);
            }
            startServiceForStrategy();
            getActivity().registerReceiver(updateRecordReceiver,intentFilter);
            getActivity().registerReceiver(updateStepTarget,updateStepTargetIntentFilter);
        }else {
            //重用weekfragment视图
            rootView=inflater.inflate(R.layout.sport_step_count_fragment,container,false);
            initView(rootView);
            initComponent();
            initSportRecord();
            initRecycleView();
            delayHandler = new Handler(this);
            startServiceForStrategy();
            getActivity().registerReceiver(updateRecordReceiver,intentFilter);
            getActivity().registerReceiver(updateStepTarget,updateStepTargetIntentFilter);

            getFrontDayKm();
        }
        return rootView;
    }

    private void initView(View v){
        Kilometer=(TextView)v.findViewById(R.id.Kilometer);
        calorie=(TextView)v.findViewById(R.id.calorie);
        mCircleProgress = (CircleProgress)v.findViewById(R.id.circle_progress);
        HStepRecord=(RecyclerView)v.findViewById(R.id.HStepRecord);
    }

    private void initRecycleView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        HStepRecord.setLayoutManager(layoutManager);
        HStepRecordAdapter hStepRecordAdapter=new HStepRecordAdapter(sportsRecordsList);
        HStepRecord.setAdapter(hStepRecordAdapter);

        hStepRecordAdapter.setOnItemClickListener(new HStepRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportsRecord sportsRecord=sportsRecordsList.get(position);
                continueOrStop="myStop";
                delayHandler.removeMessages(Constant.REQUEST_SERVER);
                Intent step_count=new Intent(getActivity(),StepCount.class);
                step_count.putExtra("id",sportsRecord.getId());
                startActivityForResult(step_count,1);
            }
        });
    }

    private void initComponent(){
        List<SportsRecord> list= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
        Log.i(TAG, "initComponent: 今天日期为；"+MyTime.getTodayDate());
        if(list.size()==0||list.isEmpty()){
            Log.i(TAG, "initComponent: 没有记录");
            mCircleProgress.setValue(0);
            Kilometer.setText(0.00+"");
            calorie.setText(0+"");
        }else if(list.size()==1){
            Log.i(TAG, "initComponent: 有一条记录");
            mCircleProgress.setValue(list.get(0).getRealStep());
            Kilometer.setText(list.get(0).getMileage()+"");
            calorie.setText(list.get(0).getCalorie()+"");
        }
        mCircleProgress.setMaxValue(DbHelper.getRTStepTarget());
    }

    private void initSportRecord()  {
        sportsRecordsList.clear();
        List<SportsRecord> sportsRecords= DataSupport
                .limit(30)
                .offset(1)
                .order("id desc")
                .find(SportsRecord.class);
        for(SportsRecord mySportRecord:sportsRecords){
            try {
                if(MyTime.isYesterday(mySportRecord.getDate())){
                    mySportRecord.setDate("昨天");
                }else {
                    try {
                        mySportRecord.setDate(MyTime.changeFormat(mySportRecord.getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sportsRecordsList.add(mySportRecord);
        }
    }

    //以bind形式开启service，故有ServiceConnection接收回调
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            try {
                //我们可以通过从Service的onBind方法中返回的IBinder初始化一个指向Service端的Messenger
                messenger = new Messenger(service);
                isBound=true;
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //messenger=null;
            isBound=false;
            startServiceForStrategy();

        }
    };

    //接收从服务端回调的步数
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                //更新步数
                Kilometer.setText(msg.getData().getFloat("Kilometer")+"");
                calorie.setText(msg.getData().getInt("calorie")+"");
                step=msg.getData().getInt("step");

                if(isAdded()){
                    ChallengeTenThousandSteps(step);
                    ChallengeTwentyThousandSteps(step);
                    ChallengeThirtyThousandSteps(step);

                    ForThreeDays(step);
                    ForFiveDays(step);
                    ForTenDays(step);
                    ForTwentyDays(step);
                    ForFiftyDays(step);
                    ForOneHundredDays(step);

                    TotalTenkilometers();
                    TotalTwentykilometers();
                    TotalFiftykilometers();
                    TotalOneHundredkilometers();
                    TotalFiveHundredkilometers();
                    TotalThousandkilometers();
                }

                Constant.CURR_STEP=step;
                Constant.CURR_CALORIE=msg.getData().getInt("calorie");
                if(step!=lastStep){
                    lastStep=step;
                    mCircleProgress.setValue((float)step);
                }
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                if(continueOrStop.equals("myContinue")){
                    if(!isBound){
                        startServiceForStrategy();
                    }
                    if(isBound){
                        try {
                            Message msgl = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                            msgl.replyTo = mGetReplyMessenger;
                            messenger.send(msgl);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void startServiceForStrategy() {
        if (!isServiceWork(getActivity(), StepService.class.getName())) {
            setupService(true);
        } else {
            setupService(false);
        }

    }

    /**
     * 开启服务
     * true-bind和start两种方式一起执行 false-只执行bind方式
     */
    private void setupService(boolean flag) {
        Intent intent = new Intent(getActivity(), StepService.class);
        isBind=getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);            //第三个参数表示活动与服务进行绑定后自动创建服务
        if(flag){
            getActivity().startService(intent);
        }

        Intent SedentaryReminder = new Intent(getActivity(), SedentaryReminderService.class);
        getActivity().startService(SedentaryReminder);         //启动久坐提醒服务
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    continueOrStop=data.getStringExtra("continueOrStop");
                    delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                }
                break;
            default:
        }
    }


    @Override
    public void onDestroyView() {
        //isBind=false;
        if(isBind){
            getActivity().getApplicationContext().unbindService(conn);
        }
        getActivity().unregisterReceiver(updateRecordReceiver);
        getActivity().unregisterReceiver(updateStepTarget);
        super.onDestroyView();
    }

    public class UpdateRecordReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 日期变化");
            initComponent();
            initSportRecord();
            initRecycleView();
            abortBroadcast();
            getFrontDayKm();
        }
    }

    public class UpdateStepTarget extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 计步目标改变"+DbHelper.getRTStepTarget());
            mCircleProgress.setMaxValue(DbHelper.getRTStepTarget());
            abortBroadcast();           //截断广播
        }
    }

    public int getStep(){
        return step;
    }

    //挑战10000步
    public void ChallengeTenThousandSteps(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeTenThousandSteps))){
            if(Math.abs(step-10000)<5&&step>10000){
                Log.i(TAG, "挑战10000步");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeTenThousandSteps));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeTenThousandSteps));
                startActivity(intent);
            }
        }
    }

    //挑战20000步
    public void ChallengeTwentyThousandSteps(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeTwentyThousandSteps))){
            if(Math.abs(step-20000)<5&&step>20000){
                Log.i(TAG, "挑战20000步");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeTwentyThousandSteps));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeTwentyThousandSteps));
                startActivity(intent);
            }
        }
    }

    //挑战30000步
    public void ChallengeThirtyThousandSteps(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ChallengeThirtyThousandSteps))){
            if(Math.abs(step-30000)<5&&step>30000){
                Log.i(TAG, "挑战30000步");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.ChallengeThirtyThousandSteps));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.ChallengeThirtyThousandSteps));
                startActivity(intent);
            }
        }
    }

    //连续3天
    public void ForThreeDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForThreeDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForThreeDays=false;                          //是否连续三天运动超过10000步
                for(int i=1;i<3;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForThreeDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForThreeDays = true;
                        } else {
                            isForThreeDays = false;
                            break;
                        }
                    }
                }
                if(isForThreeDays){
                    Log.i(TAG, "连续3天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForThreeDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForThreeDays));
                    startActivity(intent);
                }
            }
        }
    }

    //连续5天
    public void ForFiveDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForFiveDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForFiveDays=false;
                for(int i=1;i<5;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForFiveDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForFiveDays = true;
                        } else {
                            isForFiveDays = false;
                            break;
                        }
                    }
                }
                if(isForFiveDays){
                    Log.i(TAG, "连续5天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForFiveDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForFiveDays));
                    startActivity(intent);
                }
            }
        }
    }

    //连续10天
    public void ForTenDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForTenDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForTenDays=false;
                for(int i=1;i<10;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForTenDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForTenDays = true;
                        } else {
                            isForTenDays = false;
                            break;
                        }
                    }
                }
                if(isForTenDays){
                    Log.i(TAG, "连续10天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForTenDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForTenDays));
                    startActivity(intent);
                }
            }
        }
    }

    //连续20天
    public void ForTwentyDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForTwentyDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForTwentyDays=false;                          //是否连续三天运动超过10000步
                for(int i=1;i<20;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForTwentyDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForTwentyDays = true;
                        } else {
                            isForTwentyDays = false;
                            break;
                        }
                    }
                }
                if(isForTwentyDays){
                    Log.i(TAG, "连续20天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForTwentyDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForTwentyDays));
                    startActivity(intent);
                }
            }
        }
    }

    //连续50天
    public void ForFiftyDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForFiftyDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForFiftyDays=false;                          //是否连续三天运动超过10000步
                for(int i=1;i<50;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForFiftyDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForFiftyDays = true;
                        } else {
                            isForFiftyDays = false;
                            break;
                        }
                    }
                }
                if(isForFiftyDays){
                    Log.i(TAG, "连续50天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForFiftyDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForFiftyDays));
                    startActivity(intent);
                }
            }
        }
    }

    //连续100天
    public void ForOneHundredDays(int step){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.ForOneHundredDays))){
            if(Math.abs(step-10000)<5&&step>10000){
                String Today=MyTime.getTodayDate();
                String frontDay;
                Boolean isForOneHundredDays=false;                          //是否连续三天运动超过10000步
                for(int i=1;i<100;i++){
                    frontDay=MyTime.getFrontDate(Today,i);
                    List<SportsRecord> sportsRecordList=DbHelper.queryByDateToStepCount(frontDay);
                    if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                        isForOneHundredDays=false;
                        break;
                    }else if(sportsRecordList.size()==1) {
                        if (sportsRecordList.get(0).getRealStep() > 10000) {
                            isForOneHundredDays = true;
                        } else {
                            isForOneHundredDays = false;
                            break;
                        }
                    }
                }
                if(isForOneHundredDays){
                    Log.i(TAG, "连续100天");
                    BadgeRecord badgeRecord=new BadgeRecord();
                    badgeRecord.setTitle(getResources().getString(R.string.ForOneHundredDays));
                    badgeRecord.setWhetherGet(true);
                    DbHelper.saveBadgeRecord(badgeRecord);
                    Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                    intent.putExtra("title",getResources().getString(R.string.ForOneHundredDays));
                    startActivity(intent);
                }
            }
        }
    }

    //获得今天以前所有的步数
    public void getFrontDayKm(){
        List<SportsRecord> sportsRecordList=DataSupport.offset(1).order("id desc").find(SportsRecord.class);
        if(!(sportsRecordList.size()==0)||!sportsRecordList.isEmpty()){
            for (SportsRecord mySportsRecord:sportsRecordList){
                frontTotalKm+=mySportsRecord.getMileage();
            }
        }
    }

    //累计10公里
    public void TotalTenkilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalTenkilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=10){
                Log.i(TAG, "累计10公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalTenkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalTenkilometers));
                startActivity(intent);
            }
        }
    }

    //累计20公里
    public void TotalTwentykilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalTwentykilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=20){
                Log.i(TAG, "累计20公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalTwentykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalTwentykilometers));
                startActivity(intent);
            }
        }
    }

    //累计50公里
    public void TotalFiftykilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalFiftykilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=50){
                Log.i(TAG, "累计50公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalFiftykilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalFiftykilometers));
                startActivity(intent);
            }
        }
    }

    //累计100公里
    public void TotalOneHundredkilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalOneHundredkilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=100){
                Log.i(TAG, "累计100公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalOneHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalOneHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计500公里
    public void TotalFiveHundredkilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalFiveHundredkilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=500){
                Log.i(TAG, "累计500公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalFiveHundredkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalFiveHundredkilometers));
                startActivity(intent);
            }
        }
    }

    //累计1000公里
    public void TotalThousandkilometers(){
        if(!DbHelper.getwhetherGet(getResources().getString(R.string.TotalThousandkilometers))){
            List<SportsRecord> sportsRecordList= DbHelper.queryByDateToStepCount(MyTime.getTodayDate());
            float totalKm=0;
            if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
                totalKm=frontTotalKm;
            }else if(sportsRecordList.size()==1){
                totalKm=frontTotalKm+sportsRecordList.get(0).getMileage();
            }
            if(totalKm>=1000){
                Log.i(TAG, "累计1000公里");
                BadgeRecord badgeRecord=new BadgeRecord();
                badgeRecord.setTitle(getResources().getString(R.string.TotalThousandkilometers));
                badgeRecord.setWhetherGet(true);
                DbHelper.saveBadgeRecord(badgeRecord);
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",getResources().getString(R.string.TotalThousandkilometers));
                startActivity(intent);
            }
        }
    }

}
