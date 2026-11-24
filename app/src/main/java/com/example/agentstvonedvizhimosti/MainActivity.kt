
package com.example.agentstvonedvizhimosti

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

import com.example.agentstvonedvizhimosti.network.ApiClient
import com.example.agentstvonedvizhimosti.network.OwnerOption
import com.example.agentstvonedvizhimosti.network.RealtorOption
import com.example.agentstvonedvizhimosti.network.PropertyCreateRequest
import com.example.agentstvonedvizhimosti.network.UserLoginRequest
import com.example.agentstvonedvizhimosti.network.UserRegisterRequest
import com.example.agentstvonedvizhimosti.screens.LoginScreen
import com.example.agentstvonedvizhimosti.screens.RealtorRegistrationScreen
import com.example.agentstvonedvizhimosti.screens.OwnerRegistrationScreen
import com.example.agentstvonedvizhimosti.screens.BuyerRegistrationScreen
import com.example.agentstvonedvizhimosti.screens.PropertyRegistrationScreen
import com.example.agentstvonedvizhimosti.screens.PropertySearchScreen
import com.example.agentstvonedvizhimosti.screens.DealRegistrationScreen
import com.example.agentstvonedvizhimosti.screens.DealsListScreen
import com.example.agentstvonedvizhimosti.ui.theme.AgentstvoNedvizhimostiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            LaunchedEffect(Unit) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        ApiClient.apiService.checkServer()
                    }

                    Log.d(
                        "SERVER_CHECK",
                        response["message"] ?: "Сервер ответил"
                    )

                } catch (e: Exception) {
                    Log.e(
                        "SERVER_CHECK",
                        "Ошибка подключения к серверу",
                        e
                    )
                }
            }

            AgentstvoNedvizhimostiTheme {

                var currentScreen by remember {
                    mutableStateOf("login")
                }

                var loginMessage by remember {
                    mutableStateOf("")
                }

                var loggedInRealtorId by remember {
                    mutableStateOf<Int?>(null)
                }

                var owners by remember {
                    mutableStateOf<List<OwnerOption>>(emptyList())
                }

                var realtors by remember {
                    mutableStateOf<List<RealtorOption>>(emptyList())
                }

                var propertyMessage by remember {
                    mutableStateOf("")
                }

                when (currentScreen) {

                    "login" -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LoginScreen(
                                onLoginClick = { login, password, role ->
                                    if (login.isBlank() || password.isBlank()) {
                                        loginMessage = "Введите логин и пароль"
                                    } else {
                                        loginMessage = ""

                                        lifecycleScope.launch {
                                            try {
                                                val response = withContext(Dispatchers.IO) {
                                                    ApiClient.apiService.login(
                                                        role,
                                                        UserLoginRequest(
                                                            login = login,
                                                            password = password
                                                        )
                                                    )
                                                }

                                                loggedInRealtorId =
                                                    if (role == "realtor") {
                                                        response.user_id
                                                    } else {
                                                        null
                                                    }

                                                loginMessage = response.message
                                                currentScreen = "main"

                                            } catch (e: Exception) {
                                                loginMessage = "Неверный логин или пароль"
                                                Log.e(
                                                    "LOGIN",
                                                    "Ошибка авторизации",
                                                    e
                                                )
                                            }
                                        }
                                    }
                                },
                                onRegisterClick = {
                                    loginMessage = ""
                                    currentScreen = "registration"
                                }
                            )

                            if (loginMessage.isNotEmpty()) {
                                Text(
                                    text = loginMessage,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }

                    "registration" -> {
                        RegistrationMenuScreen(
                            onRealtorClick = {
                                currentScreen = "realtor"
                            },
                            onOwnerClick = {
                                currentScreen = "owner"
                            },
                            onBuyerClick = {
                                currentScreen = "buyer"
                            },
                            onBackClick = {
                                currentScreen = "login"
                            }
                        )
                    }

                    "realtor" -> {
                        RealtorRegistrationScreen(
                            onBack = {
                                currentScreen = "registration"
                            },
                            onRegister = { surname, firstName, patronymic, login, password ->
                                lifecycleScope.launch {
                                    try {
                                        val response = withContext(Dispatchers.IO) {
                                            ApiClient.apiService.register(
                                                "realtor",
                                                UserRegisterRequest(
                                                    surname = surname,
                                                    first_name = firstName,
                                                    patronymic = patronymic,
                                                    login = login,
                                                    password = password
                                                )
                                            )
                                        }

                                        Log.d("REGISTER", response.message)
                                        currentScreen = "login"

                                    } catch (e: Exception) {
                                        Log.e(
                                            "REGISTER",
                                            "Ошибка регистрации риелтора",
                                            e
                                        )
                                    }
                                }
                            }
                        )
                    }

                    "owner" -> {
                        OwnerRegistrationScreen(
                            onBack = {
                                currentScreen = "registration"
                            },
                            onRegister = { surname, firstName, patronymic, passport, documentUri, login, password ->
                                lifecycleScope.launch {
                                    try {
                                        val response = withContext(Dispatchers.IO) {
                                            val documentBytes = contentResolver
                                                .openInputStream(documentUri)
                                                ?.use { it.readBytes() }
                                                ?: throw Exception("Не удалось прочитать PDF-документ")

                                            val documentPart = MultipartBody.Part.createFormData(
                                                "file",
                                                "ownership_document.pdf",
                                                documentBytes.toRequestBody(
                                                    "application/pdf".toMediaType()
                                                )
                                            )

                                            val uploadResponse = ApiClient.apiService.uploadDocument(
                                                documentPart
                                            )

                                            ApiClient.apiService.register(
                                                "owner",
                                                UserRegisterRequest(
                                                    surname = surname,
                                                    first_name = firstName,
                                                    patronymic = patronymic,
                                                    passport = passport,
                                                    ownership_document = uploadResponse.file_path,
                                                    login = login,
                                                    password = password
                                                )
                                            )
                                        }

                                        Log.d("REGISTER", response.message)
                                        currentScreen = "login"

                                    } catch (e: Exception) {
                                        Log.e(
                                            "REGISTER",
                                            "Ошибка регистрации собственника",
                                            e
                                        )
                                    }
                                }
                            }
                        )
                    }

                    "buyer" -> {
                        BuyerRegistrationScreen(
                            onBack = {
                                currentScreen = "registration"
                            },
                            onRegister = { surname, firstName, patronymic, passport, login, password ->
                                lifecycleScope.launch {
                                    try {
                                        val response = withContext(Dispatchers.IO) {
                                            ApiClient.apiService.register(
                                                "buyer",
                                                UserRegisterRequest(
                                                    surname = surname,
                                                    first_name = firstName,
                                                    patronymic = patronymic,
                                                    passport = passport,
                                                    login = login,
                                                    password = password
                                                )
                                            )
                                        }

                                        Log.d("REGISTER", response.message)
                                        currentScreen = "login"

                                    } catch (e: Exception) {
                                        Log.e(
                                            "REGISTER",
                                            "Ошибка регистрации покупателя",
                                            e
                                        )
                                    }
                                }
                            }
                        )
                    }

                    "property" -> {
                        LaunchedEffect(Unit) {
                            propertyMessage = "Загрузка списков..."

                            try {
                                val result = withContext(Dispatchers.IO) {
                                    val loadedOwners = ApiClient.apiService.getOwners()
                                    val loadedRealtors = ApiClient.apiService.getRealtors()

                                    Pair(loadedOwners, loadedRealtors)
                                }

                                owners = result.first
                                realtors = result.second
                                propertyMessage = ""

                            } catch (e: Exception) {
                                propertyMessage = "Не удалось загрузить списки собственников и риелторов"
                                Log.e(
                                    "PROPERTY",
                                    "Ошибка загрузки списков",
                                    e
                                )
                            }
                        }

                        PropertyRegistrationScreen(
                            owners = owners,
                            realtors = realtors,
                            statusMessage = propertyMessage,
                            onBack = {
                                currentScreen = "main"
                            },
                            onAdd = { propertyType, address, district, area, rooms, price, ownerId, realtorId, documentUri ->
                                lifecycleScope.launch {
                                    propertyMessage = "Сохранение недвижимости..."

                                    try {
                                        val response = withContext(Dispatchers.IO) {
                                            val documentBytes = contentResolver
                                                .openInputStream(documentUri)
                                                ?.use { it.readBytes() }
                                                ?: throw Exception("Не удалось прочитать PDF-документ")

                                            val documentPart = MultipartBody.Part.createFormData(
                                                "file",
                                                "property_document.pdf",
                                                documentBytes.toRequestBody(
                                                    "application/pdf".toMediaType()
                                                )
                                            )

                                            val uploadResponse = ApiClient.apiService.uploadDocument(
                                                documentPart
                                            )

                                            ApiClient.apiService.createProperty(
                                                PropertyCreateRequest(
                                                    property_type = propertyType,
                                                    address = address,
                                                    district = district,
                                                    area = area,
                                                    rooms = rooms,
                                                    price = price,
                                                    owner_id = ownerId,
                                                    realtor_id = realtorId,
                                                    document = uploadResponse.file_path
                                                )
                                            )
                                        }

                                        Log.d("PROPERTY", response.message)
                                        propertyMessage = response.message
                                        currentScreen = "main"

                                    } catch (e: Exception) {
                                        propertyMessage = "Не удалось добавить недвижимость: ${e.message}"
                                        Log.e(
                                            "PROPERTY",
                                            "Ошибка добавления недвижимости",
                                            e
                                        )
                                    }
                                }
                            }
                        )
                    }

                    "search" -> {
                        PropertySearchScreen(
                            onBack = {
                                currentScreen = "main"
                            }
                        )
                    }

                    "deal" -> {
                        DealRegistrationScreen(
                            realtorId = loggedInRealtorId,
                            onBack = {
                                currentScreen = "main"
                            }
                        )
                    }

                    "deals" -> {
                        DealsListScreen(
                            realtorId = loggedInRealtorId,
                            onBack = {
                                currentScreen = "main"
                            }
                        )
                    }

                    else -> {
                        MainScreen(
                            onPropertyClick = {
                                propertyMessage = ""
                                owners = emptyList()
                                realtors = emptyList()
                                currentScreen = "property"
                            },
                            onSearchClick = {
                                currentScreen = "search"
                            },
                            onDealClick = {
                                currentScreen = "deal"
                            },
                            onDealsListClick = {
                                currentScreen = "deals"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationMenuScreen(
    onRealtorClick: () -> Unit,
    onOwnerClick: () -> Unit,
    onBuyerClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "РЕГИСТРАЦИЯ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRealtorClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Регистрация риелтора")
        }

        Button(
            onClick = onOwnerClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Регистрация собственника")
        }

        Button(
            onClick = onBuyerClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Регистрация покупателя")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}

@Composable
fun MainScreen(
    onPropertyClick: () -> Unit,
    onSearchClick: () -> Unit,
    onDealClick: () -> Unit,
    onDealsListClick: () -> Unit
) {
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

        Button(
            onClick = onPropertyClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Добавить недвижимость")
        }

        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Поиск недвижимости")
        }

        Button(
            onClick = onDealClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Оформить сделку")
        }

        Button(
            onClick = onDealsListClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text("Список сделок")
        }
    }
}
