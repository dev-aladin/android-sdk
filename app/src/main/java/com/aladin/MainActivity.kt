package com.aladin


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.aladin.android.sdk.AladinSession
import org.aladin.android.sdk.Result
import org.aladin.android.sdk.Scope
import org.aladin.android.sdk.model.*
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


private const val username = "dev_android_sdk.id.aladin";

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private val textFileName = "message.txt"
    private val imageFileName = "team.jpg"

    private var _AladinSession: AladinSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        val config = ""
                .toAladinConfig(arrayOf(Scope.StoreWrite))


        _AladinSession = AladinSession(this@MainActivity, config)
        signInButton.isEnabled = true
        getUserAppFileUrlButton.isEnabled = true

        validateProofsButton.isEnabled = false
        getStringFileButton.isEnabled = false
        putStringFileButton.isEnabled = false
        deleteStringFileButton.isEnabled = false
        getImageFileButton.isEnabled = false
        putImageFileButton.isEnabled = false
        getStringFileFromUserButton.isEnabled = false
        getAppBucketUrlButton.isEnabled = false
        listFilesButton.isEnabled = false


        signInButton.setOnClickListener { _: View ->
            AladinSession().redirectUserToSignIn { errorResult ->
                if (errorResult.hasErrors) {
                    Toast.makeText(this, "error: " + errorResult.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        signInButtonWithGaia.setOnClickListener { _: View ->
            val key = AladinSession().generateAndStoreTransitKey()
            val authRequest = AladinSession().makeAuthRequest(key, Date(System.currentTimeMillis() + 3600000).time, mapOf(Pair("solicitGaiaHubUrl", true)))
            AladinSession().redirectToSignInWithAuthRequest(authRequest) { errorResult ->
                if (errorResult.hasErrors) {
                    Toast.makeText(this, "error: " + errorResult.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        validateProofsButton.setOnClickListener { _ ->

            AladinSession().signUserOut()
            onSignOut()

        }

        getStringFileButton.setOnClickListener { _ ->
            fileContentsTextView.text = "Downloading..."
            val options = GetFileOptions()
            AladinSession().getFile(textFileName, options, { contentResult ->
                if (contentResult.hasValue) {
                    val content = contentResult.value!!
                    Log.d(TAG, "File contents: ${content as String}")
                    runOnUiThread {
                        fileContentsTextView.text = content
                    }
                } else {
                    Toast.makeText(this, "error: " + contentResult.error, Toast.LENGTH_SHORT).show()
                }
            })
        }

        deleteStringFileButton.setOnClickListener {
            deleteFileMessageTextView.text = "Deleting..."
            AladinSession().deleteFile(textFileName, DeleteFileOptions()) {
                if (it.hasErrors) {
                    Toast.makeText(this, "error " + it.error, Toast.LENGTH_SHORT).show()
                } else {
                    runOnUiThread {
                        deleteFileMessageTextView.text = "File $textFileName deleted."
                    }
                }
            }
        }

        putStringFileButton.setOnClickListener { _ ->
            readURLTextView.text = "Uploading..."
            val options = PutFileOptions()
            AladinSession().putFile(textFileName, "Hello Android!", options,
                    { readURLResult ->
                        if (readURLResult.hasValue) {
                            val readURL = readURLResult.value!!
                            Log.d(TAG, "File stored at: ${readURL}")
                            runOnUiThread {
                                readURLTextView.text = "File stored at: ${readURL}"
                            }
                        } else {
                            Toast.makeText(this, "error: " + readURLResult.error, Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        putImageFileButton.setOnClickListener { _ ->
            imageFileTextView.text = "Uploading..."

            val drawable: BitmapDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back) as BitmapDrawable

            val bitmap = drawable.getBitmap()
            val stream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()

            val options = PutFileOptions(false)
            AladinSession().putFile(imageFileName, bitMapData, options,
                    { readURLResult ->
                        if (readURLResult.hasValue) {
                            val readURL = readURLResult.value!!
                            Log.d(TAG, "File stored at: ${readURL}")
                            runOnUiThread {
                                imageFileTextView.text = "File stored at: ${readURL}"
                            }
                        } else {
                            Toast.makeText(this, "error: " + readURLResult.error, Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        getImageFileButton.setOnClickListener { _ ->
            val options = GetFileOptions(decrypt = false)
            AladinSession().getFile(imageFileName, options, { contentsResult ->
                if (contentsResult.hasValue) {
                    val contents = contentsResult.value!!
                    val imageByteArray = contents as ByteArray
                    val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                } else {
                    Toast.makeText(this, "error: " + contentsResult.error, Toast.LENGTH_SHORT).show()
                }
            })
        }

        getStringFileFromUserButton.setOnClickListener { _ ->

            val zoneFileLookupUrl = URL("")
            fileFromUserContentsTextView.text = "Downloading file from other user..."
            AladinSession().lookupProfile(username, zoneFileLookupURL = zoneFileLookupUrl) { profileResult ->
                if (profileResult.hasValue) {
                    val profile = profileResult.value!!
                    runOnUiThread {
                        val options = GetFileOptions(username = username,
                                zoneFileLookupURL = zoneFileLookupUrl,
                                app = "https://flamboyant-darwin-d11c17.netlify.com",
                                decrypt = false)
                        AladinSession().getFile(textFileName, options, { contentResult: Result<Any> ->
                            if (contentResult.hasValue) {
                                val content = contentResult.value!!
                                runOnUiThread {
                                    fileFromUserContentsTextView.text = "from ${profile.name}($username):\n ${content as String}"
                                }
                            } else {
                                val errorMsg = "error: " + contentResult.error
                                fileFromUserContentsTextView.text = errorMsg
                                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    val errorMsg = "error: " + profileResult.error
                    fileFromUserContentsTextView.text = errorMsg
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        getAppBucketUrlButton.setOnClickListener { _ ->
            getAppBucketUrlText.text = "Getting url ..."
            val it = AladinSession().loadUserData()
            it?.let {
                AladinSession().getAppBucketUrl(it.hubUrl, it.appPrivateKey) { result ->
                    runOnUiThread {
                        getAppBucketUrlText.text =
                                if (result.hasValue) {
                                    result.value
                                } else {
                                    result.error
                                }
                    }
                }
            }
        }

        getUserAppFileUrlButton.setOnClickListener { _ ->
            getUserAppFileUrlText.text = "Getting url ..."
            val zoneFileLookupUrl = ""
            AladinSession().getUserAppFileUrl(textFileName, username, "", zoneFileLookupUrl) {
                runOnUiThread {
                    getUserAppFileUrlText.text = if (it.hasValue) {
                        it.value
                    } else {
                        it.error
                    }
                }
            }
        }

        listFilesButton.setOnClickListener {
            listFilesText.text = "...."
            AladinSession().listFiles({ urlResult ->
                if (urlResult.hasValue) {
                    if (listFilesText.text === "....") {
                        listFilesText.text = urlResult.value
                    } else {
                        listFilesText.text = listFilesText.text.toString() + "\n" + urlResult.value
                    }
                }
                true
            }, { countResult ->
                Log.d(TAG, "files count " + if (countResult.hasValue) {
                    countResult.value
                } else {
                    countResult.error
                })
            })
        }

        getNameInfoButton.setOnClickListener { _ ->
            getNameInfoText.text = "Getting info ..."
            AladinSession().network.getNameInfo(username) {
                runOnUiThread {
                    Log.d(TAG, it.value?.json.toString())
                    getNameInfoText.text = if (it.hasValue) {
                        it.value?.json.toString()
                    } else {
                        it.error
                    }
                }
            }
        }

        if (AladinSession().loadUserData() != null) {
            onSignIn(AladinSession().loadUserData()!!)
        }

        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }

    }

    private fun onSignIn(userData: UserData) {
        userDataTextView.text = "Signed in as ${userData.identityAddress}"
        showUserAvatar(userData.profile?.avatarImage)
        signInButton.isEnabled = false

        validateProofsButton.isEnabled = true
        getStringFileButton.isEnabled = true
        putStringFileButton.isEnabled = true
        deleteStringFileButton.isEnabled = true
        putImageFileButton.isEnabled = true
        getImageFileButton.isEnabled = true
        getStringFileFromUserButton.isEnabled = true
        getAppBucketUrlButton.isEnabled = true
        listFilesButton.isEnabled = true
    }

    private fun onSignOut() {
        userDataTextView.text = ""
        deleteFileMessageTextView.text = ""
        fileContentsTextView.text = ""
        readURLTextView.text = ""
        listFilesText.text = ""

        showUserAvatar(null)
        signInButton.isEnabled = true

        validateProofsButton.isEnabled = false
        getStringFileButton.isEnabled = false
        putStringFileButton.isEnabled = false
        deleteStringFileButton.isEnabled = false
        putImageFileButton.isEnabled = false
        getImageFileButton.isEnabled = false
        getStringFileFromUserButton.isEnabled = false
        getAppBucketUrlButton.isEnabled = false
        listFilesButton.isEnabled = false
    }


    private fun showUserAvatar(avatarImage: String?) {
        if (avatarImage != null) {
            // use whatever suits your app architecture best to asynchronously load the avatar
            // better use a image loading library than the code below
            GlobalScope.launch(Dispatchers.Main) {
                val avatar = withContext(Dispatchers.IO) {
                    try {
                        BitmapDrawable.createFromStream(URL(avatarImage).openStream(), "src")
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                        null
                    }
                }
                avatarView.setImageDrawable(avatar)
            }
        } else {
            avatarView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent")

        if (intent?.action == Intent.ACTION_MAIN) {
            val userData = AladinSession().loadUserData()
            if (userData != null) {
                runOnUiThread {
                    onSignIn(userData)
                }
            } else {
                Toast.makeText(this, "no user data", Toast.LENGTH_SHORT).show()
            }
        } else if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    private fun handleAuthResponse(intent: Intent) {
        val response = intent.data?.query
        Log.d(TAG, "response ${response}")
        if (response != null) {
            val authResponseTokens = response.split('=')

            if (authResponseTokens.size > 1) {
                val authResponse = authResponseTokens[1]
                Log.d(TAG, "authResponse: ${authResponse}")
                AladinSession().handlePendingSignIn(authResponse) { userDataResult: Result<UserData> ->
                    if (userDataResult.hasValue) {
                        val userData = userDataResult.value!!
                        Log.d(TAG, "signed in!")
                        runOnUiThread {
                            onSignIn(userData)
                        }
                    } else {
                        Toast.makeText(this, "error: " + userDataResult.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun AladinSession(): AladinSession {
        val session = _AladinSession
        if (session != null) {
            return session
        } else {
            throw IllegalStateException("No session.")
        }
    }

    override fun onDestroy() {
        println("MainActivity.onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        println("MainActivity.onRestart")
        super.onRestart()
    }

}
