package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.MutableLiveData


class NavigationManager {
    enum class Page(val locationName: String? = null) {
        MY_LOCATION(),
        SAN_FRANCISCO("San Francisco"),
        SYDNEY("Sydney")
    }

    val currentPage = MutableLiveData<Page>()

    fun goTo(page: Page) {
        currentPage.value = page
    }
}