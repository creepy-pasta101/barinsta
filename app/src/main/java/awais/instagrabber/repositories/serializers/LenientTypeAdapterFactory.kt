package awais.instagrabber.repositories.serializers

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Meta's private API has drifted: the same field is returned as a JSON number, a string or a
 * boolean depending on endpoint and app version (e.g. `pk`, `id`, counts). A single type mismatch
 * makes Gson abort the *whole* response, which surfaces as an empty feed or a profile that will not
 * load. This adapter coerces between the three instead of failing.
 */
class LenientTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val raw = type.rawType
        val numeric = raw == Long::class.java || raw == Long::class.javaPrimitiveType ||
            raw == Int::class.java || raw == Int::class.javaPrimitiveType ||
            raw == Double::class.java || raw == Double::class.javaPrimitiveType ||
            raw == Float::class.java || raw == Float::class.javaPrimitiveType
        val string = raw == String::class.java
        val bool = raw == Boolean::class.java || raw == Boolean::class.javaPrimitiveType
        if (!numeric && !string && !bool) return null

        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) = delegate.write(out, value)

            @Suppress("UNCHECKED_CAST")
            override fun read(reader: JsonReader): T {
                val token = reader.peek()
                if (token == JsonToken.NULL) return delegate.read(reader)
                when {
                    numeric && token == JsonToken.STRING -> {
                        val s = reader.nextString().trim()
                        return when {
                            raw == Long::class.java || raw == Long::class.javaPrimitiveType ->
                                (s.toLongOrNull() ?: s.toDoubleOrNull()?.toLong() ?: 0L) as T
                            raw == Int::class.java || raw == Int::class.javaPrimitiveType ->
                                (s.toIntOrNull() ?: s.toDoubleOrNull()?.toInt() ?: 0) as T
                            raw == Float::class.java || raw == Float::class.javaPrimitiveType ->
                                (s.toFloatOrNull() ?: 0f) as T
                            else -> (s.toDoubleOrNull() ?: 0.0) as T
                        }
                    }
                    bool && token == JsonToken.STRING -> {
                        val s = reader.nextString().trim().lowercase()
                        return (s == "true" || s == "1") as T
                    }
                    string && (token == JsonToken.NUMBER || token == JsonToken.BOOLEAN) ->
                        return reader.nextString() as T
                    else -> return delegate.read(reader)
                }
            }
        }
    }
}
