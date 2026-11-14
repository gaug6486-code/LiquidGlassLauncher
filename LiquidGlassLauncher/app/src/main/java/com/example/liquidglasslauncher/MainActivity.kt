package com.example.liquidglasslauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ice = Color(0xFFEAF6FF)
private val Glass = Color(0x38FFFFFF)
private val Stroke = Color(0x65FFFFFF)

class MainActivity : ComponentActivity() {
    data class AppEntry(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        val apps = loadApps()
        setContent { LiquidHome(apps, onLaunch = { launchApp(it) }) }
    }

    private fun loadApps(): List<AppEntry> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .map { AppEntry(it.loadLabel(packageManager).toString(), it.activityInfo.packageName, it.loadIcon(packageManager)) }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun launchApp(app: AppEntry) {
        packageManager.getLaunchIntentForPackage(app.packageName)?.let { startActivity(it) }
    }
}

@Composable
private fun LiquidHome(apps: List<MainActivity.AppEntry>, onLaunch: (MainActivity.AppEntry) -> Unit) {
    var drawer by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(false) }
    var glassStrength by remember { mutableFloatStateOf(0.22f) }

    val filtered = apps.filter { it.label.contains(search, ignoreCase = true) }
    val time = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val date = remember { SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF071629), Color(0xFF172D50), Color(0xFF4C4D80), Color(0xFF101B35)))
        )
    ) {
        // Decorative ambient light fields
        Box(Modifier.offset(x = 210.dp, y = 90.dp).size(220.dp).clip(CircleShape).background(Color(0x334CCBFF)))
        Box(Modifier.offset(x = (-65).dp, y = 390.dp).size(240.dp).clip(CircleShape).background(Color(0x335B69FF)))
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("LIQUID", color = Ice.copy(alpha = .88f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                IconButton(onClick = { settings = !settings }) {
                    Icon(Icons.Default.Tune, contentDescription = "Tùy chỉnh", tint = Ice)
                }
            }
            Spacer(Modifier.height(46.dp))
            Text(time.format(Date()), color = Color.White, fontSize = 66.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
            Text(date.format(Date()).uppercase(Locale.getDefault()), color = Ice.copy(alpha = .78f), fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(28.dp))

            GlassPanel(glassStrength, Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("GOOD DAY", color = Ice.copy(alpha = .65f), fontSize = 10.sp, letterSpacing = 2.sp)
                        Text("Stay in your flow.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Một ngày mới, một không gian mới.", color = Ice.copy(alpha = .72f), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFE6A3), modifier = Modifier.size(34.dp))
                }
            }

            Spacer(Modifier.weight(1f))
            AnimatedVisibility(settings, enter = fadeIn(), exit = fadeOut()) {
                GlassPanel(glassStrength, Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("TÙY CHỈNH KÍNH", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Text("Độ đậm lớp kính", color = Ice.copy(alpha = .75f), fontSize = 12.sp)
                        Slider(value = glassStrength, onValueChange = { glassStrength = it }, valueRange = .10f..0.42f)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            GlassPanel(glassStrength, Modifier.fillMaxWidth().clickable { drawer = true }) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Ice.copy(alpha = .8f))
                    Spacer(Modifier.width(12.dp))
                    Text("Tìm kiếm ứng dụng", color = Ice.copy(alpha = .8f), fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.GridView, contentDescription = null, tint = Ice)
                }
            }
            Spacer(Modifier.height(16.dp))
            GlassPanel(glassStrength, Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(Icons.Default.Phone, Icons.Default.Message, Icons.Default.CameraAlt, Icons.Default.Language).forEachIndexed { index, icon ->
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(17.dp)).background(Color.White.copy(alpha = .12f)).border(1.dp, Stroke, RoundedCornerShape(17.dp)), contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = listOf("Điện thoại", "Tin nhắn", "Máy ảnh", "Trình duyệt")[index], tint = Color.White, modifier = Modifier.size(23.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Vuốt lên để mở ứng dụng", color = Ice.copy(alpha = .58f), fontSize = 10.sp, letterSpacing = 1.sp)
        }

        // Basic tap-to-open app drawer; swipe-up gesture is a later enhancement.
        if (drawer) {
            Box(Modifier.fillMaxSize().background(Color(0xB8071020)).clickable { drawer = false }) {
                Column(
                    Modifier.fillMaxSize().padding(top = 60.dp).clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xEE263B5B), Color(0xF20B172B))))
                        .clickable(enabled = false) {}.padding(horizontal = 18.dp, vertical = 22.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ỨNG DỤNG", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { drawer = false }) { Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White) }
                    }
                    OutlinedTextField(
                        value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tìm ứng dụng...", color = Ice.copy(alpha = .6f)) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Ice) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Stroke, unfocusedBorderColor = Stroke,
                            cursorColor = Color.White
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(filtered, key = { it.packageName }) { app ->
                            Column(Modifier.fillMaxWidth().clickable { onLaunch(app); drawer = false }.padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                androidx.compose.ui.viewinterop.AndroidView(
                                    factory = { context ->
                                        android.widget.ImageView(context).apply {
                                            setImageDrawable(app.icon)
                                            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                                        }
                                    },
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(app.label, color = Color.White, fontSize = 10.sp, maxLines = 1, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassPanel(alpha: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier.clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = alpha))
            .border(1.dp, Color.White.copy(alpha = .25f), RoundedCornerShape(25.dp))
    ) { content() }
}
