/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.swad.taptable.exception.NotValidOrderException;

/** Represents the lifecycle status of an order. */
public enum OrderStatus {
  PENDING,
  COMPLETED,
  CANCELLED;

  /**
   * Parses a status from its string representation.
   *
   * @param value the string value to parse.
   * @return the matching {@link OrderStatus}.
   * @throws NotValidOrderException if the value does not correspond to any status.
   */
  public static OrderStatus fromString(String value) throws NotValidOrderException {
    try {
      return OrderStatus.valueOf(value);
    } catch (IllegalArgumentException e) {
      throw new NotValidOrderException("Invalid status: " + value);
    }
  }
}
