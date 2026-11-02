package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    session: SessionViewModel,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val auth = remember { AuthRepository() }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Image(painter = painterResource(R.drawable.logo), contentDescription = null,
            modifier = Modifier.width(287.dp).height(118.dp))
        Spacer(Modifier.height(62.dp))
        Text("Вход", fontFamily = Dop, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(85.dp))

        InputField(email, { email = it }, "Электронная почта")
        Spacer(Modifier.height(16.dp))
        InputField(password, { password = it }, "Пароль", isPassword = true)
        Spacer(Modifier.height(12.dp))

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
        }

        Text(
            text = buildAnnotatedString {
                append("У меня нет аккаунта. ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Зарегистрироваться") }
            },
            fontFamily = Osnova, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { onRegisterClick() }
        )

        Spacer(Modifier.height(150.dp))

        Button(
            onClick = {
                errorMessage = null
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Заполните все поля"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val result = auth.login(email, password)
                    isLoading = false
                    result.onSuccess {
                        session.refresh()
                        onLoginSuccess()
                    }.onFailure { errorMessage = it.message }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.width(200.dp).height(40.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text(if (isLoading) "..." else "Войти",
                fontFamily = Osnova, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        }
    }
}