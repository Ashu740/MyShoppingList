package com.example.myshoppinglistapp

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp

data class  ShoppingItem( val id: Int,
                          var name : String,
                          var quantity: Int,
                          var isEditing :Boolean=false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainShoppingList(){
    var sItems by remember {
        mutableStateOf(listOf<ShoppingItem>())
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var itemName by remember {
        mutableStateOf("")
    }
    var itemQuantity by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {showDialog=true;},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItems) {
                item ->
                if (item.isEditing){
                    shoppingItemEditor(item = item , onEditComplete = {
                        editedName, editedQuantity ->
                        sItems =sItems.map{ it.copy(isEditing = false)}
                        val editedItem = sItems.find{it.id == item.id}
                        editedItem?.let {
                            it.name =editedName
                            it.quantity = editedQuantity
                        }
                    })
                }else{
                    shoppingListItems(item =item , onEditClick = {
                        //finding which are we editing
                        sItems= sItems.map {
                            it.copy(isEditing = it.id==item.id)
                        }
                    }, onDeleteClick = {
                        sItems= sItems-item
                    })
                }
            }
        }
    }
    if (showDialog){
        AlertDialog(onDismissRequest = {showDialog = false},
            title = { Text("Add Shopping Item") },
            text ={ Column {
                OutlinedTextField(value = itemName , onValueChange ={
                    itemName=it
                },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                OutlinedTextField(value = itemQuantity , onValueChange ={
                    itemQuantity=it
                },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }},
            confirmButton = { Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { showDialog=false }) {
                    Text("Cancel")
                }
                Button(onClick = {
                    if (itemName.isNotBlank()){
                        val newItem =ShoppingItem(
                            id =sItems.size+1,
                            name = itemName,
                            quantity = itemQuantity.toIntOrNull()?:1
                        )
                        sItems=sItems+newItem
                        showDialog=false
                        itemName=""
                        itemQuantity=""
                    }
                }) {
                    Text("Add")
                }
            }}
        )
    }
}
@SuppressLint("ComposableNaming")
@Composable
fun shoppingItemEditor(
    item: ShoppingItem,
    onEditComplete: (String,Int) -> Unit
){
    var editedName by remember {
        mutableStateOf(item.name)
    }
    var editedQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    var isEditing by remember {
        mutableStateOf(item.isEditing)
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(
            color = colorScheme.surface
        ),
        horizontalArrangement = Arrangement.SpaceEvenly){
        Column {
            BasicTextField(value = editedName,
                onValueChange = {editedName=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(value = editedQuantity,
                onValueChange = {editedQuantity=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            Button(onClick = {
                isEditing = false
                onEditComplete(editedName,editedQuantity.toIntOrNull()?:1)
            }) {
                Text(text = "Save")
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun shoppingListItems(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,//lambda_fun
){
    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .border(
            border = BorderStroke(2.dp, color = colorScheme.primary),
            shape = RoundedCornerShape(20)
        ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription ="Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription ="Delete")
            }
        }
    }
}
