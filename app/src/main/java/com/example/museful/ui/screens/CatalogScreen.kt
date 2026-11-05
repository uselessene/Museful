package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.museful.R
import com.example.museful.network.Book
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova

@Composable
fun CatalogScreen(
    onCatalogClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onBookClick: (Long) -> Unit
) {
    val viewModel: BookViewModel = viewModel()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val filteredBooks = viewModel.books.filter {
        it.title.contains(searchText.text, ignoreCase = true) ||
                it.author.contains(searchText.text, ignoreCase = true)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(143.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.vetci),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().height(143.dp)
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp)
                    )
                    Text(
                        text = "Каталог",
                        fontFamily = Dop,
                        fontSize = 36.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SearchField(
                value = searchText,
                onValueChange = { searchText = it }
            )

            Spacer(modifier = Modifier.height(45.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 40.dp, end = 40.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = filteredBooks, key = { it.id }) { book ->
                    BookCard(book, onClick = { onBookClick(book.id) })
                }
            }
        }

        Footer(
            onCatalogClick = onCatalogClick,
            onHomeClick = onHomeClick,
            onSeriesClick = onSeriesClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SearchField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.pero),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.text.isEmpty()) {
                    Text(
                        text = "Поиск",
                        fontFamily = Osnova,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontFamily = Osnova,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Image(
                painter = painterResource(R.drawable.lupa),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(book.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(271.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = book.title,
            fontFamily = Osnova,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = Color(0xFF803D4D),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = book.author,
            fontFamily = Osnova,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            color = Color(0xFF3B2D2D),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Footer(
    onCatalogClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().height(79.dp).background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.katalog), contentDescription = null,
            modifier = Modifier.size(40.dp).clickable { onCatalogClick() })
        Image(painter = painterResource(R.drawable.glavnaya), contentDescription = null,
            modifier = Modifier.size(40.dp).clickable { onHomeClick() })
        Image(painter = painterResource(R.drawable.seriya), contentDescription = null,
            modifier = Modifier.size(40.dp).clickable { onSeriesClick() })
        Image(painter = painterResource(R.drawable.profil), contentDescription = null,
            modifier = Modifier.size(40.dp).clickable { onProfileClick() })
    }
}