package cz.kinst.jakub.vmbk.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cz.kinst.jakub.vmbk.*
import cz.kinst.jakub.vmbk.databinding.ActivityMainBinding

interface MainView {
    val pageAdapter: FragmentPagerAdapter
}

class MainActivity : AppCompatActivity(), MainView {

    val prefString by sharedPrefs().string()

    val permissionManager = PermissionManager(this)

    val vmb = vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main, { MainViewModel(application, intent) })

    override val pageAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int) = NowFragment.newInstance(NavigationManager.Page.values()[position].locationName)
        override fun getCount() = NavigationManager.Page.values().size
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmb.viewModel.navigationManager.currentPage.observe(this, Observer {
            vmb.binding.pager.setCurrentItem(it!!.ordinal, true)
        })
        vmb.viewModel.displayMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        vmb.viewModel.permissionRequest.observe(this, Observer {
            permissionManager.requestPermission(it!!)
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onPermissionResult(requestCode, permissions, grantResults)
    }
}


class MainViewModel(app: Application, intent: Intent) : AndroidViewModel(app), LifecycleReceiver {

    val displayMessage = SingleLiveData<String>()
    val navigationManager = NavigationManager()
    val permissionRequest = SingleLiveData<PermissionRequest>()

    init {
        navigationManager.goTo(NavigationManager.Page.MY_LOCATION)
    }

}