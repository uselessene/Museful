package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.ReadingDay
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    session: SessionViewModel,
    onCatalogClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSeriesDayClick: () -> Unit
) {
    val auth = remember { AuthRepository() }
    val repo = remember { ReadingRepository() }
    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var days by remember { mutableStateOf<List<ReadingDay>>(emptyList()) }
    val booksMap = viewModel.books.associateBy { it.id }

    LaunchedEffect(Unit) {
        session.refresh()
        val userId = auth.currentUserId() ?: return@LaunchedEffect
        days = repo.getDays(userId)
    }

    val calendar = Calendar.getInstance()
    val dow = calendar.get(Calendar.DAY_OF_WEEK)
    val shift = if (dow == Calendar.SUNDAY) -6 else (Calendar.MONDAY - dow)
    val monday = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, shift) }
    val weekDates = (0..6).map {
        (monday.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, it) }
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val daysMap = days.associateBy { it.date }

    var streak = 0
    var cursor = Calendar.getInstance()
    while (daysMap.containsKey(sdf.format(cursor.time))) {
        streak++
        cursor.add(Calendar.DAY_OF_MONTH, -1)
    }

    val lastBookId = days.lastOrNull()?.bookId
    val lastBook = lastBookId?.let { booksMap[it] }
    val totalPagesRead = days.filter { it.bookId == lastBookId }.sumOf { it.pagesRead }
    val leftPages = ((lastBook?.pageCount ?: 0) - totalPagesRead).coerceAtLeast(0)

    val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val currentMonth = monthFormat.format(Date())
    val monthPages = days.filter { it.date.startsWith(currentMonth) }.sumOf { it.pagesRead }
    val mondayStr = sdf.format(monday.time)
    val weekPages = days.filter { it.date >= mondayStr }.sumOf { it.pagesRead }

    val monthLabels = (8 downTo 0).map { offset ->
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, -offset)
        val key = monthFormat.format(c.time)
        val label = SimpleDateFormat("MM", Locale.getDefault()).format(c.time)
        key to label
    }
    val monthPagesMap = monthLabels.associate { (key, _) ->
        key to days.filter { it.date.startsWith(key) }.sumOf { it.pagesRead }
    }
    val maxMonth = monthPagesMap.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(160.dp)) {
                Image(painter = painterResource(R.drawable.vetci), contentDescription = null,
                    contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().height(143.dp))
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(R.drawable.logo), contentDescription = null,
                        modifier = Modifier.width(287.dp).height(118.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Column(Modifier.padding(horizontal = 24.dp)) {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.seriya), contentDescription = null,
                            modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ударный режим недели", fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach {
                            Text(it, fontFamily = Dop, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        weekDates.forEach { c ->
                            val dateStr = sdf.format(c.time)
                            val marked = daysMap.containsKey(dateStr)
                            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                                Text(SimpleDateFormat("dd", Locale.getDefault()).format(c.time),
                                    fontFamily = Dop, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                if (marked) {
                                    Image(painter = painterResource(R.drawable.seriya), contentDescription = null,
                                        modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Text("Читаю сейчас", fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text("Продолжить чтение: $totalPagesRead страница",
                                fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("До конца осталось: $leftPages страниц",
                                fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(20.dp))
                            Button(onClick = onSeriesDayClick, shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                Text("Отметиться", fontFamily = Dop, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        lastBook?.let { b ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                    .data(b.coverUrl).build(), contentDescription = null,
                                    modifier = Modifier.width(60.dp).height(90.dp).clip(RoundedCornerShape(8.dp)))
                                Text(b.title, fontFamily = Osnova, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                Text(b.author, fontFamily = Osnova, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Text("Статистика", fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Страниц прочитано за этот месяц: $monthPages",
                        fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Страниц прочитано за эту неделю: $weekPages",
                        fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth().height(150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom) {
                        monthLabels.forEach { (key, label) ->
                            val pages = monthPagesMap[key] ?: 0
                            val h = (pages.toFloat() / maxMonth * 110).dp.coerceAtLeast(10.dp)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.width(22.dp).height(h).clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.tertiary),
                                    contentAlignment = Alignment.Center) {
                                    if (pages > 0) {
                                        Text(pages.toString(), fontFamily = Dop, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(label, fontFamily = Dop, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(100.dp))
            }
        }

        Footer(onCatalogClick = onCatalogClick, onHomeClick = onHomeClick,
            onSeriesClick = onSeriesClick, onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter))
    }
}