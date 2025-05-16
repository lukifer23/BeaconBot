package io.smollmandroid.ui.screens.model_download

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.smollmandroid.R
import io.smollmandroid.llm.exampleModelsList
import io.smollmandroid.ui.components.AppAlertDialog
import io.smollmandroid.ui.components.AppProgressDialog
import io.smollmandroid.ui.components.createAlertDialog
import io.smollmandroid.ui.screens.chat.ChatActivity
import io.smollmandroid.ui.theme.SmolLMAndroidTheme
import org.koin.android.ext.android.inject

class DownloadModelActivity : ComponentActivity() {
    private var openChatScreen: Boolean = true
    private val viewModel: DownloadModelsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmolLMAndroidTheme {
                Surface {
                    DownloadModelScreen(
                        viewModel = viewModel,
                        onComplete = { openChatActivity() }
                    )
                }
                AppProgressDialog()
                AppAlertDialog()
            }
        }
        openChatScreen = intent.extras?.getBoolean("openChatScreen") ?: true
    }

    private fun openChatActivity() {
        if (openChatScreen) {
            Intent(this, ChatActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        } else {
            finish()
        }
    }

    @Composable
    private fun DownloadModelScreen(
        viewModel: DownloadModelsViewModel,
        onComplete: () -> Unit,
    ) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                activityResult.data?.let {
                    it.data?.let { uri ->
                        if (checkGGUFFile(uri)) {
                            viewModel.copyModelFile(uri, onComplete)
                        } else {
                            createAlertDialog(
                                dialogTitle = "Invalid File",
                                dialogText = "The selected file is not a valid GGUF file.",
                                dialogPositiveButtonText = "OK",
                                onPositiveButtonClick = {},
                                dialogNegativeButtonText = null,
                                onNegativeButtonClick = null,
                            )
                        }
                    }
                }
            }
        SmolLMAndroidTheme {
            Surface {
                Column(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize()
                            .padding(16.dp)
                            .windowInsetsPadding(WindowInsets.safeContent)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(R.string.download_model_title),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.download_model_popular_models),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ModelsList(viewModel)
                    Spacer(modifier = Modifier.height(4.dp))
                    ModelURLInput(viewModel)

                    Spacer(modifier = Modifier.height(16.dp))
                    // Local model import only; HF model browsing removed
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.download_model_desc_download_location),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                stringResource(R.string.download_model_select_gguf),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val intent =
                                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                            setType("application/octet-stream")
                                            putExtra(
                                                DocumentsContract.EXTRA_INITIAL_URI,
                                                Environment
                                                    .getExternalStoragePublicDirectory(
                                                        Environment.DIRECTORY_DOWNLOADS,
                                                    ).toUri(),
                                            )
                                        }
                                    launcher.launch(intent)
                                },
                            ) {
                                Text(stringResource(R.string.download_models_select_gguf_button))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ModelsList(viewModel: DownloadModelsViewModel) {
        var selectedModel by remember { viewModel.selectedModelState }
        Column(verticalArrangement = Arrangement.Center) {
            exampleModelsList.forEach { model ->
                Row(
                    Modifier
                        .clickable { selectedModel = model }
                        .fillMaxWidth()
                        .background(
                            if (model == selectedModel) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.background
                            },
                            RoundedCornerShape(
                                8.dp,
                            ),
                        ).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (model == selectedModel) {
                        Icon(Icons.Default.Done, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        color =
                            if (model == selectedModel) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                MaterialTheme
                                    .colorScheme.onBackground
                            },
                        text = model.name,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }

    @Composable
    private fun ModelURLInput(viewModel: DownloadModelsViewModel) {
        var modelUrl by remember { viewModel.modelUrlState }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = modelUrl,
            onValueChange = { modelUrl = it },
            placeholder = {
                Text(text = stringResource(R.string.download_model_gguf_url))
            },
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri,
                ),
        )
    }

    // check if the first four bytes of the file
    // represent the GGUF magic number
    // see:https://github.com/ggml-org/ggml/blob/master/docs/gguf.md#file-structure
    private fun checkGGUFFile(uri: Uri): Boolean {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val ggufMagicNumberBytes = ByteArray(4)
            inputStream.read(ggufMagicNumberBytes)
            return ggufMagicNumberBytes.contentEquals(byteArrayOf(71, 71, 85, 70))
        }
        return false
    }
}
