package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova

private val statusList = listOf(
    "Читаю" to Color(0xFFE6D5B6),
    "В планах" to Color(0xFFA1BBD3),
    "Брошено" to Color(0xFFC0C0C0),
    "Прочитано" to Color(0xFFBAD5B7),
    "Любимое" to Color(0xFFFBBCBC)
)

@Composable
fun BookScreen(
    session: SessionViewModel,
    bookId: Long,
    onBackClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    onRequireAuth: () -> Unit,
    viewModel: BookDetailViewModel = viewModel()
) {
    val auth = remember { AuthRepository() }
    val state by viewModel.state.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(bookId, session.isLoggedIn) {
        val userId = if (session.isLoggedIn) auth.currentUserId() else null
        viewModel.load(bookId, userId)
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(30.dp))

            Box(Modifier.fillMaxWidth().height(70.dp)) {
                Image(
                    painter = painterResource(R.drawable.nazad),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).align(Alignment.CenterStart).clickable { onBackClick() }
                )
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("О книге", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(30.dp))

            state.book?.let { b ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(b.coverUrl).crossfade(true).build(),
                        contentDescription = b.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(204.dp).height(323.dp).clip(RoundedCornerShape(30.dp))
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(b.title, fontFamily = Osnova, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text(b.author, fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))

                val currentColor = statusList.find { it.first == state.status }?.second ?: Color(0xFFDDD0D0)

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box {
                        Box(
                            Modifier.width(220.dp).height(40.dp).clip(RoundedCornerShape(30.dp))
                                .background(currentColor)
                                .clickable {
                                    if (!session.isLoggedIn) onRequireAuth()
                                    else menuOpen = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    state.status ?: "Добавить в библиотеку",
                                    fontFamily = Osnova, fontSize = 12.sp, color = Color(0xFF3B2D2D),
                                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1
                                )
                                Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                                    modifier = Modifier.size(10.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            modifier = Modifier.width(220.dp).background(MaterialTheme.colorScheme.surface).clip(RoundedCornerShape(20.dp))
                        ) {
                            statusList.forEach { (name, color) ->
                                DropdownMenuItem(
                                    text = {
                                        Box(
                                            Modifier.fillMaxWidth().clip(RoundedCornerShape(100.dp))
                                                .background(color).padding(vertical = 8.dp, horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name, fontFamily = Osnova, fontSize = 12.sp,
                                                color = Color(0xFF3B2D2D), textAlign = TextAlign.Center)
                                        }
                                    },
                                    onClick = {
                                        menuOpen = false
                                        val userId = auth.currentUserId() ?: return@DropdownMenuItem
                                        viewModel.saveStatus(bookId, userId, name)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surface).padding(16.dp)
                ) {
                    Column {
                        Text("Краткое содержание", fontFamily = Osnova, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Text(b.description ?: "Описание отсутствует",
                            fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surface).padding(16.dp)
                ) {
                    Column {
                        Text("Характеристики книги", fontFamily = Osnova, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))

                        val genresText = if (state.genres.isEmpty()) "Не указан" else state.genres.joinToString(", ")

                        Text("Жанр: $genresText", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Год издания: ${b.publishedYear ?: "—"}", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Количество страниц: ${b.pageCount ?: "—"}", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Язык: ${b.language ?: "—"}", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("ISBN: ${b.isbn ?: "—"}", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(30.dp))

                Text("Рецензии", fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))

                if (state.review == null) {
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                if (!session.isLoggedIn) onRequireAuth()
                                else onWriteReviewClick()
                            }
                            .padding(16.dp)
                    ) {
                        Text("+ Написать рецензию", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                } else {
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onWriteReviewClick() }
                            .padding(16.dp)
                    ) {
                        Text(state.review?.content ?: "", fontFamily = Osnova, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}