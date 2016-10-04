/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

// FIXME: Don't use this. Use the X.509 Cert support

package com.bc.amazon;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.codec.binary.Base64;

public class HmacSecurityHandler extends BasicHandler {
    /*
     * Configuration keys to use in the WSDD.
     */

    /** Use this to specify the AWS Access Key ID */
    public static final String OPTION_AWS_ACCESS_KEY_ID = "awsAccessKeyId";

    /** Use this to specify the AWS Secret Key */
    public static final String OPTION_AWS_SECRETY_KEY   = "awsSecretyKey";

    /** Algorithm used to calculate string hashes */
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";

    /** Namespace for all AWS Security elements */
    private static final String AWS_SECURITY_NS = "http://security.amazonaws.com/doc/2007-01-01/";

    /** Prefix for AWS Security namespace */
    private static final String AWS_SECURITY_NS_PREFIX = "aws";

    private String awsAccessKeyId   = null;
    private String awsSecretKey     = null;
    private SecretKeySpec keySpec   = null;
    private Mac mac                 = null;
    
    /* (non-Javadoc)
     * @see org.apache.axis.handlers.BasicHandler#init()
     */
    
    /**
     * Initializes the handler. Will throw RuntimeException if 
     */
    public void init() {
        super.init();

        this.awsAccessKeyId = (String) getOption(OPTION_AWS_ACCESS_KEY_ID);
        this.awsSecretKey = (String) getOption(OPTION_AWS_SECRETY_KEY);

        if (null == this.awsAccessKeyId
                || null == this.awsSecretKey
                || this.awsAccessKeyId.length() == 0 
                || this.awsSecretKey.length() == 0) 
        {
            throw new RuntimeException("Missing configuration for handler!");
        }

        try {
            byte[] bytes = awsSecretKey.getBytes("UTF-8");
            this.keySpec = new SecretKeySpec(bytes, SIGNATURE_ALGORITHM);
            this.mac = Mac.getInstance(SIGNATURE_ALGORITHM);
            this.mac.init(keySpec);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        
    }

    public void invoke(MessageContext mc) throws AxisFault {
        String actionUri = mc.getSOAPActionURI();
        String tokens[] = actionUri.split("/");
        String action = tokens[tokens.length - 1];

        String timestamp = getTimestamp();
        String signature;
        try {
            signature = this.calculateSignature(action, timestamp);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

    	/* -- UNCOMMENT THIS
        try {
            SOAPMessageContext smc = (SOAPMessageContext)mc;
            SOAPMessage message = smc.getMessage();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            SOAPHeader header = envelope.getHeader();
            header.addNamespaceDeclaration(AWS_SECURITY_NS_PREFIX, AWS_SECURITY_NS);

            Name akidName = envelope.createName("AWSAccessKeyId", AWS_SECURITY_NS_PREFIX, AWS_SECURITY_NS);
            Name tsName = envelope.createName("Timestamp", AWS_SECURITY_NS_PREFIX, AWS_SECURITY_NS);
            Name sigName = envelope.createName("Signature", AWS_SECURITY_NS_PREFIX, AWS_SECURITY_NS);

            SOAPHeaderElement akidElement = header.addHeaderElement(akidName);
            SOAPHeaderElement tsElement = header.addHeaderElement(tsName);
            SOAPHeaderElement sigElement = header.addHeaderElement(sigName);

            akidElement.addTextNode(awsAccessKeyId);
            tsElement.addTextNode(timestamp);
            sigElement.addTextNode(signature);
        } catch (SOAPException e) {
            throw AxisFault.makeFault(e);
        }
            */
    }

    /**
     * Calculates a time stamp from "now" in UTC and returns it in ISO8601 string
     * format. The soap message expires 15 minutes after this time stamp.
     * AWS only supports UTC and it's canonical representation as 'Z' in an
     * ISO8601 time string. E.g.  2008-02-10T23:59:59Z
     * 
     * See http://www.w3.org/TR/xmlschema-2/#dateTime for more details.
     * 
     * @return ISO8601 time stamp string for "now" in UTC.
     */
    private String getTimestamp() {
        Calendar         calendar  = Calendar.getInstance();
        SimpleDateFormat is08601   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        is08601.setTimeZone(TimeZone.getTimeZone("UTC"));
        return is08601.format(calendar.getTime());
    }

    /**
     * Method borrowed from AWS example code at http://developer.amazonwebservices.com/
     * @param  action The single SOAP body element that is the action the
     *                      request is taking.
     * @param  timestamp  The time stamp string as provided in the &lt;aws:Timestamp&gt;
     *                      header element.
     * @return A hash calculated according to AWS security rules to be provided in the
     *         &lt;aws:signature&gt; header element.
     * @throws Exception If there were errors or missing, required classes when
     *                   trying to calculate the hash.
     */
    private String calculateSignature(String action, String timestamp) throws Exception {
        String toSign = (action + timestamp);

        byte[] sigBytes = mac.doFinal(toSign.getBytes());
        return new String(Base64.encodeBase64(sigBytes));
    }

    /**
     * Keep serialization happy. 
     */
    private static final long serialVersionUID = -5427862350845747488L;

}
