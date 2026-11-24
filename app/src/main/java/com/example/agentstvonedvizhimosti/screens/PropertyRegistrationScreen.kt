
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agentstvonedvizhimosti.network.OwnerOption
import com.example.agentstvonedvizhimosti.network.RealtorOption

@Composable
fun PropertyRegistrationScreen(
    owners: List<OwnerOption>,
    realtors: List<RealtorOption>,
    statusMessage: String,
    onBack: () -> Unit,
    onAdd: (String, String, String, Double, Int, Double, Int, Int, Uri) -> Unit
) {

    var propertyType by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var documentUri by remember { mutableStateOf<Uri?>(null) }

    var selectedOwner by remember { mutableStateOf<OwnerOption?>(null) }
    var selectedRealtor by remember { mutableStateOf<RealtorOption?>(null) }

    var ownerMenuExpanded by remember { mutableStateOf(false) }
    var realtorMenuExpanded by remember { mutableStateOf(false) }

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
            text = "Добавление недвижимости",
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
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес") },
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
            value = area,
            onValueChange = { area = it },
            label = { Text("Площадь") },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Собственник",
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { ownerMenuExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = owners.isNotEmpty()
        ) {
            Text(
                selectedOwner?.let {
                    "${it.surname} ${it.first_name} ${it.patronymic}"
                } ?: "Выбрать собственника"
            )
        }

        DropdownMenu(
            expanded = ownerMenuExpanded,
            onDismissRequest = { ownerMenuExpanded = false }
        ) {
            owners.forEach { owner ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${owner.surname} ${owner.first_name} ${owner.patronymic}"
                        )
                    },
                    onClick = {
                        selectedOwner = owner
                        ownerMenuExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Риелтор",
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { realtorMenuExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = realtors.isNotEmpty()
        ) {
            Text(
                selectedRealtor?.let {
                    "${it.surname} ${it.first_name} ${it.patronymic}"
                } ?: "Выбрать риелтора"
            )
        }

        DropdownMenu(
            expanded = realtorMenuExpanded,
            onDismissRequest = { realtorMenuExpanded = false }
        ) {
            realtors.forEach { realtor ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${realtor.surname} ${realtor.first_name} ${realtor.patronymic}"
                        )
                    },
                    onClick = {
                        selectedRealtor = realtor
                        realtorMenuExpanded = false
                    }
                )
            }
        }

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

        if (owners.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Список собственников пуст")
        }

        if (realtors.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Список риелторов пуст")
        }

        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(statusMessage)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val selectedDocument = documentUri
                val owner = selectedOwner
                val realtor = selectedRealtor
                val parsedArea = area.replace(',', '.').toDoubleOrNull()
                val parsedRooms = rooms.toIntOrNull()
                val parsedPrice = price.replace(',', '.').toDoubleOrNull()

                if (
                    propertyType.isNotBlank() &&
                    address.isNotBlank() &&
                    district.isNotBlank() &&
                    parsedArea != null &&
                    parsedArea > 0 &&
                    parsedRooms != null &&
                    parsedRooms >= 0 &&
                    parsedPrice != null &&
                    parsedPrice > 0 &&
                    owner != null &&
                    realtor != null &&
                    selectedDocument != null
                ) {
                    onAdd(
                        propertyType,
                        address,
                        district,
                        parsedArea,
                        parsedRooms,
                        parsedPrice,
                        owner.id,
                        realtor.id,
                        selectedDocument
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить недвижимость")
        }
    }
}
