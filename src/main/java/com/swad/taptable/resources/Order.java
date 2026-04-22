package com.swad.taptable.resources;

import java.io.OutputStream;

public class Order extends AbstractResource {
  private final int id;
  private final OrderStatus status;
  private final float totalPrice;
  private final int userId;
  private final int promotionId;

  public Order(int id, OrderStatus status, float totalPrice, int userId, int promotionId) {
    this.id = id;
    this.status = status;
    this.totalPrice = totalPrice;
    this.userId = userId;
    this.promotionId = promotionId;
  }

  public int getId() {
    return id;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public float getTotalPrice() {
    return totalPrice;
  }

  public int getUserId() {
    return userId;
  }

  public int getPromotionId() {
    return promotionId;
  }

  @Override
  protected void writeJSON(OutputStream out) throws Exception {}
}
