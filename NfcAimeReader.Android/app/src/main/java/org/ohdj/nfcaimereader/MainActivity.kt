package org.ohdj.nfcaimereader

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import org.ohdj.nfcaimereader.data.datastore.FelicaPreferenceViewModel
import org.ohdj.nfcaimereader.data.datastore.UserPreferenceViewModel
import org.ohdj.nfcaimereader.ui.navigation.Navigation
import org.ohdj.nfcaimereader.ui.theme.NfcAimeReaderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 当前系统栏颜色（与 TopAppBar / 底部导航栏同步），onResume 时重新套用
    private var statusBarColor: Int = Color.WHITE
    private var navigationBarColor: Int = Color.WHITE
    private var lightStatusBarIcons: Boolean = true
    private var lightNavigationBarIcons: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: UserPreferenceViewModel = viewModel()
            val felicaPreferenceViewModel: FelicaPreferenceViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState(initial = false)

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                },
                navigationBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
            )

            val supportsDynamicTheming = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            NfcAimeReaderTheme(
                darkTheme = darkTheme,
                dynamicColor = supportsDynamicTheming && dynamicColorEnabled
            ) {
                // 状态栏背景 = TopAppBar 颜色（surface）
                // 导航栏背景 = 底部导航栏颜色（surfaceContainer）
                val topBarColor = MaterialTheme.colorScheme.surface
                val bottomBarColor = MaterialTheme.colorScheme.surfaceContainer
                SideEffect {
                    statusBarColor = topBarColor.toArgb()
                    navigationBarColor = bottomBarColor.toArgb()
                    lightStatusBarIcons = topBarColor.luminance() > 0.5f
                    lightNavigationBarIcons = bottomBarColor.luminance() > 0.5f
                    applySystemBarColors()
                }
                Navigation(viewModel, felicaPreferenceViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applySystemBarColors()
    }

    /** 套用系统栏颜色：状态栏 = TopAppBar 色，导航栏 = 底栏色（从全屏页返回时也重新套用） */
    private fun applySystemBarColors() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = statusBarColor
        @Suppress("DEPRECATION")
        window.navigationBarColor = navigationBarColor
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        controller.isAppearanceLightStatusBars = lightStatusBarIcons
        controller.isAppearanceLightNavigationBars = lightNavigationBarIcons
    }

    override fun onPause() {
        super.onPause()
    }
}