package vn.lichviet.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.compositeOver

/** Giao diện đang dùng, để màn hình lấy màu tốt/xấu, tên nhóm... */
val LocalLich = staticCompositionLocalOf<ThemeSpec?> { null }

@Composable
fun LichTheme(spec: ThemeSpec, content: @Composable () -> Unit) {
    val container = spec.accent.copy(alpha = 0.18f).compositeOver(spec.card)
    val scheme = if (spec.dark) {
        darkColorScheme(
            primary = spec.accent, onPrimary = spec.onAccent,
            primaryContainer = container, onPrimaryContainer = spec.ink,
            secondary = spec.accent, onSecondary = spec.onAccent,
            secondaryContainer = container, onSecondaryContainer = spec.ink,
            tertiary = spec.good,
            background = spec.bg, onBackground = spec.ink,
            surface = spec.bg, onSurface = spec.ink,
            surfaceVariant = spec.card, onSurfaceVariant = spec.dim,
            surfaceContainerLowest = spec.card, surfaceContainerLow = spec.card, surfaceContainer = spec.card,
            surfaceContainerHigh = spec.card, surfaceContainerHighest = spec.card,
            outline = spec.dim, outlineVariant = spec.dim.copy(alpha = 0.4f),
            error = spec.bad,
        )
    } else {
        lightColorScheme(
            primary = spec.accent, onPrimary = spec.onAccent,
            primaryContainer = container, onPrimaryContainer = spec.ink,
            secondary = spec.accent, onSecondary = spec.onAccent,
            secondaryContainer = container, onSecondaryContainer = spec.ink,
            tertiary = spec.good,
            background = spec.bg, onBackground = spec.ink,
            surface = spec.bg, onSurface = spec.ink,
            surfaceVariant = spec.card, onSurfaceVariant = spec.dim,
            surfaceContainerLowest = spec.card, surfaceContainerLow = spec.card, surfaceContainer = spec.card,
            surfaceContainerHigh = spec.card, surfaceContainerHighest = spec.card,
            outline = spec.dim, outlineVariant = spec.dim.copy(alpha = 0.4f),
            error = spec.bad,
        )
    }
    CompositionLocalProvider(LocalLich provides spec) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
