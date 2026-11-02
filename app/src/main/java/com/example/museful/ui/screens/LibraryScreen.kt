package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.UserBook
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

private val statusList = listOf(
    "Читаю" to Color(0xFFE6D5B6),
    "В планах" to Color(0xFFA1BBD3),
    "Брошено" to Color(0xFFC0C0C0),
    "Прочитано" to Color(0xFFBAD5B7),
    "Любимое" to Color(0xFFFBBCBC)
)

@Composable
fun LibraryScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit
) {
    val auth = remember { AuthRepository() }
    val repo = remember { ReadingRepository() }
    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val scope = rememberCoroutineScope()

    var userBooks by remember { mutableStateOf<List<UserBook>>(emptyList()) }

    fun reloadBooks() {
        val userId = auth.currentUserId() ?: return
        scope.launch {
            userBooks = repo.getUserBooks(userId)
        }
    }

    LaunchedEffect(Unit) { session.refresh() }
    LaunchedEffect(session.isLoggedIn) { reloadBooks() }

    val booksMap = viewModel.books.associateBy { it.id }
    val books = userBooks.mapNotNull { booksMap[it.bookId] }
    val statusMap = userBooks.associate { it.bookId to it.status }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(30.dp))

            Box(Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 24.dp)) {
                Image(
                    painter = painterResource(R.drawable.nazad),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).align(Alignment.CenterStart).clickable { onBackClick() }
                )
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Мои книги", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (books.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Text("Ваша библиотека пуста", fontFamily = Osnova,
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 20.dp))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 40.dp, end = 40.dp, bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = books, key = { it.id }) { book ->
                        val currentStatus = statusMap[book.id] ?: "Читаю"
                        val currentColor = statusList.find { it.first == currentStatus }?.second ?: Color.Gray
                        var menuExpanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                Box(
                                    Modifier.fillMaxWidth().height(30.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(currentColor)
                                        .clickable { menuExpanded = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(currentStatus, fontFamily = Osnova, fontSize = 11.sp,
                                            color = Color(0xFF3B2D2D), modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Center)
                                        Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                                            modifier = Modifier.size(12.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface).clip(RoundedCornerShape(20.dp))
                                ) {
                                    statusList.forEach { (name, color) ->
                                        DropdownMenuItem(
                                            text = {
                                                Box(
                                                    Modifier.fillMaxWidth().clip(RoundedCornerShape(100.dp))
                                                        .background(color).padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(name, fontFamily = Osnova, fontSize = 12.sp, color = Color(0xFF3B2D2D))
                                                }
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                val userId = auth.currentUserId() ?: return@DropdownMenuItem
                                                scope.launch {
                                                    repo.saveUserBook(UserBook(userId = userId, bookId = book.id, status = name))
                                                    reloadBooks()
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Column(Modifier.clickable { onBookClick(book.id) }) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(271.dp).clip(RoundedCornerShape(30.dp))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(book.title, fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center, maxLines = 2,
                                    overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
                                Text(book.author, fontFamily = Osnova, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center, maxLines = 1,
                                    overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }
}