package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import cz.kinst.jakub.vmbk.R
import cz.kinst.jakub.vmbk.SingleLiveData
import cz.kinst.jakub.vmbk.ViewModelBinding
import cz.kinst.jakub.vmbk.databinding.ActivityMainBinding
import cz.kinst.jakub.vmbk.vmb


// example of Base classes
abstract class BaseActivity<VM : BaseViewModel, B : ViewDataBinding> : AppCompatActivity() {
    abstract val vmb: ViewModelBinding<VM, B>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmb.viewModel.displayMessage.observe(this, Observer {
            Snackbar.make(vmb.rootView, it!!, Snackbar.LENGTH_SHORT).show()
        })
    }
}

abstract class BaseViewModel : ViewModel() {
    val displayMessage = SingleLiveData<String>()
}

class MyGreatActivity : BaseActivity<MyGreatViewModel, ActivityMainBinding>() {
    override val vmb = vmb<MyGreatViewModel, ActivityMainBinding>(R.layout.activity_main)
}

class MyGreatViewModel : BaseViewModel() {
    fun showMessage() {
        displayMessage.value = "Test Message!"
    }
}