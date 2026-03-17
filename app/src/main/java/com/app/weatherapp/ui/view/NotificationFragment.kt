package com.app.weatherapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.weatherapp.databinding.FragmentNotificationBinding
import com.app.weatherapp.ui.viewmodel.NotificationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.app.weatherapp.R
import com.app.weatherapp.ui.adapter.NotificationAdapter

class NotificationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, NotificationViewModel.Factory(requireContext()))[NotificationViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notifications.collect { notiList ->
                    if (notiList.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvNew.visibility = View.GONE
                        binding.tvEarlier.visibility = View.GONE
                        binding.rvNotificationsNew.visibility = View.GONE
                        binding.rvNotificationsEarlier.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.tvNew.visibility = View.VISIBLE

                        val newList = listOf(notiList[0])
                        binding.rvNotificationsNew.visibility = View.VISIBLE
                        binding.rvNotificationsNew.adapter =
                            NotificationAdapter(newList, isNewSection = true)

                        if (notiList.size > 1) {
                            val earlierList = notiList.drop(1)
                            binding.tvEarlier.visibility = View.VISIBLE
                            binding.rvNotificationsEarlier.visibility = View.VISIBLE
                            binding.rvNotificationsEarlier.adapter = NotificationAdapter(earlierList, isNewSection = false)
                        } else {
                            binding.tvEarlier.visibility = View.GONE
                            binding.rvNotificationsEarlier.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams

            val behavior = BottomSheetBehavior.from(bottomSheet)
            val screenHeight = resources.displayMetrics.heightPixels

            behavior.peekHeight = (screenHeight * 0.75).toInt()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}