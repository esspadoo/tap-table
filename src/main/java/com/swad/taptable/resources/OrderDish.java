/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

/**
 * Represents a dish entry inside an {@link Order}.
 *
 * @author SWAD Team
 */
public class OrderDish {
  private final Integer dishId;
  private final Integer quantity;
  private final Boolean isLiked;
  private final String dishName;

  /**
   * Creates a new ordered dish entry without a resolved dish name.
   *
   * @param dishId the identifier of the ordered dish.
   * @param quantity the amount requested for the dish.
   * @param isLiked the feedback flag associated with the dish, if available.
   */
  public OrderDish(final Integer dishId, final Integer quantity, final Boolean isLiked) {
    this(dishId, quantity, isLiked, null);
  }

  /**
   * Creates a new ordered dish entry with a resolved dish name.
   *
   * @param dishId the identifier of the ordered dish.
   * @param quantity the amount requested for the dish.
   * @param isLiked the feedback flag associated with the dish, if available.
   * @param dishName the human-readable name of the dish.
   */
  public OrderDish(
      final Integer dishId, final Integer quantity, final Boolean isLiked, final String dishName) {
    this.dishId = dishId;
    this.quantity = quantity;
    this.isLiked = isLiked;
    this.dishName = dishName;
  }

  /**
   * Returns the identifier of the ordered dish.
   *
   * @return the dish identifier.
   */
  public Integer getDishId() {
    return dishId;
  }

  /**
   * Returns the ordered quantity.
   *
   * @return the ordered quantity.
   */
  public Integer getQuantity() {
    return quantity;
  }

  /**
   * Returns whether the user liked the dish.
   *
   * @return {@code true} if the dish is marked as liked, {@code false} if disliked, or {@code null}
   *     if no feedback is available.
   */
  public Boolean isLiked() {
    return isLiked;
  }

  /**
   * Returns the human-readable name of the dish, or {@code null} if not resolved.
   *
   * @return the dish name.
   */
  public String getDishName() {
    return dishName;
  }
}
