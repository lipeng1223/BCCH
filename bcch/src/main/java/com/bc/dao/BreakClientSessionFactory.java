package com.bc.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class BreakClientSessionFactory {

    private static SessionFactory sessionFactory = null;

    private BreakClientSessionFactory(){}
    
    public static Session getSession(){
        if (sessionFactory == null)
            sessionFactory = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
        return sessionFactory.openSession();
    }
}
