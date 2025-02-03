package com.example.adsxml.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adsxml.databinding.FragmentFirstBinding
import com.monetization.adsmain.commons.sdkNativeAdd
import com.monetization.core.ui.LayoutInfo
import com.remote.firebaseconfigs.RemoteCommons.toConfigString

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.let { binding ->
            binding.adFrame.sdkNativeAdd(
                adLayout = LayoutInfo.LayoutByName("small_native_ad_main"),
                adKey = "Native",
                placementKey = "FirstFragment",
                showNewAdEveryTime = true,
                lifecycle = lifecycle,
                activity = requireActivity()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}