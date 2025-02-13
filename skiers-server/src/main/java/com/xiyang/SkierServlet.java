package com.xiyang;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class SkierServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write("Hello, Skier!");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        // Validate the url parameters
        String urlPath = req.getPathInfo();
        if (urlPath == null || urlPath.equals("/")) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing parameters in URL");
            return;
        }

        // /resortID/seasons/seasonID/days/dayID/skiers/skierID
        String[] urlParts = urlPath.split("/");
        if (urlParts.length != 8) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }

        try {
            int resortId = Integer.parseInt(urlParts[1]);
            String seasonId = urlParts[3];
            String dayId = urlParts[5];
            int skierId = Integer.parseInt(urlParts[7]);

            if (resortId < 1 || resortId > 10) {
                sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid resort ID");
                return;
            }
            if (!seasonId.equals("2025")) {
                sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid season ID");
                return;
            }
            if (!dayId.equals("1")) {
                sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid day ID");
                return;
            }
            if (skierId < 1 || skierId > 100000) {
                sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid skier ID");
                return;
            }

            // Request body
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String requestBody = sb.toString();
            if (!isValidLiftRideJson(requestBody)) {
                sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }

            // response
            res.setContentType("application/json");
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write("{\"message\": \"Successfully created lift ride event\"}");

        } catch (NumberFormatException e) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter");
        }
    }

    private boolean isValidLiftRideJson(String json) {
        return json.contains("time") && json.contains("liftID");
    }

    private void sendError(HttpServletResponse res, int status, String message)
            throws IOException {
        res.setStatus(status);
        res.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}