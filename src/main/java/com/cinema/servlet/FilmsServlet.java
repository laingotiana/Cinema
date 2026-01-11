package com.cinema.servlet;

import com.cinema.dao.FilmDAO;
import com.cinema.model.Film;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de test pour afficher la liste des films
 * URL: /films
 */
@WebServlet(name = "FilmsServlet", urlPatterns = {"/films"})
public class FilmsServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(FilmsServlet.class);
    private FilmDAO filmDAO;
    private Gson gson;
    
    @Override
    public void init() {
        filmDAO = new FilmDAO();
        gson = new Gson();
        logger.info("FilmsServlet initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Récupérer tous les films
            List<Film> films = filmDAO.findAll();
            
            // Si le client demande du JSON
            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("application/json")) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(films));
            } else {
                // Sinon, transférer vers JSP
                request.setAttribute("films", films);
                request.getRequestDispatcher("/WEB-INF/views/films.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des films", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur de base de données");
        }
    }
}
