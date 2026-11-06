package com.lastwave.app.ui.common

import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Movie
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Hardware-accelerated looping GIF view using [ImageDecoder] + [AnimatedImageDrawable] on API 28+,
 * with a deterministic [Movie] canvas fallback on legacy Android 7.0–8.1 (API 24–27).
 */
@Composable
fun AnimatedGifBanner(
    @RawRes rawResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    this.contentDescription = contentDescription
                    runCatching {
                        val source = ImageDecoder.createSource(ctx.resources, rawResId)
                        val drawable = ImageDecoder.decodeDrawable(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                        setImageDrawable(drawable)
                        if (drawable is AnimatedImageDrawable) {
                            drawable.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
                            drawable.start()
                        }
                    }
                }
            },
            update = { imageView ->
                val d = imageView.drawable
                if (d is AnimatedImageDrawable && !d.isRunning) {
                    d.start()
                }
            },
        )
    } else {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                LegacyMovieGifView(ctx, rawResId).apply {
                    this.contentDescription = contentDescription
                }
            },
        )
    }
}

@Suppress("DEPRECATION")
private class LegacyMovieGifView(
    context: android.content.Context,
    @RawRes rawResId: Int,
) : View(context) {
    private val movie: Movie? = runCatching {
        context.resources.openRawResource(rawResId).use { stream ->
            val bytes = stream.readBytes()
            Movie.decodeByteArray(bytes, 0, bytes.size)
        }
    }.getOrNull()

    private var movieStartMs: Long = 0L

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val m = movie ?: return
        val now = SystemClock.uptimeMillis()
        if (movieStartMs == 0L) movieStartMs = now
        val duration = m.duration().let { if (it <= 0) 1000 else it }
        val relTime = ((now - movieStartMs) % duration).toInt()
        m.setTime(relTime)

        val mw = m.width().coerceAtLeast(1).toFloat()
        val mh = m.height().coerceAtLeast(1).toFloat()
        val vw = width.coerceAtLeast(1).toFloat()
        val vh = height.coerceAtLeast(1).toFloat()
        val scale = maxOf(vw / mw, vh / mh)
        val dx = (vw - mw * scale) / (2f * scale)
        val dy = (vh - mh * scale) / (2f * scale)

        canvas.save()
        canvas.scale(scale, scale)
        m.draw(canvas, dx, dy)
        canvas.restore()
        postInvalidateOnAnimation()
    }
}
