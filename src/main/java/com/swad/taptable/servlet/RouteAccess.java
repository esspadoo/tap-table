/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

/** Declares the minimum access level required for a route. */
enum RouteAccess {
  /** No authentication required. */
  PUBLIC,
  /** Any user with a valid JWT. */
  AUTHENTICATED,
  /** Users with role STAFF or ADMIN. */
  STAFF_OR_ADMIN,
  /** Users with role ADMIN only. */
  ADMIN_ONLY
}
