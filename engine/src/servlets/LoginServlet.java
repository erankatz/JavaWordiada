package servlets;

import com.google.gson.Gson;
import users.LoginStatus;
import users.User;
import users.Users;
import users.LoginManager;
import users.SessionUtils;

/**
 * Created by eran on 07/08/17.
 */

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet
{
    private LoginManager loginManager = LoginManager.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        switch (action)
        {
            case "login":
                loginAction(request,response);
                break;
            case "logout":
                logoutAction(request,response);
                break;
            case "status":
                statusAction(request,response);
                break;
            case "users":
                usersAction(request,response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        switch (action)
        {
            case "close":
                closeAction(request, response);
                break;
        }
    }

    private void closeAction(HttpServletRequest request, HttpServletResponse response)
    {
        if (SessionUtils.hasSession(request))
        {
            String userName = SessionUtils.getUsername(request.getSession(),request);
            loginManager.removeUser(SessionUtils.getUsername(request.getSession(),request));
            SessionUtils.logoutUser(request.getSession());
            if (loginManager.isUserInGame(userName))
            {
                loginManager.userLeaveGame(userName);
            }

        }
    }

    private void statusAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        Gson gson = new Gson();
        PrintWriter out = response.getWriter();

        if (SessionUtils.hasSession(request) && SessionUtils.isLoggedIn(request.getSession()))
        {
            String userName = SessionUtils.getUsername(request.getSession(false),request);
            boolean isComputer = SessionUtils.isComputer(request.getSession(false));
            User user = loginManager.getUser(userName);
            out.println(gson.toJson(new LoginStatus(true, null, userName, isComputer, user.getInGameNumber())));
        }
        else
        {
            out.println(gson.toJson(new LoginStatus(false)));
        }
    }

    private void logoutAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        Gson gson = new Gson();
        PrintWriter out = response.getWriter();

        if (SessionUtils.hasSession(request) && SessionUtils.isLoggedIn(request.getSession()))
        {
            loginManager.removeUser(SessionUtils.getUsername(request.getSession(false),request));
            SessionUtils.logoutUser(request.getSession());
            out.println(gson.toJson(new LoginStatus(false)));
        }
        else
        {
            out.println(gson.toJson(new LoginStatus(false, "User is allready logged out..")));
        }
    }

    private void loginAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        String userName = request.getParameter("userName");
        String isComputerString = request.getParameter("isComputer");
        boolean isComputer = Objects.equals(isComputerString, "true");

        Gson gson = new Gson();
        PrintWriter out = response.getWriter();

        if (SessionUtils.hasSession(request) && SessionUtils.isLoggedIn(request.getSession()))
        {
            out.println(gson.toJson(new LoginStatus(false, "User is allready logged in.")));
        }
        else if(userName == null || userName.isEmpty())
        {
            out.println(gson.toJson(new LoginStatus(false, "User name is empty.")));
        }
        else if (!loginManager.isNameValid(userName))
        {
            out.println(gson.toJson(new LoginStatus(false, "User name is allready taken.")));
        }
        else
        {
            User newUser = new User(userName, isComputer);
            loginManager.addUser(newUser);
            SessionUtils.loginUser(request.getSession(true), userName, isComputer);
            out.println(gson.toJson(new LoginStatus(true, null, userName, isComputer, -1)));
        }
    }

    private void usersAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        Gson gson = new Gson();
        PrintWriter out = response.getWriter();

        if (SessionUtils.hasSession(request) && SessionUtils.isLoggedIn(request.getSession()))
        {
            out.println(gson.toJson(new Users(loginManager.getLoggedInUsers())));
        }
        else
        {
            out.println(gson.toJson(new LoginStatus(false, "User is not logged in.")));
        }
    }
}
