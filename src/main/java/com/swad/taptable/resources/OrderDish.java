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

  /**
   * Creates a new ordered dish entry.
   *
   * @param dishId the identifier of the ordered dish.
   * @param quantity the amount requested for the dish.
   * @param isLiked the feedback flag associated with the dish, if available.
   */
  public OrderDish(final Integer dishId, final Integer quantity, final Boolean isLiked) {
    this.dishId = dishId;
    this.quantity = quantity;
    this.isLiked = isLiked;
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
   *         if no feedback is available.
   */
  public Boolean isLiked() {
    return isLiked;
  }
}
