package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v4.app.Fragment


class NavigationManager {
    val currentFragment = MutableLiveData<Fragment>()

    fun goToChat() {
        currentFragment.value = ChatFragment().apply {
            arguments = Bundle().apply {
                putString("key", "value")
            }
        }
    }

    fun goToMain() {
        currentFragment.value = Fragment() // MainFragment, etc.
    }

    // ...
}