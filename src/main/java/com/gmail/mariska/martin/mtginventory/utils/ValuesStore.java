package com.gmail.mariska.martin.mtginventory.utils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utils for store value pairs
 */
public class ValuesStore {
    private Map<String, Object> values = new ConcurrentHashMap<String, Object>();

    public Object put(String key, Object value) {
        return values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Object get(String key, Object defaultWhneNull) {
        Object o = this.get(key);
        return o != null ? o : defaultWhneNull;
    }

    public void save(File file) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(values);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void load(File file) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            values = (Map<String, Object>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(ois != null) {
                try {
                    ois.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

}
