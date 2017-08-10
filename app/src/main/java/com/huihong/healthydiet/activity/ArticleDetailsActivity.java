package com.huihong.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huihong.healthydiet.AppUrl;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.base.BaseTitleActivity;
import com.huihong.healthydiet.adapter.LvTagAdapterForArticleList;
import com.huihong.healthydiet.model.ArticleInfo;
import com.huihong.healthydiet.bean.GetArticleItemInfo;
import com.huihong.healthydiet.mInterface.HttpUtilsListener;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.common.SPUtils;
import com.huihong.healthydiet.utils.current.HttpUtils;
import com.huihong.healthydiet.widget.HorizontalListView;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by zangyi_shuai_ge on 2017/7/27
 * 文章详情展示界面
 */

public class ArticleDetailsActivity extends BaseTitleActivity {

    private WebView mWebView;
    private ImageView ivThumbsUp;
    private TextView tvClickCount;
    private TextView tvLoveCount;

    private int loveNum;
    private boolean isLove = false;
    private int id;

    private LinearLayout layoutThumbsUp;


    private int pos = -1;
    GetArticleItemInfo.ListDataBean mListDataBean;


    @Override
    public int setLayoutId() {
        return R.layout.activity_article_details;
    }

    @Override
    public void initUI() {
        setTitle("推荐饮食");
        ivThumbsUp = (ImageView) findViewById(R.id.ivThumbsUp);
        layoutThumbsUp = (LinearLayout) findViewById(R.id.layoutThumbsUp);
        mWebView = (WebView) findViewById(R.id.mWebView);

        ArticleInfo mInfo = (ArticleInfo) getIntent().getSerializableExtra("info");
//        SearchVagueRestaurant.ListData2Bean mInfo2= (SearchVagueRestaurant.ListData2Bean) getIntent().getSerializableExtra("info2");

        pos = getIntent().getIntExtra("pos", -1);


        if(mInfo!=null){
            loveNum = mInfo.getLoveCount();
            isLove = mInfo.isPointPraise();
            id = mInfo.getArticleId();

        if (mInfo.isPointPraise()) {
            ivThumbsUp.setImageResource(R.mipmap.thumbs_up);
        } else {
            ivThumbsUp.setImageResource(R.mipmap.thumbs_up_normal);
        }


        WebSettings webSettings = mWebView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
//            webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
//            webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(false);
        //加载需要显示的网页
        mWebView.loadUrl(mInfo.getUrl());
        //设置Web视图
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);
                return true;
            }
        });

        TextView tvArticleTitle = (TextView) findViewById(R.id.tvArticleTitle);
        tvArticleTitle.setText(mInfo.getTitle());


        tvClickCount = (TextView) findViewById(R.id.tvClickCount);
        tvClickCount.setText((mInfo.getCilckCount() + 1) + "");

        tvLoveCount = (TextView) findViewById(R.id.tvLoveCount);
        tvLoveCount.setText(mInfo.getLoveCount() + "");

        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText(mInfo.getATime() + "");

        HorizontalListView lvTag = (HorizontalListView) findViewById(R.id.lvTag);
        lvTag.setAdapter(new LvTagAdapterForArticleList(ArticleDetailsActivity.this, mInfo.getTags()));


        layoutThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLove) {
                    thumbsUp("Delete");
                } else {
                    thumbsUp("Insert");
                }
            }
        });


//        see();
        getNewArticleInfo();

        setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMyActivity();
            }
        });

    }
//        if(mInfo2!=null){
//            loveNum = mInfo2.getLoveCount();
//            isLove = mInfo2.isPointPraise();
//            id = mInfo2.getArticleId();
//
//            if (mInfo2.isPointPraise()) {
//                ivThumbsUp.setImageResource(R.mipmap.thumbs_up);
//            } else {
//                ivThumbsUp.setImageResource(R.mipmap.thumbs_up_normal);
//            }
//
//
//            WebSettings webSettings = mWebView.getSettings();
//            //设置WebView属性，能够执行Javascript脚本
////            webSettings.setJavaScriptEnabled(true);
//            //设置可以访问文件
////            webSettings.setAllowFileAccess(true);
//            //设置支持缩放
//            webSettings.setBuiltInZoomControls(false);
//            //加载需要显示的网页
//            mWebView.loadUrl(mInfo2.getUrl());
//            //设置Web视图
//            mWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String request) {
//                    view.loadUrl(request);
//                    return true;
//                }
//            });
//
//            TextView tvArticleTitle = (TextView) findViewById(R.id.tvArticleTitle);
//            tvArticleTitle.setText(mInfo2.getTitle());
//
//
//            tvClickCount = (TextView) findViewById(R.id.tvClickCount);
//            tvClickCount.setText((mInfo2.getCilckCount() + 1) + "");
//
//            tvLoveCount = (TextView) findViewById(R.id.tvLoveCount);
//            tvLoveCount.setText(mInfo2.getLoveCount() + "");
//
//            TextView tvTime = (TextView) findViewById(R.id.tvTime);
//            tvTime.setText(mInfo2.getATime() + "");
//
//            HorizontalListView lvTag = (HorizontalListView) findViewById(R.id.lvTag);
//            lvTag.setAdapter(new LvTagAdapterForArticleList(ArticleDetailsActivity.this, mInfo2.getTags()));
//
//
//            layoutThumbsUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isLove) {
//                        thumbsUp("Delete");
//                    } else {
//                        thumbsUp("Insert");
//                    }
//                }
//            });
//
//
////        see();
//            getNewArticleInfo();
//
//            setLeftOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finishMyActivity();
//                }
//            });
//
//        }


    }

    private void getNewArticleInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("Id", id + "");
        map.put("UserId", SPUtils.get(ArticleDetailsActivity.this, "UserId", 0) + "");
        HttpUtils.sendHttpAddToken(ArticleDetailsActivity.this, AppUrl.GET_ARTICLE_ITEM_INFO
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("文章详情", response);
                        Gson gson = new Gson();
                        GetArticleItemInfo mGetArticleItemInfo = gson.fromJson(response, GetArticleItemInfo.class);
                        mListDataBean = mGetArticleItemInfo.getListData().get(0);
                        isLove = mListDataBean.isPointPraise();
                        if (isLove) {
                            ivThumbsUp.setImageResource(R.mipmap.thumbs_up);
                        } else {
                            ivThumbsUp.setImageResource(R.mipmap.thumbs_up_normal);
                        }

                        tvClickCount.setText(mListDataBean.getCilckCount() + "");
                        tvLoveCount.setText(mListDataBean.getLoveCount() + "");
                        loveNum = mListDataBean.getLoveCount();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("文章详情", e.toString());
                    }
                });


    }

    private void see() {
        Map<String, String> map = new HashMap<>();
        map.put("Id", id + "");

        HttpUtils.sendHttpAddToken(ArticleDetailsActivity.this, AppUrl.ARTICLE_CLICK
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("文章查看", response);

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("文章查看", e.toString());
                    }
                });

    }


    //点赞
    private void thumbsUp(String Opertion) {

        Map<String, String> map = new HashMap<>();
        map.put("OtherId", id + "");
        map.put("UserId", SPUtils.get(ArticleDetailsActivity.this, "UserId", 0) + "");
        map.put("Opertion", Opertion);


        HttpUtils.sendHttpAddToken(ArticleDetailsActivity.this, AppUrl.ARTICLE_POINT_PRAISE
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("文章点赞", response);

                        if (isLove) {
                            ivThumbsUp.setImageResource(R.mipmap.thumbs_up_normal);
                            loveNum = loveNum - 1;
                            tvLoveCount.setText(loveNum + "");
                            isLove = false;


                        } else {
                            loveNum = loveNum + 1;
                            ivThumbsUp.setImageResource(R.mipmap.thumbs_up);
                            tvLoveCount.setText(loveNum + "");
                            isLove = true;
                        }
                        if (mListDataBean != null) {
                            mListDataBean.setLoveCount(loveNum);
                            mListDataBean.setPointPraise(isLove);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("文章点赞", e.toString());
                    }
                });


    }

    @Override
    public void initOnClickListener() {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishMyActivity();

    }

    private void finishMyActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", mListDataBean);
        LogUtil.i("嘿嘿",pos+"");
        bundle.putInt("pos",pos);
        //通过Intent对象返回结果，调用setResult方法
        intent.putExtras(bundle);
        setResult(2,intent);
        finish();//结束当前的activity的生命周期
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
