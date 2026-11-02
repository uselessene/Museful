package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.data.ThemeManager
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit
) {
    val email = session.profile?.email ?: "user@gmail.com"
    val scope = rememberCoroutineScope()
    val auth = remember { AuthRepository() }
    val context = LocalContext.current

    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    val hiddenEmail = remember(email) {
        val parts = email.split("@")
        if (parts.size == 2) {
            val name = parts[0]
            val domain = parts[1]
            val visible = if (name.length > 2) name.take(2) else name
            "$visible***@$domain"
        } else {
            "***"
        }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(30.dp))

            Box(Modifier.fillMaxWidth().height(70.dp)) {
                Image(painter = painterResource(R.drawable.nazad), contentDescription = null,
                    modifier = Modifier.size(20.dp).align(Alignment.CenterStart)
                        .clickable { onBackClick() })
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Настройки", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Блок с данными профиля
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Эл. почта", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f))
                    Box(
                        Modifier.width(180.dp).height(30.dp).clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.tertiary).clickable { showEmailDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(hiddenEmail, fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Пароль", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f))
                    Box(
                        Modifier.width(180.dp).height(30.dp).clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.tertiary).clickable { showPasswordDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("***************", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Блок с переключателем темы
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Темная тема", fontFamily = Osnova, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f))

                Switch(
                    checked = ThemeManager.isDarkTheme,
                    onCheckedChange = { ThemeManager.toggleTheme(context) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(0.6f).height(40.dp).align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable { session.logout { onBackClick() } },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Выход", fontFamily = Osnova, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(40.dp))
        }

        if (showEmailDialog) {
            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(30.dp),
                title = { Text("Изменить почту", fontFamily = Dop, color = MaterialTheme.colorScheme.primary) },
                text = {
                    BasicTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        textStyle = TextStyle(fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(10.dp)).padding(8.dp)
                    )
                },
                confirmButton = {
                    Text("Сохранить", fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable {
                        scope.launch {
                            auth.updateEmail(newEmail)
                            session.refresh()
                            showEmailDialog = false
                        }
                    })
                }
            )
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(30.dp),
                title = { Text("Изменить пароль", fontFamily = Dop, color = MaterialTheme.colorScheme.primary) },
                text = {
                    BasicTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        textStyle = TextStyle(fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(10.dp)).padding(8.dp)
                    )
                },
                confirmButton = {
                    Text("Сохранить", fontFamily = Osnova, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable {
                        scope.launch {
                            auth.updatePassword(newPassword)
                            showPasswordDialog = false
                        }
                    })
                }
            )
        }
    }
}