package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of a customer order.
 *
 * @author SWAD Team
 */
public class Order extends AbstractResource {
  private final Integer id;
  private final OrderStatus status;
  private final Float totalPrice;
  private final Integer userId;
  private final Integer promotionId;
  private final List<OrderDish> dishes;

  /**
   * Creates a new {@code Order} from the values collected by the builder.
   *
   * @param builder the builder containing the order data.
   */
  private Order(Builder builder) {
    this.id = builder.id;
    this.status = builder.status;
    this.totalPrice = builder.totalPrice;
    this.userId = builder.userId;
    this.promotionId = builder.promotionId;
    this.dishes = builder.dishes == null ? null : List.copyOf(builder.dishes);
  }

  /**
   * Returns the order identifier.
   *
   * @return the order identifier.
   */
  public Integer getId() {
    return id;
  }

  /**
   * Returns the current order status.
   *
   * @return the order status.
   */
  public OrderStatus getStatus() {
    return status;
  }

  /**
   * Returns the total price associated with the order.
   *
   * @return the total order price.
   */
  public Float getTotalPrice() {
    return totalPrice;
  }

  /**
   * Returns the identifier of the user who placed the order.
   *
   * @return the user identifier.
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * Returns the identifier of the applied promotion, if any.
   *
   * @return the promotion identifier.
   */
  public Integer getPromotionId() {
    return promotionId;
  }

  /**
   * Returns the dishes included in the order.
   *
   * @return the ordered dishes.
   */
  public List<OrderDish> getDishes() {
    return dishes;
  }

  /**
   * Parses an {@code Order} from a JSON payload.
   *
   * @param inputStream the servlet input stream containing the JSON payload.
   * @return the parsed order.
   * @throws IOException if an error occurs while reading the stream.
   * @throws UnexpectedKeyException if the payload contains unsupported fields.
   */
  public static Order fromJSON(ServletInputStream inputStream)
      throws IOException, UnexpectedKeyException {
    Integer id = null;
    OrderStatus status = null;
    Float totalPrice = null;
    Integer userId = null;
    Integer promotionId = null;
    List<OrderDish> dishes = null;

    final JsonParser jp = JSON_FACTORY.createParser(inputStream);

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
        continue;
      }

      switch (jp.currentName()) {
        case "id":
          jp.nextToken();
          id = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
          break;
        case "status":
          jp.nextToken();
          status = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null
              : OrderStatus.fromString(jp.getText());
          break;
        case "total_price":
          jp.nextToken();
          totalPrice = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getFloatValue();
          break;
        case "user_id":
          jp.nextToken();
          userId = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
          break;
        case "promotion_id":
          jp.nextToken();
          promotionId = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
          break;
        case "dishes":
          if (jp.nextToken() == JsonToken.VALUE_NULL)
            break;

          dishes = new ArrayList<>();
          while (jp.nextToken() != JsonToken.END_ARRAY) {
            Integer dishId = null;
            Integer quantity = null;
            Boolean isLiked = null;
            while (jp.nextToken() != JsonToken.END_OBJECT) {
              if (jp.getCurrentToken() != JsonToken.FIELD_NAME)
                continue;
              switch (jp.currentName()) {
                case "dish_id":
                  jp.nextToken();
                  dishId = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
                  break;
                case "quantity":
                  jp.nextToken();
                  quantity = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
                  break;
                case "is_liked":
                  jp.nextToken();
                  isLiked =
                      jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getBooleanValue();
                  break;
                default:
                  throw new UnexpectedKeyException(
                      String.format("Unexpected key '%s' in dish JSON", jp.currentName()));
              }
            }
            dishes.add(new OrderDish(dishId, quantity, isLiked));
          }
          break;
        default:
          throw new UnexpectedKeyException(
              String.format("Unexpected key '%s' in Order JSON", jp.currentName()));
      }
    }

    return new Order.Builder().id(id).status(status).totalPrice(totalPrice).userId(userId)
        .promotionId(promotionId).dishes(dishes).build();
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    if (id == null)
      jg.writeNullField("id");
    else
      jg.writeNumberField("id", id);

    if (status == null)
      jg.writeNullField("status");
    else
      jg.writeStringField("status", status.name());

    if (totalPrice == null)
      jg.writeNullField("total_price");
    else
      jg.writeNumberField("total_price", totalPrice);

    if (userId == null)
      jg.writeNullField("user_id");
    else
      jg.writeNumberField("user_id", userId);

    if (promotionId == null)
      jg.writeNullField("promotion_id");
    else
      jg.writeNumberField("promotion_id", promotionId);

    if (dishes == null) {
      jg.writeNullField("dishes");
    } else {
      jg.writeArrayFieldStart("dishes");
      for (final OrderDish dish : dishes) {
        jg.writeStartObject();
        if (dish.getDishId() == null)
          jg.writeNullField("dish_id");
        else
          jg.writeNumberField("dish_id", dish.getDishId());
        if (dish.getQuantity() == null)
          jg.writeNullField("quantity");
        else
          jg.writeNumberField("quantity", dish.getQuantity());
        if (dish.isLiked() == null)
          jg.writeNullField("is_liked");
        else
          jg.writeBooleanField("is_liked", dish.isLiked());
        jg.writeEndObject();
      }

      jg.writeEndArray();
    }

    jg.writeEndObject();

    jg.flush();
  }

  /**
   * Builder used to assemble immutable {@link Order} instances.
   */
  public static class Builder {
    private Integer id;
    private OrderStatus status;
    private Float totalPrice;
    private Integer userId;
    private Integer promotionId;
    private List<OrderDish> dishes = new ArrayList<>();

    /**
     * Sets the order identifier.
     *
     * @param id the order identifier.
     * @return this builder.
     */
    public Builder id(Integer id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the order status.
     *
     * @param status the order status.
     * @return this builder.
     */
    public Builder status(OrderStatus status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the total order price.
     *
     * @param totalPrice the total order price.
     * @return this builder.
     */
    public Builder totalPrice(Float totalPrice) {
      this.totalPrice = totalPrice;
      return this;
    }

    /**
     * Sets the identifier of the user who placed the order.
     *
     * @param userId the user identifier.
     * @return this builder.
     */
    public Builder userId(Integer userId) {
      this.userId = userId;
      return this;
    }

    /**
     * Sets the identifier of the applied promotion.
     *
     * @param promotionId the promotion identifier.
     * @return this builder.
     */
    public Builder promotionId(Integer promotionId) {
      this.promotionId = promotionId;
      return this;
    }

    /**
     * Sets the ordered dishes.
     *
     * @param dishes the ordered dishes.
     * @return this builder.
     */
    public Builder dishes(List<OrderDish> dishes) {
      this.dishes = dishes;
      return this;
    }

    /**
     * Builds a new {@link Order} instance.
     *
     * @return the built order.
     */
    public Order build() {
      return new Order(this);
    }
  }
}
