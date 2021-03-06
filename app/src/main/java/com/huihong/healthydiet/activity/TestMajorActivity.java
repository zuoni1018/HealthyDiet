package com.huihong.healthydiet.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huihong.healthydiet.AppUrl;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.base.BaseTitleActivity;
import com.huihong.healthydiet.adapter.RvMajorAnswerAdapter;
import com.huihong.healthydiet.model.gsonbean.GetQuestionProfessionList;
import com.huihong.healthydiet.model.mybean.MajorAnswer;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.common.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.zhy.http.okhttp.OkHttpUtils.post;

/**
 * Created by zangyi_shuai_ge on 2017/7/25
 */

public class TestMajorActivity extends BaseTitleActivity {

    private RecyclerView rvAnswer;
    private RvMajorAnswerAdapter rvMajorAnswerAdapter;
    private TextView tvAnswerTitle;
    private List<GetQuestionProfessionList.ListDataBean> getListData;
    private List<MajorAnswer> MajorAnswerList;

    private int num = 0;

    private ProgressDialog progressDialog;

    @Override
    public int setLayoutId() {
        return R.layout.activity_test_major;
    }

    @Override
    public void initUI() {
        progressDialog=new ProgressDialog(TestMajorActivity.this);
        MajorAnswerList = new ArrayList<>();
        setTitle("体质测试");
        tvAnswerTitle = (TextView) findViewById(R.id.tvAnswerTitle);

        rvAnswer = (RecyclerView) findViewById(R.id.rvAnswer);
        rvAnswer.setLayoutManager(new LinearLayoutManager(TestMajorActivity.this, LinearLayoutManager.VERTICAL, false));
        rvMajorAnswerAdapter = new RvMajorAnswerAdapter(TestMajorActivity.this);
        rvAnswer.setAdapter(rvMajorAnswerAdapter);

        findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getListData != null) {
                    int answer = rvMajorAnswerAdapter.getAnswer();
                    if (answer == -1) {
                        Toast.makeText(TestMajorActivity.this, "请先选择本题答案", Toast.LENGTH_SHORT).show();
                    } else {
                        rvMajorAnswerAdapter.resetChooseId();
                        //
                        MajorAnswer majorAnswer = new MajorAnswer();
                        majorAnswer.setAnswer(answer);
                        majorAnswer.setQuestionId(getListData.get(num).getQuestionId());
                        MajorAnswerList.add(majorAnswer);
                        //
                        num++;
                        if (num < getListData.size()) {
                            tvAnswerTitle.setText((num+1) + "、" + getListData.get(num).getQuestionContent());
                        } else {
                            getInfo2();
                            finish();
                            Toast.makeText(TestMajorActivity.this, "答题完成", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        getInfo();
    }

    @Override
    public void initOnClickListener() {

    }

    private void getInfo2() {

        String a="";
        for (int i = 0; i <MajorAnswerList.size() ; i++) {
            a=a+MajorAnswerList.get(i).getQuestionId()+","+MajorAnswerList.get(i).getAnswer()+"|";
        }


        post()
                .url(AppUrl.GET_SUBMIT_QUESTION)
                .addParams("UserId",  SPUtils.get(TestMajorActivity.this,"UserId",0)+"")
                .addParams("answer",a)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("error" + e);
                        Toast.makeText(TestMajorActivity.this, R.string.service_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("接口，专业版测试:", response);


                    }
                });

    }


    private void getInfo() {
        progressDialog.setMessage("正在获取问卷中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpUtils
                .post()
                .url(AppUrl.GET_QUESTION_PROFESSION_LIST)
                .addParams("UserId",  SPUtils.get(TestMajorActivity.this,"UserId",0)+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("error" + e);
                        Toast.makeText(TestMajorActivity.this, R.string.service_error, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();
                        LogUtil.i("接口，专业版测试:", response);
                        Gson gson = new Gson();
                        GetQuestionProfessionList mGetQuestionProfessionList = gson.fromJson(response, GetQuestionProfessionList.class);
                        int code = mGetQuestionProfessionList.getHttpCode();
                        if (code == 200) {
                            getListData = mGetQuestionProfessionList.getListData();
                            //设置第一题
                            tvAnswerTitle.setText("1、" + getListData.get(0).getQuestionContent());
                            TextView tvNum = (TextView) findViewById(R.id.tvNum);
                            tvNum.setText("*共" + getListData.size() + "道题，均为单选。");

                        } else {
                            String message = mGetQuestionProfessionList.getMessage();
                            Toast.makeText(TestMajorActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });

    }
}
