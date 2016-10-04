package com.bc.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.bc.util.cache.LoginSessionCache;

public class LoginSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent sessionEvent) {
        LoginSessionCache.add(sessionEvent.getSession());
    }

    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        LoginSessionCache.removeById(sessionEvent.getSession().getId());
    }

    
}
