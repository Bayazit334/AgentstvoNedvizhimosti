
package com.example.agentstvonedvizhimosti.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agentstvonedvizhimosti.network.ApiClient
import com.example.agentstvonedvizhimosti.network.DealListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DealsListScreen(
    realtorId: Int?,
    onBack: () -> Unit
) {
    var deals by remember {
        mutableStateOf<List<DealListItem>>(emptyList())
    }

    var statusMessage by remember {
        mutableStateOf("Загрузка сделок...")
    }

    LaunchedEffect(realtorId) {
        if (realtorId == null) {
            statusMessage = "Не удалось определить риелтора"
        } else {
            try {
                val result = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getDeals(realtorId)
                }

                deals = result
                statusMessage = if (result.isEmpty()) {
                    "У вас пока нет зарегистрированных сделок"
                } else {
                    ""
                }

            } catch (e: Exception) {
                statusMessage = "Не удалось загрузить сделки: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "МОИ СДЕЛКИ",
            fontSize = 24.sp
        )

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(deals) { deal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Сделка № ${deal.id}",
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Недвижимость: ${deal.property_type}"
                        )

                        Text(
                            text = "Адрес: ${deal.property_address}"
                        )

                        Text(
                            text = "Покупатель: ${deal.buyer_name}"
                        )

                        Text(
                            text = "Собственник: ${deal.owner_name}"
                        )

                        Text(
                            text = "Дата сделки: ${deal.deal_date}"
                        )

                        Text(
                            text = "Сумма: ${deal.deal_amount.toLong()} руб."
                        )
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}
