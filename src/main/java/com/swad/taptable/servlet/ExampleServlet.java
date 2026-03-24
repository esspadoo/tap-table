package com.swad.taptable.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

// TODO: This is just an example servlet to show how to set up a servlet and map it to a URL. We can
// delete this later and replace it with our actual servlets (e.g. LoginServlet, RegisterServlet,
// etc.)
// N.B. We can also use servlet-mapping in web.xml instead of @WebServlet annotation -> to be
// evaluated

/**
 * @author SWAD Team
 */
@WebServlet("/example")
public class ExampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // TODO
    }
}
