package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.network.Review
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

@Composable
fun WriteReviewScreen(bookId: Long, onBackClick: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repo = remember { ReadingRepository() }
    val auth = remember { AuthRepository() }

    LaunchedEffect(bookId) {
        val userId = auth.currentUserId() ?: return@LaunchedEffect
        val existing = repo.getReviewForBook(userId, bookId)
        if (existing != null) {
            text = existing.content
            mood = existing.mood
        }
    }

    Column(
        Modifier.fillMaxSize().background(Color(0xFFF8E9E2))
            .padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
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
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp, 23.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text("Рецензия", fontFamily = Dop, fontSize = 36.sp, color = Color(0xFF803D4D))
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.positiv),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(15.dp))
                    .background(if (mood == "positive") Color(0xFFEBC8C1) else Color.Transparent)
                    .clickable { mood = "positive" }.padding(4.dp)
            )
            Spacer(Modifier.width(20.dp))
            Image(
                painter = painterResource(R.drawable.neitral),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(15.dp))
                    .background(if (mood == "neutral") Color(0xFFEBC8C1) else Color.Transparent)
                    .clickable { mood = "neutral" }.padding(4.dp)
            )
            Spacer(Modifier.width(20.dp))
            Image(
                painter = painterResource(R.drawable.negativ),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(15.dp))
                    .background(if (mood == "negative") Color(0xFFEBC8C1) else Color.Transparent)
                    .clickable { mood = "negative" }.padding(4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier.fillMaxWidth().heightIn(min = 350.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFF1DED7)).padding(16.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontFamily = Osnova, fontSize = 13.sp, color = Color(0xFF3B2D2D)),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                val userId = auth.currentUserId() ?: return@Button
                scope.launch {
                    repo.saveReview(Review(userId = userId, bookId = bookId, content = text, mood = mood))
                    onBackClick()
                }
            },
            modifier = Modifier.fillMaxWidth(0.75f).height(40.dp).align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBC8C1))
        ) {
            Text("Сохранить", fontFamily = Osnova, fontSize = 13.sp, color = Color(0xFF3B2D2D))
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun WriteReviewPreview() {
    WriteReviewScreen(bookId = 1L, onBackClick = {})
}