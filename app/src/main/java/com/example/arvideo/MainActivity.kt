package com.example.arvideo

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import android.app.Activity
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var exoPlayer: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
            }
            title = ""
        })

        // Inicializar ExoPlayer
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri("https://d3krfb04kdzji1.cloudfront.net/chorro-quevedo-video-ia.mp4"))
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_ALL
        }

        supportFragmentManager.commit {
            add(R.id.containerFragment, MainFragment(exoPlayer))
        }

        supportFragmentManager.commit {
            replace(R.id.containerFragment2, ReproductorFragment(exoPlayer))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release() // Liberar recursos del ExoPlayer
    }

    fun Fragment.setFullScreen(
        fullScreen: Boolean = true,
        hideSystemBars: Boolean = true,
        fitsSystemWindows: Boolean = true
    ) {
        requireActivity().setFullScreen(
            this.requireView(),
            fullScreen,
            hideSystemBars,
            fitsSystemWindows
        )
    }
}

private fun Activity.setFullScreen(
    rootView: View,
    fullScreen: Boolean = true,
    hideSystemBars: Boolean = true,
    fitsSystemWindows: Boolean = true
) {
    rootView.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)
            WindowInsetsControllerCompat(window, rootView).apply {
                if (hideSystemBars) {
                    if (fullScreen) {
                        hide(
                            WindowInsetsCompat.Type.statusBars() or
                                    WindowInsetsCompat.Type.navigationBars()
                        )
                    } else {
                        show(
                            WindowInsetsCompat.Type.statusBars() or
                                    WindowInsetsCompat.Type.navigationBars()
                        )
                    }
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
}

private fun View.doOnApplyWindowInsets(action: (systemBarsInsets: Insets) -> Unit) {
    doOnAttach {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            action(insets.getInsets(WindowInsetsCompat.Type.systemBars()))
            WindowInsetsCompat.CONSUMED
        }
    }
}