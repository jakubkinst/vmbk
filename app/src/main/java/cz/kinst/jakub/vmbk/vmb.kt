package cz.kinst.jakub.vmbk
//
import android.app.Activity
import android.arch.lifecycle.*
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
//
//
//// ViewModelBinding extension functions
//inline fun <reified VM : ViewModel, B : ViewDataBinding> FragmentActivity.vmb(@LayoutRes layoutResId: Int, viewModelProvider: ViewModelProvider? = null)
//        = ViewModelBinding<VM, B>(this, VM::class.java, layoutResId, viewModelProvider)
//
//inline fun <reified VM : ViewModel, B : ViewDataBinding> Fragment.vmb(@LayoutRes layoutResId: Int, f: (Bundle) -> VM)
//        = ViewModelBinding<VM, B>(this, VM::class.java, layoutResId, viewModel { f(arguments) })
//
//
//
//inline fun <reified T: ViewModel> Fragment.viewModel(crossinline f: () -> T): T {
//    return ViewModelProviders.of(this, factory(f)).get(T::class.java)
//}
//

//
//
//
//// ViewModelBinding class itself
//class ViewModelBinding<VM : ViewModel, B : ViewDataBinding> constructor(
//        private val lifecycleOwner: LifecycleOwner,
//        val viewModelClass: Class<VM>,
//        @LayoutRes val layoutResId: Int,
//        var viewModelProvider: ViewModelProvider?
//) {
//    init {
//        if (!(lifecycleOwner is FragmentActivity || lifecycleOwner is Fragment))
//            throw IllegalArgumentException("Provided LifecycleOwner must be one of FragmentActivity or Fragment")
//    }
//
//    val binding: B by lazy {
//        initializeVmb()
//        DataBindingUtil.inflate<B>(activity.layoutInflater, layoutResId, null, false)
//    }
//
//    val viewModel: VM by lazy {
//        initializeVmb()
//        viewModelProvider!!.get(viewModelClass)
//    }
//    val rootView by lazy { binding.root }
//
//    val fragment: Fragment? = if (lifecycleOwner is Fragment) lifecycleOwner else null
//    val activity: FragmentActivity by lazy {
//        if (lifecycleOwner is FragmentActivity) lifecycleOwner else (lifecycleOwner as Fragment).activity
//    }
//
//    private var initialized = false
//
//    init {
//        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
//            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//            fun onCreate() {
//                // setup binding variables
//                binding.setVariable(BR.viewModel, viewModel)
//                binding.setVariable(BR.view, fragment ?: activity)
//
//                if (viewModel is LifecycleReceiver)
//                    (viewModel as LifecycleReceiver).onLifecycleReady(lifecycleOwner)
//
//                if (lifecycleOwner is Activity)
//                    activity.setContentView(binding.root)
//            }
//        })
//    }
//
//    fun initializeVmb() {
//        if (initialized) return
//        if (viewModelProvider == null)
//            viewModelProvider = if (fragment != null) ViewModelProviders.of(fragment) else ViewModelProviders.of(activity)
//        initialized = true
//    }
//}
//
interface LifecycleReceiver {
    fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {}
}

// extension functions connecting LiveData with ObservableField
fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observableField: ObservableField<T>) {
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer { observableField.set(it) })
}

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner): ObservableField<T> {
    val observableField = ObservableField<T>()
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer { observableField.set(it) })
    return observableField
}

fun <T> ObservableField<T>.observe(observer: (T) -> Unit) {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            observer(this@observe.get())
        }
    })
}
//
//
// single-event LiveData
class SingleLiveData<T> : MutableLiveData<T>() {
    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (hasActiveObservers()) {
            Log.w("SingleLiveData", "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer {
            if (pending) {
                pending = false
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(t: T?) {
        pending = true
        super.setValue(t)
    }

}