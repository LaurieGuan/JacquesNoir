package com.example.jacquesnoir.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable

private val SchemaClair = lightColorScheme(
    primary = AppColors.ActionClaire,
    onPrimary = AppColors.TexteCarte,
    secondary = AppColors.InteractionClaire,
    onSecondary = AppColors.TexteCarte,
    tertiary = AppColors.Espace,
    error = AppColors.Autorite,
    background = AppColors.FondClair,
    onBackground = AppColors.TexteClair,
    surface = AppColors.SurfaceClair,
    onSurface = AppColors.TexteClair,
    surfaceVariant = AppColors.SurfaceSecondaireClair,
    onSurfaceVariant = AppColors.TexteSecondaireClair,
    outline = AppColors.BordureClaire,
    outlineVariant = AppColors.BordureAttenueeClaire
)

private val SchemaSombre = darkColorScheme(
    primary = AppColors.ActionSombre,
    onPrimary = AppColors.TexteCarte,
    secondary = AppColors.InteractionSombre,
    onSecondary = AppColors.TexteCarte,
    tertiary = AppColors.Futur,
    error = AppColors.Autorite,
    background = AppColors.FondSombre,
    onBackground = AppColors.TexteSombre,
    surface = AppColors.SurfaceSombre,
    onSurface = AppColors.TexteSombre,
    surfaceVariant = AppColors.FondSecondaireSombre,
    onSurfaceVariant = AppColors.TexteSecondaireSombre,
    outline = AppColors.BordureSombre,
    outlineVariant = AppColors.BordureAttenueeSombre
)

@Composable
fun ThemeJacqueNoir(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val schema = if (darkTheme) SchemaSombre else SchemaClair
    MaterialTheme(
        colorScheme = schema,
        typography = Typography,
    ) {
        CompositionLocalProvider(LocalContentColor provides schema.onBackground) {
            content()
        }
    }
}
