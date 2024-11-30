package com.example.praktikumpapb.pages


import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.praktikumpapb.AuthViewModel


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktikumpapb.AuthState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.painterResource
import com.example.praktikumpapb.R
import com.example.praktikumpapb.data.ToDoItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val authState = authViewModel?.authState?.observeAsState()
    var inputText by remember { mutableStateOf("") }  // State untuk input text user
    var savedTexts by remember { mutableStateOf(mutableListOf<String>()) }  // State untuk menyimpan text dalam list
    var editIndex by remember { mutableStateOf(-1) }  // Index untuk mengetahui text mana yang sedang di edit
    val db = Firebase.firestore
    val curUser = Firebase.auth.currentUser
    val uid = curUser?.uid.toString()
    val docRef = db.collection("Users").document(uid)
    val toDoItem = ToDoItem()
    val toDoItems = remember { mutableStateListOf<Pair<MutableState<ToDoItem>, String>>() }

    LaunchedEffect(authState?.value) {
        when (authState?.value) {
            is AuthState.Unauthenticated -> navController?.navigate("login")
            else -> Unit
        }
        docRef.get().addOnSuccessListener { document ->
            if(!document.exists()){
                val user = hashMapOf(
                    "uid" to uid
                )
                db.collection("Users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        db.collection("Users").document(uid).collection("ToDoList").document("1")
                            .set(toDoItem).addOnSuccessListener {
                                println(uid)
                            }
                            .addOnFailureListener { e ->
                                println("error: $e")
                            }
                    }
            } else {
                db.collection("Users")
                    .document(uid)
                    .collection("ToDoList")
                    .get()
                    .addOnSuccessListener {
                            querySnapshot ->
                        for (document in querySnapshot) {
                            val todoItem = mutableStateOf(ToDoItem( // Create mutableStateOf
                                name = document.getString("name") ?: "",
                                isCompleted = document.getBoolean("isCompleted") ?: false
                            ))
                            toDoItems.add(Pair(todoItem, document.id))
                        }
                    }
                    .addOnFailureListener{ exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "To do List",
                            color = Color(0xFF532a24),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,

                        )
                    }
                    // Logout Button
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End,

                    ){
                        Button(
                            onClick = { authViewModel?.signout() },
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                                .padding(5.dp),
                            shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff532a24)
                            ),
                            contentPadding = PaddingValues(1.dp),

                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.logout),
                                contentDescription = null
                            )
                        }

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFfbd28f)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start // Mengatur tulisan ke kiri
        ) {

            // Tampilkan email pengguna yang telah login

            val currentState = authState?.value
            if (currentState is AuthState.Authenticated) {
                Column(
                    horizontalAlignment = Alignment.Start, // Mengatur teks ke kiri
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "To Do:",
                        fontSize = 20.sp, // Ukuran lebih besar untuk "Selamat Datang"
                        fontWeight = FontWeight.Bold,
                        color = Color(0xAA532a24)
                    )

                    toDoItems.forEach { (todoItemState, documentId) ->
                        UpdatableTodoItemRow(
                            todoItemState,
                            documentId,
                            toDoItems, // Pass todoItems
                            onDelete = { id -> // Callback function
                                toDoItems.removeAll { it.second == id }
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {

                Button(onClick = {
                    val newTodoItem = ToDoItem(name = "New To-Do", isCompleted = false)
                    val dbb = FirebaseFirestore.getInstance()
                    val currentUser = auth.currentUser

                    if (currentUser != null) {
                        dbb.collection("Users").document(currentUser.uid).collection("ToDoList")
                            .add(newTodoItem)
                            .addOnSuccessListener { documentReference ->
                                // Add the new item to the todoItems list with the generated document ID
                                toDoItems.add(Pair(mutableStateOf(newTodoItem), documentReference.id))
                                Log.d("FirestoreAdd", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreAdd", "Error adding document", e)
                            }
                    }
                },
                    modifier = Modifier.padding(bottom = 16.dp), // Add padding
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF532a24) // Primary color
                    )
                ) {
                    Text("Add New To-Do", color = Color.White)
                }

            }

        }
    }
}

@Composable
fun UpdatableTodoItemRow(
    todoItemState: MutableState<ToDoItem>,
    documentId: String,
    todoItems: MutableList<Pair<MutableState<ToDoItem>, String>>, // Add todoItems
    onDelete: (String) -> Unit // Add onDelete callback
) {
    val todoItem = todoItemState.value
    var expanded by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf<String>(todoItem.name) }
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    Row(verticalAlignment = Alignment.CenterVertically) {

        if (editingName) {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                todoItem.name = newName
                editingName = false
                if (currentUser != null) {
                    db.collection("Users").document(currentUser.uid).collection("ToDoList").document(documentId)
                        .update("name", newName)
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF532a24) // Orange color for Save button
                )
            ) {
                Text("Save", color = Color.White)
            }
        } else {
            Text(
                text = todoItem.name,
                modifier = Modifier
                    .weight(1f)
                    .clickable { editingName = true }
            )
        }

        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = todoItem.isCompleted,
                    onCheckedChange = { isChecked ->
                        todoItemState.value = todoItem.copy(isCompleted = isChecked)
                        if (currentUser != null) {
                            db.collection("Users").document(currentUser.uid).collection("ToDoList")
                                .document(documentId)
                                .update("isCompleted", isChecked)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF532a24), // Primary color
                        uncheckedColor = Color.Gray // Light gray for unchecked
                    )
                )
                Text(
                    text = if (todoItem.isCompleted) "Completed" else "Pending",
                    color = if (todoItem.isCompleted) Color(0xFF532a24) else Color.Gray, // Match checkbox color
                    modifier = Modifier.padding(start = 8.dp) // Add padding between checkbox and text
                )
            }


        }
        IconButton(onClick = {
            if (currentUser != null) {
                db.collection("Users").document(currentUser.uid).collection("ToDoList").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        // Remove the item from the todoItems list
                        todoItems.removeAll { it.second == documentId }
                        Log.d("FirestoreDelete", "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("FirestoreDelete", "Error deleting document", e)
                    }
            }
            onDelete(documentId)
            },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color(0xFF532a24))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomePage() {
    HomePage()
}
