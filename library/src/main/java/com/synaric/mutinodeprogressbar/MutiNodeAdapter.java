package com.synaric.mutinodeprogressbar;

import android.view.View;

/**
 *
 * Created by Synaric on 2016/5/20 0020.
 */
public interface MutiNodeAdapter<T> {
    /**
     * 设置描述的View。
     */
    View getDescView(int position);

    /**
     * 设置节点的View，比起nodeSrc有更大的自由度。但是节点的大小应当一致。
     */
    View getNodeView(int position);
}
