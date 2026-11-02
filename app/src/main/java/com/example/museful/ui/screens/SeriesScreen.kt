package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.ReadingDay
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SeriesScreen(
    session: SessionViewModel,
    onCatalogClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val auth = remember { AuthRepository() }
    val repo = remember { ReadingRepository() }
    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var days by remember { mutableStateOf<List<ReadingDay>>(emptyList()) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var readToday by remember { mutableStateOf("Да") }
    var bookName by remember { mutableStateOf("") }
    var pages by remember { mutableStateOf("") }
    var impression by remember { mutableStateOf("") }
    var bookMenu by remember { mutableStateOf(false) }
    var readMenu by remember { mutableStateOf(false) }
    var monthOffset by remember { mutableStateOf(0) }
    var monthMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun reload() {
        val userId = auth.currentUserId() ?: return
        days = repo.getDays(userId)
    }

    LaunchedEffect(Unit) {
        session.refresh()
        reload()
    }

    LaunchedEffect(selectedDate, days) {
        val day = days.find { it.date == selectedDate }
        if (day != null) {
            readToday = "Да"
            bookName = viewModel.books.find { it.id == day.bookId }?.title ?: ""
            pages = if (day.pagesRead > 0) day.pagesRead.toString() else ""
            impression = day.impression ?: ""
        } else {
            readToday = "Да"
            bookName = ""
            pages = ""
            impression = ""
        }
    }

    val daysSet = days.map { it.date }.toSet()
    var streak = 0
    var cursor = Calendar.getInstance()
    while (daysSet.contains(sdf.format(cursor.time))) {
        streak++
        cursor.add(Calendar.DAY_OF_MONTH, -1)
    }

    val displayedMonth = (Calendar.getInstance().clone() as Calendar).apply { add(Calendar.MONTH, monthOffset) }
    val monthName = SimpleDateFormat("LLLL", Locale("ru")).format(displayedMonth.time).replaceFirstChar { it.uppercase() }
    val year = displayedMonth.get(Calendar.YEAR)
    val monthIndex = displayedMonth.get(Calendar.MONTH)
    val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDay = (displayedMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val offset = (firstDay.get(Calendar.DAY_OF_WEEK) + 5) % 7

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(143.dp)) {
                Image(painter = painterResource(R.drawable.vetci), contentDescription = null,
                    contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().height(143.dp))
                Column(Modifier.fillMaxSize().padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp))
                    Text("Серия", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Column(Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.seriya), contentDescription = null,
                        modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("$streak дней", fontFamily = Dop, fontSize = 26.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(16.dp))

                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Row(Modifier.fillMaxWidth().clickable { monthMenu = true }, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(year.toString(), fontFamily = Osnova, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(monthName, fontFamily = Dop, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                            modifier = Modifier.size(14.dp))
                    }
                    DropdownMenu(expanded = monthMenu, onDismissRequest = { monthMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface).clip(RoundedCornerShape(20.dp))) {
                        (-6..0).forEach { off ->
                            val c = (Calendar.getInstance().clone() as Calendar).apply { add(Calendar.MONTH, off) }
                            val label = SimpleDateFormat("LLLL yyyy", Locale("ru")).format(c.time).replaceFirstChar { it.uppercase() }
                            DropdownMenuItem(
                                text = { Text(label, fontFamily = Dop, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { monthOffset = off; monthMenu = false }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach {
                            Text(it, fontFamily = Dop, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Spacer(Modifier.height(6.dp))

                    val totalCells = offset + daysInMonth
                    val rows = (totalCells + 6) / 7
                    for (r in 0 until rows) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            for (c in 0..6) {
                                val cellIndex = r * 7 + c
                                val dayNum = cellIndex - offset + 1
                                if (dayNum in 1..daysInMonth) {
                                    val dateStr = "%04d-%02d-%02d".format(year, monthIndex + 1, dayNum)
                                    val marked = daysSet.contains(dateStr)
                                    Box(modifier = Modifier.size(28.dp).clickable { selectedDate = dateStr },
                                        contentAlignment = Alignment.Center) {
                                        Text("%02d".format(dayNum), fontFamily = Dop, fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface)
                                        if (marked) {
                                            Image(painter = painterResource(R.drawable.seriya),
                                                contentDescription = null, modifier = Modifier.size(30.dp))
                                        }
                                    }
                                } else {
                                    Spacer(Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                val displayDate = SimpleDateFormat("LLLL d", Locale("ru"))
                    .format(sdf.parse(selectedDate) ?: Date()).replaceFirstChar { it.uppercase() }
                Text(displayDate, fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(12.dp))

                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Вы читали?", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f))
                        Box {
                            Box(Modifier.width(140.dp).height(25.dp).clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.tertiary).clickable { readMenu = true },
                                contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(readToday, fontFamily = Osnova, fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                                        modifier = Modifier.size(12.dp).padding(end = 8.dp))
                                }
                            }
                            DropdownMenu(expanded = readMenu, onDismissRequest = { readMenu = false },
                                modifier = Modifier.width(120.dp).background(MaterialTheme.colorScheme.surface).clip(RoundedCornerShape(20.dp))) {
                                listOf("Да","Нет").forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt, fontFamily = Osnova, fontSize = 12.sp) },
                                        onClick = { readToday = opt; readMenu = false })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Какую книгу?", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f))
                        Box {
                            Box(Modifier.width(140.dp).height(25.dp).clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.tertiary).clickable { bookMenu = true },
                                contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(bookName.ifEmpty { "" }, fontFamily = Osnova, fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center, maxLines = 1)
                                    Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                                        modifier = Modifier.size(12.dp).padding(end = 8.dp))
                                }
                            }
                            DropdownMenu(expanded = bookMenu, onDismissRequest = { bookMenu = false },
                                modifier = Modifier.width(150.dp).background(MaterialTheme.colorScheme.surface).clip(RoundedCornerShape(20.dp))) {
                                viewModel.books.forEach { b ->
                                    DropdownMenuItem(text = { Text(b.title, fontFamily = Osnova, fontSize = 12.sp) },
                                        onClick = { bookName = b.title; bookMenu = false })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Сколько страниц?", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f))
                        Box(Modifier.width(140.dp).height(25.dp).clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.tertiary), contentAlignment = Alignment.Center) {
                            BasicTextField(
                                value = pages,
                                onValueChange = { new -> if (new.all { it.isDigit() }) pages = new },
                                textStyle = TextStyle(fontFamily = Osnova, fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ваши впечатления", fontFamily = Osnova, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Image(painter = painterResource(R.drawable.pero), contentDescription = null,
                                modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(Modifier.fillMaxWidth().heightIn(min = 80.dp).clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.tertiary).padding(12.dp)) {
                            BasicTextField(
                                value = impression,
                                onValueChange = { impression = it },
                                textStyle = TextStyle(fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val userId = auth.currentUserId() ?: return@Button
                        val bookId = viewModel.books.find { it.title == bookName }?.id
                        scope.launch {
                            if (readToday == "Нет") {
                                repo.saveDay(ReadingDay(userId = userId, bookId = null, date = selectedDate,
                                    pagesRead = 0, impression = null))
                            } else {
                                repo.saveDay(ReadingDay(userId = userId, bookId = bookId, date = selectedDate,
                                    pagesRead = pages.toIntOrNull() ?: 0, impression = impression.ifEmpty { null }))
                            }
                            reload()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.75f).height(40.dp).align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Сохранить", fontFamily = Osnova, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(100.dp))
            }
        }

        Footer(onCatalogClick = onCatalogClick, onHomeClick = onHomeClick,
            onSeriesClick = onSeriesClick, onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter))
    }
}