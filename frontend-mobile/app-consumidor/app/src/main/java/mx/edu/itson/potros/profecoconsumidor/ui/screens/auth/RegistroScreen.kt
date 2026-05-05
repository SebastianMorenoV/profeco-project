package mx.edu.itson.potros.profecoconsumidor.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.SuccessBox

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolverALogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    var nombreCompleto by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 16.dp)
                ) {
                    Text(
                        "Crear cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Regístrate para usar la app",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nombreCompleto,
                            onValueChange = { nombreCompleto = it; vm.limpiarMensajes() },
                            label = { Text("Nombre completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = usuario,
                            onValueChange = { usuario = it.filter { c -> !c.isWhitespace() }; vm.limpiarMensajes() },
                            label = { Text("Usuario") },
                            singleLine = true,
                            supportingText = { Text("Mínimo 4 caracteres, sin espacios.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; vm.limpiarMensajes() },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            supportingText = { Text("Mínimo 6 caracteres.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = confirmar,
                            onValueChange = { confirmar = it; vm.limpiarMensajes() },
                            label = { Text("Confirmar contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (ui.errorMensaje != null) ErrorBox(ui.errorMensaje!!)
                        if (ui.okMensaje != null) SuccessBox(ui.okMensaje!!)

                        Button(
                            onClick = {
                                vm.registrar(usuario, password, confirmar, nombreCompleto, onRegistroExitoso)
                            },
                            enabled = !ui.procesando,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (ui.procesando) "Creando cuenta…" else "Crear cuenta")
                        }

                        TextButton(
                            onClick = onVolverALogin,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ya tengo cuenta, iniciar sesión")
                        }
                    }
                }
            }
        }
    }
}
