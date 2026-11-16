package vn.lichviet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import vn.lichviet.ui.R

/** Be Vietnam Pro – font chữ do người Việt thiết kế, hiển thị dấu tiếng Việt rõ ràng. */
val BeVietnam = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
    Font(R.font.be_vietnam_pro_extrabold, FontWeight.ExtraBold),
    Font(R.font.be_vietnam_pro_extrabold, FontWeight.Black),
)

private val base = Typography()
val LichTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = BeVietnam), displayMedium = base.displayMedium.copy(fontFamily = BeVietnam),
    displaySmall = base.displaySmall.copy(fontFamily = BeVietnam), headlineLarge = base.headlineLarge.copy(fontFamily = BeVietnam),
    headlineMedium = base.headlineMedium.copy(fontFamily = BeVietnam), headlineSmall = base.headlineSmall.copy(fontFamily = BeVietnam),
    titleLarge = base.titleLarge.copy(fontFamily = BeVietnam, fontWeight = FontWeight.Bold), titleMedium = base.titleMedium.copy(fontFamily = BeVietnam),
    titleSmall = base.titleSmall.copy(fontFamily = BeVietnam), bodyLarge = base.bodyLarge.copy(fontFamily = BeVietnam),
    bodyMedium = base.bodyMedium.copy(fontFamily = BeVietnam), bodySmall = base.bodySmall.copy(fontFamily = BeVietnam),
    labelLarge = base.labelLarge.copy(fontFamily = BeVietnam), labelMedium = base.labelMedium.copy(fontFamily = BeVietnam),
    labelSmall = base.labelSmall.copy(fontFamily = BeVietnam),
)

/** Giao diện đang dùng, để màn hình lấy màu tốt/xấu, tên nhóm... */
val LocalLich = staticCompositionLocalOf<ThemeSpec?> { null }

@Composable
fun LichTheme(spec: ThemeSpec, content: @Composable () -> Unit) {
    val container = spec.accent.copy(alpha = 0.18f).compositeOver(spec.card)
    val container2 = spec.accent2.copy(alpha = 0.18f).compositeOver(spec.card)
    val scheme = if (spec.dark) {
        darkColorScheme(
            primary = spec.accent, onPrimary = spec.onAccent,
            primaryContainer = container, onPrimaryContainer = spec.ink,
            secondary = spec.accent2, onSecondary = spec.onAccent2,
            secondaryContainer = container2, onSecondaryContainer = spec.ink,
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
            secondary = spec.accent2, onSecondary = spec.onAccent2,
            secondaryContainer = container2, onSecondaryContainer = spec.ink,
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
        MaterialTheme(colorScheme = scheme, typography = LichTypography) {
            androidx.compose.material3.ProvideTextStyle(LichTypography.bodyMedium, content)
        }
    }
}
