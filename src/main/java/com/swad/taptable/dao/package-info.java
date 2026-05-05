/**
 * Provides the Data Access Object (DAO) layer for interacting with the database.
 *
 * <p>This package contains the {@link com.swad.taptable.dao.DataAccessObject} interface, which
 * defines the contract for all DAOs, and {@link com.swad.taptable.dao.AbstractDAO}, which provides
 * a consistent infrastructure for connection management, transaction commit/rollback, and exception
 * handling. Concrete DAO subclasses implement {@code doAccess()} to define their specific database
 * logic.
 *
 * @author SWAD Team
 */
package com.swad.taptable.dao;
