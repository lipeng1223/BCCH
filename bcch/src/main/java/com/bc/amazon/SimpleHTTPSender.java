package com.bc.amazon;

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * SimpleHTTPSender is an HTTP transport handler for the Axis library, providing
 * basic HTTP communication with the server.  This class is needed in order to use
 * MapPoint .NET, because Axis does not yet support digest authentication.  The
 * CommonsHTTPSender almost works, but it currently fails with large POST requests,
 * such as those that are sent when calling GetRouteMap with a very long route.
 * These problems might be fixed in the future, eliminating the need for this class.
 *
 * You must configure Axis to use this class.  The easiest way is to provide a file
 * named "client-config.wsdd" in a directory on the classpath, containing these lines:
 *
 *   <?xml version="1.0" encoding="UTF-8"?>
 *   <deployment name="defaultClientConfig"
 *           xmlns="http://xml.apache.org/axis/wsdd/"
 *           xmlns:java="http://xml.apache.org/axis/wsdd/providers/java">
 *     <transport name="http" pivot="java:org.apache.axis.transport.http.SimpleHTTPSender"/>
 *     <transport name="local" pivot="java:org.apache.axis.transport.local.LocalSender"/>
 *   </deployment>
 */
public class SimpleHTTPSender extends BasicHandler {
    /**
     * Invoke a remote call using an URLConnection.  
     */
    public void invoke(MessageContext messageContext) throws AxisFault {
        try {
            URL url = new URL(messageContext.getStrProp(MessageContext.TRANS_URL));
            URLConnection conn = url.openConnection();
            writeToConnection(conn, messageContext);
            readFromConnection(conn, messageContext);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Write the SOAP request message to an URLConnection.
     */
    private void writeToConnection(URLConnection conn, MessageContext messageContext) throws Exception {
        conn.setDoOutput(true);
        Message request = messageContext.getRequestMessage();
        String contentType = request.getContentType(messageContext.getSOAPConstants());
        conn.setRequestProperty("Content-Type", contentType);
        if (messageContext.useSOAPAction()) {
            conn.setRequestProperty("SOAPAction", messageContext.getSOAPActionURI());
        }
        OutputStream out = new BufferedOutputStream(conn.getOutputStream(), 8192);
        request.writeTo(out);
        out.flush();
    }

    /**
     * Read the SOAP response message from an URLConnection.
     */
    private void readFromConnection(URLConnection conn, MessageContext messageContext) throws Exception {
        String contentType = conn.getContentType();
        String contentLocation = conn.getHeaderField("Content-Location");

        InputStream in = ((HttpURLConnection) conn).getErrorStream();
        if (in == null) {
            in = conn.getInputStream();
        }
        in = new BufferedInputStream(in, 8192);
        Message response = new Message(in, false, contentType, contentLocation);
        response.setMessageType(Message.RESPONSE);
        messageContext.setResponseMessage(response);
    }
}
