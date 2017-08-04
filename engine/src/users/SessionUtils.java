package users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by eran on 07/08/17.
 */
public class SessionUtils {
    public static boolean hasSession(HttpServletRequest request)
    {
        return request.getSession(false) != null;
    }

    public static boolean isLoggedIn(HttpSession session)
    {
        return session.getAttribute("userName") != null;
    }

    public static void loginUser(HttpSession session, String userName, boolean isComputer)
    {
        session.setAttribute("userName", userName);
        session.setAttribute("isComputer", isComputer);
    }

    public static void logoutUser(HttpSession session)
    {
        session.invalidate();
    }

    public static String getUsername(HttpSession session,HttpServletRequest request){
        String user = (String) session.getAttribute("userName");
        if (user == null){
            user = request.getParameter("user");
        }
        return user;
    }

    public static boolean isComputer(HttpSession session)
    {
        return (boolean) session.getAttribute("isComputer");
    }
}
