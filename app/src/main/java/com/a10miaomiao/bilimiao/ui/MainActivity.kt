package com.a10miaomiao.bilimiao.ui

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.a10miaomiao.bilimiao.R
import kotlinx.android.synthetic.main.activity_main.*
import com.a10miaomiao.bilimiao.ui.home.MainFragment
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import android.view.WindowInsets
import com.a10miaomiao.bilimiao.netword.LoginHelper
import com.a10miaomiao.bilimiao.store.FilterStore
import com.a10miaomiao.bilimiao.store.TimeSettingStore
import com.a10miaomiao.bilimiao.store.UserStore
import com.a10miaomiao.bilimiao.ui.video.VideoInfoFragment
import com.a10miaomiao.bilimiao.utils.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator


class MainActivity : SupportActivity() {

    var windowInsets: WindowInsets? = null
    val behavior by lazy {
        BottomSheetBehavior.from(bottomSheet)
    }
    var bottomSheetFragment: Fragment? = null

    val timeSettingStore by lazy { TimeSettingStore(this) }
    val filterStore by lazy { FilterStore(this) }
    val userStore by lazy { UserStore(this) }
    val themeUtil by lazy { ThemeUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.init()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            windowInsets = rootLayout.rootWindowInsets
        }
        initBottomSheet()
        if (findFragment(MainFragment::class.java) == null) {
            loadRootFragment(R.id.rootContainer, MainFragment())
        }
//        loadRootFragment(R.id.rightContainer, SearchFragment())
        LoginHelper.oss()
    }

    override fun start(toFragment: ISupportFragment?) {
//        loadMultipleRootFragment(R.id.rightContainer,1, toFragment)
//        if (SupportHelper.getActiveFragment(supportFragmentManager) == null) {
//            loadMultipleRootFragment()
//        } else {
//            super.start(toFragment)
//        }
        if (toFragment is VideoInfoFragment) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            // 判断是否选择了使用 外部播放器
            if (prefs.getBoolean("is_bili_player", false)) {
                val id = toFragment.arguments!!.getString(ConstantUtil.ID)
                try {
                    var intent = Intent(Intent.ACTION_VIEW)
                    var url = "bilibili://video/$id"
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                } catch (e: Exception) {
                    var intent = Intent(Intent.ACTION_VIEW)
                    var url = "http://www.bilibili.com/video/av$id"
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
                return
            }
            super.start(toFragment, SupportFragment.SINGLETASK)
        } else {
            super.start(toFragment)
        }

    }

    override fun onBackPressedSupport() {
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN)
            super.onBackPressedSupport()
        else
            hideBottomSheet()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return when (prefs.getString("fragment_animator", "vertical")) {
            "vertical" -> DefaultVerticalAnimator()
            "horizontal" -> DefaultHorizontalAnimator()
            else -> super.onCreateFragmentAnimator()
        }
    }

    fun openDrawer() {
        findFragment(MainFragment::class.java)?.openDrawer()
    }

    private fun initBottomSheet() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                if (p1 < 0) {
                    shadeView.alpha = (p1 + 1) * 0.6f
                } else {
                    shadeView.alpha = 0.6f
                }
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    shadeView.visibility = View.GONE
                    bottomSheetFragment?.let {
                        supportFragmentManager.beginTransaction()
                                .remove(it)
                                .commit()
                        bottomSheetFragment = null
                    }
                } else {
                    shadeView.visibility = View.VISIBLE
                }
            }
        })

        shadeView.setOnClickListener { hideBottomSheet() }
        (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams)
                .setMargins(0, getStatusBarHeight(), 0, 0)
        shadeView.visibility =
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN)
                    View.GONE
                else
                    View.VISIBLE
    }

    fun showBottomSheet(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.bottomSheettContainer, fragment)
                .commit()
        bottomSheetFragment = fragment
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun hideBottomSheet() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun dynamicTheme(owner: LifecycleOwner, builder: () -> View) = themeUtil.dynamicTheme(owner, builder)

    companion object {
        fun of(context: Context): MainActivity {
            if (context is MainActivity) {
                return context
            } else {
                throw Exception()
            }
        }
    }
}