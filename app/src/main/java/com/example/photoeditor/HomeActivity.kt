package com.example.photoeditor

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.photoeditor.fragments.EffectsFragment
import com.example.photoeditor.fragments.ImageFragment
import com.example.photoeditor.fragments.SaveFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val image = intent.getParcelableExtra<Parcelable>("Image")
        val imageFragment = ImageFragment.newInstance(image)
        val saveFragment = SaveFragment.newInstance(image)
        val effectsFragment = EffectsFragment.newInstance()

        selectScreen(ImageFragment.TAG, imageFragment)

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

    private fun selectScreen(tag: String, fragment: Fragment) {
        /*supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }*/
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

    private fun findActiveFragment() = supportFragmentManager.fragments.find {it.isVisible}
}