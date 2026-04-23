/**
 * Provides the Jakarta Servlet classes that act as the entry point for HTTP requests.
 *
 * <p>
 * This package contains {@link com.swad.taptable.servlet.AbstractDatabaseServlet}, a base servlet
 * that manages access to the HikariCP connection pool, and
 * {@link com.swad.taptable.servlet.RestDispatcherServlet}, which routes incoming REST requests to
 * the appropriate {@link com.swad.taptable.rest.RestResource} handler based on the request URI and
 * method.
 * </p>
 *
 * @author SWAD Team
 */
package com.swad.taptable.servlet;