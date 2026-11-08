package com.example.recursosmultimedia

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.DisposableEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CityTourApp()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityTourApp() {

    val context = LocalContext.current

    // Fotografías del monumento
    val imagenes = listOf(
        R.drawable.piramide,
        R.drawable.ruinas,
        R.drawable.xochicalco,
    )

    // Estado del carrusel
    val pagerState = rememberPagerState {
        imagenes.size
    }

    // Estado del reproductor
    var reproductor by remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    var estadoAudio by remember {
        mutableStateOf("Detenido")
    }

    var volumen by remember {
        mutableFloatStateOf(1f)
    }

    // Liberar el reproductor al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            reproductor?.release()
            reproductor = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Bienvenido",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF174A70)
        )

        Text(
            text = "Guía Turística Interactiva",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Piramides de Xochicalco",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Carrusel de imágenes
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) { pagina ->

            Image(
                painter = painterResource(
                    id = imagenes[pagina]
                ),
                contentDescription = "Fotografía del monumento",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Imagen ${pagerState.currentPage + 1} de ${imagenes.size}"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Historia del monumento",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Escucha la explicación histórica de las piramides de Xochicalco.",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Botón reproducir
        Button(
            onClick = {
                try {
                    if (reproductor == null) {
                        reproductor = MediaPlayer.create(
                            context,
                            R.raw.mystery
                        )

                        reproductor?.setOnCompletionListener {
                            estadoAudio = "Finalizado"
                        }
                    }

                    if (estadoAudio == "Finalizado") {
                        reproductor?.seekTo(0)
                    }

                    reproductor?.start()
                    estadoAudio = "Reproduciendo"

                } catch (_: Exception) {
                    estadoAudio = "Error al reproducir"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("▶ Reproducir audio")
        }

        // Botón pausa
        Button(
            onClick = {
                if (reproductor?.isPlaying == true) {
                    reproductor?.pause()
                    estadoAudio = "Pausado"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⏸ Pausar audio")
        }

        // Botón reanudar
        Button(
            onClick = {
                if ((reproductor != null) &&
                    (!reproductor!!.isPlaying) &&
                    (estadoAudio == "Pausado")
                ) {
                    reproductor?.start()
                    estadoAudio = "Reproduciendo"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("▶ Reanudar audio")
        }

        // Botón detener
        OutlinedButton(
            onClick = {
                reproductor?.let {
                    if (it.isPlaying) {
                        it.pause()
                    }
                    it.seekTo(0)
                }
                estadoAudio = "Detenido"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⏹ Detener audio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Estado: $estadoAudio",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Control de volumen
        Text("Volumen")

        Slider(
            value = volumen,
            onValueChange = {
                volumen = it
                reproductor?.setVolume(it, it)
            },
            valueRange = 0f..1f
        )
    }
}
