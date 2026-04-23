package com.swad.taptable.resources;

public class OrderDish {
  private final Integer dishId;
  private final Integer quantity;
  private final Boolean isLiked;

  public OrderDish(final Integer dishId, final Integer quantity, final Boolean isLiked) {
    this.dishId = dishId;
    this.quantity = quantity;
    this.isLiked = isLiked;
  }

  public Integer getDishId() {
    return dishId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Boolean isLiked() {
    return isLiked;
  }
}