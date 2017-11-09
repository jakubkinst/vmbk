package cz.kinst.jakub.vmbk

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun <T> Call<T>.then(callback: (Response<T>?, error: Throwable?) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response, null)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            callback(null, t)
        }
    })
}

fun <T> Call<T>.liveData(): LiveData<T> =
        object : LiveData<T>(), Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                value = response.body()
            }

            override fun onFailure(call: Call<T>?, t: Throwable) {
                t.printStackTrace()
            }

            override fun onActive() {
                this@liveData.enqueue(this)
            }

            override fun onInactive() {
                this@liveData.cancel()
            }
        }
