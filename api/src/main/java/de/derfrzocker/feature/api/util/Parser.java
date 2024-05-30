package de.derfrzocker.feature.api.util;

import com.google.gson.JsonElement;

public interface Parser<T> {

    JsonElement toJson(T value);

    T fromJson(JsonElement jsonElement);
}
