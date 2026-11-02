package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.SessionViewModel
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    session: SessionViewModel,
    onCatalogClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSeriesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onBooksClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onQuotesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    LaunchedEffect(Unit) { session.refresh() }

    if (!session.isLoggedIn) {
        WelcomeScreen(onRegisterClick = onLoginClick, onLoginClick = onLoginClick)
        return
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().height(143.dp)) {
                Image(painter = painterResource(R.drawable.vetci), contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().height(143.dp))
                Column(
                    Modifier.fillMaxSize().padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Профиль", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(110.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center) {
                    Box(Modifier.size(106.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(avatarRes(session.profile?.avatarId ?: 1)),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(CircleShape))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(session.profile?.username ?: "Гость",
                    fontFamily = Dop, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.width(6.dp))
                Image(painter = painterResource(R.drawable.pero), contentDescription = null,
                    modifier = Modifier.size(16.dp).clickable { onEditProfileClick() })
            }

            Spacer(Modifier.height(24.dp))

            Column(Modifier.padding(horizontal = 32.dp)) {
                MenuButton(R.drawable.katalog, "Книги") { onBooksClick() }
                Spacer(Modifier.height(8.dp))
                MenuButton(R.drawable.recenz, "Рецензии") { onReviewsClick() }
                Spacer(Modifier.height(8.dp))
                MenuButton(R.drawable.citata, "Цитаты") { onQuotesClick() }
                Spacer(Modifier.height(8.dp))
                MenuButton(R.drawable.parametr, "Настройки") { onSettingsClick() }
            }

            Spacer(Modifier.weight(1f))

            Column(Modifier.fillMaxWidth().padding(bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Мы вместе уже", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(timeSince(session.profile?.createdAt),
                    fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Image(painter = painterResource(R.drawable.serdce), contentDescription = null,
                    modifier = Modifier.size(20.dp))
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
fun MenuButton(icon: Int, text: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surface).clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

fun avatarRes(id: Int): Int = when (id) {
    1 -> R.drawable.icon1
    2 -> R.drawable.icon2
    3 -> R.drawable.icon3
    4 -> R.drawable.icon4
    5 -> R.drawable.icon5
    else -> R.drawable.icon1
}

fun timeSince(createdAt: String?): String {
    if (createdAt.isNullOrEmpty()) return "1 день"
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val start = fmt.parse(createdAt.substring(0, 19)) ?: return "1 день"
        val days = ((Date().time - start.time) / (1000 * 60 * 60 * 24)).toInt()
        val years = days / 365
        val months = (days % 365) / 30
        val remDays = (days % 365) % 30
        buildString {
            if (years > 0) append("$years год ")
            if (months > 0) append("$months месяцев ")
            append("$remDays дней")
        }.trim()
    } catch (e: Exception) { "1 день" }
}