package cz.kinst.jakub.vmbk.ui

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import cz.kinst.jakub.vmbk.*
import cz.kinst.jakub.vmbk.databinding.FragmentNowBinding
import cz.kinst.jakub.vmbk.model.Api
import cz.kinst.jakub.vmbk.model.LocationLiveData
import cz.kinst.jakub.vmbk.model.WeatherForecastItem
import cz.kinst.jakub.vmbk.model.WeatherResponse
import me.tatarka.bindingcollectionadapter2.ItemBinding

interface NowView {
    val forecastItemBinding: ItemBinding<WeatherForecastItem>
}

class NowFragment : Fragment(), NowView {
    companion object {
        const val ARG_LOCATION_NAME = "location_name"
        fun newInstance(locationName: String? = null) = NowFragment().apply {
            arguments = Bundle().apply { putString(ARG_LOCATION_NAME, locationName) }
        }
    }

    val vmb = vmb<NowViewModel, FragmentNowBinding>(R.layout.fragment_now, {
        val mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        NowViewModel(activity.application, mainViewModel, arguments.getString(ARG_LOCATION_NAME))
    })

    override val forecastItemBinding = ItemBinding.of<WeatherForecastItem>(BR.item, R.layout.item_weather_forecast)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = vmb.rootView

}

class NowViewModel(app: Application, val mainViewModel: MainViewModel, val locationName: String?) : AndroidViewModel(app), LifecycleReceiver {

    var location = ObservableField<Location>()
    var weather = ObservableField<WeatherResponse?>()
    var forecast = ObservableField<List<WeatherForecastItem>>()

    val locationLiveData = LocationLiveData(getApplication())

    private var lifecycleOwner: LifecycleOwner? = null

    override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        locationLiveData.observe(lifecycleOwner, Observer {
            location.set(it)
            updateWeather()
        })
    }

    init {
        updateWeather()
        mainViewModel.permissionRequest.value = SinglePermissionRequest(Manifest.permission.ACCESS_FINE_LOCATION, {
            locationLiveData.onPermissionsChanged()
        })
    }

    private fun updateWeather() {
        if (locationName != null) {
            Api.WEATHER.getWeatherByPlace(locationName).then { response, error ->
                error?.apply {
                    printStackTrace()
                    mainViewModel.displayMessage.value = "Error fetching Weather data"
                }
                response?.apply {
                    weather.set(body())
                }
            }
            Api.WEATHER.getWeatherForecastByPlace(locationName).then { response, error ->
                error?.apply {
                    printStackTrace()
                    mainViewModel.displayMessage.value = "Error fetching Weather data"
                }
                response?.apply {
                    forecast.set(body()!!.list)
                }
            }
        } else {
            location.get()?.let {
                Api.WEATHER.getWeatherByLocation(location.get()!!.latitude, location.get()!!.longitude).then { response, error ->
                    error?.apply {
                        printStackTrace()
                        mainViewModel.displayMessage.value = "Error fetching Weather data"
                    }
                    response?.apply {
                        weather.set(body())
                    }
                }
                Api.WEATHER.getWeatherForecastByLocation(location.get()!!.latitude, location.get()!!.longitude).then { response, error ->
                    error?.apply {
                        printStackTrace()
                        mainViewModel.displayMessage.value = "Error fetching Weather data"
                    }
                    response?.apply {
                        forecast.set(body()!!.list)
                    }
                }
            }
        }
    }


}