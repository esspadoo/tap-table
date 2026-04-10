package com.swad.taptable.util;

import com.swad.taptable.resources.Message;

/**
 * This class defines the error codes used in the application. Each error code is a string that
 * uniquely identifies a specific error condition. The error codes are used in the {@link Message}
 * class to provide more detailed information about errors that occur during the processing of REST
 * resources.
 * 
 * @author SWAD Team
 * @see Message
 */
public class ErrorCodes {
    /**
     * <pre>
     * Error code for the case when the output media type is not specified in the request.
     * 
     * HTTP status code: 400 Bad Request
     * </pre>
     */
    public static final String OUTPUT_MEDIA_TYPE_NOT_SPECIFIED = "E4A1";

    /**
     * <pre>
     * Error code for the case when the output media type specified in the request is not supported
     * by the server.
     * 
     * HTTP status code: 406 Not Acceptable
     * </pre>
     */
    public static final String UNSUPPORTED_OUTPUT_MEDIA_TYPE = "E4A2";

    /**
     * <pre>
     * Error code for the case when the input media type is not specified in the request.
     * 
     * HTTP status code: 400 Bad Request
     * </pre>
     */
    public static final String INPUT_MEDIA_TYPE_NOT_SPECIFIED = "E4A3";

    /**
     * <pre>
     * Error code for the case when the input media type specified in the request is not supported
     * by the server.
     * 
     * HTTP status code: 415 Unsupported Media Type
     * </pre>
     */
    public static final String UNSUPPORTED_INPUT_MEDIA_TYPE = "E4A4";

    /**
     * <pre>
     * Error code for the case when the requested operation is not supported by the server for the
     * requested resource.
     * 
     * HTTP status code: 405 Method Not Allowed
     * </pre>
     */
    public static final String UNSUPPORTED_OPERATION = "E4A5";

    /**
     * <pre>
     * Error code for the case when the requested resource is not found on the server.
     * 
     * HTTP status code: 404 Not Found
     * </pre>
     */
    public static final String UNKNOWN_RESOURCE_REQUESTED = "E4A6";

    /**
     * <pre>
     * Error code for the case when the URI format of the requested resource is incorrect.
     * 
     * HTTP status code: 400 Bad Request
     * </pre>
     */
    public static final String WRONG_URI_FORMAT = "E5A7";

    /**
     * <pre>
     * Error code for the case when the requested resource is found but the provided representation
     * of the resource is incorrect.
     * 
     * HTTP status code: 400 Bad Request
     * </pre>
     */
    public static final String WRONG_RESOURCE_PROVIDED = "E5A8";

    /**
     * <pre>
     * Error code for the case when an unexpected error occurs while processing the request.
     * 
     * HTTP status code: 500 Internal Server Error
     * </pre>
     * 
     */
    public static final String UNEXPECTED_ERROR = "E5A1";

    /**
     * <pre>
     * Error code for the case when the resource already exists on the server and cannot be created
     * again.
     * 
     * HTTP status code: 409 Conflict
     * </pre>
     */
    public static final String RESOURCE_ALREADY_EXISTS = "E5A2";

    /**
     * <pre>
     * Error code for the case when the requested resource is not found on the server.
     * HTTP status code: 404 Not Found
     * </pre>
     */
    public static final String RESOURCE_NOT_FOUND = "E5A3";

    /**
     * <pre>
     * Error code for the case when the requested resource cannot be deleted because it is still
     * referenced by other resources.
     * 
     * HTTP status code: 409 Conflict
     * </pre>
     */
    public static final String RESOURCE_STILL_REFERENCED = "E5A4";

    /**
     * <pre>
     * Error code for unexpected database errors.
     *
     * HTTP status code: 500 Internal Server Error
     * </pre>
     */
    public static final String UNEXPECTED_DB_ERROR = "E5A5";

    /**
     * Error code for invalid input parameters supplied in the request.
     *
     * HTTP status code: 400 Bad Request
     */
    public static final String INVALID_INPUT_PARAMETER = "E4A7";

    /**
     * Error code for invalid login credentials (wrong email or password).
     *
     * HTTP status code: 401 Unauthorized
     */
    public static final String INVALID_CREDENTIALS = "E4B1";

    /**
     * Error code for a missing Authorization header.
     *
     * HTTP status code: 401 Unauthorized
     */
    public static final String MISSING_AUTH_HEADER = "E4B2";

    /**
     * Error code for an invalid or expired access token.
     *
     * HTTP status code: 401 Unauthorized
     */
    public static final String INVALID_ACCESS_TOKEN = "E4B3";

    /**
     * Error code for an invalid or expired refresh token.
     *
     * HTTP status code: 401 Unauthorized
     */
    public static final String INVALID_REFRESH_TOKEN = "E4B4";
}
