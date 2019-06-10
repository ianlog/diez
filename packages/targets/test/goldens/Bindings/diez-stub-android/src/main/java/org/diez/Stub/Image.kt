package org.diez.stub

import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.Toolbar
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import android.widget.ImageView
import android.widget.TextView

var ImageView.image: Image?
    set(image) {
        if (image == null) {
            return
        }

        if (Environment.isHot) {
            getFromNetwork(image, this, fun(drawable) {
                setImageDrawable(drawable)
            })
            return
        }

        setImageDrawable(image.drawableFromRawResource)
    }
    get() {
        return null
    }

var ImageView.file: File?
    set(file) {
        if (file == null) {
            return
        }

        if (Environment.isHot) {
            Glide.with(this.context).load(file.url).signature(ObjectKey(file.src + System.currentTimeMillis())).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
                    setImageDrawable(drawable)
                }
            })
            return
        }

        setImageBitmap(BitmapFactory.decodeStream(resources.openRawResource(file.resourceId)))
    }
    get() {
        return null
    }

var TextView.leftDrawable: Image?
    set(image) {
        if (image == null) {
            return
        }

        if (Environment.isHot) {
            getFromNetwork(image, this, fun(drawable) {
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            })
            return
        }

        setCompoundDrawablesWithIntrinsicBounds(image.drawableFromRawResource, null, null, null)
    }
    get() {
        return null
    }

var Toolbar.icon: Image?
    set(image) {
        if (image == null) {
            return
        }

        if (Environment.isHot) {
            getFromNetwork(image, this, fun(drawable) {
                navigationIcon = drawable
            })
            return
        }

        navigationIcon = image.drawableFromRawResource
    }
    get() {
        return null
    }

var View.backgroundImage : Image?
    set(image) {
        if (image == null) {
            return
        }

        if (Environment.isHot) {
            val view = this
            getFromNetwork(image, this, fun(drawable) {
                drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                view.background = drawable
            })
            return
        }

        val drawable = image.drawableFromRawResource
        (drawable as BitmapDrawable).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        this.background = drawable
    }
    get() {
        return null
    }

private val effectiveDensity: Int
    get () {
        val density = Environment.resources.displayMetrics.density.toDouble()
        return Math.ceil(density).toInt()
    }

private val Image.correctDensityFile: File
    get () {
        return when (effectiveDensity) {
            1 -> file
            2 -> file2x
            3 -> file3x
            else -> file4x
        }
    }

private val Image.resourceId: Int
    get () {
        return this.correctDensityFile.resourceId
    }

private fun getFromNetwork(image: Image, view: View, callback: (BitmapDrawable) -> Unit) {
    val width = (image.width * Environment.resources.displayMetrics.density.toDouble()).toInt()
    val height = (image.height * Environment.resources.displayMetrics.density.toDouble()).toInt()
    Glide
        .with(view.context)
        .load(image.correctDensityFile.url)
        .override(width, height)
        .signature(ObjectKey(image.file.src + System.currentTimeMillis()))
        .into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            callback(drawable as BitmapDrawable)
        }
    })
}


private val Image.drawableFromRawResource: Drawable?
    get () {
        return ResourcesCompat.getDrawable(
            Environment.resources,
            Environment.resources.getIdentifier(
                file.resourceName,
                "drawable",
                Environment.packageName
            ),
            null
        )
    }

data class Image(
    val file: File,
    val file2x: File,
    val file3x: File,
    val file4x: File,
    val width: Int,
    val height: Int
) {
    companion object {}
}