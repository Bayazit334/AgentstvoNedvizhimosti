
package com.example.agentstvonedvizhimosti.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agentstvonedvizhimosti.network.ApiClient
import com.example.agentstvonedvizhimosti.network.PropertySearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PropertySearchScreen(onBack: () -> Unit) {

    var propertyType by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("") }

    var searchResults by remember {
        mutableStateOf<List<PropertySearchResult>>(emptyList())
    }

    var searchMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

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
            text = "Поиск недвижимости",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = propertyType,
            onValueChange = { propertyType = it },
            label = { Text("Тип недвижимости") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = minPrice,
            onValueChange = { minPrice = it },
            label = { Text("Минимальная цена") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = maxPrice,
            onValueChange = { maxPrice = it },
            label = { Text("Максимальная цена") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("Район") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = rooms,
            onValueChange = { rooms = it },
            label = { Text("Количество комнат") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val parsedMinPrice = minPrice
                    .replace(',', '.')
                    .toDoubleOrNull()

                val parsedMaxPrice = maxPrice
                    .replace(',', '.')
                    .toDoubleOrNull()

                val parsedRooms = rooms.toIntOrNull()

                when {
                    minPrice.isNotBlank() && parsedMinPrice == null -> {
                        searchMessage = "Проверьте минимальную цену"
                    }

                    maxPrice.isNotBlank() && parsedMaxPrice == null -> {
                        searchMessage = "Проверьте максимальную цену"
                    }

                    rooms.isNotBlank() && parsedRooms == null -> {
                        searchMessage = "Проверьте количество комнат"
                    }

                    parsedMinPrice != null &&
                            parsedMaxPrice != null &&
                            parsedMinPrice > parsedMaxPrice -> {
                        searchMessage = "Минимальная цена не может быть больше максимальной"
                    }

                    else -> {
                        isLoading = true
                        searchMessage = ""
                        searchResults = emptyList()

                        coroutineScope.launch {
                            try {
                                val results = withContext(Dispatchers.IO) {
                                    ApiClient.apiService.searchProperties(
                                        propertyType = propertyType.ifBlank { null },
                                        minPrice = parsedMinPrice,
                                        maxPrice = parsedMaxPrice,
                                        district = district.ifBlank { null },
                                        rooms = parsedRooms
                                    )
                                }

                                searchResults = results

                                searchMessage = if (results.isEmpty()) {
                                    "По вашему запросу ничего не найдено"
                                } else {
                                    "Найдено объектов: ${results.size}"
                                }

                            } catch (e: Exception) {
                                searchMessage = "Ошибка поиска: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Поиск..." else "Найти"
            )
        }

        if (searchMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = searchMessage,
                fontWeight = FontWeight.Bold
            )
        }

        searchResults.forEach { property ->

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = property.property_type,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Адрес: ${property.address}")
                    Text("Район: ${property.district}")
                    Text("Площадь: ${property.area} м²")
                    Text("Количество комнат: ${property.rooms}")
                    Text("Цена: ${property.price}")
                }
            }
        }
    }
}
