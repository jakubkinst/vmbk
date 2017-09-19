package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cz.kinst.jakub.vmbk.R
import cz.kinst.jakub.vmbk.SingleLiveData
import cz.kinst.jakub.vmbk.databinding.ActivityMainBinding
import cz.kinst.jakub.vmbk.vmb

class MainActivity : AppCompatActivity() {

    val vmb by vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main) { _ -> // bundle optional
        MainViewModel(message = "Testable")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmb.viewModel.navigationManager.currentFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().replace(R.id.container, it).commit()
        })
        vmb.viewModel.displayMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

    }
}


class MainViewModel(val message: String) : ViewModel() {
    val displayMessage = SingleLiveData<String>()
    val navigationManager = NavigationManager()

    fun showChat() {
        displayMessage.value = message
        navigationManager.goToChat()
    }
}