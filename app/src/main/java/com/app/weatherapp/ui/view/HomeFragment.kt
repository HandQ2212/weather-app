package com.app.weatherapp.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.remote.RetrofitInstance
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.databinding.FragmentHomeBinding
import com.app.weatherapp.R
import com.app.weatherapp.ui.viewmodel.HomeViewModel
import com.app.weatherapp.ui.viewmodel.WeatherViewModelFactory
import com.app.weatherapp.utils.Resource
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceStore: UserPreferenceStore

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            val hasLocationPermission = permissionResult[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissionResult[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (hasLocationPermission) {
                fetchAndSaveCurrentLocation()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    preferenceStore.saveLocationQuery("Hanoi")
                }
            }
        }

    private val homeViewModel: HomeViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(RetrofitInstance.apiService),
            AppDatabase.getDatabase(requireContext()).notificationDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceStore = UserPreferenceStore(requireContext())
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnNotification.setOnClickListener {
            val notificationFragment = NotificationFragment()
            notificationFragment.show(parentFragmentManager, "NotificationBottomSheet")
        }

        binding.btnDown.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        binding.cardWeather.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_forecastFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.weatherState.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let {
                            val iconUrl = it.current.condition.icon
                                .takeIf { raw -> raw.isNotBlank() }
                                ?.let { raw -> if (raw.startsWith("//")) "https:$raw" else raw }

                            binding.tvCity.text = it.location.name
                            binding.tvTemp.text = "${it.current.temp_c.toInt()}°"
                            binding.tvCondition.text = it.current.condition.text
                            binding.tvWindValue.text = "${it.current.wind_kph.toInt()} km/h"
                            binding.tvHumValue.text = "${it.current.humidity}%"
                            binding.tvDate.text = it.location.localtime

                            Glide.with(this@HomeFragment)
                                .load(iconUrl)
                                .placeholder(R.drawable.ic_cloud)
                                .error(R.drawable.ic_cloud)
                                .into(binding.ivMainIcon)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            preferenceStore.locationQueryFlow
                .map { query -> query.ifBlank { "Hanoi" } }
                .distinctUntilChanged()
                .collectLatest { effectiveQuery ->
                    homeViewModel.fetchWeather(BuildConfig.WEATHER_API_KEY, effectiveQuery)
                }
        }

        maybeInitializeLocationFromGps()
    }

    private fun maybeInitializeLocationFromGps() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentSaved = preferenceStore.locationQueryFlow.first()
            if (currentSaved.isNotBlank()) return@launch

            if (hasLocationPermission()) {
                fetchAndSaveCurrentLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val context = requireContext()
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun fetchAndSaveCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (!hasLocationPermission()) return

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        preferenceStore.saveLocationQuery("Hanoi")
                    }
                    return@addOnSuccessListener
                }

                val query = "${location.latitude},${location.longitude}"
                viewLifecycleOwner.lifecycleScope.launch {
                    preferenceStore.saveLocationQuery(query)
                }
            }
            .addOnFailureListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    preferenceStore.saveLocationQuery("Hanoi")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}