package com.example.baseproject.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.baseproject.R

class PermissionUtil {
    private var mActivity: Activity? = null
    private var mFragment: Fragment? = null
    private var mListener: OnPermissionGrantCallback? = null
    private val PERMISSION_CODE = 1001

    private val TAG = PermissionUtil::class.java.getSimpleName()
    private var permissions: Array<String> = arrayOf()
    private var isRationale = false

    fun setActivity(
        activity: Activity,
        permission: Array<String>,
        listener: OnPermissionGrantCallback?
    ) {
        mActivity = activity
        mListener = listener
        onPermissionCheck(permission, activity)
    }

    fun setFragment(
        fragment: Fragment,
        permission: Array<String>,
        listener: OnPermissionGrantCallback?
    ) {
        mFragment = fragment
        mListener = listener
        onPermissionCheck(permission, fragment.requireActivity())
    }

    private fun onPermissionCheck(permission: Array<String>, activity: Activity) {
        isRationale = false
        var isPermissionDenied = false
        for (p in permission) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
                isRationale = true
            }
            if (activity.checkSelfPermission(p) == PackageManager.PERMISSION_DENIED) {
                isPermissionDenied = true
            }
        }
        if (!isPermissionDenied) {
            allowToMakePermissionRequest(permission)
        } else {
            if (permission.contentEquals(permissionForCameraOrStorage())) {
                alertPermission(
                    activity,
                    "permission_required_for_camera_storage",
                    dialogCallbacks = object : OkDialogCallbacks {
                        override fun onConfirmDialog() {
                            if (!isRationale)
                                Util.openSetting(activity)
                            else
                                allowToMakePermissionRequest(permission)
                        }
                    }
                )
            }
        }
    }

    private fun allowToMakePermissionRequest(permission: Array<String>) {
        val reqPermissions = requestForPermission(permission)
        if (reqPermissions.isNotEmpty()) {
            if (mActivity != null) {
                ActivityCompat.requestPermissions(
                    mActivity!!, reqPermissions
                        .toTypedArray<String>(), PERMISSION_CODE
                )
            } else {
                mFragment!!.requestPermissions(
                    reqPermissions
                        .toTypedArray<String>(), PERMISSION_CODE
                )
            }
        } else {
            if (mListener != null) {
                mListener!!.onPermissionGranted()
            }
        }
    }

    private fun requestForPermission(permission: Array<String>): List<String> {
        val reqPermission: MutableList<String> = ArrayList()
        for (i in permission.indices) {
            if (mActivity != null) {
                if (mActivity?.checkSelfPermission(permission[i])
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "Permission is granted ==> " + permission[i])
                } else {
                    reqPermission.add(permission[i])
                }
            } else {
                if (mFragment!!.activity != null) {
                    if (ContextCompat.checkSelfPermission(
                            mFragment?.requireActivity()!!,
                            permission[i]
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "Permission is granted ==> " + permission[i])
                    } else {
                        reqPermission.add(permission[i])
                    }
                }
            }
        }
        return reqPermission
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        try {
            var isGranted = true
            var missedPermission: String? = ""
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Not Granted Permission is == > " + grantResults[i])
                    missedPermission = permissions[i]
                    isGranted = false
                    break
                }
            }
            if (isGranted) {
                if (mListener != null) mListener!!.onPermissionGranted()
            } else {
                if (mListener != null) mListener!!.onPermissionError(missedPermission)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (mListener != null) {
                mListener!!.onPermissionError(e.message)
            }
        }
    }

    private fun alertPermission(
        context: Context?,
        title: String,
        description: String? = null,
        okText: String? = null,
        dialogCallbacks: OkDialogCallbacks
    ) {
//        val dialog = Dialog(context!!, R.style.SettingsDialogTheme) //TODO(design style theme for your dialog)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setContentView(R.layout.dialog_confirm_layout) //TODO(design your alert dialog)
//        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
//        tvHeader.text = title.ifEmpty { "Permission is required!" }
//        val tvDisc = dialog.findViewById<TextView>(R.id.tvDisc)
//        tvDisc.text = description
//        val btnCancelDialog = dialog.findViewById<TextView>(R.id.txtBtnCancel)
//        val btnConfirmDialog = dialog.findViewById<TextView>(R.id.txtBtnAccept)
//        if (!okText.isNullOrBlank()) {
//            btnConfirmDialog.text = okText
//        }
//        dialog.show()
//        btnCancelDialog.setOnClickListener { dialog.dismiss() }
//        btnConfirmDialog.setOnClickListener {
//            dialog.dismiss()
//            dialogCallbacks.onConfirmDialog()
//        }
    }


    interface OkDialogCallbacks {
        fun onConfirmDialog()
    }

    interface OnPermissionGrantCallback {
        fun onPermissionGranted()
        fun onPermissionError(permission: String?)
    }

    interface BuildVersionCallback {
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        fun buildVersion14()

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun buildVersion13()

        @RequiresApi(Build.VERSION_CODES.Q)
        fun buildVersion10AndAbove()
        fun buildVersionBelow10()
    }

    private fun checkVersion(callback: BuildVersionCallback) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            callback.buildVersion14()
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            callback.buildVersion13()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            callback.buildVersion10AndAbove()
        } else {
            callback.buildVersionBelow10()
        }
    }

    fun permissionForCameraOrStorage(): Array<String> {
        checkVersion(object : BuildVersionCallback {
            override fun buildVersion14() {
                permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }

            override fun buildVersion13() {
                permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            override fun buildVersion10AndAbove() {
                permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            }

            override fun buildVersionBelow10() {
                permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        })
        return permissions
    }
}
