package cz.kinst.jakub.vmbk

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(
        defaultValue: T, key: String? = null,
        crossinline getter: SharedPreferences.(String, T) -> T,
        crossinline setter: Editor.(String, T) -> Editor
) =
        object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T =
                    getter(key ?: property.name, defaultValue)!!

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
                    edit().setter(key ?: property.name, value).apply()
        }

fun SharedPreferences.int(def: Int = 0, key: String? = null): ReadWriteProperty<Any, Int> = delegate(def, key, SharedPreferences::getInt, Editor::putInt)

fun SharedPreferences.long(def: Long = 0, key: String? = null): ReadWriteProperty<Any, Long> = delegate(def, key, SharedPreferences::getLong, Editor::putLong)

fun SharedPreferences.float(def: Float = 0f, key: String? = null): ReadWriteProperty<Any, Float> = delegate(def, key, SharedPreferences::getFloat, Editor::putFloat)

fun SharedPreferences.boolean(def: Boolean = false, key: String? = null): ReadWriteProperty<Any, Boolean> = delegate(def, key, SharedPreferences::getBoolean, Editor::putBoolean)

fun SharedPreferences.stringSet(def: Set<String> = emptySet(), key: String? = null): ReadWriteProperty<Any, Set<String>> = delegate(def, key, SharedPreferences::getStringSet, Editor::putStringSet)

fun SharedPreferences.string(def: String = "", key: String? = null): ReadWriteProperty<Any, String> = delegate(def, key, SharedPreferences::getString, Editor::putString)


private fun sharedPrefs(context: Context, name: String?, mode: Int) =
        if (name == null)
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        else
            context.applicationContext.getSharedPreferences(name, mode)


fun AndroidViewModel.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(getApplication(), name, mode)
fun Activity.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
fun Fragment.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(activity, name, mode)
fun Application.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
fun Service.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
