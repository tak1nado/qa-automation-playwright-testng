package com.project.qa.helpers;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ThreadVarsHashMap<E extends Enum<E>> {

    private final InheritableThreadLocal<HashMap<E, Object>> tlHashMapList = new InheritableThreadLocal<>();

    public void put(E key, Object value) {
        if (tlHashMapList.get() == null) tlHashMapList.set(new HashMap<>());
        tlHashMapList.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(E key) {
        if (tlHashMapList.get() == null) return null;
        else return (T) tlHashMapList.get().getOrDefault(key, null);
    }

    public String getString(E key) {
        if (tlHashMapList.get() == null) return null;
        else return tlHashMapList.get().get(key) != null ? tlHashMapList.get().get(key).toString() : null;
    }

    public void clear() {
        if (tlHashMapList.get() != null) tlHashMapList.get().clear();
    }

    @Override
    public String toString() {
        return tlHashMapList.get().entrySet().toString();
    }
}