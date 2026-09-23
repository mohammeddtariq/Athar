package com.athar.app.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.athar.app.R
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimaryMuted
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.updater.AppReleaseInfo
import com.athar.app.updater.AppUpdateManager
import kotlinx.coroutines.launch
import java.io.File

private sealed interface UpdateUiState {
    data object Idle : UpdateUiState
    data class Downloading(val progress: Float) : UpdateUiState
    data class Ready(val apkFile: File) : UpdateUiState
    data class Failed(val errorMsg: String) : UpdateUiState
}

/**
 * Centered floating modal card notifying the user of an available update.
 * Features version badge, aesthetic title, persistent GitHub release link,
 * collapsible release notes, animated wavy progress bar, and action buttons.
 */
@Composable
fun AppUpdateDialog(
    releaseInfo: AppReleaseInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isChangelogExpanded by remember { mutableStateOf(false) }
    var uiState by remember { mutableStateOf<UpdateUiState>(UpdateUiState.Idle) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }

    fun startDownloadOrInstall() {
        val current = uiState
        if (current is UpdateUiState.Ready) {
            AppUpdateManager.installApk(context, current.apkFile)
            return
        }
        if (current is UpdateUiState.Downloading) return

        val apkUrl = releaseInfo.downloadUrl
        if (apkUrl.isNullOrEmpty()) {
            // Fallback: If no direct APK asset exists on GitHub, open the release page
            val intent = Intent(Intent.ACTION_VIEW, releaseInfo.htmlUrl.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        uiState = UpdateUiState.Downloading(0f)
        downloadProgress = 0f

        scope.launch {
            val result = AppUpdateManager.downloadApk(
                context = context,
                downloadUrl = apkUrl,
                onProgress = { progress, _, _ ->
                    downloadProgress = progress
                    uiState = UpdateUiState.Downloading(progress)
                }
            )

            result.onSuccess { apkFile ->
                uiState = UpdateUiState.Ready(apkFile)
                AppUpdateManager.installApk(context, apkFile)
            }.onFailure { error ->
                uiState = UpdateUiState.Failed(error.localizedMessage ?: "Download failed")
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (uiState !is UpdateUiState.Downloading) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = uiState !is UpdateUiState.Downloading,
            dismissOnClickOutside = uiState !is UpdateUiState.Downloading,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(AtharCardSurface)
                .border(1.dp, AtharCardBorder, RoundedCornerShape(26.dp))
        ) {
            IslamicPatternBackground(
                modifier = Modifier.matchParentSize(),
                alpha = 0.05f,
                animated = false
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header: Close button (if not downloading) & Spark icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AtharPrimarySubtle)
                            .border(1.dp, AtharCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SystemUpdate,
                            contentDescription = null,
                            tint = AtharPrimaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (uiState !is UpdateUiState.Downloading) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AtharBackground.copy(alpha = 0.6f))
                                .border(1.dp, AtharCardBorder, CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = AtharTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Title & Subtitle in serene poetic tone
                Text(
                    text = stringResource(R.string.update_dialog_title),
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.update_dialog_subtitle),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.5.sp,
                    color = AtharTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                // Version Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtharPrimarySubtle)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.update_version_badge, releaseInfo.versionName),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = AtharPrimaryLight
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Collapsible Changelog Card
                if (releaseInfo.changelogBody.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AtharBackground.copy(alpha = 0.6f))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { isChangelogExpanded = !isChangelogExpanded }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.update_changelog_title),
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = AtharPrimaryLight
                                )
                                Icon(
                                    imageVector = if (isChangelogExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = AtharTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = isChangelogExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 160.dp)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    ChangelogContent(releaseInfo.changelogBody)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                }

                // Persistent GitHub Release Link
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, releaseInfo.htmlUrl.toUri()).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.update_github_link),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AtharPrimaryMuted
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            tint = AtharTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Live Wavy Progress Bar section (when downloading or failed)
                when (val state = uiState) {
                    is UpdateUiState.Downloading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            WavyProgressBar(
                                progress = downloadProgress,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.update_status_downloading, (downloadProgress * 100).toInt()),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AtharPrimaryLight
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    is UpdateUiState.Ready -> {
                        Text(
                            text = stringResource(R.string.update_status_installing),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AtharPrimaryLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    is UpdateUiState.Failed -> {
                        Text(
                            text = stringResource(R.string.update_status_error),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = AtharPrimaryMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    UpdateUiState.Idle -> {
                        // Progress bar not active
                    }
                }

                // Action Buttons: Update Now & Later
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Update Later
                    if (uiState !is UpdateUiState.Downloading) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AtharBackground.copy(alpha = 0.7f))
                                .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.update_action_later),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = AtharTextSecondary
                            )
                        }
                    }

                    // Update Now
                    val isDownloading = uiState is UpdateUiState.Downloading
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDownloading) AtharPrimary.copy(alpha = 0.5f) else AtharPrimary)
                            .clickable(
                                enabled = !isDownloading,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { startDownloadOrInstall() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState is UpdateUiState.Ready) {
                                stringResource(R.string.update_status_installing)
                            } else {
                                stringResource(R.string.update_action_now)
                            },
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = AtharTextOnPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangelogContent(changelog: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val lines = changelog.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            when {
                trimmed.startsWith("###") || trimmed.startsWith("##") || trimmed.startsWith("#") -> {
                    val header = trimmed.replace(Regex("^#+\\s*"), "")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = header,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = AtharPrimaryLight
                    )
                }
                trimmed.startsWith("-") || trimmed.startsWith("*") -> {
                    val bulletText = trimmed.replace(Regex("^[-*]\\s*"), "")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = AtharPrimary
                        )
                        Text(
                            text = bulletText,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.5.sp,
                            color = AtharTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
                else -> {
                    Text(
                        text = trimmed,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.5.sp,
                        color = AtharTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
