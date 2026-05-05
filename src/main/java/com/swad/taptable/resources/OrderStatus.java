/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.swad.taptable.exception.NotValidOrderException;

public enum OrderStatus {
  PENDING,
  COMPLETED,
  CANCELLED;

  public static OrderStatus fromString(String value) throws NotValidOrderException {
    try {
      return OrderStatus.valueOf(value);
    } catch (IllegalArgumentException e) {
      throw new NotValidOrderException("Invalid status: " + value);
    }
  }
}
