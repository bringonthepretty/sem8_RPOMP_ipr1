package com.wah.sem8_rpomp_ipr1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.coroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.wah.sem8_rpomp_ipr1.drive.GoogleDriveFileHolder
import com.wah.sem8_rpomp_ipr1.ui.theme.NotesScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.Collections

class MainActivity : ComponentActivity() {

    private val accessDriveScope = Scope(Scopes.DRIVE_FILE)
    private val emailScope = Scope(Scopes.EMAIL)

    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val data = it.data

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                task.getResult(ApiException::class.java)
                checkForGooglePermissions(data!!.getStringExtra("data")!!)
            } catch (_: ApiException) {

            }
        }
    }

    private fun checkForGooglePermissions(data: String) {
        if (!GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(this),
            accessDriveScope,
            emailScope
        )) {
            GoogleSignIn.requestPermissions(
                this,
                1234,
                GoogleSignIn.getLastSignedInAccount(this),
                accessDriveScope,
                emailScope
            )
        } else {
            lifecycle.coroutineScope.launch { driveSetup(data) }
        }
    }

    private fun uploadFile(
        driveService: Drive,
        mimeType: String,
        file: File
    ): GoogleDriveFileHolder {
        val root = listOf("root")

        val metadata = com.google.api.services.drive.model.File()
            .setParents(root)
            .setMimeType(mimeType)
            .setName(file.name)

        val fileMetadata = driveService.files().create(
            metadata,
            FileContent(mimeType, file)
        ).execute()

        val googleDriveFileHolder = GoogleDriveFileHolder()
        googleDriveFileHolder.id = fileMetadata.id
        googleDriveFileHolder.name = fileMetadata.name

        return googleDriveFileHolder
    }

    private suspend fun driveSetup(data: String) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(Scopes.DRIVE_FILE))
        credential.selectedAccount = account?.account

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            null,
            credential
        ).setApplicationName("sem8_rpomp_ipr1").build()

        withContext(Dispatchers.Default) {
            val file = File("placeholder")
            file.writeBytes(data.toByteArray())
            val uploadFileTask = async {
                uploadFile(
                    googleDriveService,
                    "text/plain",
                    file
                )
            }
            uploadFileTask.await()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInAndUploadIntent = googleSignInClient.signInIntent
        setContent {
            NotesScreen(launcher, signInAndUploadIntent)
        }
    }
}