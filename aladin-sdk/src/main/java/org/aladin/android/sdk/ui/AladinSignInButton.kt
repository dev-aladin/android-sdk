package org.aladin.android.sdk.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import org.aladin.android.sdk.R


/**
 * This is a customized Aladin Sign In button. Supports:
 * - android:text - set text on button
 */
class AladinSignInButton : AppCompatButton {
    /**
     * Text that user wants the button to have.
     * If null it shows "Log in"
     */
    private var mText: String? = null

    private var mBackgroundTint: ColorStateList? = null


    /**
     * Constructor
     *
     * @param context Context
     */
    constructor(context: Context) : super(context) {}

    /**
     * Constructor
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet, 0)
    }

    /**
     * Constructor
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     * @param defStyleAttr int
     */
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet, defStyleAttr)
    }

    /**
     * Initialize the process to get custom attributes from xml and set button params.
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    private fun init(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) {
        parseAttributes(context, attributeSet, defStyleAttr)
        setButtonParams()
    }

    /**
     * Parses out the custom attributes.
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    private fun parseAttributes(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) {
        if (attributeSet == null) {
            return
        }

        // Retrieve styled attribute information from the styleable.
        val typedArray = context.theme.obtainStyledAttributes(attributeSet, org.aladin.android.sdk.R.styleable.ButtonStyleable, defStyleAttr, 0)

        try {
            // Get text which user wants to set the button.
            mText = typedArray.getString(org.aladin.android.sdk.R.styleable.ButtonStyleable_android_text)
            mBackgroundTint = typedArray.getColorStateList(org.aladin.android.sdk.R.styleable.ButtonStyleable_backgroundTint)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * Set button parameters.
     */
    private fun setButtonParams() {
        // We need not have only upper case character.
        this.transformationMethod = null
        // Set button text
        setButtonText()
        // Set button text size
        setButtonTextSize()
        // Set button text color
        setButtonTextColor()
        // Set background of button
        setButtonBackground()
    }

    /**
     * Set the text size to standard as mentioned in guidelines.
     */
    private fun setButtonTextSize() {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, BUTTON_TEXT_SIZE)
    }

    /**
     * Check the theme and set background based on theme which is a selector.
     * The selector handles the background color when button is clicked.
     */
    private fun setButtonBackground() {
        /**
         * Set application logo on SDK button
         *
         */
        setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, AppCompatResources.getDrawable(context, R.drawable.img_logo))

        if (mBackgroundTint == null) {
            mBackgroundTint = ContextCompat.getColorStateList(context, R.color.org_aladin_tint)
        }

        ViewCompat.setBackgroundTintList(this, mBackgroundTint)
    }

    /**
     * Check the theme and set text color based on theme.
     */
    private fun setButtonTextColor() {
        val textColor = ContextCompat.getColorStateList(context, R.color.org_aladin_text)
        this.setTextColor(textColor)
    }

    /**
     * If user has set text, that takes priority else use default button text.
     */
    private fun setButtonText() {
        if (mText == null || mText!!.isEmpty()) {
            mText = context.getString(R.string.org_aladin_login)
        }
        this.text = mText
    }

    companion object {
        private val BUTTON_TEXT_SIZE = 14f
    }
}
