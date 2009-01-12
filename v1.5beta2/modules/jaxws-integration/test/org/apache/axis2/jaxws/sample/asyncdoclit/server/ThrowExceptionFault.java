/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package org.apache.axis2.jaxws.sample.asyncdoclit.server;

import javax.xml.ws.WebFault;
import org.test.asyncdoclit.ThrowExceptionFaultBean;

@WebFault(name = "throwExceptionFaultBean", targetNamespace = "http://org/test/asyncdoclit")
public class ThrowExceptionFault
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ThrowExceptionFaultBean faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public ThrowExceptionFault(String message, ThrowExceptionFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public ThrowExceptionFault(String message, ThrowExceptionFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.test.asyncdoclit.ThrowExceptionFaultBean
     */
    public ThrowExceptionFaultBean getFaultInfo() {
        return faultInfo;
    }

}
