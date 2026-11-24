
package com.example.agentstvonedvizhimosti.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OwnerRegistrationScreen(
    onBack: () -> Unit,
    onRegister: (String, String, String, String, Uri, String, String) -> Unit
) {

    var surname by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var passport by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var documentUri by remember { mutableStateOf<Uri?>(null) }

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        documentUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = onBack) {
            Text("Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Регистрация собственника",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = patronymic,
            onValueChange = { patronymic = it },
            label = { Text("Отчество") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = passport,
            onValueChange = { passport = it },
            label = { Text("Паспортные данные") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                documentPicker.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выбрать документ PDF")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (documentUri != null) {
                "Документ выбран"
            } else {
                "Документ не выбран"
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val selectedDocument = documentUri

                if (
                    surname.isNotBlank() &&
                    firstName.isNotBlank() &&
                    patronymic.isNotBlank() &&
                    passport.isNotBlank() &&
                    login.isNotBlank() &&
                    password.isNotBlank() &&
                    selectedDocument != null
                ) {
                    onRegister(
                        surname,
                        firstName,
                        patronymic,
                        passport,
                        selectedDocument,
                        login,
                        password
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрировать")
        }
    }
}
