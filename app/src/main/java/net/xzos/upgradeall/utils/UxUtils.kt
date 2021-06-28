package net.xzos.upgradeall.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import net.xzos.upgradeall.application.MyApplication
import net.xzos.upgradeall.core.utils.getAppName
import net.xzos.upgradeall.utils.egg.setAppEggTitleSuffix
import java.util.regex.Pattern

object UxUtils {
    fun getStatusBarHeight(resources: Resources): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun getRandomColor(): Int {
        val range = if (isDarkMode()) {
            (68..136)
        } else {
            (132..200)
        }
        val r = range.random()
        val g = range.random()
        val b = range.random()

        return Color.parseColor(String.format("#%02x%02x%02x", r, g, b))
    }

    fun getFirstChar(str: String, upperCase: Boolean): String {
        val s = str.toCharArray().find { !firstCharPattern.matcher(it.toString()).find() }
        return if (upperCase) {
            s?.uppercaseChar()
        } else {
            s
        }?.toString() ?: ""
    }

    fun getRandomBackgroundTint() = ColorStateList.valueOf(getRandomColor())

    private val firstCharPattern =
        Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]$")

    fun getAppTitle(context: Context, view: View): SpannableStringBuilder {
        val sb = SpannableStringBuilder()
        getAppName(context)?.run {
            sb.append(this)
            setAppEggTitleSuffix(sb, view)
        }
        return sb
    }

    fun isDarkMode(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> isDarkModeOnSystem()
            else -> false
        }
    }

    fun isDarkModeOnSystem(): Boolean {
        return when (MyApplication.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    fun setSystemBarStyle(window: Window, needLightStatusBar: Boolean = true) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        if (!isDarkMode()) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needLightStatusBar) {
                window.decorView.systemUiVisibility = (
                        window.decorView.systemUiVisibility
                                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }
    }
}