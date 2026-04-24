/**
 * Provides the REST resource handler infrastructure for processing HTTP requests and responses.
 *
 * <p>
 * This package contains the {@link com.swad.taptable.rest.RestResource} interface, which defines
 * the {@code serve()} contract, and {@link com.swad.taptable.rest.AbstractRR}, which provides
 * shared scaffolding for all concrete REST resource handlers: Accept/Content-Type validation,
 * structured error responses via {@link com.swad.taptable.resources.Message}, logging context
 * management, and a database connection. Concrete subclasses implement {@code doServe()} to define
 * their specific request-handling logic.
 * </p>
 *
 * @author SWAD Team
 */
package com.swad.taptable.rest;
