package com.swad.taptable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

  @DisplayName("Test that always passes")
  @Test
  void alwaysPass() {
    assertTrue(true);
  }

  @DisplayName("Test that always fails")
  @Test
  void alwaysFail() {
    assertTrue(false, "This test is designed to fail");
  }
}
