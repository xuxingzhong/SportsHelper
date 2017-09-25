package com.example.root.sportshelper;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.example.root.sportshelper.database.GpsRecord;
import com.example.root.sportshelper.utils.MiscUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ShowMap extends AppCompatActivity implements  LocationSource , AMapLocationListener ,View.OnClickListener{
    public static final String TAG = "ShowMap";
    //显示地图需要的变量
    private MapView mapView;                //地图控件
    private AMap aMap;                      //地图对象
    TextView distance;
    TextView speed;
    TextView UseTime;

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器

    MyLocationStyle myLocationStyle;            //定位蓝点

    Button myExit;                  //退出
    Button location_bt;             //定位

    ArrayList<GpsRecord> locations = null;
    float curr_dist = 0;
    float total_dist = 0;
    double center_lat = 0;
    double center_lng = 0;


    private final PolylineOptions polylines=new PolylineOptions();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private Timer timer;
    public static final int UPDATE=1;


    private Handler handler=new Handler(){

        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE:
                    speed.setText(String.format("%s", MiscUtil.getSpeedString(InRunning.curr_speed)));           //速度
                    distance.setText(String.format("%.2f", MiscUtil.getDistance(InRunning.curr_distance)));          //距离
                    UseTime.setText(InRunning.timeStr);                                                         //时间
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_map);

        locations = (ArrayList<GpsRecord>)getIntent().getSerializableExtra("myLocations");
        for (GpsRecord r: locations) {
            total_dist += r.getDistance();
        }
        Log.i(TAG, "location numbler: " + locations.size());
        polylines.color(Color.BLUE).width(10);

        initView();
        //显示地图
        mapView=(MapView)findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initaMap();
        initPolylines();
        //开始定位
        initLoc();

        aMap.addPolyline(polylines);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(center_lat / locations.size(), center_lng / locations.size())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        updateUi();
    }

    private void initView(){
        distance=(TextView)findViewById(R.id.distance);
        speed=(TextView)findViewById(R.id.speed);
        UseTime=(TextView)findViewById(R.id.UseTime);

        myExit=(Button)findViewById(R.id.myExit);
        location_bt=(Button)findViewById(R.id.location_bt);
        myExit.setOnClickListener(this);
        location_bt.setOnClickListener(this);
    }

    private void initaMap(){
        if(aMap==null){
            //获取地图对象
            aMap = mapView.getMap();
        }
        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(false);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(false);

        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.landian));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);               //设置定位蓝点的Style
        //aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void initPolylines(){
        if (null != aMap && locations != null) {
            // Add a marker for every earthquake
            int cnt = 0;
            for (GpsRecord rec: locations) {
                Log.i(TAG, rec.toString());
                cnt++;
                if (cnt==1 || cnt == locations.size() ) {
                    // Add a new marker
                    MarkerOptions mk = new MarkerOptions().position(new LatLng(rec.getLat(), rec.getLng()));

                    // Set the title of the Marker's information window
                    if (cnt==1) {
                        mk.title(String.valueOf(getResources().getString(R.string.startplace)));
                        //0.0f表示红色，见http://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html?2D/com/amap/api/maps2d/model/BitmapDescriptorFactory.html
                        mk.icon(BitmapDescriptorFactory.defaultMarker(0.0f));
                        aMap.addMarker(mk).showInfoWindow();
                    } else if (cnt == locations.size()){
                        mk.title(String.valueOf(getResources().getString(R.string.endplace)));
                        //颜色表见：http://a.amap.com/lbs/static/unzip/Android_Map_Doc/2D/constant-values.html#com.amap.api.maps2d.model.BitmapDescriptorFactory.HUE_RED
                        mk.icon(BitmapDescriptorFactory.defaultMarker(210.0f));
                        aMap.addMarker(mk).showInfoWindow();
                    }
                    builder.include(mk.getPosition());
                }
                curr_dist += rec.getDistance();
                center_lat += rec.getLat();
                center_lng += rec.getLng();

//                polylines.add(MiscUtil.getLatLng(rec.loc));
                polylines.add(new LatLng(rec.getLat(),rec.getLng()));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myExit:

                finish();
                break;
            case R.id.location_bt:
                if(mLocationClient!=null){
                    isFirstLoc=true;
                    mLocationClient.startLocation();
                }
                break;
        }
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

    //定位回调函数
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

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    //aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +"" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
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

    public void updateUi(){
        timer=new Timer(true);          //是否为后台线程，后台线程当且仅当进程结束时，自动注销掉
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=UPDATE;
                handler.sendMessage(message);
            }
        };
        timer.schedule(timerTask,0,1000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
        //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

        }
        return false;
    }

}
