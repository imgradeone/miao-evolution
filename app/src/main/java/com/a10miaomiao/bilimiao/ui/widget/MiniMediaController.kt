package com.a10miaomiao.bilimiao.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import cn.a10miaomiao.player.MyMediaController
import cn.a10miaomiao.player.MyMediaController.generateTime
import cn.a10miaomiao.player.callback.MediaController
import cn.a10miaomiao.player.callback.MediaPlayerListener
import com.a10miaomiao.bilimiao.R
import kotlinx.android.synthetic.main.layout_mini_media_controller.view.*


class MiniMediaController : FrameLayout, MediaController, View.OnClickListener, View.OnTouchListener {

    var mMediaPlayer: MediaPlayerListener? = null
    private var mDragging = false

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.layout_mini_media_controller, this)
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        mPauseButton.setOnClickListener(this)
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun show(timeout: Int) {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }

    override fun isShowing(): Boolean {
        return visibility == View.VISIBLE
    }

    override fun setMediaPlayer(player: MediaPlayerListener) {
        mMediaPlayer = player
    }

    override fun setAnchorView(v: View) {

    }

    override fun setTitle(title: String) {
        mToolbar.title = title
    }

    override fun onClick(v: View) {
        val mPlayer = mMediaPlayer ?: return
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            mPauseButton.setImageResource(cn.a10miaomiao.player.R.drawable.bili_player_play_can_play)
        } else {
            mPlayer.start()
            mPauseButton.setImageResource(cn.a10miaomiao.player.R.drawable.bili_player_play_can_pause)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return false
    }

    fun setBackOnClick(onClickListener: OnClickListener) {
        mToolbar.setNavigationOnClickListener(onClickListener)
    }

    fun setZoomOnClick(onClickListener: OnClickListener) {
        mZoomIv.setOnClickListener(onClickListener)
    }

    /**
     * 设置播放进度
     */
    fun setProgress(): Long {
        val mPlayer = mMediaPlayer
        if (mPlayer == null || mDragging) {
            return 0
        }
        val position = mPlayer.currentPosition
        val duration = mPlayer.duration
        if (mSeekBar != null) {
            if (duration > 0) {
                val pos = 1000L * position / duration
                mSeekBar.progress = pos.toInt()
            }
            val percent = mPlayer.bufferPercentage
            mSeekBar.secondaryProgress = (percent * 10).toInt()
        }
//        mDuration = duration
        mEndTime.text = MyMediaController.generateTime(duration)
        mCurrentTime.text = MyMediaController.generateTime(position)
        return position
    }

    fun setProgress(position: Long) {
        val mPlayer = mMediaPlayer
        if (mPlayer == null || mDragging) {
            return
        }
        val duration = mPlayer.duration
        val pos = 1000L * position / duration
        mSeekBar.progress = pos.toInt()
        mCurrentTime.text = MyMediaController.generateTime(position)
    }

    fun updatePausePlay() {
        val mPlayer = mMediaPlayer
        if (mPlayer != null) {
            if (mPlayer.isPlaying) {
                mPauseButton.setImageResource(cn.a10miaomiao.player.R.drawable.bili_player_play_can_pause)
            } else {
                mPauseButton.setImageResource(cn.a10miaomiao.player.R.drawable.bili_player_play_can_play)
            }
        }
    }

}
