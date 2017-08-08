package com.huihong.healthydiet.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.huihong.healthydiet.R;
import com.huihong.healthydiet.cache.sp.CacheUtils;
import com.huihong.healthydiet.utils.common.DateFormattedUtils;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.common.SPUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 这个服务用于监听闹铃
 */
public class AlarmClockService extends Service implements MediaPlayer.OnCompletionListener {

    private String TAG = "AlarmClockService";
    private MediaPlayer player;//音乐播放器
    private BroadcastReceiver mBatInfoReceiver;//广播接受者
    private AlertDialog dialog;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate()");
        player = MediaPlayer.create(this, R.raw.tt);
        player.setOnCompletionListener(this);
        initBroadcastReceiver();
    }

    /**
     * 注册广播
     */
    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        //注册广播接收器
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action)) {//这个日期监听更准一点
                    //判断是否开启了闹钟
                    if (CacheUtils.isOpenAlarm(AlarmClockService.this)) {
                        isAlarmClock();
                    }
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    //判断当前时间是否为闹铃
    private void isAlarmClock() {
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date()) + "";

        //去缓存中拿时间

        int startHour = (int) SPUtils.get(this, "startHour", 23);
        int startMin = (int) SPUtils.get(this, "startMin", 0);
        int endHour = (int) SPUtils.get(this, "endHour", 8);
        int endMin = (int) SPUtils.get(this, "endMin", 0);

        //格式化时间
        String sleepTime = DateFormattedUtils.formattedDate(startHour) + DateFormattedUtils.formattedDate(startMin);
        String getUpTime = DateFormattedUtils.formattedDate(endHour) + DateFormattedUtils.formattedDate(endMin);


        if (getUpTime.equals(nowTime)&&isSetWeek()) {
            //时间是到了需要判断星期
            //当前为起床闹铃播放闹铃
            player.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_get_up, null);
            Button button = (Button) view.findViewById(R.id.tvGetUp);
            button.setText("起床");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //话说只有点了起床按钮才能保存到数据库中去
                    //这时候我点了
                    //拿到昨天设置的睡眠时间 并吧日期存储到昨天去





                    if (player.isPlaying()) {
                        player.stop();
                    }
                    dialog.dismiss();
                }
            });

            TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvTime.setText(getUpTime);
            builder.setView(view);
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            dialog.show();
        } else if (sleepTime.equals(nowTime)&&isSetWeek()) {
            //当前为睡觉闹铃 只播放提示声音
//            player.start();
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
            rt.play();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_get_up, null);
            Button button = (Button) view.findViewById(R.id.tvGetUp);
            button.setText("睡觉");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (player.isPlaying()) {
//                        player.stop();
//                    }
                    dialog.dismiss();
                }
            });
            TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvTime.setText(sleepTime);
            builder.setView(view);
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            dialog.show();
        }
    }

    private boolean isSetWeek() {
        List<Boolean> mSleepWeek = CacheUtils.getSleepWeek(this);
        //获得当前的星期
        Calendar c = Calendar.getInstance();
        int week = c.get(Calendar.DAY_OF_WEEK);
        //从1开始 1为周天
        if(week==1){
            return mSleepWeek.get(6);
        }else {
            return  mSleepWeek.get(week);
        }
    }


    //音乐播放器完成
    @Override
    public void onCompletion(MediaPlayer mp) {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消前台进程
        stopForeground(true);
        unregisterReceiver(mBatInfoReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
