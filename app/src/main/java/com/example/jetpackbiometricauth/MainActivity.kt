package com.example.jetpackbiometricauth


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.jetpackbiometricauth.ui.theme.JetpackBiometricAuthTheme

//Cambia la clase para soporter componententes biometricos
//class MainActivity : ComponentActivity() {

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackBiometricAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Auth()
                }
            }
        }
        //SETUP
        setupAuth()
    }

    //METHODS
    private var canAutenticate = false
    private lateinit var promptInfo: PromptInfo
    private fun setupAuth() {
        //Comprobamos si nuestro dispositivo admite biometria
        if (BiometricManager.from(this)
                .canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                ) == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            canAutenticate = true
            promptInfo = PromptInfo.Builder()
                .setTitle("Autenticacion Biometrica")
                .setSubtitle("Autenticate utilizando el sensor biometrico")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()
        }
    }


    private fun authenticate(auth: (auth: Boolean) -> Unit) {
        if (canAutenticate) {
            BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        auth(true)
                    }
                }).authenticate(promptInfo)
        } else {
            auth(true)
        }
    }

//COMPOSABLES
//Introducimos dentro la clase todos nuestros componentes composables

    @Composable
    fun Auth() {

        //Variable que debe de ser mutable y hacer referencia a estados ya que hacemos referencia
        // a una estructura visual estatica

        var auth by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(if (auth) Color.Green else Color.Red) // Si auth Green sino Red
                .fillMaxSize(), // Ocupa la totalidad del contenedor
            horizontalAlignment = Alignment.CenterHorizontally, // Contenido al centro
            verticalArrangement = Arrangement.Center
        ) {
            //Si esta autenticado si no necesitas autenticarte
            Text(
                if (auth) "Estás autenticado" else "Necesitas autenticarte",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                if (auth) {
                    auth = false
                } else {
                    authenticate {
                        auth = it
                    }
                }
            }) {
                //Si esta autenticado cierra sino se muestra autenticar
                Text(if (auth) "Cerrar" else "Autenticar")
            }

        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        JetpackBiometricAuthTheme {
            Auth()
        }
    }
}


