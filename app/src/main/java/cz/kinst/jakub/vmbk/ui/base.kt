package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.ViewModel
import android.databinding.ViewDataBinding
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import cz.kinst.jakub.vmbk.R
import cz.kinst.jakub.vmbk.ViewModelBinding
import cz.kinst.jakub.vmbk.databinding.ActivityMainBinding
import cz.kinst.jakub.vmbk.vmb


// example of BaseActivity
abstract class BaseActivity<VM : ViewModel, B : ViewDataBinding> : AppCompatActivity() {
    abstract val vmb: ViewModelBinding<VM, B>

    fun showSnackbar(message: String) {
        Snackbar.make(vmb.rootView, message, Snackbar.LENGTH_SHORT).show()
    }
}

class MyGreatActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val vmb = vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main)
}