package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import cz.kinst.jakub.vmbk.R
import cz.kinst.jakub.vmbk.SingleLiveData
import cz.kinst.jakub.vmbk.databinding.ActivityMainBinding
import cz.kinst.jakub.vmbk.vmb

class MainActivity : AppCompatActivity() {
    val vmb = vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main, { MainViewModel(intent) })

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


class MainViewModel(intent: Intent) : ViewModel() {
    val displayMessage = SingleLiveData<String>()
    val navigationManager = NavigationManager()

    init {
        Log.d("INTENT", intent.toString())
    }

    fun showChat() {
        displayMessage.value = "Switched to Chat."
        navigationManager.goToChat()
    }
}