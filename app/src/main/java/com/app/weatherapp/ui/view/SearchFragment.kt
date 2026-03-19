package com.app.weatherapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.remote.RetrofitInstance
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.databinding.FragmentSearchBinding
import com.app.weatherapp.ui.adapter.SearchCityAdapter
import com.app.weatherapp.ui.viewmodel.WeatherViewModel
import com.app.weatherapp.ui.viewmodel.WeatherViewModelFactory
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchCityAdapter
    private lateinit var preferenceStore: UserPreferenceStore
    private var searchJob: Job? = null

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(RetrofitInstance.api),
            AppDatabase.getDatabase(requireContext()).notificationDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore = UserPreferenceStore(requireContext())

        adapter = SearchCityAdapter { city ->
            viewLifecycleOwner.lifecycleScope.launch {
                preferenceStore.saveLocationQuery(city.name)
                Toast.makeText(requireContext(), "Đã chọn ${city.name}", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.rvSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResult.adapter = adapter

        binding.etSearch.doAfterTextChanged { editable ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(350)
                weatherViewModel.searchCity(BuildConfig.WEATHER_API_KEY, editable?.toString().orEmpty())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.searchState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBarSearch.visibility = View.VISIBLE
                        binding.tvSearchHint.text = "Đang tìm kiếm..."
                    }

                    is Resource.Success -> {
                        binding.progressBarSearch.visibility = View.GONE
                        val data = state.data.orEmpty()
                        adapter.submitList(data)
                        binding.tvSearchHint.text = if (data.isEmpty()) {
                            "Không có kết quả"
                        } else {
                            "Chạm vào thành phố để chọn"
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBarSearch.visibility = View.GONE
                        binding.tvSearchHint.text = state.message ?: "Lỗi tìm kiếm"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}