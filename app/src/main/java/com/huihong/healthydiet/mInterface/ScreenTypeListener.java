package com.huihong.healthydiet.mInterface;

/**
 * Created by zangyi_shuai_ge on 2017/6/27
 * 筛选
 */

public interface ScreenTypeListener {
    //传2个参数 是否为右边  筛选类型 是否为左右切换
    void screenType(boolean isRight,String type,int typeId,boolean isSwitch);
}
