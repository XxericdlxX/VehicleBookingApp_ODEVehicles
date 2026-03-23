package cat.copernic.odecoches.core.ui

import android.content.Context
import androidx.annotation.StringRes

/**
 * Representa un text que es pot mostrar a la interfície.
 *
 * Pot ser un text escrit directament o un text obtingut
 * des dels recursos de l'aplicació.
 */
sealed class UiText {

    /**
     * Text normal escrit directament.
     *
     * @property value Text que es mostrarà a la pantalla.
     */
    data class DynamicString(val value: String) : UiText()

    /**
     * Text obtingut d'un recurs string d'Android.
     *
     * També pot incloure valors per completar el text.
     *
     * @property resId Identificador del recurs de text.
     * @property args Valors que es faran servir per donar format al text.
     */
    data class StringResource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    /**
     * Converteix aquest [UiText] en un text normal.
     *
     * @param context Context necessari per accedir als recursos.
     * @return El text final llest per mostrar.
     */
    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId, *args.toTypedArray())
    }
}