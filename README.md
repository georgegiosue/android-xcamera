# android-xcamera
[![Version](https://img.shields.io/badge/version-1.0.1-brightgreen)](https://github.com/16george/android-xcamera/releases/tag/1.0.1)
![API Level](https://img.shields.io/badge/Android%20API-v21%2B-blue)

A simple library to simplify the use of the camera in your Android application

### Adding dependencies
Add this to your build.gradle:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependencies in your app/build.gradle:
```groovy
dependencies {
    ....
    implementation 'com.github.16george:android-xcamera:x.x.x'
}
```

### How to use the library

In the XML layout *for activities or fragments**, create a PreviewView :

```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <androidx.camera.view.PreviewView
        android:id="@+id/viewFinder"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Activity:**

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    private val camera by lazy { xCamera() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        
        // Permissions...
        
        val surfaceProvider = binding.viewFinder.surfaceProvider
        
        camera.start(
            selector = XCamera.BACK, 
            mode = XCamera.MAX_QUALITY, 
            surface = surfaceProvider)
        
        
        // Take a Photo
        
        binding.myButtom.setOnClickListener { takePhoto() }
        
        private fun takePhoto() {
            camera.takePhoto(
                extF = XCamera.JPG,
                withSuccessful = { imagePath ->

                    // Your Code

                }, withError = { exception ->

                    // Your Code

                }
            )
        }
      
        ....
    }
}
```

**Fragment:**

```kotlin
class MainFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    
    private val camera by lazy { xCamera() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Permissions...
        
        val surfaceProvider = binding.viewFinder.surfaceProvider
        
        camera.start(
            selector = XCamera.BACK, 
            mode = XCamera.MAX_QUALITY, 
            surface = surfaceProvider)
        
        
        // Take a Photo
        
        binding.myButtom.setOnClickListener { takePhoto() }
        
        private fun takePhoto() {
            camera.takePhoto(
                extF = XCamera.JPG,
                withSuccessful = { imagePath ->

                    // Your Code

                }, withError = { exception ->

                    // Your Code

                }
            )
        }
      
        ....
    }
}
```

:D



