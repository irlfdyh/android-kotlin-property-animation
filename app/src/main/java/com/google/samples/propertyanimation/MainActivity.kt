/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById(R.id.rotateButton)
        translateButton = findViewById(R.id.translateButton)
        scaleButton = findViewById(R.id.scaleButton)
        fadeButton = findViewById(R.id.fadeButton)
        colorizeButton = findViewById(R.id.colorizeButton)
        showerButton = findViewById(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {
        // Create an ObjectAnimator that acts on the target "star". It runs an animation
        // on the ROTATION property of the star.
        val animator =
            ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        // Setup duration of the animation.
        animator.duration = 1000
        animator.disableViewDuringAnimation(rotateButton)
        // Start animation.
        animator.start()
    }

    private fun translater() {
        val animator =
            ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        // Set the numbers of repetitions
        animator.repeatCount = 1
        // Set repeat mode
        animator.repeatMode =ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler() {

        // [PropertyViewHolder] values 4f means the star will scale to 4 times its default size.
        // This object is look similar to the [ObjectAnimator], but this object only hold the
        // property and value information for the animation, not the target.
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

        // Create [ObjectAnimator] object, and use scaleX and scaleY to specify the property
        // or value information.
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            star, scaleX, scaleY
        )

        // Create the animation reverse to make it back to the original size.
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE

        animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    private fun fader() {
        // Use View.ALPHA to denote the amount of opacity in an object.
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.repeatCount = 1
        // Repeat from 0f to 1f again (it's default values of View object)
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    private fun colorizer() {
        val animator =
            ObjectAnimator.ofArgb(
                star.parent, "backgroundColor", Color.BLACK, Color.RED)
        animator.duration = 500
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }

    private fun shower() {
        // Get reference to the star field.
        val container = star.parent as ViewGroup

        // Get width and height of that container.
        val containerW = container.width
        val containerH = container.height

        // Default width and height of the star
        var starW = star.width.toFloat()
        var starH = star.height.toFloat()

        // View to hold the star graphic. Because the star is a [VectorDrawable] asset, use
        // an [AppCompatImageView], which has the ability to host that kind of resource.
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        // Add new star to the container
        container.addView(newStar)

        // Modify the star to have a random size from .1x to 1.6x of its default size
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        // Use the scale factor to change the cached width/height values.
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        // Random beginning position of the star
        newStar.translationX = Math.random().toFloat() * containerW - starW / 2

        // Moving animation to make the star fall
        val mover = ObjectAnimator.ofFloat(
            newStar, View.TRANSLATION_Y, -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)
        // Rotating animation
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION, (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()

        // Play the animation in parallel together.
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        // Random duration between 500 - 2000 millisecond.
        set.duration = (Math.random() * 1500 + 500).toLong()

        // Remove the star
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()

    }

    /**
     * Extension function to handle the recur action from user.
     */
    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

}
