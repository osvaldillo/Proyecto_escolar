package com.example.proyectoescolar
/*
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecognizerActivity : Activity(), CvCameraViewListener2 {
    private var mItemFace50: MenuItem? = null
    private var mItemFace40: MenuItem? = null
    private var mItemFace30: MenuItem? = null
    private var mItemFace20: MenuItem? = null
    private var mItemType: MenuItem? = null
    private var mRgba: Mat? = null
    private lateinit var mGray: Mat
    private var mCascadeFile: File? = null
    private var mJavaDetector: CascadeClassifier? = null
    private var mNativeDetector: DetectionBasedTracker? = null
    private var mDetectorType = JAVA_DETECTOR
    private val mDetectorName: Array<String?>
    private var mRelativeFaceSize = 0.2f
    private var mAbsoluteFaceSize = 0
    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker")
                    try {
                        // load cascade file from application resources
                        val `is` = resources.openRawResource(R.raw.lbpcascade_frontalface)
                        val cascadeDir = getDir("cascade", MODE_PRIVATE)
                        mCascadeFile = File(cascadeDir, "lbpcascade_frontalface.xml")
                        val os = FileOutputStream(mCascadeFile)
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (`is`.read(buffer).also { bytesRead = it } != -1) {
                            os.write(buffer, 0, bytesRead)
                        }
                        `is`.close()
                        os.close()
                        mJavaDetector = CascadeClassifier(mCascadeFile!!.absolutePath)
                        if (mJavaDetector!!.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier")
                            mJavaDetector = null
                        } else Log.i(
                            TAG,
                            "Loaded cascade classifier from " + mCascadeFile!!.absolutePath
                        )
                        mNativeDetector = DetectionBasedTracker(mCascadeFile!!.absolutePath, 0)
                        cascadeDir.delete()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e(TAG, "Failed to load cascade. Exception thrown: $e")
                    }
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    init {
        mDetectorName = arrayOfNulls(2)
        mDetectorName[JAVA_DETECTOR] = "Java"
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)"
        Log.i(TAG, "Instantiated new " + this.javaClass)
    }

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_recognizer)
        mOpenCvCameraView =
            findViewById<View>(R.id.fd_activity_surface_view) as CameraBridgeViewBase
        mOpenCvCameraView!!.visibility = CameraBridgeViewBase.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mGray = Mat()
        mRgba = Mat()
    }

    override fun onCameraViewStopped() {
        mGray!!.release()
        mRgba!!.release()
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        mRgba = inputFrame.rgba()
        mGray = inputFrame.gray()
        if (mAbsoluteFaceSize == 0) {
            val height:Int = mGray.height()
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize)
            }
            mNativeDetector?.setMinFaceSize(mAbsoluteFaceSize)
        }
        val faces = MatOfRect()
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null) mJavaDetector!!.detectMultiScale(
                mGray, faces, 1.1, 2, 2,  // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                Size(mAbsoluteFaceSize.toDouble(), mAbsoluteFaceSize.toDouble()), Size()
            )
        } else if (mDetectorType == NATIVE_DETECTOR) {
            mNativeDetector?.detect(mGray, faces)
        } else {
            Log.e(TAG, "Detection method is not selected!")
        }
        val facesArray = faces.toArray()
        for (i in facesArray.indices) Imgproc.rectangle(
            mRgba,
            facesArray[i].tl(),
            facesArray[i].br(),
            FACE_RECT_COLOR,
            3
        )
        return mRgba as Mat?
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.i(TAG, "called onCreateOptionsMenu")
        mItemFace50 = menu.add("Face size 50%")
        mItemFace40 = menu.add("Face size 40%")
        mItemFace30 = menu.add("Face size 30%")
        mItemFace20 = menu.add("Face size 20%")
        mItemType = menu.add(mDetectorName[mDetectorType])
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, "called onOptionsItemSelected; selected item: $item")
        if (item === mItemFace50) setMinFaceSize(0.5f) else if (item === mItemFace40) setMinFaceSize(
            0.4f
        ) else if (item === mItemFace30) setMinFaceSize(0.3f) else if (item === mItemFace20) setMinFaceSize(
            0.2f
        ) else if (item === mItemType) {
            val tmpDetectorType = (mDetectorType + 1) % mDetectorName.size
            item.title = mDetectorName[tmpDetectorType]
            setDetectorType(tmpDetectorType)
        }
        return true
    }

    private fun setMinFaceSize(faceSize: Float) {
        mRelativeFaceSize = faceSize
        mAbsoluteFaceSize = 0
    }

    private fun setDetectorType(type: Int) {
        if (mDetectorType != type) {
            mDetectorType = type
            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled")
                mNativeDetector?.start()
            } else {
                Log.i(TAG, "Cascade detector enabled")
                mNativeDetector?.stop()
            }
        }
    }

    companion object {
        private const val TAG = "OCVSample::Activity"
        private val FACE_RECT_COLOR = Scalar(0.0, 255.0, 0.0, 255.0)
        const val JAVA_DETECTOR = 0
        const val NATIVE_DETECTOR = 1
    }
}*/

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.widget.TextView
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.proyectoescolar.databinding.ActivityRecognizerBinding
import com.google.firebase.auth.FirebaseAuth
//import org.opencv.android.OpenCVLoader

class RecognizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecognizerBinding
    private lateinit var tvHelloWorld: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvHelloWorld = binding.tvHelloWorld
        tvHelloWorld.setOnClickListener {
            signOut()
        }
        //if(OpenCVLoader.initDebug()) Toast.makeText(this, "OpenCV inicializado exitosamente", Toast.LENGTH_SHORT).show()
        //else Toast.makeText(this, "No se ha podido inicializar OpenCV", Toast.LENGTH_SHORT).show()
        //sleep(1000L)
        if (!Python.isStarted()) Python.start(AndroidPlatform(this)); Toast.makeText(this, "Python iniciado", Toast.LENGTH_SHORT).show()
    }
    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
