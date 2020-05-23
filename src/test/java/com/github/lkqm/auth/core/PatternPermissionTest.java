package com.github.lkqm.auth.core;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PatternPermissionTest {

    @Test
    public void match() {
        PatternPermission patternPermission = new PatternPermission("/user/update", "post,put");
        assertTrue(patternPermission.match("/user/update", "post"));
        assertTrue(patternPermission.match("/user/update", "put"));
    }
}