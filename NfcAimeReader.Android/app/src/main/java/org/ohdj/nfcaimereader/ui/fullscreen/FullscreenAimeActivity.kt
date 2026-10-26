package org.ohdj.nfcaimereader.ui.fullscreen

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.ohdj.nfcaimereader.R

/**
 * 全屏展示 Aime 读卡图片。
 *
 * 独立 Activity：进入时隐藏系统栏，返回（系统返回键）时 finish 本 Activity，
 * 回到 MainActivity —— 主界面的系统栏状态完全不受影响，不会出现黑边。
 */
class FullscreenAimeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            FullscreenAimeContent()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
private fun FullscreenAimeContent() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeColor.White)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val painter = painterResource(id = R.drawable.aime_fullscreen)
        val intrinsic = painter.intrinsicSize
        val imageAspect = if (intrinsic.width > 0f && intrinsic.height > 0f) {
            intrinsic.width / intrinsic.height
        } else {
            1600f / 2608f
        }
        val imageHeight = maxWidth / imageAspect // 宽度 100% 时的高度
        Image(
            painter = painter,
            contentDescription = "Aime 读卡",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
        )
    }
}
