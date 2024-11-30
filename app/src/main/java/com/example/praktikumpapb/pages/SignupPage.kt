package com.example.praktikumpapb.pages


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.praktikumpapb.AuthState
import com.example.praktikumpapb.AuthViewModel
import com.example.praktikumpapb.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authState = authViewModel?.authState?.observeAsState()
    val context = LocalContext.current

    // Handle authentication state changes
    LaunchedEffect(authState?.value) {
        when (authState?.value) {
            is AuthState.Authenticated -> {
                navController?.navigate("home")
                Toast.makeText(context, "Signed up successfully", Toast.LENGTH_SHORT).show()
            }
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFfbd28f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(105.dp))

        // Logo or Image
        Image(
            modifier = Modifier
                .width(400.dp)
                .height(200.dp),
            painter = painterResource(id = R.drawable.logogo),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Descriptive Text
        Text(
            textAlign = TextAlign.Center,
            text = "COBA AJA DULU !",
            color = Color(0xFF532a24),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp)),
            label = { Text(text = "Email Address", color = Color(0xFF6a3c26)) },
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor =  Color(0xFFa15a3e),
                unfocusedBorderColor = Color(0xFF6a3c26)
            )

        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password input field
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp)),
            label = { Text(text = "Password", color = Color(0xFF6a3c26)) },
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor =  Color(0xFFa15a3e),
                unfocusedBorderColor = Color(0xFF6a3c26)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Confirm Password input field
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp)),
            label = { Text(text = "Repeat Password", color = Color(0xFF6a3c26)) },
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  Color(0xFFa15a3e),
                unfocusedBorderColor = Color(0xFF6a3c26)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(Color(0xffFD8C00)),
            onClick = {
                authViewModel?.signup(email, password, confirmPassword)
            }
        ) {
            Text(text = "REGISTER", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Login now text
        TextButton(
            onClick = {
                navController?.navigate("login")
            },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append("Already have an account? ")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                        append("Login now")
                    }
                }
            )
        }

        // Login with Google button
//        Button(
//            modifier = Modifier
//                .padding(horizontal = 30.dp)
//                .fillMaxWidth()
//                .height(50.dp),
//            colors = ButtonDefaults.buttonColors(Color.Transparent),
//            border = BorderStroke(2.dp, Color(0xFFFFA500)),
//            onClick = {
//                if (navController != null) {
//                    authViewModel?.handleGoogleSignin(context, navController)
//                } else {
//                    Toast.makeText(context, "Navigation is not available", Toast.LENGTH_SHORT).show()
//                }
//            }
//        ){
//            Image(
//                painter = painterResource(id = R.drawable.google),  // Add your Google logo image in drawable folder
//                contentDescription = null,
//                modifier = Modifier.size(48.dp)
//            )
//            Spacer(modifier = Modifier.width(20.dp))
//            Text(text = "Continue with Google", color = Color.White)
//        }




        Spacer(modifier = Modifier.height(10.dp))

        // Terms and Conditions text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                    append("By signing up, you agree with our ")
                }
                withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                    append("Terms & Conditions")
                }
            }
        )
    }
}

// Preview with mock ViewModel
@Preview(showBackground = true)
@Composable
fun PreviewSignupPage() {
    SignupPage()
}
