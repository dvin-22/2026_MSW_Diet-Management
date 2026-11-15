package com.example.diet_management.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GrapefruitLightPrimary,
    onPrimary = GrapefruitLightOnPrimary,
    primaryContainer = GrapefruitLightPrimaryContainer,
    onPrimaryContainer = GrapefruitLightOnPrimaryContainer,
    secondary = GrapefruitLightSecondary,
    onSecondary = GrapefruitLightOnSecondary,
    secondaryContainer = GrapefruitLightSecondaryContainer,
    onSecondaryContainer = GrapefruitLightOnSecondaryContainer,
    tertiary = GrapefruitLightTertiary,
    onTertiary = GrapefruitLightOnTertiary,
    tertiaryContainer = GrapefruitLightTertiaryContainer,
    onTertiaryContainer = GrapefruitLightOnTertiaryContainer,
    background = GrapefruitLightBackground,
    onBackground = GrapefruitLightOnBackground,
    surface = GrapefruitLightBackground,
    onSurface = GrapefruitLightOnBackground,
    surfaceVariant = GrapefruitLightSurfaceVariant,
    onSurfaceVariant = GrapefruitLightOnSurfaceVariant,
    outline = GrapefruitLightOutline,
    outlineVariant = GrapefruitLightOutlineVariant,
    surfaceContainerLowest = GrapefruitLightSurfaceContainerLowest,
    surfaceContainerLow = GrapefruitLightSurfaceContainerLow,
    surfaceContainer = GrapefruitLightSurfaceContainer,
    surfaceContainerHigh = GrapefruitLightSurfaceContainerHigh,
    surfaceContainerHighest = GrapefruitLightSurfaceContainerHighest,
)

private val DarkColorScheme = darkColorScheme(
    primary = GrapefruitDarkPrimary,
    onPrimary = GrapefruitDarkOnPrimary,
    primaryContainer = GrapefruitDarkPrimaryContainer,
    onPrimaryContainer = GrapefruitDarkOnPrimaryContainer,
    secondary = GrapefruitDarkSecondary,
    onSecondary = GrapefruitDarkOnSecondary,
    secondaryContainer = GrapefruitDarkSecondaryContainer,
    onSecondaryContainer = GrapefruitDarkOnSecondaryContainer,
    tertiary = GrapefruitDarkTertiary,
    onTertiary = GrapefruitDarkOnTertiary,
    tertiaryContainer = GrapefruitDarkTertiaryContainer,
    onTertiaryContainer = GrapefruitDarkOnTertiaryContainer,
    background = GrapefruitDarkBackground,
    onBackground = GrapefruitDarkOnBackground,
    surface = GrapefruitDarkBackground,
    onSurface = GrapefruitDarkOnBackground,
    surfaceVariant = GrapefruitDarkSurfaceVariant,
    onSurfaceVariant = GrapefruitDarkOnSurfaceVariant,
    outline = GrapefruitDarkOutline,
    outlineVariant = GrapefruitDarkOutlineVariant,
    surfaceContainerLowest = GrapefruitDarkSurfaceContainerLowest,
    surfaceContainerLow = GrapefruitDarkSurfaceContainerLow,
    surfaceContainer = GrapefruitDarkSurfaceContainer,
    surfaceContainerHigh = GrapefruitDarkSurfaceContainerHigh,
    surfaceContainerHighest = GrapefruitDarkSurfaceContainerHighest,
)

/**
 * 앱 전체 테마. 색·글꼴·모양을 이곳 한 곳에서 관리한다.
 * 시스템 다크 모드 설정을 따르며, 자몽 색을 유지하려고 다이나믹 컬러는 쓰지 않는다.
 */
@Composable
fun DietmanagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
