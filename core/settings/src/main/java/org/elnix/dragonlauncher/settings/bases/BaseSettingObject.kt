package org.elnix.dragonlauncher.settings.bases

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.resolveDataStore


class BaseSettingObject <T, R> (
    override val key: String,
    val dataStoreName: DataStoreName,
    val default: T,
    private val preferenceKey: Preferences.Key<R>,
    val encode: (T) -> R?,
    val decode: (Any?) -> T
) : AnySettingObject {


    /**
     * Returns the current value of this setting in a type-erased form.
     *
     * This method is part of the type-erased settings API and allows heterogeneous
     * collections of settings to be accessed without knowing their concrete generic
     * type parameters at compile time.
     *
     * Internally, this simply delegates to [get], preserving the original value,
     * but exposes it as [Any?] so it can be used in generic containers such as
     * maps or lists of mixed setting types.
     *
     * @param ctx Android context used to access the underlying data store.
     * @return The current value of this setting, or its default value if none
     *         has been persisted yet.
     */
    override suspend fun getAny(ctx: Context) = get(ctx)

    /**
     * Sets the value of this setting using a type-erased input.
     *
     * This method exists to support bulk operations (such as restore, import,
     * or map-based updates) where the concrete generic type of the setting is
     * not known at compile time.
     *
     * The provided [value] is first cast to the raw representation type [R],
     * then converted into the setting's strongly-typed value using [decode],
     * and finally persisted via [set].
     *
     * @param ctx Android context used to access the underlying data store.
     * @param value The raw, type-erased value to apply to this setting.
     *
     * @throws ClassCastException if [value] is not of the expected raw type [R].
     */
    override suspend fun setAny(ctx: Context, value: Any?) {
        @Suppress("UNCHECKED_CAST")
        set(ctx, value as? T)
    }



    /* ───────────── GETTERS ───────────── */

    /**
     * Get the value one shot for logic, no flow
     *
     * @param ctx
     * @return decoded value of settings type [T]
     */
    suspend fun get(ctx: Context): T {

        val raw = ctx.applicationContext
            .resolveDataStore(dataStoreName)
            .data
            .first()[preferenceKey]

        return raw?.let { decode(it) } ?: default
    }

    /**
     * Get the value one shot for logic, no flow
     * Returns null if the value is not defined (default)
     *
     * @param ctx
     * @return decoded nullable value of settings type [T?]
     */
    suspend fun getNull(ctx: Context): T? {

        val raw = ctx.applicationContext
            .resolveDataStore(dataStoreName)
            .data
            .first()[preferenceKey]

        return raw?.let { decode(it) }
    }


    /**
     * Get the value one shot for logic, no flow
     *
     * @param ctx
     * @return decoded value of settings type [T]
     */
    suspend fun getEncoded(ctx: Context): R? {

        val raw = ctx.applicationContext
            .resolveDataStore(dataStoreName)
            .data
            .first()[preferenceKey]

        // Shitty but should work
        return raw?.let { encode(decode(it)) }
    }


    /**
     * Flow, outputs a flow of the value, for compose
     *
     * @param ctx
     * @return [Flow] of the settings type [T]
     */
    fun flow(ctx: Context): Flow<T> {


        return ctx.applicationContext
            .resolveDataStore(dataStoreName)
            .data
            .map { prefs ->
                val raw = prefs[preferenceKey]
                raw?.let {
                    decode(it)
                } ?: default
            }
    }


    /* ───────────── SETTERS ───────────── */

    /**
     * Set; saves the value in the datastore for persistence
     *
     * @param ctx
     * @param value either the good type or a null, to reset
     */
    suspend fun set(ctx: Context, value: T?) {

        ctx.applicationContext
            .resolveDataStore(dataStoreName).edit {

            if (value != null) {
                val encoded = encode(value)
                encoded?.let { encodedNotNull ->
                    it[preferenceKey] = encodedNotNull
                } ?: it.remove(preferenceKey)
            } else {
                it.remove(preferenceKey)
            }
        }
    }


    /**
     * Reset; removes the value of the [preferenceKey] from the datastore
     *
     * @param ctx
     */
    suspend fun reset(ctx: Context) {
        ctx.resolveDataStore(dataStoreName).edit {
            it.remove(preferenceKey)
        }
    }
}
