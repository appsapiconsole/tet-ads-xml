package com.monetization.core.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.monetization.core.R
import com.monetization.core.commons.NativeConstants.makeVisible
import com.monetization.core.databinding.SdkLoadingDialogBinding

class SdkDialogs(
    private val context: Activity
) {
    private var loadingDialog: Dialog? = null

    fun showLoadingDialog(
        message: String = "Loading Ad...",
        textColor: Int = R.color.black,
        progressColor: Int = R.color.black,
        bgColor: Int = android.R.color.transparent,
        showProgress: Boolean = true
    ) {
        val colorOfBg = ContextCompat.getColor(context, bgColor)
        val colorOfText = ContextCompat.getColor(context, textColor)
        val colorOfProgress = ContextCompat.getColor(context, progressColor)
        if (loadingDialog?.isShowing == true) {
            hideLoadingDialog()
        }
        loadingDialog = Dialog(context).apply {
            val binding = SdkLoadingDialogBinding.inflate(LayoutInflater.from(context), null, false)
            setContentView(binding.root)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            binding.progressText.makeVisible(message.isNotBlank())
            if (message.isNotBlank()) {
                binding.progressText.text = message
                binding.progressText.setTextColor(colorOfText)
            }
            binding.progressBar.makeVisible(showProgress)
            binding.rootLayout.setBackgroundColor(colorOfBg)
            binding.progressBar.indeterminateTintList = ColorStateList.valueOf(colorOfProgress)
            show()
        }
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}