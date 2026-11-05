package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.Review
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

@Composable
fun ReviewsScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val auth = remember { AuthRepository() }
    val repo = remember { ReadingRepository() }
    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val scope = rememberCoroutineScope()

    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var selectedTab by remember { mutableStateOf("Все") }
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }

    val booksMap = viewModel.books.associateBy { it.id }

    fun reload() {
        val userId = auth.currentUserId() ?: return
        scope.launch {
            reviews = repo.getReviews(userId)
        }
    }

    LaunchedEffect(Unit) { session.refresh() }
    LaunchedEffect(session.isLoggedIn) { reload() }

    val filteredReviews = when (selectedTab) {
        "positive" -> reviews.filter { it.mood == "positive" }
        "neutral" -> reviews.filter { it.mood == "neutral" }
        "negative" -> reviews.filter { it.mood == "negative" }
        else -> reviews
    }

    val posCount = reviews.count { it.mood == "positive" }
    val neuCount = reviews.count { it.mood == "neutral" }
    val negCount = reviews.count { it.mood == "negative" }

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
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Рецензии", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem("Все", reviews.size.toString(), null, selectedTab) { selectedTab = it }
                TabItem("positive", posCount.toString(), R.drawable.positiv, selectedTab) { selectedTab = it }
                TabItem("neutral", neuCount.toString(), R.drawable.neitral, selectedTab) { selectedTab = it }
                TabItem("negative", negCount.toString(), R.drawable.negativ, selectedTab) { selectedTab = it }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.primary))

            Spacer(Modifier.height(20.dp))

            if (filteredReviews.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Text("Вы еще не написали ни одной рецензии", fontFamily = Osnova,
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 20.dp))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredReviews) { r ->
                        val book = booksMap[r.bookId]
                        val moodIcon = when (r.mood) {
                            "positive" -> R.drawable.positiv
                            "neutral" -> R.drawable.neitral
                            "negative" -> R.drawable.negativ
                            else -> null
                        }

                        Column(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                                .background(MaterialTheme.colorScheme.surface).padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${book?.author ?: ""} - ${book?.title ?: ""}",
                                    fontFamily = Osnova, fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))

                                moodIcon?.let {
                                    Image(painter = painterResource(it), contentDescription = null,
                                        modifier = Modifier.size(28.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { reviewToDelete = r }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(r.content, fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .clickable { onEditClick(r.bookId) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Редактировать", fontFamily = Osnova, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.width(4.dp))
                                Image(painter = painterResource(R.drawable.pero), contentDescription = null,
                                    modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }

        if (reviewToDelete != null) {
            AlertDialog(
                onDismissRequest = { reviewToDelete = null },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(30.dp),
                title = { Text("Удалить рецензию?", fontFamily = Dop, color = MaterialTheme.colorScheme.primary) },
                text = { Text("Вы уверены, что хотите удалить эту рецензию?", fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface) },
                confirmButton = {
                    Text("Удалить", fontFamily = Osnova, color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val userId = auth.currentUserId()
                            val toDelete = reviewToDelete
                            if (userId != null && toDelete != null) {
                                scope.launch {
                                    repo.deleteReview(userId, toDelete.bookId)
                                    reload()
                                }
                            }
                            reviewToDelete = null
                        })
                },
                dismissButton = {
                    Text("Отмена", fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { reviewToDelete = null })
                }
            )
        }
    }
}

@Composable
fun TabItem(
    label: String, count: String, iconRes: Int?, selectedTab: String, onClick: (String) -> Unit
) {
    val isSelected = selectedTab == label
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .clickable { onClick(label) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (iconRes != null) {
                Image(painter = painterResource(iconRes), contentDescription = null,
                    modifier = Modifier.size(20.dp).padding(end = 4.dp))
            } else {
                Text(if (label == "Все") label else "", fontFamily = Osnova, fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(Modifier.width(4.dp))
            }
            Text(count, fontFamily = Osnova, fontSize = 14.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}