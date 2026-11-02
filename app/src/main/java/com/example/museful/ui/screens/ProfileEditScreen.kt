package com.example.museful.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.data.supabase
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun ProfileEditScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit) {
    val auth = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        session.refresh()
    }

    var name by remember { mutableStateOf(session.profile?.username ?: "") }
    var selectedAvatar by remember { mutableStateOf(session.profile?.avatarId ?: 1) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(session.profile) {
        session.profile?.let {
            name = it.username
            selectedAvatar = it.avatarId
        }
    }

    Column(
        Modifier.fillMaxSize().background(Color(0xFFF8E9E2)).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        Box(Modifier.fillMaxWidth().height(143.dp)) {
            Image(painter = painterResource(R.drawable.nazad), contentDescription = null,
                modifier = Modifier.size(20.dp).align(Alignment.CenterStart).clickable { onBackClick() })
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                    modifier = Modifier.size(38.dp, 23.dp))
                Text("Профиль", fontFamily = Dop, fontSize = 36.sp, color = Color(0xFF803D4D))
            }
        }

        Spacer(Modifier.height(40.dp))

        Box(
            Modifier.size(130.dp).clip(CircleShape).background(Color(0xFF803D4D)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.size(126.dp).clip(CircleShape).background(Color(0xFFF1DED7)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(avatarRes(selectedAvatar)),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            (1..5).forEach { i ->
                Box(
                    Modifier.size(46.dp).clip(CircleShape)
                        .background(if (selectedAvatar == i) Color(0xFF803D4D) else Color(0xFFF1DED7))
                        .clickable { selectedAvatar = i },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(avatarRes(i)), contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape))
                }
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        InputField(name, { name = it }, "Имя пользователя")

        Spacer(Modifier.height(40.dp))

        error?.let {
            Text(it, color = Color.Red, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val userId = auth.currentUserId() ?: return@Button
                scope.launch {
                    try {
                        supabase.from("users").update({
                            set("username", name)
                            set("avatar_id", selectedAvatar)
                        }) { filter { eq("id", userId) } }
                        session.refresh()
                        onBackClick()
                    } catch (e: Exception) {
                        Log.e("ProfileEdit", "Ошибка: ${e.message}")
                        error = "Имя уже занято"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.75f).height(40.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBC8C1))
        ) {
            Text("Сохранить", fontFamily = Osnova, fontSize = 13.sp, color = Color(0xFF3B2D2D))
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 916)
@Composable
fun ProfileEditPreview() {
    ProfileEditScreen(
        session = remember { SessionViewModel() },
        onBackClick = {}
    )
}