package com.synaric.mutinodeprogressbar;

import android.view.View;

import java.util.List;

/**
 * 适配器。
 * Created by Synaric on 2016/5/20 0020.
 */
public abstract class SimpleMutiNodeAdapter<T> implements MutiNodeAdapter<T>{

    private List<T> data;

    public SimpleMutiNodeAdapter(List<T> data){
        this.data = data;
    }

    public int getCount(){
        return data.size();
    }

    public Object getItem(int position){
        return data.get(position);
    }

    /**
     * 生成节点下方的描述视图。
     */
    @Override
    public abstract View getDescView(int position);

    /**
     * 生成节点视图。
     */
    @Override
    public View getNodeView(int position){return null;}
}
