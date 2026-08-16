package com.d4viddf.sigki

import android.app.NotificationManager
import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.d4viddf.sigki.ui.component.AccessibilityNewCustom
import com.d4viddf.sigki.ui.component.AppsCustom
import com.d4viddf.sigki.ui.component.ScreenshotFrameCustom
import com.d4viddf.sigki.ui.theme.SigkiTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    companion object {
        var latestScreenshot: Bitmap? = null
        private var _screenshotTriggerCount = mutableIntStateOf(0)
        val screenshotTriggerCount: State<Int> = _screenshotTriggerCount
        
        fun triggerScreenshot() {
            _screenshotTriggerCount.intValue++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (intent?.getStringExtra("action") == "SHOW_SCREENSHOT") {
            triggerScreenshot()
            intent.removeExtra("action")
        }
        
        setContent {
            SigkiTheme {
                SigkiApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getStringExtra("action") == "SHOW_SCREENSHOT") {
            triggerScreenshot()
            intent.removeExtra("action")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigkiApp() {
    val context = LocalContext.current
    val settings = remember { SettingsManager(context) }
    var showSettings by remember { mutableStateOf(false) }
    var showAppSelector by remember { mutableStateOf(false) }
    var showContactSelector by remember { mutableStateOf(false) }
    var showContactActionSelector by remember { mutableStateOf(value = false) }
    var showMediaActionSelector by remember { mutableStateOf(value = false) }
    var showMediaAppSelector by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showScreenshotPreviewSheet by remember { mutableStateOf(false) }
    var capturedScreenshot by remember { mutableStateOf<Bitmap?>(null) }
    
    // Handle incoming intents for screenshots reactively and instantly
    val triggerCount by MainActivity.screenshotTriggerCount
    var handledTriggerCount by rememberSaveable { mutableIntStateOf(0) }
    
    LaunchedEffect(triggerCount) {
        if (triggerCount > handledTriggerCount) {
            capturedScreenshot = MainActivity.latestScreenshot
            showScreenshotPreviewSheet = true
            handledTriggerCount = triggerCount
        }
    }
    
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
    val screenshotPreviewSheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
    
    var selectedAppPackage by remember { mutableStateOf(settings.selectedAppPackage) }
    var selectedAppName by remember { mutableStateOf(settings.selectedAppName) }
    
    var selectedMediaAppPackage by remember { mutableStateOf(settings.mediaAppPackage) }
    var selectedMediaAppName by remember { mutableStateOf(settings.mediaAppName) }
    
    var selectedContactUri by remember { mutableStateOf(settings.selectedContactUri) }
    var selectedContactName by remember { mutableStateOf(settings.selectedContactName) }
    var contactAction by remember { mutableStateOf(settings.contactAction) }
    var mediaAction by remember { mutableStateOf(settings.mediaAction) }
    var permanentOptionsPanel by remember { mutableStateOf(settings.permanentOptionsPanel) }
    
    val items = remember {
        listOf(
            SelectorItem("Flashlight", R.string.item_flashlight, Icons.Rounded.FlashlightOn),
            SelectorItem("Camera", R.string.item_camera, Icons.Rounded.PhotoCamera),
            SelectorItem("Do not disturb", R.string.item_dnd, Icons.Rounded.DoNotDisturbOn),
            SelectorItem("Media", R.string.item_media, Icons.Rounded.MusicNote),
            SelectorItem("App", R.string.item_app, AppsCustom),
            SelectorItem("Contact", R.string.item_contact, Icons.Rounded.Contacts),
            SelectorItem("Screenshot", R.string.item_screenshot, ScreenshotFrameCustom),
            SelectorItem("Open URL", R.string.item_url, Icons.Rounded.Link),
            SelectorItem("TalkBack", R.string.item_talkback, AccessibilityNewCustom),
        )
    }
    
    val pagerState = rememberPagerState(
        initialPage = settings.selectedIndex,
        pageCount = { items.size }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // Persist permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            settings.screenshotFolder = it.toString()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        settings.selectedIndex = pagerState.currentPage
        showOptions = false // Hide options when page changes
        
        val selected = items[pagerState.currentPage].id
        
        // Ensure options are hidden if the new item has none
        if ((selected == "Flashlight") || (selected == "TalkBack")) {
            showOptions = false
        }

        val neededPermissions = mutableListOf<String>()
        
        when (selected) {
            "Flashlight", "Camera" -> neededPermissions.add(android.Manifest.permission.CAMERA)
            "Contact" -> {
                neededPermissions.add(android.Manifest.permission.READ_CONTACTS)
                neededPermissions.add(android.Manifest.permission.CALL_PHONE)
            }
        }
        
        val ungranted = neededPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (ungranted.isNotEmpty()) {
            permissionLauncher.launch(ungranted.toTypedArray())
        }
    }

    var isDefaultAssistant by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val assistant = Settings.Secure.getString(context.contentResolver, "assistant")
                val component = ComponentName.unflattenFromString(assistant ?: "")
                isDefaultAssistant = component?.packageName == context.packageName
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SelectorCarousel(
                    items = items,
                    pagerState = pagerState,
                    showOptions = showOptions,
                    showOptionsButton = !permanentOptionsPanel,
                    onOptionsClick = { showOptions = !showOptions }
                )
            }

            if (!isDefaultAssistant) {
                AssistantWarningBanner(
                    onClick = {
                        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }

            val currentItem = items[pagerState.currentPage]
            val hasOptions = (currentItem.id != "Flashlight") && (currentItem.id != "TalkBack")

            AnimatedVisibility(
                visible = (showOptions || permanentOptionsPanel) && hasOptions,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .imePadding()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                OptionPanel(
                    selectedItem = currentItem,
                    settings = settings,
                    selectedAppName = selectedAppName ?: stringResource(R.string.value_not_selected),
                    selectedContactName = selectedContactName ?: stringResource(R.string.value_not_selected),
                    contactAction = contactAction,
                    mediaAction = mediaAction,
                    selectedMediaAppName = selectedMediaAppName ?: stringResource(R.string.value_not_selected),
                    onChooseApp = { showAppSelector = true },
                    onChooseContact = { showContactSelector = true },
                    onChooseContactAction = { showContactActionSelector = true },
                    onChooseFolder = { folderPickerLauncher.launch(null) },
                    onChooseMediaAction = { showMediaActionSelector = true },
                    onChooseMediaApp = { showMediaAppSelector = true }
                )
            }
        }

        if (showSettings) {
            SettingsBottomSheet(
                onDismiss = { showSettings = false },
                sheetState = sheetState,
                settings = settings,
                onPermanentOptionsChanged = { permanentOptionsPanel = it },
                onAboutClick = { showAbout = true }
            )
        }

        if (showAppSelector) {
            AppSelectorBottomSheet(
                onDismiss = { showAppSelector = false },
                onAppSelected = { pkg, name ->
                    settings.selectedAppPackage = pkg
                    settings.selectedAppName = name
                    selectedAppPackage = pkg
                    selectedAppName = name
                    showAppSelector = false
                },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
            )
        }

        if (showContactSelector) {
            ContactSelectorBottomSheet(
                onDismiss = { showContactSelector = false },
                onContactSelected = { uri, name, lookup ->
                    settings.selectedContactUri = uri
                    settings.selectedContactName = name
                    settings.selectedContactLookupUri = lookup
                    selectedContactUri = uri
                    selectedContactName = name
                    showContactSelector = false
                },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
            )
        }

        if (showContactActionSelector) {
            ContactActionSelectorBottomSheet(
                currentAction = contactAction,
                onActionSelected = { action ->
                    settings.contactAction = action
                    contactAction = action
                    showContactActionSelector = false
                },
                onDismiss = { showContactActionSelector = false },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
            )
        }

        if (showMediaActionSelector) {
            MediaActionSelectorBottomSheet(
                currentAction = mediaAction,
                onActionSelected = { action ->
                    settings.mediaAction = action
                    mediaAction = action
                    showMediaActionSelector = false
                },
                onDismiss = { showMediaActionSelector = false },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
            )
        }

        if (showMediaAppSelector) {
            AppSelectorBottomSheet(
                onDismiss = { showMediaAppSelector = false },
                onAppSelected = { pkg, name ->
                    settings.mediaAppPackage = pkg
                    settings.mediaAppName = name
                    selectedMediaAppPackage = pkg
                    selectedMediaAppName = name
                    showMediaAppSelector = false
                },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)),
                filterMediaOnly = true
            )
        }

        if (showAbout) {
            AboutBottomSheet(
                onDismiss = { showAbout = false },
                sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded))
            )
        }

        if (showScreenshotPreviewSheet) {
            val shareChooserTitle = stringResource(R.string.share_chooser_title)
            val editChooserTitle = stringResource(R.string.edit_chooser_title)
            
            ScreenshotPreviewSheet(
                screenshot = capturedScreenshot,
                onShareClick = {
                    capturedScreenshot?.let { bitmap ->
                        val uri = saveBitmapToCache(context, bitmap)
                        if (uri != null) {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_TITLE, shareChooserTitle)
                                clipData = ClipData.newRawUri(null, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, shareChooserTitle))
                        }
                    }
                },
                onEditClick = {
                    capturedScreenshot?.let { bitmap ->
                        val uri = saveBitmapToCache(context, bitmap)
                        if (uri != null) {
                            val intent = Intent(Intent.ACTION_EDIT).apply {
                                setDataAndType(uri, "image/png")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, editChooserTitle))
                        }
                    }
                },
                onDismissRequest = { showScreenshotPreviewSheet = false },
                sheetState = screenshotPreviewSheetState
            )
        }
    }
}

@Composable
fun AssistantWarningBanner(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Text(
                text = stringResource(R.string.banner_not_default),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    settings: SettingsManager,
    onPermanentOptionsChanged: (Boolean) -> Unit = {},
    onAboutClick: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        SettingsBottomSheetContent(
            settings = settings,
            onPermanentOptionsChanged = onPermanentOptionsChanged,
            onAboutClick = onAboutClick
        )
    }
}

@Composable
fun SettingsBottomSheetContent(
    settings: SettingsManager,
    onPermanentOptionsChanged: (Boolean) -> Unit = {},
    onAboutClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Permanent Options Toggle
        SettingsCard {
            var permanentOptions by remember { mutableStateOf(settings.permanentOptionsPanel) }
            ListItem(
                supportingContent = { Text(stringResource(R.string.settings_permanent_panel_desc)) },
                trailingContent = {
                    Switch(
                        checked = permanentOptions,
                        onCheckedChange = { 
                            permanentOptions = it
                            settings.permanentOptionsPanel = it
                            onPermanentOptionsChanged(it)
                        }
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            ) {
                Text(stringResource(R.string.settings_permanent_panel))
            }
        }

        // Set Default Assistant Button
        SettingsCard(
            onClick = {
                val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                context.startActivity(intent)
            }
        ) {
            ListItem(
                supportingContent = { Text(stringResource(R.string.settings_system_settings)) },
                trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            ) {
                Text(stringResource(R.string.settings_default_assistant))
            }
        }

        // About Button
        SettingsCard(onClick = onAboutClick) {
            ListItem(
                trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            ) {
                Text(stringResource(R.string.settings_about))
            }
        }

        // Developer
        val githubUrl = stringResource(R.string.developer_github_url)
        SettingsCard(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri())
                context.startActivity(intent)
            }
        ) {
            ListItem(
                supportingContent = { Text(stringResource(R.string.settings_dev_name)) },
                trailingContent = { Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, modifier = Modifier.size(20.dp)) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            ) {
                Text(stringResource(R.string.settings_developer))
            }
        }
    }
}

@Composable
fun SettingsCard(
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

data class SelectorItem(val id: String, val titleRes: Int, val icon: ImageVector)

@Composable
fun SelectorCarousel(
    items: List<SelectorItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    showOptions: Boolean = false,
    showOptionsButton: Boolean = true,
    onOptionsClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 90.dp),
        pageSpacing = 0.dp,
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        val isSelected = pagerState.currentPage == page

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        val scale = lerp(
                            start = 0.8f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = scale
                        scaleY = scale
                        alpha = lerp(
                            start = 0.6f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Selector Content
                Selector(
                    title = stringResource(items[page].titleRes),
                    icon = items[page].icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(12.dp),
                    onClick = {
                        if (!isSelected) {
                            scope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        }
                    }
                )

                // Selection Border (Fades in/out smoothly)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            // Sharper fade: starts appearing when within 0.4 offset
                            alpha = (1f - (pageOffset * 2.5f)).coerceIn(0f, 1f)
                        }
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(40.dp)
                        )
                )
            }

            if (isSelected && showOptionsButton && (items[page].id != "Flashlight") && (items[page].id != "TalkBack")) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onOptionsClick,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (showOptions) stringResource(R.string.button_hide_options) else stringResource(R.string.button_options))
                }
            } else {
                Spacer(modifier = Modifier.height(64.dp)) // Height of Spacer + Button
            }
        }
    }
}

@Composable
fun OptionPanel(
    selectedItem: SelectorItem,
    settings: SettingsManager,
    selectedAppName: String,
    selectedContactName: String,
    contactAction: String,
    mediaAction: String,
    selectedMediaAppName: String,
    modifier: Modifier = Modifier,
    onChooseApp: () -> Unit = {},
    onChooseContact: () -> Unit = {},
    onChooseContactAction: () -> Unit = {},
    onChooseFolder: () -> Unit = {},
    onChooseMediaAction: () -> Unit = {},
    onChooseMediaApp: () -> Unit = {}
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        AnimatedContent(
            targetState = selectedItem,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
                    .using(SizeTransform(clip = false))
            },
            label = "OptionPanelTransition"
        ) { targetItem ->
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.options_title_suffix, stringResource(targetItem.titleRes)),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                when (targetItem.id) {
                    "Camera" -> {
                        var openOnLock by remember { mutableStateOf(settings.cameraOpenOnLock) }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                supportingContent = { Text(stringResource(R.string.camera_lockscreen_desc)) },
                                leadingContent = {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Rounded.PhotoCamera,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    Switch(
                                        checked = openOnLock, 
                                        onCheckedChange = { 
                                            openOnLock = it
                                            settings.cameraOpenOnLock = it
                                        }
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            ) {
                                Text(stringResource(R.string.camera_lockscreen), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    "Do not disturb" -> {
                        val isPreview = LocalInspectionMode.current
                        
                        val notificationManager = remember(context) {
                            if (isPreview) null else context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        }
                        
                        var hasPermission by remember { 
                            mutableStateOf(notificationManager?.isNotificationPolicyAccessGranted ?: false) 
                        }

                        if (!isPreview) {
                            // Refresh permission status when returning to app
                            val lifecycleOwner = LocalLifecycleOwner.current
                            DisposableEffect(lifecycleOwner) {
                                val observer = LifecycleEventObserver { _, event ->
                                    if (event == Lifecycle.Event.ON_RESUME) {
                                        hasPermission = notificationManager?.isNotificationPolicyAccessGranted ?: false
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)
                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }
                        }

                        if (hasPermission) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE8F5E9), // Soft success green
                                    contentColor = Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.dnd_granted),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Rounded.Warning, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.dnd_grant_button))
                            }
                        }
                    }

                    "Media" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = onChooseMediaAction,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { 
                                    Text(
                                        text = when(mediaAction) {
                                            "STOP" -> stringResource(R.string.action_stop)
                                            else -> stringResource(R.string.action_play_pause)
                                        }
                                    )
                                },
                                    leadingContent = {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    when(mediaAction) {
                                                        "STOP" -> Icons.Rounded.Stop
                                                        else -> Icons.Rounded.PlayArrow
                                                    },
                                                    null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.media_action), fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Surface(
                                onClick = onChooseMediaApp,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { Text(selectedMediaAppName) },
                                    leadingContent = {
                                        Surface(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Rounded.MusicNote,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.media_fallback_app), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    "App" -> {
                        Surface(
                            onClick = onChooseApp,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                supportingContent = { Text(selectedAppName) },
                                leadingContent = {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                AppsCustom,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            ) {
                                Text(stringResource(R.string.app_target), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    "Contact" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = onChooseContact,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { Text(selectedContactName) },
                                    leadingContent = {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (selectedContactName != stringResource(R.string.value_not_selected)) {
                                                    Text(
                                                        text = selectedContactName.take(1).uppercase(),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                } else {
                                                    Icon(
                                                        Icons.Rounded.Contacts,
                                                        null,
                                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.contact_selected), fontWeight = FontWeight.SemiBold)
                                }
                            }
                            
                            Surface(
                                onClick = onChooseContactAction,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { 
                                        Text(when(contactAction) {
                                            "CALL" -> stringResource(R.string.action_call)
                                            "MESSAGE" -> stringResource(R.string.action_message)
                                            else -> stringResource(R.string.action_view)
                                        })
                                    },
                                    leadingContent = {
                                        Surface(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    when(contactAction) {
                                                        "CALL" -> Icons.Rounded.Call
                                                        "MESSAGE" -> Icons.AutoMirrored.Rounded.Message
                                                        else -> Icons.AutoMirrored.Rounded.OpenInNew
                                                    },
                                                    null,
                                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.contact_action), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    "Screenshot" -> {
                        var showPreview by remember { mutableStateOf(settings.showScreenshotPreview) }
                        var folder by remember { mutableStateOf(settings.screenshotFolder) }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Toggle for showing preview
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { Text(stringResource(R.string.screenshot_show_preview_desc)) },
                                    trailingContent = {
                                        Switch(
                                            checked = showPreview,
                                            onCheckedChange = { 
                                                showPreview = it
                                                settings.showScreenshotPreview = it
                                            }
                                        )
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.screenshot_show_preview))
                                }
                            }

                            // Save Directory
                            Surface(
                                onClick = onChooseFolder,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    supportingContent = { 
                                        val displayName = remember(folder) {
                                            try {
                                                val uri = folder.toUri()
                                                if (uri.scheme == "content") {
                                                    DocumentFile.fromTreeUri(context, uri)?.name ?: folder
                                                } else {
                                                    uri.lastPathSegment ?: folder
                                                }
                                            } catch (_: Exception) {
                                                folder
                                            }
                                        }
                                        Text(
                                            text = displayName,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingContent = { 
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Rounded.Folder, 
                                                    null, 
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.screenshot_save_location), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    "Open URL" -> {
                        var url by remember { mutableStateOf(settings.targetUrl) }
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ListItem(
                                    supportingContent = { Text(stringResource(R.string.url_destination_desc)) },
                                    leadingContent = {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Rounded.Link,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                ) {
                                    Text(stringResource(R.string.url_destination), fontWeight = FontWeight.SemiBold)
                                }
                                OutlinedTextField(
                                    value = url,
                                    onValueChange = { 
                                        url = it
                                        settings.targetUrl = it
                                    },
                                    placeholder = { Text(stringResource(R.string.url_placeholder)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(R.string.options_default_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSelectorBottomSheet(
    onDismiss: () -> Unit,
    onContactSelected: (String, String, String?) -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ContactSelectorBottomSheetContent(onContactSelected = onContactSelected)
    }
}

@Composable
fun ContactSelectorBottomSheetContent(
    onContactSelected: (String, String, String?) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val contacts = remember(searchQuery) {
        val list = mutableListOf<Triple<String, String, String?>>()
        val cursor = context.contentResolver.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER,
                android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                android.provider.ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
            ),
            if (searchQuery.isEmpty()) null else "${android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            if (searchQuery.isEmpty()) null else arrayOf("%$searchQuery%"),
            "${android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
            val idIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val lookupIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
            
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                val id = it.getLong(idIndex)
                val lookupKey = it.getString(lookupIndex)
                
                val lookupUri = android.provider.ContactsContract.Contacts.getLookupUri(id, lookupKey)
                list.add(Triple(name, "tel:$number", lookupUri?.toString()))
            }
        }
        list.distinctBy { it.first }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_contact),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text(stringResource(R.string.search_contacts)) },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) { (name, uri, lookup) ->
                Surface(
                    onClick = { onContactSelected(uri, name, lookup) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ) {
                    ListItem(
                        supportingContent = { Text(uri.removePrefix("tel:")) },
                        leadingContent = {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    ) {
                        Text(name)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactActionSelectorBottomSheet(
    currentAction: String,
    onActionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ContactActionSelectorBottomSheetContent(
            currentAction = currentAction,
            onActionSelected = onActionSelected
        )
    }
}

@Composable
fun ContactActionSelectorBottomSheetContent(
    currentAction: String,
    onActionSelected: (String) -> Unit
) {
    val options = listOf(
        Triple("CALL", stringResource(R.string.action_call), stringResource(R.string.action_call_desc)),
        Triple("MESSAGE", stringResource(R.string.action_message), stringResource(R.string.action_message_desc)),
        Triple("VIEW", stringResource(R.string.action_view), stringResource(R.string.action_view_desc))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_action),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(Modifier.selectableGroup()) {
            options.forEach { (action, title, description) ->
                val selected = action == currentAction
                Surface(
                    onClick = { onActionSelected(action) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    ListItem(
                        supportingContent = { Text(description) },
                        trailingContent = {
                            RadioButton(
                                selected = selected,
                                onClick = null // handled by Card
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    ) {
                        Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaActionSelectorBottomSheet(
    currentAction: String,
    onActionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        MediaActionSelectorBottomSheetContent(
            currentAction = currentAction,
            onActionSelected = onActionSelected
        )
    }
}

@Composable
fun MediaActionSelectorBottomSheetContent(
    currentAction: String,
    onActionSelected: (String) -> Unit
) {
    val options = listOf(
        Triple("PLAY_PAUSE", stringResource(R.string.action_play_pause), stringResource(R.string.action_play_pause_desc)),
        Triple("STOP", stringResource(R.string.action_stop), stringResource(R.string.action_stop_desc))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_action),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(Modifier.selectableGroup()) {
            options.forEach { (action, title, description) ->
                val selected = action == currentAction
                Surface(
                    onClick = { onActionSelected(action) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    ListItem(
                        supportingContent = { Text(description) },
                        trailingContent = {
                            RadioButton(
                                selected = selected,
                                onClick = null
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    ) {
                        Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotPreviewSheet(
    screenshot: Bitmap?,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ScreenshotPreviewContent(
            screenshot = screenshot,
            onShareClick = onShareClick,
            onEditClick = onEditClick,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
fun ScreenshotPreviewContent(
    screenshot: Bitmap?,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val shareLabel = stringResource(R.string.button_share)
    val editLabel = stringResource(R.string.button_edit)
    val closeLabel = stringResource(R.string.button_close)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(9f / 16f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (screenshot != null) {
                Image(
                    bitmap = screenshot.asImageBitmap(),
                    contentDescription = "Screenshot Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.screenshot_waiting),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) }
        ) {
            customItem(
                buttonGroupContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    FilledTonalIconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .animateWidth(interactionSource)
                            .weight(1f),
                        interactionSource = interactionSource
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = shareLabel)
                    }
                },
                menuContent = {
                    DropdownMenuItem(
                        text = { Text(shareLabel) },
                        leadingIcon = { Icon(Icons.Rounded.Share, null) },
                        onClick = {
                            onShareClick()
                            it.dismiss()
                        }
                    )
                }
            )

            customItem(
                buttonGroupContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    FilledTonalIconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .animateWidth(interactionSource)
                            .weight(1f),
                        interactionSource = interactionSource
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = editLabel)
                    }
                },
                menuContent = {
                    DropdownMenuItem(
                        text = { Text(editLabel) },
                        leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                        onClick = {
                            onEditClick()
                            it.dismiss()
                        }
                    )
                }
            )

            customItem(
                buttonGroupContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .animateWidth(interactionSource)
                            .weight(1f),
                        interactionSource = interactionSource
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = closeLabel)
                    }
                },
                menuContent = {
                    DropdownMenuItem(
                        text = { Text(closeLabel) },
                        leadingIcon = { Icon(Icons.Rounded.Close, null) },
                        onClick = {
                            onDismissRequest()
                            it.dismiss()
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        AboutBottomSheetContent()
    }
}

@Composable
fun AboutBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_monochrome),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        
        Text(
            text = stringResource(R.string.app_version_value),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
            )
        ) {
            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Selector(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectorBottomSheet(
    onDismiss: () -> Unit,
    onAppSelected: (String, String) -> Unit,
    sheetState: SheetState,
    filterMediaOnly: Boolean = false
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        AppSelectorBottomSheetContent(
            onAppSelected = onAppSelected,
            filterMediaOnly = filterMediaOnly
        )
    }
}

@Composable
fun AppSelectorBottomSheetContent(
    onAppSelected: (String, String) -> Unit,
    filterMediaOnly: Boolean = false
) {
    val context = LocalContext.current
    val apps = remember(filterMediaOnly) {
        val pm = context.packageManager
        
        // Get launcher apps
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val allApps = pm.queryIntentActivities(launcherIntent, 0)
        
        val filtered = if (filterMediaOnly) {
            // Find apps that handle audio intents or are commonly known players
            val audioIntent = Intent(Intent.ACTION_VIEW).apply {
                type = "audio/*"
            }
            val audioApps = pm.queryIntentActivities(audioIntent, 0)
                .asSequence()
                .map { it.activityInfo.packageName }
                .toSet()
            
            allApps.filter { app ->
                val pkg = app.activityInfo.packageName.lowercase()
                audioApps.contains(pkg) || 
                pkg.contains("music") || 
                pkg.contains("player") || 
                pkg.contains("audio") || 
                pkg.contains("video") || 
                pkg.contains("spotify") || 
                pkg.contains("youtube") || 
                pkg.contains("podcasts") ||
                pkg.contains("sound") ||
                pkg.contains("radio")
            }
        } else {
            allApps
        }

        filtered
            .asSequence()
            .map {
                AppInfo(
                    name = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName,
                    icon = it.loadIcon(pm)
                )
            }
            .sortedBy { it.name }
            .toList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (filterMediaOnly) stringResource(R.string.media_fallback_app) else stringResource(R.string.choose_app),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(apps) { app ->
                AppCard(app = app, onClick = { onAppSelected(app.packageName, app.name) })
            }
        }
    }
}

@Composable
fun AppCard(app: AppInfo, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutBottomSheetPreview() {
    SigkiTheme {
        AboutBottomSheetContent()
    }
}

@Preview(showBackground = true)
@Composable
fun MediaActionSelectorBottomSheetPreview() {
    SigkiTheme {
        MediaActionSelectorBottomSheetContent(
            currentAction = "PLAY_PAUSE",
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun MediaOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Media", R.string.item_media, Icons.Rounded.MusicNote),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "Spotify",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppCardPreview() {
    val context = LocalContext.current
    SigkiTheme {
        AppCard(
            app = AppInfo(
                name = "SigKi",
                packageName = context.packageName,
                icon = context.packageManager.getApplicationIcon(context.packageName)
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenshotPreviewContentPreview() {
    SigkiTheme {
        ScreenshotPreviewContent(
            screenshot = null, // Shows the "Waiting for system..." loading state
            onShareClick = {},
            onEditClick = {},
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppSelectorBottomSheetPreview() {
    SigkiTheme {
        AppSelectorBottomSheetContent(
            onAppSelected = { _, _ -> },
            filterMediaOnly = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediaAppSelectorBottomSheetPreview() {
    SigkiTheme {
        AppSelectorBottomSheetContent(
            onAppSelected = { _, _ -> },
            filterMediaOnly = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactSelectorBottomSheetPreview() {
    SigkiTheme {
        ContactSelectorBottomSheetContent(
            onContactSelected = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactActionSelectorBottomSheetPreview() {
    SigkiTheme {
        ContactActionSelectorBottomSheetContent(
            currentAction = "CALL",
            onActionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssistantWarningBannerPreview() {
    SigkiTheme {
        AssistantWarningBanner(onClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullAppPreview() {
    SigkiTheme {
        SigkiApp()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsBottomSheetPreview() {
    val context = LocalContext.current
    SigkiTheme {
        SettingsBottomSheetContent(
            settings = SettingsManager(context),
            onPermanentOptionsChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectorCarouselPreview() {
    val context = LocalContext.current
    SigkiTheme {
        val items = listOf(
            SelectorItem("Flashlight", R.string.item_flashlight, Icons.Rounded.FlashlightOn),
            SelectorItem("Camera", R.string.item_camera, Icons.Rounded.PhotoCamera),
            SelectorItem("Do not disturb", R.string.item_dnd, Icons.Rounded.DoNotDisturbOn),
            SelectorItem("Media", R.string.item_media, Icons.Rounded.MusicNote),
            SelectorItem("App", R.string.item_app, AppsCustom),
            SelectorItem("Contact", R.string.item_contact, Icons.Rounded.Contacts),
            SelectorItem("Screenshot", R.string.item_screenshot, ScreenshotFrameCustom),
            SelectorItem("Open URL", R.string.item_url, Icons.Rounded.Link),
            SelectorItem("TalkBack", R.string.item_talkback, AccessibilityNewCustom),
        )
        val pagerState = rememberPagerState(initialPage = 4, pageCount = { items.size })
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                SelectorCarousel(
                    items = items,
                    pagerState = pagerState,
                    showOptions = true,
                    modifier = Modifier.align(Alignment.Center)
                )
                OptionPanel(
                    selectedItem = items[4],
                    settings = SettingsManager(context),
                    selectedAppName = "Test App",
                    selectedContactName = "John Doe",
                    contactAction = "CALL",
                    mediaAction = "PLAY_PAUSE",
                    selectedMediaAppName = "Spotify",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Camera", R.string.item_camera, Icons.Rounded.PhotoCamera),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DNDOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Do not disturb", R.string.item_dnd, Icons.Rounded.DoNotDisturbOn),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("App", R.string.item_app, AppsCustom),
            settings = SettingsManager(context),
            selectedAppName = "Gmail",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Contact", R.string.item_contact, Icons.Rounded.Contacts),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "John Doe",
            contactAction = "MESSAGE",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenshotOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Screenshot", R.string.item_screenshot,
                ScreenshotFrameCustom
            ),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OpenURLOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("Open URL", R.string.item_url, Icons.Rounded.Link),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TalkBackOptionPreview() {
    val context = LocalContext.current
    SigkiTheme {
        OptionPanel(
            selectedItem = SelectorItem("TalkBack", R.string.item_talkback, AccessibilityNewCustom),
            settings = SettingsManager(context),
            selectedAppName = "",
            selectedContactName = "",
            contactAction = "CALL",
            mediaAction = "PLAY_PAUSE",
            selectedMediaAppName = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectorPreview() {
    SigkiTheme {
        Selector(
            title = "Flashlight",
            icon = Icons.Rounded.FlashlightOn,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    val imagesFolder = File(context.cacheDir, "screenshots")
    imagesFolder.mkdirs()
    val file = File(imagesFolder, "shared_screenshot.png")
    return try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (_: IOException) {
        null
    }
}
