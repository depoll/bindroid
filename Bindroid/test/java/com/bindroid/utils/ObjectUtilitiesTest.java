package com.bindroid.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectUtilitiesTest {
    private static class ExplodingEquals {
        @Override public boolean equals(Object obj) { throw new RuntimeException("boom"); }
    }

    @Test
    public void equals_handlesNulls_and_exceptions() {
        assertTrue(ObjectUtilities.equals(null, null));
        assertFalse(ObjectUtilities.equals(null, "x"));
        assertFalse(ObjectUtilities.equals("x", null));
        assertTrue(ObjectUtilities.equals("abc", new String("abc")));
        assertFalse(ObjectUtilities.equals("abc", "def"));
        assertFalse(ObjectUtilities.equals(new ExplodingEquals(), new Object()));
    }
}

