/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

/**
 * Holds raw image data together with its MIME type.
 *
 * @author SWAD Team
 */
public final class Image {

  private final byte[] bytes;
  private final ImageType type;

  /**
   * Creates a new {@code Image}.
   *
   * @param bytes the raw image bytes.
   * @param type the MIME type of the image.
   */
  public Image(final byte[] bytes, final ImageType type) {
    this.bytes = bytes;
    this.type = type;
  }

  /** Returns the raw image bytes. */
  public byte[] getBytes() {
    return bytes;
  }

  /** Returns the MIME type of the image. */
  public ImageType getType() {
    return type;
  }
}
