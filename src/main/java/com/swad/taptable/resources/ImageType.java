/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

/**
 * Represents the allowed MIME types for uploaded images, as defined by the {@code IMAGE_TYPE}
 * PostgreSQL enum.
 *
 * @author SWAD Team
 */
public enum ImageType {
  WEBP("image/webp"),
  JPEG("image/jpeg"),
  PNG("image/png");

  private final String mimeType;

  ImageType(final String mimeType) {
    this.mimeType = mimeType;
  }

  /** Returns the MIME type string for this image type (e.g. {@code "image/webp"}). */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Returns the {@code ImageType} matching the given MIME type string.
   *
   * @param mimeType the MIME type string (case-insensitive).
   * @return the matching {@code ImageType}.
   * @throws IllegalArgumentException if {@code mimeType} does not match any allowed type.
   */
  public static ImageType fromMimeType(final String mimeType) {
    if (mimeType == null) return null;
    for (final ImageType t : values()) {
      if (t.mimeType.equalsIgnoreCase(mimeType.trim())) return t;
    }
    throw new IllegalArgumentException(
        String.format(
            "Unknown image MIME type: %s. Accepted: image/webp, image/jpeg, image/png.", mimeType));
  }
}
