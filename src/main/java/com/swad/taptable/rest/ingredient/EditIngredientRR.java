/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.ingredient;

import com.swad.taptable.dao.ingredient.EditIngredientDAO;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.datatransfer.MimeTypeParseException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * REST resource that handles editing of an existing ingredient.
 *
 * @author SWAD Team
 */
public class EditIngredientRR extends AbstractRR {

  /**
   * Creates the REST resource that updates an ingredient.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public EditIngredientRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.EDIT_INGREDIENT, req, res);
  }

  @Override
  protected boolean checkMethodMediaType(
      final HttpServletRequest req, final HttpServletResponse res) throws IOException {
    final String accept = req.getHeader("Accept");
    final String contentType = req.getHeader("Content-Type");
    final OutputStream out = res.getOutputStream();

    if (accept == null) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Output media type not specified.",
              ErrorCodes.OUTPUT_MEDIA_TYPE_NOT_SPECIFIED,
              "Accept request header missing.");
      m.toJSON(out);
      return false;
    }

    if (!accept.contains(JSON_MEDIA_TYPE) && !accept.contains(ALL_MEDIA_TYPE)) {
      res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      Message m =
          new Message(
              "Unsupported output media type. Resources are represented only in application/json.",
              ErrorCodes.UNSUPPORTED_OUTPUT_MEDIA_TYPE,
              String.format("Requested representation is %s.", accept));
      m.toJSON(out);
      return false;
    }

    if (contentType == null || !contentType.contains(MULTIPART_MEDIA_TYPE)) {
      res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      Message m =
          new Message(
              "Unsupported input media type. This endpoint requires multipart/form-data.",
              ErrorCodes.UNSUPPORTED_INPUT_MEDIA_TYPE,
              String.format("Submitted representation is %s.", contentType));
      m.toJSON(out);
      return false;
    }

    return true;
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Ingredient in = Ingredient.fromMultipart(req);

      if (in.getId() == null
          || in.getName() == null
          || in.getName().isBlank()
          || in.isFrozen() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m =
            new Message(
                "Missing required fields: id, name and is_frozen must be provided.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final Ingredient out = new EditIngredientDAO(in).access().getOutputParam();

      if (out == null) {
        LOGGER.warn("Ingredient with id %d not found for editing.", in.getId());
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m =
            new Message(
                "Ingredient with id " + in.getId() + " not found.",
                ErrorCodes.RESOURCE_NOT_FOUND,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      out.toJSON(res.getOutputStream());

    } catch (final MimeTypeParseException e) {
      LOGGER.error("Unsupported image MIME type while editing ingredient: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      Message m = new Message(e.getMessage(), ErrorCodes.UNSUPPORTED_INPUT_MEDIA_TYPE, null);
      m.toJSON(res.getOutputStream());
    } catch (final NumberFormatException e) {
      LOGGER.error("Invalid numeric field while editing ingredient: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Invalid numeric field value.", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final IllegalArgumentException e) {
      LOGGER.error("Invalid allergen value while editing ingredient: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Invalid allergen value.", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final ServletException e) {
      LOGGER.error(
          "Failed to parse multipart request while editing ingredient: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Failed to parse multipart request.",
              ErrorCodes.WRONG_RESOURCE_PROVIDED,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Database error while editing ingredient.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
