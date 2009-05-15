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
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAPBody;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.axis2.soap.SOAPHeaderBlock;

public class GroupcStructArrayUtil implements SunRound2ClientUtil {

    public SOAPEnvelope getEchoSoapEnvelope() {

        SOAPFactory omfactory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope reqEnv = omfactory.createSOAPEnvelope();
        reqEnv.declareNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        reqEnv.declareNamespace("http://www.w3.org/2001/XMLSchema", "xsd");
        reqEnv.declareNamespace("http://schemas.xmlsoap.org/soap/encoding/", "SOAP-ENC"); //xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
        reqEnv.declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        reqEnv.declareNamespace("http://soapinterop.org/xsd", "s");
        reqEnv.declareNamespace("http://soapinterop.org/", "m");

        SOAPHeader header = omfactory.createSOAPHeader(reqEnv);
        OMNamespace hns = reqEnv.declareNamespace("http://soapinterop.org/echoheader/", "hns"); //xmlns:m0="http://soapinterop.org/echoheader/
        SOAPHeaderBlock block1 = header.addHeaderBlock("echoMeStringRequest", hns);
        block1.addAttribute("xsi:type", "xsd:string", null);
        block1.addChild(omfactory.createText("string"));
        // header.addChild(headerChild);
        header.addChild(block1);

        SOAPHeaderBlock block2 = header.addHeaderBlock("echoMeStructRequest", hns);
        block2.addAttribute("xsi:type", "s:SOAPStruct", null);

        OMElement h2Val1 = omfactory.createOMElement("varString", null);
        h2Val1.addAttribute("xsi:type", "xsd:string", null);
        h2Val1.addChild(omfactory.createText("string"));

        OMElement h2Val2 = omfactory.createOMElement("varInt", null);
        h2Val2.addAttribute("xsi:type", "xsd:int", null);
        h2Val2.addChild(omfactory.createText("150"));

        OMElement h2Val3 = omfactory.createOMElement("varFloat", null);
        h2Val3.addAttribute("xsi:type", "xsd:float", null);
        h2Val3.addChild(omfactory.createText("456.321"));

        block2.addChild(h2Val1);
        block2.addChild(h2Val2);
        block2.addChild(h2Val3);

        OMElement operation = omfactory.createOMElement("echoStructArray", "http://soapinterop.org/", null);
        SOAPBody body = omfactory.createSOAPBody(reqEnv);
        body.addChild(operation);
        operation.addAttribute("soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", null);

        OMElement part = omfactory.createOMElement("inputStructArray", "", null);
        part.addAttribute("xsi:type", "SOAP-ENC:Array", null);
        part.addAttribute("SOAP-ENC:arrayType", "s:SOAPStruct[3]", null);

        OMElement item0 = omfactory.createOMElement("item0", null);

        OMElement value00 = omfactory.createOMElement("varString", "", null);
        value00.addAttribute("xsi:type", "xsd:string", null);
        value00.addChild(omfactory.createText("strss fdfing1"));
        OMElement value01 = omfactory.createOMElement("varInt", "", null);
        value01.addAttribute("xsi:type", "xsd:int", null);
        value01.addChild(omfactory.createText("25"));
        OMElement value02 = omfactory.createOMElement("varFloat", "", null);
        value02.addAttribute("xsi:type", "xsd:float", null);
        value02.addChild(omfactory.createText("25.23"));

        OMElement item1 = omfactory.createOMElement("item0", null);

        OMElement value10 = omfactory.createOMElement("varString", "", null);
        value10.addAttribute("xsi:type", "xsd:string", null);
        value10.addChild(omfactory.createText("strss fdfing1"));
        OMElement value11 = omfactory.createOMElement("varInt", "", null);
        value11.addAttribute("xsi:type", "xsd:int", null);
        value11.addChild(omfactory.createText("25"));
        OMElement value12 = omfactory.createOMElement("varFloat", "", null);
        value12.addAttribute("xsi:type", "xsd:float", null);
        value12.addChild(omfactory.createText("25.23"));

        OMElement item2 = omfactory.createOMElement("item0", null);

        OMElement value20 = omfactory.createOMElement("varString", "", null);
        value20.addAttribute("xsi:type", "xsd:string", null);
        value20.addChild(omfactory.createText("strss fdfing1"));
        OMElement value21 = omfactory.createOMElement("varInt", "", null);
        value21.addAttribute("xsi:type", "xsd:int", null);
        value21.addChild(omfactory.createText("25"));
        OMElement value22 = omfactory.createOMElement("varFloat", "", null);
        value22.addAttribute("xsi:type", "xsd:float", null);
        value22.addChild(omfactory.createText("25.23"));

        item0.addChild(value00);
        item0.addChild(value01);
        item0.addChild(value02);

        item1.addChild(value10);
        item1.addChild(value11);
        item1.addChild(value12);

        item2.addChild(value20);
        item2.addChild(value21);
        item2.addChild(value22);

        part.addChild(item0);
        part.addChild(item1);
        part.addChild(item2);

        operation.addChild(part);

        //reqEnv.getBody().addChild(method);
        return reqEnv;

    }
}