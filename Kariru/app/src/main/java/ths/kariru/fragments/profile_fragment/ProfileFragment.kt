package ths.kariru.fragments.profile_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ths.kariru.R
import ths.kariru.databinding.FragmentProfileBinding
import ths.kariru.fragments.search_fragment.SearchFragmentViewModel
import ths.kariru.utils.SettingsActivity

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
        binding.viewModel = viewModel

        binding.settingsButton.setOnClickListener {

            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}
