package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecolab.R
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onNavigateUp: () -> Unit) {
    val user = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }
    var username by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    val scope = rememberCoroutineScope()
    val ctx = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                            val current = auth.currentUser
                            if (current != null) {
                                val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(username.trim())
                                    .build()
                                runCatching { current.updateProfile(profileUpdate).await() }
                                if (email.trim().isNotEmpty() && email.trim() != current.email) {
                                    runCatching { current.updateEmail(email.trim()).await() }
                                }
                                val fs = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                val map = hashMapOf<String, Any?>()
                                map["name"] = username.trim()
                                map["email"] = email.trim()
                                runCatching { fs.collection("users").document(current.uid).update(map).await() }
                            }
                            onNavigateUp()
                        }
                    }) {
                        Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Palette.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Palette.primary)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ecolab_logo),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Palette.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EcoLabTheme {
        EditProfileScreen(onNavigateUp = {})
    }
}
