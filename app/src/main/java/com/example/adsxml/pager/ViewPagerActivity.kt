package com.example.adsxml.pager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adsxml.databinding.ActivityViewPagerBinding

class ViewPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPagerBinding
    private lateinit var pager: MyPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pager = MyPager(this)
        binding.pager.apply {
            adapter = pager
        }

    }

    inner class MyPager(val activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                FirstFragment()
            } else {
                SecondFragment()
            }
        }

    }
}