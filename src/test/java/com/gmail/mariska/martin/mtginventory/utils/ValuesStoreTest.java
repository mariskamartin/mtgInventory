package com.gmail.mariska.martin.mtginventory.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by mar on 9. 4. 2015.
 */
public class ValuesStoreTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testStoreProperties() throws IOException {
        ValuesStore vs = new ValuesStore();
        vs.put("key1", "value1");
        assertEquals("value1", vs.get("key1"));
    }

    @Test
    public void testStoreSaveLoadProperties() throws IOException {
        File tempFile = testFolder.newFile("ValueStore.data");

        ValuesStore vs = new ValuesStore();
        vs.put("key1", "value1");
        vs.put("key2", 3.5);
        vs.put("key3", new Date(10001000));
        vs.save(tempFile);

        ValuesStore vs2 = new ValuesStore();
        vs2.load(tempFile);

        assertEquals("value1", vs2.get("key1"));
        assertEquals(3.5, vs2.get("key2"));
        assertEquals(new Date(10001000), vs2.get("key3"));
    }
}