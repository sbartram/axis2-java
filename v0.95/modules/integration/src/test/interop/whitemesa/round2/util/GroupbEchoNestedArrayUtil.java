/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.interop.whitemesa.round2.util;

import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import test.interop.whitemesa.SunClientUtil;

public class GroupbEchoNestedArrayUtil implements SunClientUtil {

    public SOAPEnvelope getEchoSoapEnvelope() {

        SOAPFactory omfactory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope reqEnv = omfactory.getDefaultEnvelope();

        reqEnv.declareNamespace("http://www.w3.org/2001/XMLSchema", "xsd");

        reqEnv.declareNamespace("http://soapinterop.org/", "tns");
        reqEnv.declareNamespace("http://soapinterop.org/xsd", "s");
        reqEnv.declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        OMNamespace encNs = reqEnv.declareNamespace("http://schemas.xmlsoap.org/soap/encoding/", "SOAP-ENC");
        OMNamespace envNs = reqEnv.declareNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");

        OMElement operation = omfactory.createOMElement("echoNestedArray", "http://soapinterop.org/", null);
        reqEnv.getBody().addChild(operation);
        operation.declareNamespace(envNs);
        operation.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", envNs);

        OMElement part = omfactory.createOMElement("inputStruct", null);
        part.addAttribute("xsi:type", "s:SOAPStruct", null);


        OMElement value0 = omfactory.createOMElement("varString", null);
        value0.addAttribute("xsi:type", "xsd:string", null);
        value0.addChild(omfactory.createText("strss fdfing1"));

        OMElement value1 = omfactory.createOMElement("varInt", null);
        value1.addAttribute("xsi:type", "xsd:int", null);
        value1.addChild(omfactory.createText("25"));

        OMElement value2 = omfactory.createOMElement("varFloat", null);
        value2.addAttribute("xsi:type", "xsd:float", null);
        value2.addChild(omfactory.createText("25.23"));

        OMElement value3 = omfactory.createOMElement("varArray", null);
        part.addAttribute("xsi:type", "s:SOAPArrayStruct", null);
        value3.declareNamespace(encNs);
        value3.addAttribute("arrayType", "xsd:string[3]", encNs);

        OMElement value30 = omfactory.createOMElement("item", null);
        value30.addAttribute("xsi:type", "xsd:string", null);
        value30.addChild(omfactory.createText("strss fdfing1"));

        OMElement value31 = omfactory.createOMElement("item", null);
        value31.addAttribute("xsi:type", "xsd:string", null);
        value31.addChild(omfactory.createText("strss fdfing2"));

        OMElement value32 = omfactory.createOMElement("item", null);
        value32.addAttribute("xsi:type", "xsd:string", null);
        value32.addChild(omfactory.createText("strss fdfing3"));

        value3.addChild(value30);
        value3.addChild(value31);
        value3.addChild(value32);

        part.addChild(value0);
        part.addChild(value1);
        part.addChild(value2);
        part.addChild(value3);

        operation.addChild(part);

        return reqEnv;

    }


}