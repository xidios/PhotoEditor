package com.example.photoeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.example.photoeditor.fragments.EffectsFragment
import com.example.photoeditor.fragments.ImageFragment
import com.example.photoeditor.fragments.SaveFragment
import kotlinx.android.synthetic.main.activity_current_image.*

class CurrentImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_image)
        val imageFragmente = ImageFragment()
        val effectsFragment = EffectsFragment()
        val saveFragment = SaveFragment()

        makeCurrentFragment(imageFragmente)
        imageView1.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.imageBtn -> makeCurrentFragment(imageFragmente)
                R.id.effectsBtn -> makeCurrentFragment(effectsFragment)
                R.id.saveBtn -> makeCurrentFragment(saveFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}