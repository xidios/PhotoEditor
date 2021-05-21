package com.example.photoeditor

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.photoeditor.fragments.EffectsFragment
import com.example.photoeditor.fragments.ImageFragment
import com.example.photoeditor.fragments.SaveFragment
import com.example.photoeditor.model.Tools
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.fragment_effects.*
import kotlinx.android.synthetic.main.fragment_save.*
import java.util.*

class HomeActivity : AppCompatActivity(R.layout.activity_home) {
    private val REQUEST_ID = 1
    private val RESULT_TAG = "resultImage"

    private val effectsFragmentTarget =
        supportFragmentManager.findFragmentByTag(EffectsFragment.TAG)
    private val saveFragmentTarget = supportFragmentManager.findFragmentByTag(SaveFragment.TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedImage = intent.getParcelableExtra<Parcelable>("Image")
        hiddenImage.setImageURI(receivedImage as Uri)
        val bitmap = (hiddenImage.drawable as BitmapDrawable).bitmap
        val receivedCopy = Tools.saveTempImage(this, bitmap)

        val imageFragment = ImageFragment.newInstance(receivedCopy)
        val saveFragment = SaveFragment.newInstance(receivedCopy)
        val effectsFragment = EffectsFragment.newInstance(receivedCopy)

        firstRun(imageFragment, saveFragment, effectsFragment)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.imageSectionButton -> {
                    selectScreen(ImageFragment.TAG, imageFragment)
                    true
                }
                R.id.effectSectionButton -> {
                    selectScreen(EffectsFragment.TAG, effectsFragment)
                    true
                }
                R.id.saveSectionButton -> {
                    selectScreen(SaveFragment.TAG, saveFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun firstRun(
        imageFragment: ImageFragment,
        saveFragment: SaveFragment,
        effectsFragment: EffectsFragment
    ) {
        supportFragmentManager.commit {
            add(R.id.fragmentContainer, saveFragment, SaveFragment.TAG)
            hide(saveFragment)
            add(R.id.fragmentContainer, effectsFragment, EffectsFragment.TAG)
            hide(effectsFragment)
            selectScreen(ImageFragment.TAG, imageFragment)
        }
    }

    private fun selectScreen(tag: String, fragment: Fragment) {
        supportFragmentManager.commit {
            val active = findActiveFragment()
            val target = supportFragmentManager.findFragmentByTag(tag)

            if (active != null && target != null && active == target) return@commit
            if (active != null) hide(active)
            if (target == null) {
                add(R.id.fragmentContainer, fragment, tag)
            } else {
                show(target)
            }
        }
    }

    private fun findActiveFragment() = supportFragmentManager.fragments.find { it.isVisible }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ID -> {
                    val image = data.getParcelableExtra<Parcelable>(RESULT_TAG)
                    (effectsFragmentTarget as? EffectsFragment)?.rewriteImage(image)
                    (saveFragmentTarget as? SaveFragment)?.rewriteImage(image)
                }
            }
        }
    }
}