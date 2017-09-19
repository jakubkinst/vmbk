package cz.kinst.jakub.vmbk

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun <reified VM: ViewModel, B: ViewDataBinding> vmb(layout: Int, noinline f: (Bundle?) -> VM = { _ -> VM::class.java.newInstance() }): ReadOnlyProperty<Any, ViewModelBinding<VM, B>> {
    return object : ReadOnlyProperty<Any, ViewModelBinding<VM, B>> {
        override fun getValue(thisRef: Any, property: KProperty<*>): ViewModelBinding<VM, B> {
            return when (thisRef) {
                is Fragment         -> ViewModelBinding(thisRef, layout, f, VM::class.java)
                is FragmentActivity -> ViewModelBinding(thisRef, layout, f, VM::class.java)
                else -> error("vmb delegate can be used only in Fragment or FragmentActivity")
            }
        }
    }
}

class ViewModelBinding<VM : ViewModel, out B : ViewDataBinding>(
    private val lifecycleOwner: LifecycleOwner,
    private val layout: Int,
    private val vmFactory: (Bundle?) -> VM,
    private val vmClass: Class<VM>) {

    private val activity get() = (lifecycleOwner as? FragmentActivity) ?: fragment?.activity
    private val fragment get() = lifecycleOwner as? Fragment

    val binding: B by lazy {
        DataBindingUtil.inflate<B>(activity?.layoutInflater, layout, null, false)
    }

    val viewModel: VM by lazy {
        fragment?.viewModel { vmFactory(fragment?.arguments) }
            ?: activity?.viewModel { vmFactory(activity?.intent?.extras) }
            ?: error("Something is bad")
    }

    init {
        require(lifecycleOwner is FragmentActivity || lifecycleOwner is Fragment) {
            "lifecycleOwner has to be FragmentActivity or Fragment"
        }

        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                // setup binding variables
                binding.setVariable(BR.viewModel, viewModel)
                binding.setVariable(BR.view, fragment ?: activity)
                (viewModel as? LifecycleReceiver)?.onLifecycleReady(lifecycleOwner)
                (lifecycleOwner as? Activity)?.setContentView(binding.root)
            }
        })
    }

    private fun Fragment.viewModel(f: () -> VM): VM {
        return ViewModelProviders.of(this, factory(f)).get(vmClass)
    }

    private fun FragmentActivity.viewModel(f: () -> VM): VM {
        return ViewModelProviders.of(this, factory(f)).get(vmClass)
    }

    @Suppress("UNCHECKED_CAST")
    fun factory(f: () -> VM) = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>?): T {
            return f() as T
        }
    }

}
