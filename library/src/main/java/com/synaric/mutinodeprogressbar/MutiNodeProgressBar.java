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

/**
 * 多节点进度条。
 * Created by Synaric on 2016/5/20 0020.
 */
public class MutiNodeProgressBar extends RelativeLayout {

    public static final int MIN_NODE_INTERVAL = 50;

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

    public MutiNodeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.context = getContext();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MutiNodeProgressBar);
        nodeSize = typedArray.getDimensionPixelSize(R.styleable.MutiNodeProgressBar_nodeSize, 50);
        nodeSrc = typedArray.getResourceId(R.styleable.MutiNodeProgressBar_nodeSrc, R.drawable.bg_node);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.MutiNodeProgressBar_lineHeight, 10);
        lineForeColor = typedArray.getColor(R.styleable.MutiNodeProgressBar_lineForeColor,
                getResources().getColor(android.R.color.holo_blue_light));
        lineBackColor = typedArray.getColor(R.styleable.MutiNodeProgressBar_lineBackColor,
                getResources().getColor(R.color.c_ccc));
        progress = typedArray.getFloat(R.styleable.MutiNodeProgressBar_progress, 0);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = nodeSize + getPaddingTop() + getPaddingBottom();
        int maxChildHeight = 0;
        int firstNodeWidth = 0, lastNodeWidth = 0;
        if(null != adapter){
            for(int i = 0; i < nodeCount; ++i){
                View descView = adapter.getDescView(i);
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

        addViews();

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void addViews() {
        //初始化线条
        addLine(lineWidth, lineHeight);

        //初始化节点和描述
        for(int i = 0; i < nodeCount; ++i){
            ImageView ivNode = new ImageView(context);
            ivNode.setImageResource(nodeSrc);
            ivNode.setTag(adapter.getDescView(i));
            ivNode.setEnabled(i <= progress);
            addView(ivNode);
            addView(adapter.getDescView(i));
        }
    }

    private void addLine(int width, int height){
        SimpleProgressBar line = new SimpleProgressBar(context);
        LayoutParams lineParams = new LayoutParams(width, height);
        line.setForeColor(lineForeColor);
        line.setBackColor(lineBackColor);
        line.setStroke(lineHeight);
        line.setProgress(progress / (nodeCount - 1));
        line.setLayoutParams(lineParams);
        addView(line);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int realLeft = left + getPaddingLeft();
        int realRight = right - getPaddingRight();
        int realTop = top + getPaddingTop();
        int realBottom = bottom - getPaddingBottom();

        final float dHalf = (nodeSize - lineHeight) / 2;
        View line = getChildAt(0);
        //布局线
        line.layout(
                realLeft + startHalfWidth,
                (int) (realTop + dHalf + 0.5f),
                realRight - endHalfWidth,
                (int) (realTop + dHalf + lineHeight + 0.5f));

        //布局节点和节点描述
        for(int i = 0;i < nodeCount; ++i){
            View desc = getChildAt(2 * (i+1));
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
            throw new IllegalArgumentException("node count must > 0.");
        }

        removeAllViews();
        requestLayout();
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

        public MutiNodeAdapter<T> getAdapter() {
            return adapter;
        }
    }
}
