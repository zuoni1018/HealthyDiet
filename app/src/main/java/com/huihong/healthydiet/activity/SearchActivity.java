package com.huihong.healthydiet.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.base.BaseActivity;
import com.huihong.healthydiet.adapter.RvHistorySearchAdapter;
import com.huihong.healthydiet.adapter.RvHotSearchAdapter;
import com.huihong.healthydiet.utils.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zangyi_shuai_ge on 2017/7/20
 * 搜索时界面
 */

public class SearchActivity extends BaseActivity {
    private RecyclerView rvHotSearch;
    private RvHotSearchAdapter mRvHotSearchAdapter;
    private RecyclerView rvHistorySearch;
    private RvHistorySearchAdapter mRvHistorySearchAdapter;


    private ImageView ivDeleteHistory;


    private LinearLayout layoutBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
    }

    private AlertDialog mAlertDialog;

    private void initUI() {

        layoutBack= (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initHotSearch();
        initHistorySearch();

        ivDeleteHistory = (ImageView) findViewById(R.id.ivDeleteHistory);
        ivDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAlertDialog!=null){
                    mAlertDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("清除历史记录");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        historyList.clear();
                        mRvHistorySearchAdapter.notifyDataSetChanged();
                        mAlertDialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                mAlertDialog=builder.create();
                mAlertDialog.show();

            }
        });


        TextView tvSearch= (TextView) findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(SearchActivity.this, SearchResultActivity.class);
                startActivity(mIntent);
            }
        });
    }
    private List<String> historyList;

    private void initHistorySearch() {
        rvHistorySearch = (RecyclerView) findViewById(R.id.rvHistorySearch);
        historyList=new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            historyList.add("我是历史搜索" + i);
        }

        rvHistorySearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvHistorySearchAdapter = new RvHistorySearchAdapter(SearchActivity.this, historyList);
        rvHistorySearch.setAdapter(mRvHistorySearchAdapter);
    }

    private void initHotSearch() {
        rvHotSearch = (RecyclerView) findViewById(R.id.rvHotSearch);
        List<String> zz = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            zz.add("搜索" + i);
        }
        for (int i = 0; i < 4; i++) {
            zz.add("热门搜索" + i);
        }
        for (int i = 0; i < 4; i++) {
            zz.add("我真的是热门搜索" + i);
        }


        mRvHotSearchAdapter = new RvHotSearchAdapter(SearchActivity.this, zz);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager(SearchActivity.this);
        rvHotSearch.setLayoutManager(flowLayoutManager);
        rvHotSearch.setAdapter(mRvHotSearchAdapter);
    }

}