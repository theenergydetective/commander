package com.ted.commander.server.config;

import org.springframework.boot.context.embedded.ServletContextInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

//@Configuration
public class SessionTrackingConfigListener implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
//        SessionCookieConfig sessionCookieConfig = servletContext
//                .getSessionCookieConfig();
//        sessionCookieConfig.setHttpOnly(true);
//        sessionCookieConfig.setSecure(true);
//        sessionCookieConfig.setName("JSESSIONID");
//        Set<SessionTrackingMode> stmSet = new HashSet<SessionTrackingMode>();
//        stmSet.add(SessionTrackingMode.COOKIE);
//        servletContext.setSessionTrackingModes(stmSet);
    }

}
