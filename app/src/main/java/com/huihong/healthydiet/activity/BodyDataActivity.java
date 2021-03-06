package com.huihong.healthydiet.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huihong.healthydiet.AppUrl;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.base.BaseTitleActivity;
import com.huihong.healthydiet.cache.sp.CacheUtils;
import com.huihong.healthydiet.mInterface.HttpUtilsListener;
import com.huihong.healthydiet.model.gsonbean.GetUserBodyInfo;
import com.huihong.healthydiet.model.gsonbean.SetUserBodyInfo;
import com.huihong.healthydiet.model.httpmodel.PersonalAllInfo;
import com.huihong.healthydiet.model.mybean.PersonalInfo;
import com.huihong.healthydiet.utils.StringUtil;
import com.huihong.healthydiet.utils.common.DateFormattedUtils;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.common.SPUtils;
import com.huihong.healthydiet.utils.current.HttpUtils;
import com.zuoni.dialog.picker.dialog.LoadingDialog;
import com.zuoni.dialog.picker.mInterface.OnSingleDataSelectedListener;
import com.zuoni.dialog.picker.picker.DataPickerDateDialog;
import com.zuoni.dialog.picker.picker.DataPickerSingleDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by zangyi_shuai_ge on 2017/7/20
 * 设置个人身体数据
 */

public class BodyDataActivity extends BaseTitleActivity {

    private TextView tvBirthday, tvLabour;
    private DataPickerDateDialog mDataPickerDateDialog;
    private DataPickerSingleDialog mLabourDialog;
    private ImageView ivBoy, ivGirl;
    private View.OnClickListener onSexClickListener;
    private TextView tvSave;
    private EditText etWeight, etHeight;
    //用户生日
    private String userBirthTime = "2017-07-28";
    //劳动强度
    private String labInten = "Ⅰ";
    //性别
    private boolean isMan = false;

    private int birthYear = 1994;
    private Calendar mCalendar3;
    private int nowYear;

    //个人信息
    private PersonalInfo personalInfo;


    private LoadingDialog loadingDialog;

    @Override
    public int setLayoutId() {
        return R.layout.activity_body_data;
    }

    @Override
    public void initUI() {
        setTitle("身体数据");
        personalInfo = CacheUtils.getPersonalInfo(BodyDataActivity.this);
        mCalendar3 = Calendar.getInstance();
        nowYear = mCalendar3.get(Calendar.YEAR);

        LoadingDialog.Builder builder = new LoadingDialog.Builder(getContext());
        builder.setMessage("加载中...");
        loadingDialog = builder.create();


        etWeight = (EditText) findViewById(R.id.etWeight);
        etHeight = (EditText) findViewById(R.id.etHeight);


        tvBirthday = (TextView) findViewById(R.id.tvBirthday);
        tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataPickerDateDialog.Builder builder = new DataPickerDateDialog.Builder(BodyDataActivity.this);
                builder.setOnDateSelectedListener(new DataPickerDateDialog.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int[] dates) {
                        if(dates[0]>=2017){
                            Toast.makeText(BodyDataActivity.this, "请输入正确的出生年月", Toast.LENGTH_SHORT).show();
                        }else {
                            tvBirthday.setText(dates[0] + "年" + DateFormattedUtils.formattedDate(dates[1]) + "月" + DateFormattedUtils.formattedDate(dates[2]) + "日");
                            userBirthTime = dates[0] + "-" + dates[1] + "-" + dates[2];
                        }
                    }
                });
                mDataPickerDateDialog = builder.create();
                mDataPickerDateDialog.show();
            }
        });


        //劳动强度选择器
        tvLabour = (TextView) findViewById(R.id.tvLabour);
        tvLabour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> datas = new ArrayList<>();
                datas.add("Ⅰ级");
                datas.add("Ⅱ级");
                datas.add("Ⅲ级");
                datas.add("Ⅳ级");
                DataPickerSingleDialog.Builder builder = new DataPickerSingleDialog.Builder(BodyDataActivity.this);
                builder.setData(datas);
                builder.setOnDataSelectedListener(new OnSingleDataSelectedListener() {
                    @Override
                    public void onDataSelected(String itemValue) {
                        tvLabour.setText(itemValue);
                        labInten = itemValue;
                    }
                });
                mLabourDialog = builder.create();
                mLabourDialog.show();
            }
        });


        ivBoy = (ImageView) findViewById(R.id.ivBoy);
        ivGirl = (ImageView) findViewById(R.id.ivGirl);
        onSexClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGirl.setImageResource(R.mipmap.body_2);
                ivBoy.setImageResource(R.mipmap.body_3);

                switch (v.getId()) {
                    case R.id.ivBoy:
                        ivBoy.setImageResource(R.mipmap.body_4);
                        isMan = true;
                        break;
                    case R.id.ivGirl:
                        ivGirl.setImageResource(R.mipmap.body_5);
                        isMan = false;
                        break;
                }

            }
        };

        ivBoy.setOnClickListener(onSexClickListener);
        ivGirl.setOnClickListener(onSexClickListener);


        tvSave = (TextView) findViewById(R.id.tvSave);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String height = etHeight.getText().toString().trim();
                String weight = etWeight.getText().toString().trim();

                if (height.equals("")) {
                    height = etHeight.getHint().toString().trim();
                }

                if (weight.equals("")) {
                    weight = etWeight.getHint().toString().trim();
                }


                if ("".equals(height)) {
                    Toast.makeText(BodyDataActivity.this, "请输入身高", Toast.LENGTH_SHORT).show();
                } else {
                    if ("".equals(weight)) {
                        Toast.makeText(BodyDataActivity.this, "请输入体重", Toast.LENGTH_SHORT).show();
                    } else {
                        saveData(height, weight);
                    }
                }


            }
        });
        getPersonalInfo();
    }

    @Override
    public void initOnClickListener() {

    }

    //获取身体数据
    private void getPersonalInfo() {

        loadingDialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("UserId", SPUtils.get(BodyDataActivity.this, "UserId", 0) + "");

        HttpUtils
                .sendHttpAddToken(BodyDataActivity.this
                        , AppUrl.GET_USER_BODY_INFO
                        , map
                        , new HttpUtilsListener() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                loadingDialog.dismiss();
                                finish();
//                                Toast.makeText(BodyDataActivity.this, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {


                                LogUtil.i("接口，获取用户信息" + response);
                                Gson gson = new Gson();
                                GetUserBodyInfo mGetUserBodyInfo = gson.fromJson(response, GetUserBodyInfo.class);
                                int code = mGetUserBodyInfo.getHttpCode();
                                if (code == 200) {

                                    if (mGetUserBodyInfo.getListData().size() > 0) {
                                        PersonalAllInfo mInfo = mGetUserBodyInfo.getListData().get(0);

                                        //设置UI
                                        etHeight.setHint(mInfo.getHeight() + "");
                                        etWeight.setHint(mInfo.getWeight() + "");
//                                        etHeight.setText(mInfo.getHeight() + "");
//                                        etWeight.setText(mInfo.getWeight() + "");

                                        ivGirl.setImageResource(R.mipmap.body_2);
                                        ivBoy.setImageResource(R.mipmap.body_3);
                                        if (mInfo.isSex()) {
                                            ivBoy.setImageResource(R.mipmap.body_4);
                                            isMan = true;
                                        } else {
                                            ivGirl.setImageResource(R.mipmap.body_5);
                                            isMan = false;
                                        }

                                        Calendar mCalendar = StringUtil.getDate(mInfo.getBirthDay());
                                        int year = mCalendar.get(Calendar.YEAR);
                                        int month = mCalendar.get(Calendar.MONTH) + 1;
                                        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                                        userBirthTime = year + "-" + month + "-" + day;
                                        birthYear = year;
                                        tvBirthday.setText(year + "年" + DateFormattedUtils.formattedDate(month) + "月" + DateFormattedUtils.formattedDate(day) + "日");
                                        if (mInfo.getLabourIntensity() != null) {
                                            labInten = mInfo.getLabourIntensity();
                                            tvLabour.setText(labInten);
                                        }
                                        personalInfo.setName(mInfo.getName());
                                        personalInfo.setHeight(mInfo.getHeight());
                                        personalInfo.setWeight(mInfo.getWeight());
                                        personalInfo.setMan(mInfo.isSex());
                                        personalInfo.setHeadImageUrl(mInfo.getHeadImage());
                                        personalInfo.setConstitution(mInfo.getConstitution());
                                        personalInfo.setAge(mInfo.getAge());
                                        personalInfo.setPhone(mInfo.getPhone());
                                        CacheUtils.putPersonalInfo(BodyDataActivity.this, personalInfo);
                                    }
                                } else {
                                    String message = mGetUserBodyInfo.getMessage();
                                    Toast.makeText(BodyDataActivity.this, message, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                etHeight.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                    }
                                }, 500);
                            }
                        });
    }

    //保存个人信息
    public void saveData(final String height, final String weight) {
        Map<String, String> map = new HashMap<>();
        map.put("UserSex", isMan + "");
        map.put("UserBirthTime", userBirthTime);
        map.put("UserHeight", height);
        map.put("UserWeight", weight);
        map.put("labInten", labInten);
        map.put("UserId", SPUtils.get(BodyDataActivity.this, "UserId", 0) + "");

        HttpUtils.sendHttpAddToken(BodyDataActivity.this
                , AppUrl.SET_USER_BODY_INFO
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("error" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("接口，个人信息保存:", response);
                        Gson gson = new Gson();
                        SetUserBodyInfo mSetUserBodyInfo = gson.fromJson(response, SetUserBodyInfo.class);
                        int code = mSetUserBodyInfo.getHttpCode();
                        String message = mSetUserBodyInfo.getMessage();
                        Toast.makeText(BodyDataActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (code == 200) {
                            PersonalAllInfo mInfo = mSetUserBodyInfo.getListData().get(0);
                            personalInfo.setName(mInfo.getName());
                            personalInfo.setHeight(mInfo.getHeight());
                            personalInfo.setWeight(mInfo.getWeight());
                            personalInfo.setMan(mInfo.isSex());
                            personalInfo.setHeadImageUrl(mInfo.getHeadImage());
                            personalInfo.setConstitution(mInfo.getConstitution());
                            personalInfo.setAge(mInfo.getAge());
                            personalInfo.setPhone(mInfo.getPhone());
                            CacheUtils.putPersonalInfo(BodyDataActivity.this, personalInfo);
                            finish();
                        }
                    }
                });

    }
}
