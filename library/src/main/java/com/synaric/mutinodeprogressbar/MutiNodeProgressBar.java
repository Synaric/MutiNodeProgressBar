package com.synaric.mutinodeprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.synaric.libmutinode.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 多节点进度条。
 * Created by Synaric on 2016/5/20 0020.
 */
public class MutiNodeProgressBar extends RelativeLayout {

    public static final int MIN_NODE_INTERVAL = 35;

    public static final int DEFAULT_NODE_SIZE = 10;

    public static final float DEFAULT_PROGRESS = 0;

    private Context context;

    /**
     * 适配器。
     */
    private AdapterWrapper adapter;

    /**
     * 节点个数。
     */
    private int nodeCount;

    /**
     * 节点大小。
     */
    private int nodeSize;

    /**
     * 节点间隔。
     */
    private float nodeInterval;

    /**
     * 节点图像资源文件。
     */
    private int nodeSrc;

    /**
     * 线长度。
     */
    private int lineWidth;

    /**
     * 线宽度。
     */
    private int lineHeight;

    /**
     * 线颜色（激活态）。
     */
    private int lineForeColor;

    /**
     * 线颜色（原始态）。
     */
    private int lineBackColor;

    /**
     * 进度[0 - nodeCount]。
     */
    private float progress;

    /**
     * min{节点宽度，第一个节点描述的宽度} / 2
     */
    private int startHalfWidth;

    /**
     * min{节点宽度，最后一个节点描述的宽度} / 2
     */
    private int endHalfWidth;

    /**
     * 背景的线。
     */
    private SimpleProgressBar lineView;

    /**
     * 节点。
     */
    private List<View> nodeViews;

    /**
     * 描述。
     */
    private List<View> descriptionViews;

    public MutiNodeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.context = getContext();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MutiNodeProgressBar);
        nodeSize = typedArray.getDimensionPixelSize(R.styleable.MutiNodeProgressBar_nodeSize, MIN_NODE_INTERVAL);
        nodeSrc = typedArray.getResourceId(R.styleable.MutiNodeProgressBar_nodeSrc, R.drawable.bg_node);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.MutiNodeProgressBar_lineHeight, DEFAULT_NODE_SIZE);
        lineForeColor = typedArray.getColor(R.styleable.MutiNodeProgressBar_lineForeColor,
                getResources().getColor(android.R.color.holo_blue_light));
        lineBackColor = typedArray.getColor(R.styleable.MutiNodeProgressBar_lineBackColor,
                getResources().getColor(R.color.c_ccc));
        progress = typedArray.getFloat(R.styleable.MutiNodeProgressBar_progress, DEFAULT_PROGRESS);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int maxChildHeight = 0;
        int firstNodeWidth = 0, lastNodeWidth = 0;
        if(null != adapter){
            for(int i = 0; i < nodeCount; ++i){
                View descView = descriptionViews.get(i);
                descView.measure(0, 0);
                int measuredChildWidth = descView.getMeasuredWidth();
                int measuredChildHeight = descView.getMeasuredHeight();
                maxChildHeight =
                        maxChildHeight < measuredChildHeight ? measuredChildHeight : maxChildHeight;

                if(i == 0){
                    firstNodeWidth = measuredChildWidth;
                }else if(i == nodeCount - 1){
                    lastNodeWidth = measuredChildWidth;
                }
            }

            View sample = nodeViews.get(0);
            if(null != sample){
                sample.measure(0, 0);
                nodeSize = sample.getMeasuredWidth();
            }
            height = nodeSize + getPaddingTop() + getPaddingBottom();
        }
        height += maxChildHeight;

        ViewGroup.LayoutParams lp = getLayoutParams();
        startHalfWidth = (nodeSize > firstNodeWidth ? nodeSize : firstNodeWidth) / 2;
        endHalfWidth = (nodeSize > lastNodeWidth ? nodeSize : lastNodeWidth) / 2;

        //确定线的长度
        if(ViewGroup.LayoutParams.WRAP_CONTENT == lp.width){
            int width = 0;
            width += (
                    startHalfWidth +
                    endHalfWidth +
                    (lineWidth = MIN_NODE_INTERVAL * Math.max(nodeCount, 0))
            );
            nodeInterval = MIN_NODE_INTERVAL;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }else{
            final int myWidth = MeasureSpec.getSize(widthMeasureSpec);
            lineWidth = myWidth - (
                    startHalfWidth +
                    endHalfWidth +
                    getPaddingLeft() +
                    getPaddingRight()
            );
            nodeInterval = lineWidth / (nodeCount - 1);
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void addViews() {
        nodeViews = new ArrayList<>();
        descriptionViews = new ArrayList<>();

        //初始化线条
        addLine(lineWidth, lineHeight);

        //初始化节点和描述
        for(int i = 0; i < nodeCount; ++i){
            View node = adapter.getNodeView(i);
            if(node == null){
                node = createDefaultNodeView();
            }
            node.setTag(adapter.getDescView(i));
            Utils.setChildEnabled(node, i <= progress);
            addView(node);
            final View desc = adapter.getDescView(i);
            addView(desc);

            nodeViews.add(node);
            descriptionViews.add(desc);
        }
    }

    private View createDefaultNodeView(){
        ImageView iv = new ImageView(context);
        iv.setImageResource(nodeSrc);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(nodeSize, nodeSize);
        iv.setLayoutParams(lp);
        return iv;
    }

    private void addLine(int width, int height){
        lineView = new SimpleProgressBar(context);
        LayoutParams lineParams = new LayoutParams(width, height);
        lineView.setForeColor(lineForeColor);
        lineView.setBackColor(lineBackColor);
        lineView.setStroke(lineHeight);
        lineView.setProgress(progress / (nodeCount - 1));
        lineView.setLayoutParams(lineParams);
        addView(lineView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int realLeft = getPaddingLeft();
        int realRight = right - left - getPaddingRight();
        int realTop = getPaddingTop();
        int realBottom = bottom - top - getPaddingBottom();

        final float dHalf = (nodeSize - lineHeight) / 2;
        lineView = (SimpleProgressBar) getChildAt(0);
        lineView.layout(
                realLeft + startHalfWidth,
                (int) (realTop + dHalf + 0.5f),
                realRight - endHalfWidth,
                (int) (realTop + dHalf + lineHeight + 0.5f));

        //布局节点和节点描述
        for(int i = 0;i < nodeCount; ++i){
            View desc = getChildAt(2 * (i + 1));
            desc.layout(
                    (int)(realLeft + startHalfWidth - desc.getMeasuredWidth() / 2 + nodeInterval * i + 0.5f),
                    realTop + nodeSize,
                    (int)(realRight + 0.5f),
                    realBottom
            );

            View node = getChildAt(2 * i + 1);
            node.layout(
                    (int)(realLeft + startHalfWidth - nodeSize / 2 + nodeInterval * i + 0.5f),
                    realTop,
                    (int)(realLeft + startHalfWidth + nodeInterval * i + nodeSize / 2 + 0.5f),
                    realTop + nodeSize);
        }
    }

    public MutiNodeAdapter getAdapter() {
        return adapter.getAdapter();
    }

    public void setAdapter(@NonNull SimpleMutiNodeAdapter<?> adapter) {
        this.adapter = new AdapterWrapper<>(adapter);
        nodeCount = adapter.getCount();
        if(nodeCount <= 0){
            throw new IllegalArgumentException("Node count must > 0.");
        }

        removeAllViews();
        addViews();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if(null == adapter){
            throw  new IllegalArgumentException("You must call setAdapter() before setProgress().");
        }
        updateProgress(progress);
    }

    private void updateProgress(float progress) {
        for(int i = 0; i < nodeViews.size(); ++i){
            View node = nodeViews.get(i);
            Utils.setChildEnabled(node, i <= progress);
        }
        invalidate();
    }

    private class AdapterWrapper<T> implements MutiNodeAdapter<T>{

        private MutiNodeAdapter<T> adapter;

        AdapterWrapper(MutiNodeAdapter<T> adapter){
            this.adapter = adapter;
        }

        @Override
        public View getDescView(int position) {
            View descView = adapter.getDescView(position);
            descView.setTag(position);
            return descView;
        }

        @Override
        public View getNodeView(int position) {
            return adapter.getNodeView(position);
        }

        public MutiNodeAdapter<T> getAdapter() {
            return adapter;
        }
    }
}
