package utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper


fun String.parseJson(): Any {
    if (startsWith("{") && endsWith("}")) {
        val typeRef = object : TypeReference<Map<String, Any>>() {}
        return ObjectMapper().readValue(this, typeRef)
    } else {
        val typeRef = object : TypeReference<Array<Any>>() {}
        return ObjectMapper().readValue(this, typeRef)
    }
}