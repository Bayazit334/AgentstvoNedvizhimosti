
package com.example.agentstvonedvizhimosti.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginClick: (String, String, String) -> Unit,
    onRegisterClick: () -> Unit
) {

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedRole by remember { mutableStateOf("Риелтор") }
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf(
        "Риелтор",
        "Собственник",
        "Покупатель"
    )

    val roleValues = mapOf(
        "Риелтор" to "realtor",
        "Собственник" to "owner",
        "Покупатель" to "buyer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "АГЕНТСТВО НЕДВИЖИМОСТИ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Вход в приложение",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Роль: $selectedRole")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        selectedRole = role
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val role = roleValues[selectedRole] ?: "realtor"
                onLoginClick(login, password, role)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Регистрация")
        }
    }
}
