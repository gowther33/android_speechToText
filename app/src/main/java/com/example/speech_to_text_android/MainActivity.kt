package com.example.speech_to_text_android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.speech_to_text_android.ui.theme.Speech_to_text_androidTheme

class MainActivity : ComponentActivity() {

    val speechToTextParser by lazy {
        SpeechToTextParser(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var canRecord  by remember {
                mutableStateOf(false)
            }
            val recordAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {
                canRecord = it
            }
            val state by speechToTextParser.state.collectAsState()
            LaunchedEffect(key1 = recordAudioLauncher) {
                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        if(state.isSpeaking){
                            speechToTextParser.stopListening()
                        }else{
                            speechToTextParser.startListening()
                        }
                    }) {
                        AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking->
                            if(isSpeaking){
                                Icon(imageVector = Icons.Rounded.Stop, contentDescription = null)
                            }else{
                                Icon(imageVector = Icons.Rounded.Mic, contentDescription = null)
                            }
                        }
                    }
                }
            ) {padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking->
                        if (isSpeaking){
                            Text(text = "Speaking...")
                        }else{
                            Text(text = state.spokenText.ifEmpty { "Click on mic record audio" })
                        }
                    }
                }
            }
        }
    }
}
