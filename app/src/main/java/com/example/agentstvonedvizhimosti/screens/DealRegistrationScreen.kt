
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.agentstvonedvizhimosti.network.BuyerOption
import com.example.agentstvonedvizhimosti.network.DealPropertyOption
import com.example.agentstvonedvizhimosti.network.OwnerOption
import com.example.agentstvonedvizhimosti.network.DealCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DealRegistrationScreen(
    realtorId: Int?,
    onBack: () -> Unit
) {

    var properties by remember {
        mutableStateOf<List<DealPropertyOption>>(emptyList())
    }

    var buyers by remember {
        mutableStateOf<List<BuyerOption>>(emptyList())
    }

    var owners by remember {
        mutableStateOf<List<OwnerOption>>(emptyList())
    }

    var selectedProperty by remember {
        mutableStateOf<DealPropertyOption?>(null)
    }

    var selectedBuyer by remember {
        mutableStateOf<BuyerOption?>(null)
    }

    var dealDate by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var propertyExpanded by remember {
        mutableStateOf(false)
    }

    var buyerExpanded by remember {
        mutableStateOf(false)
    }

    var statusMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    val selectedOwner = owners.firstOrNull {
        it.id == selectedProperty?.owner_id
    }

    LaunchedEffect(realtorId) {
        if (realtorId != null) {
            isLoading = true
            statusMessage = "Загрузка данных..."

            try {
                val result = withContext(Dispatchers.IO) {
                    val loadedProperties =
                        ApiClient.apiService.getDealProperties()

                    val loadedBuyers =
                        ApiClient.apiService.getDealBuyers()

                    val loadedOwners =
                        ApiClient.apiService.getDealOwners()

                    Triple(
                        loadedProperties,
                        loadedBuyers,
                        loadedOwners
                    )
                }

                properties = result.first.filter {
                    it.realtor_id == realtorId
                }

                buyers = result.second
                owners = result.third

                statusMessage = ""

            } catch (e: Exception) {
                statusMessage = "Не удалось загрузить данные: ${e.message}"
            } finally {
                isLoading = false
            }
        }
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
            text = "Оформление сделки",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (realtorId == null) {
            Text(
                text = "Оформление сделок доступно только риелтору."
            )
        } else {

            Text(
                text = "Объект недвижимости",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    propertyExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && properties.isNotEmpty()
            ) {
                Text(
                    selectedProperty?.let {
                        "${it.property_type}: ${it.address}"
                    } ?: "Выберите объект недвижимости"
                )
            }

            DropdownMenu(
                expanded = propertyExpanded,
                onDismissRequest = {
                    propertyExpanded = false
                }
            ) {
                properties.forEach { property ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${property.property_type}: ${property.address}"
                            )
                        },
                        onClick = {
                            selectedProperty = property
                            propertyExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Покупатель",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    buyerExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && buyers.isNotEmpty()
            ) {
                Text(
                    selectedBuyer?.let {
                        "${it.surname} ${it.first_name} ${it.patronymic}"
                    } ?: "Выберите покупателя"
                )
            }

            DropdownMenu(
                expanded = buyerExpanded,
                onDismissRequest = {
                    buyerExpanded = false
                }
            ) {
                buyers.forEach { buyer ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${buyer.surname} ${buyer.first_name} ${buyer.patronymic}"
                            )
                        },
                        onClick = {
                            selectedBuyer = buyer
                            buyerExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Собственник",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOwner?.let {
                        "${it.surname} ${it.first_name} ${it.patronymic}"
                    } ?: "Сначала выберите объект недвижимости",
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dealDate,
                onValueChange = {
                    dealDate = it
                },
                label = {
                    Text("Дата сделки (ГГГГ-ММ-ДД)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = {
                    Text("Сумма сделки")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val parsedAmount = amount
                        .replace(',', '.')
                        .toDoubleOrNull()

                    when {
                        selectedProperty == null -> {
                            statusMessage = "Выберите объект недвижимости"
                        }

                        selectedBuyer == null -> {
                            statusMessage = "Выберите покупателя"
                        }

                        selectedOwner == null -> {
                            statusMessage = "Не удалось определить собственника"
                        }

                        !dealDate.matches(
                            Regex("\\d{4}-\\d{2}-\\d{2}")
                        ) -> {
                            statusMessage = "Введите дату в формате ГГГГ-ММ-ДД"
                        }

                        parsedAmount == null || parsedAmount <= 0 -> {
                            statusMessage = "Введите корректную сумму сделки"
                        }

                        else -> {
                            isSaving = true
                            statusMessage = ""

                            coroutineScope.launch {
                                try {
                                    val response = withContext(Dispatchers.IO) {
                                        ApiClient.apiService.createDeal(
                                            DealCreateRequest(
                                                property_id = selectedProperty!!.id,
                                                buyer_id = selectedBuyer!!.id,
                                                owner_id = selectedOwner.id,
                                                realtor_id = realtorId,
                                                deal_date = dealDate,
                                                deal_amount = parsedAmount
                                            )
                                        )
                                    }

                                    statusMessage =
                                        "${response.message}. Номер сделки: ${response.deal_id}"

                                } catch (e: Exception) {
                                    statusMessage =
                                        "Не удалось оформить сделку: ${e.message}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !isSaving
            ) {
                Text(
                    if (isSaving) "Сохранение..." else "Оформить сделку"
                )
            }
        }

        if (isLoading || statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusMessage,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
