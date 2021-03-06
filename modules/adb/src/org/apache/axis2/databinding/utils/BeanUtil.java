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

package org.apache.axis2.databinding.utils;


import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.typemapping.SimpleTypeMapper;
import org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl;
import org.apache.axis2.deployment.util.BeanExcludeInfo;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.java2wsdl.TypeTable;
import org.apache.axis2.engine.ObjectSupplier;
import org.apache.axis2.util.Loader;
import org.apache.axis2.util.StreamWrapper;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class BeanUtil {

    private static int nsCount = 1;


    /**
     * To Serilize Bean object this method is used, this will create an object array using given
     * bean object
     *
     */
    public static XMLStreamReader getPullParser(Object beanObject,
                                                QName beanName,
                                                TypeTable typeTable,
                                                boolean qualified,
                                                boolean processingDocLitBare) {

        Class beanClass = beanObject.getClass();
        List propertyQnameValueList = getPropertyQnameList(beanObject,
                beanClass, beanName, typeTable, qualified, processingDocLitBare);

        ArrayList objectAttributes = new ArrayList();

        if ((typeTable != null)){
            QName qNamefortheType = typeTable.getQNamefortheType(getClassName(beanClass));
            if (qNamefortheType != null){
                objectAttributes.add(new QName(Constants.XSI_NAMESPACE, "type", "xsi"));
                objectAttributes.add(qNamefortheType);
            }
        }

        return new ADBXMLStreamReaderImpl(beanName, propertyQnameValueList.toArray(), objectAttributes.toArray(),
                typeTable, qualified);

    }

    private static String getClassName(Class type) {
        String name = type.getName();
        if (name.indexOf("$") > 0) {
            name = name.replace('$', '_');
        }
        return name;
    }

    private static List getPropertyQnameList(Object beanObject,
                                             Class beanClass,
                                             QName beanName,
                                             TypeTable typeTable,
                                             boolean qualified,
                                             boolean processingDocLitBare) {
        List propertyQnameValueList;
        Class supperClass = beanClass.getSuperclass();

        if (!getQualifiedName(supperClass.getPackage()).startsWith("java.")) {
            propertyQnameValueList = getPropertyQnameList(beanObject,
                    supperClass, beanName, typeTable, qualified, processingDocLitBare);
        } else {
            propertyQnameValueList = new ArrayList();
        }

        try {
            QName elemntNameSpace = null;
            if (typeTable != null && qualified) {
                QName qNamefortheType = typeTable.getQNamefortheType(beanClass.getName());
                if (qNamefortheType == null) {
                    qNamefortheType = typeTable.getQNamefortheType(beanClass.getPackage().getName());
                }
                if (qNamefortheType == null) {
                    throw new AxisFault("Mapping qname not fond for the package: " +
                            beanObject.getClass().getPackage().getName());
                }

                elemntNameSpace = new QName(qNamefortheType.getNamespaceURI(), "elementName");
            }
            AxisService axisService = null;
            if (MessageContext.getCurrentMessageContext() != null) {
                axisService = MessageContext.getCurrentMessageContext().getAxisService();
            }

            BeanExcludeInfo beanExcludeInfo = null;
            if (axisService != null && axisService.getExcludeInfo() != null) {
                beanExcludeInfo = axisService.getExcludeInfo().getBeanExcludeInfoForClass(beanClass.getName());
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, beanClass.getSuperclass());
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            PropertyDescriptor property;
            for (PropertyDescriptor property1 : properties) {
                property = property1;
                if (!property.getName().equals("class")) {
                    if ((beanExcludeInfo == null) ||
                        !beanExcludeInfo.isExcludedProperty(property.getName())) {
                        Class ptype = property.getPropertyType();
                        if (SimpleTypeMapper.isSimpleType(ptype)) {
                            Method readMethod = property.getReadMethod();
                            Object value;
                            if (readMethod != null) {
                                readMethod.setAccessible(true);
                                value = readMethod.invoke(beanObject, null);
                            } else {
                                throw new AxisFault(
                                        "can not find read method for : " + property.getName());
                            }

                            addTypeQname(elemntNameSpace, propertyQnameValueList, property,
                                         beanName, processingDocLitBare);
                            propertyQnameValueList.add(
                                    value == null ? null : SimpleTypeMapper.getStringValue(value));
                        } else if (ptype.isArray()) {
                            if (SimpleTypeMapper.isSimpleType(ptype.getComponentType())) {
                                Method readMethod = property.getReadMethod();
                                Object value;
                                if (readMethod != null) {
                                    readMethod.setAccessible(true);
                                    value = readMethod.invoke(beanObject, null);
                                } else {
                                    throw new AxisFault(
                                            "can not find read method for : " + property.getName());
                                }
                                if (value != null) {
                                    if ("byte".equals(ptype.getComponentType().getName())) {
                                        addTypeQname(elemntNameSpace, propertyQnameValueList,
                                                     property, beanName, processingDocLitBare);
                                        propertyQnameValueList.add(Base64.encode((byte[])value));
                                    } else {
                                        int i1 = Array.getLength(value);
                                        for (int j = 0; j < i1; j++) {
                                            Object o = Array.get(value, j);
                                            addTypeQname(elemntNameSpace, propertyQnameValueList,
                                                         property, beanName, processingDocLitBare);
                                            propertyQnameValueList.add(o == null ? null :
                                                    SimpleTypeMapper.getStringValue(o));
                                        }
                                    }
                                } else {
                                    addTypeQname(elemntNameSpace, propertyQnameValueList, property,
                                                 beanName, processingDocLitBare);
                                    propertyQnameValueList.add(value);
                                }
                            } else {
                                Method readMethod = property.getReadMethod();
                                Object value[] = null;
                                if (readMethod != null) {
                                    readMethod.setAccessible(true);
                                    value = (Object[])property.getReadMethod().invoke(beanObject,
                                                                                      null);
                                }

                                if (value != null) {
                                    for (Object o : value) {
                                        addTypeQname(elemntNameSpace, propertyQnameValueList,
                                                     property, beanName, processingDocLitBare);
                                        propertyQnameValueList.add(o);
                                    }
                                } else {
                                    addTypeQname(elemntNameSpace, propertyQnameValueList, property,
                                                 beanName, processingDocLitBare);
                                    propertyQnameValueList.add(value);
                                }
                            }
                        } else if (SimpleTypeMapper.isCollection(ptype)) {
                            Method readMethod = property.getReadMethod();
                            readMethod.setAccessible(true);
                            Object value = readMethod.invoke(beanObject,
                                                             null);
                            Collection objList = (Collection)value;
                            if (objList != null && objList.size() > 0) {
                                //this was given error , when the array.size = 0
                                // and if the array contain simple type , then the ADBPullParser asked
                                // PullParser from That simpel type
                                for (Object o : objList) {
                                    if (SimpleTypeMapper.isSimpleType(o)) {
                                        addTypeQname(elemntNameSpace, propertyQnameValueList,
                                                     property, beanName, processingDocLitBare);
                                        propertyQnameValueList.add(o);
                                    } else {
                                        addTypeQname(elemntNameSpace, propertyQnameValueList,
                                                     property, beanName, processingDocLitBare);
                                        propertyQnameValueList.add(o);
                                    }
                                }

                            } else {
                                addTypeQname(elemntNameSpace, propertyQnameValueList, property,
                                             beanName, processingDocLitBare);
                                propertyQnameValueList.add(value);
                            }
                        } else {
                            addTypeQname(elemntNameSpace, propertyQnameValueList, property,
                                         beanName, processingDocLitBare);
                            Method readMethod = property.getReadMethod();
                            readMethod.setAccessible(true);
                            Object value = readMethod.invoke(beanObject,
                                                             null);
                            if ("java.lang.Object".equals(ptype.getName())) {
                                if ((value instanceof Integer) ||
                                    (value instanceof Short) ||
                                    (value instanceof Long) ||
                                    (value instanceof Float)) {
                                    propertyQnameValueList.add(value.toString());
                                    continue;
                                }
                            }

                            propertyQnameValueList.add(value);
                        }
                    }
                }
            }

            return propertyQnameValueList;

        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } catch (java.beans.IntrospectionException e) {
            throw new RuntimeException(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (java.lang.IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addTypeQname(QName elemntNameSpace,
                                     List propertyQnameValueList,
                                     PropertyDescriptor propDesc,
                                     QName beanName,
                                     boolean processingDocLitBare) {
        if (elemntNameSpace != null) {
            propertyQnameValueList.add(new QName(elemntNameSpace.getNamespaceURI(),
                    propDesc.getName(), elemntNameSpace.getPrefix()));
        } else {
            if (processingDocLitBare) {
                propertyQnameValueList.add(new QName(propDesc.getName()));
            } else {
                propertyQnameValueList.add(new QName(beanName.getNamespaceURI(), propDesc.getName(), beanName.getPrefix()));
            }

        }
    }

    /**
     * to get the pull parser for a given bean object , generate the wrpper element using class
     * name
     *
     * @param beanObject
     */
    public static XMLStreamReader getPullParser(Object beanObject) {
        String className = beanObject.getClass().getName();
        if (className.indexOf(".") > 0) {
            className = className.substring(className.lastIndexOf('.') + 1,
                    className.length());
        }
        return getPullParser(beanObject, new QName(className), null, false, false);
    }

    public static Object deserialize(Class beanClass,
                                     OMElement beanElement,
                                     ObjectSupplier objectSupplier,
                                     String arrayLocalName)
            throws AxisFault {
        Object beanObj =null;
        try {
            // Added this block as a fix for issues AXIS2-2055 and AXIS2-1899
            // to support polymorphism in POJO approach.
            // Retrieve the type name of the instance from the 'type' attribute
            // and retrieve the class.
            String instanceTypeName = beanElement.getAttributeValue(new QName("type"));
            if ((instanceTypeName != null) && (! beanClass.isArray())) {
                try {
                    beanClass = Loader.loadClass(beanClass.getClassLoader(), instanceTypeName);
                } catch (ClassNotFoundException ce) {
                    throw AxisFault.makeFault(ce);
                }
            }

            // check for nil attribute:
            QName nilAttName = new QName(Constants.XSI_NAMESPACE, Constants.NIL, "xsi");
            if (beanElement.getAttribute(nilAttName) != null) {
            	return null;
            }
            
            if (beanClass.isArray()) {
                ArrayList valueList = new ArrayList();
                Class arrayClassType = beanClass.getComponentType();
                if ("byte".equals(arrayClassType.getName())) {
                    return Base64.decode(beanElement.getFirstElement().getText());
                } else {
                    Iterator parts = beanElement.getChildElements();
                    OMElement omElement;
                    while (parts.hasNext()) {
                        Object objValue = parts.next();
                        if (objValue instanceof OMElement) {
                            omElement = (OMElement)objValue;
                            if ((arrayLocalName != null) && !arrayLocalName.equals(omElement.getLocalName())) {
                                continue;
                            }
                            // this is a multi dimentional array so always inner element is array
                            Object obj = deserialize(arrayClassType,
                                    omElement,
                                    objectSupplier, "array");
                            
                            	valueList.add(obj);
                        }
                    }
                    return ConverterUtil.convertToArray(arrayClassType, valueList);
                }
            } else {
                if (SimpleTypeMapper.isSimpleType(beanClass)) {
                    return SimpleTypeMapper.getSimpleTypeObject(beanClass, beanElement);
                } else if ("java.lang.Object".equals(beanClass.getName())){
                    return beanElement.getFirstOMChild();
                }
                HashMap properties = new HashMap();
                BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                PropertyDescriptor [] propDescs = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor proprty : propDescs) {
                    properties.put(proprty.getName(), proprty);
                }
                Iterator elements = beanElement.getChildren();
                beanObj = objectSupplier.getObject(beanClass);
                while (elements.hasNext()) {
                    // the beanClass could be an abstract one.
                    // so create an instance only if there are elements, in
                    // which case a concrete subclass is available to instantiate.
                    OMElement parts;
                    Object objValue = elements.next();
                    if (objValue instanceof OMElement) {
                        parts = (OMElement)objValue;
                    } else {
                        continue;
                    }
                    OMAttribute attribute = parts.getAttribute(
                            new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi"));

                    // if parts/@href != null then need to find element with id and deserialize.
                    // before that first check whether we already have it in the hashtable
                    String partsLocalName = parts.getLocalName();
                    PropertyDescriptor prty = (PropertyDescriptor)properties.remove(partsLocalName);
                    if (prty != null) {
                        Class parameters = prty.getPropertyType();
                        if (prty.getName().equals("class"))
                            continue;

                        Object partObj;
                        if (attribute != null) {
                            partObj = null;
                        } else {
                            if (SimpleTypeMapper.isSimpleType(parameters)) {
                                partObj = SimpleTypeMapper.getSimpleTypeObject(parameters, parts);
                            }    else if (SimpleTypeMapper.isHashSet(parameters)) {
                                partObj = SimpleTypeMapper.getHashSet((OMElement)
                                        parts.getParent(), prty.getName());
                            } else if (SimpleTypeMapper.isCollection(parameters)) {
                                partObj = SimpleTypeMapper.getArrayList((OMElement)
                                        parts.getParent(), prty.getName());
                            } else if (SimpleTypeMapper.isDataHandler(parameters)){
                                partObj = SimpleTypeMapper.getDataHandler(parts);
                            } else if (parameters.isArray()) {
                                partObj = deserialize(parameters, (OMElement)parts.getParent(),
                                        objectSupplier, prty.getName());
                            } else {
                                partObj = deserialize(parameters, parts, objectSupplier, null);
                            }
                        }
                        Object [] parms = new Object[] { partObj };
                        Method writeMethod = prty.getWriteMethod();
                        if (writeMethod != null) {
                            writeMethod.setAccessible(true);
                            writeMethod.invoke(beanObj, parms);
                        }
                    }
                }
                return beanObj;
            }
        } catch (IllegalAccessException e) {
            throw new AxisFault("IllegalAccessException : " + e);
        } catch (InvocationTargetException e) {
            throw new AxisFault("InvocationTargetException : " + e);
        } catch (IntrospectionException e) {
            throw new AxisFault("IntrospectionException : " + e);
        }


    }

    public static Object deserialize(Class beanClass,
                                     OMElement beanElement,
                                     MultirefHelper helper,
                                     ObjectSupplier objectSupplier) throws AxisFault {
        Object beanObj;
        try {
            HashMap properties = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor [] propDescs = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propDescs.length; i++) {
                PropertyDescriptor proprty = propDescs[i];
                properties.put(proprty.getName(), proprty);
            }

            beanObj = objectSupplier.getObject(beanClass);
            Iterator elements = beanElement.getChildren();
            while (elements.hasNext()) {
                Object child = elements.next();
                OMElement parts;
                if (child instanceof OMElement) {
                    parts = (OMElement)child;
                } else {
                    continue;
                }
                String partsLocalName = parts.getLocalName();
                PropertyDescriptor prty = (PropertyDescriptor)properties.get(
                        partsLocalName.toLowerCase());
                if (prty != null) {
                    Class parameters = prty.getPropertyType();
                    if (prty.getName().equals("class"))
                        continue;
                    Object partObj;
                    OMAttribute attr = MultirefHelper.processRefAtt(parts);
                    if (attr != null) {
                        String refId = MultirefHelper.getAttvalue(attr);
                        partObj = helper.getObject(refId);
                        if (partObj == null) {
                            partObj = helper.processRef(parameters, refId, objectSupplier);
                        }
                    } else {
                        partObj = SimpleTypeMapper.getSimpleTypeObject(parameters, parts);
                        if (partObj == null) {
                            partObj = deserialize(parameters, parts, objectSupplier, null);
                        }
                    }
                    Object [] parms = new Object[] { partObj };
                    Method writeMethod = prty.getWriteMethod();
                    if (writeMethod != null) {
                        writeMethod.setAccessible(true);
                        writeMethod.invoke(beanObj, parms);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new AxisFault("IllegalAccessException : " + e);
        } catch (InvocationTargetException e) {
            throw new AxisFault("InvocationTargetException : " + e);
        } catch (IntrospectionException e) {
            throw new AxisFault("IntrospectionException : " + e);
        }
        return beanObj;
    }


    /**
     * To get JavaObjects from XML elemnt , the element most of the time contains only one element
     * in that case that element will be converted to the JavaType specified by the javaTypes array
     * The algo is as follows, get the childerns of the response element , and if it conatian more
     * than one element then check the retuen type of that element and conver that to corresponding
     * JavaType
     *
     * @param response  OMElement
     * @param javaTypes Array of JavaTypes
     * @return Array of objects
     * @throws AxisFault
     */
    public static Object [] deserialize(OMElement response,
                                        Object [] javaTypes,
                                        ObjectSupplier objectSupplier) throws AxisFault {
        /*
         * Take the number of parameters in the method and , only take that much of child elements
         * from the OMElement , other are ignore , as an example
         * if the method is , foo(String a , int b)
         * and if the OMElemet
         * <foo>
         *  <arg0>Val1</arg0>
         *  <arg1>Val2</arg1>
         *  <arg2>Val3</arg2>
         *
         * only the val1 and Val2 take into account
         */
        int length = javaTypes.length;
        int count = 0;
        Object [] retObjs = new Object[length];

        /*
        * If the body first child contains , then there can not be any other element withot
        * refs , so I can assume if the first child of the body first element has ref then
        * the message has to handle as mutiref message.
        * as an exmple if the body is like below
        * <foo>
        *  <arg0 href="#0"/>
        * </foo>
        *
        * then there can not be any element without refs , meaning following we are not handling
        * <foo>
        *  <arg0 href="#0"/>
        *  <arg1>absbsbs</arg1>
        * </foo>
        */
        Iterator parts = response.getChildren();
        //to handle multirefs
        //have to check the instanceof
        MultirefHelper helper = new MultirefHelper((OMElement)response.getParent());
        //to support array . if the parameter type is array , then all the omelemnts with that paramtre name
        // has to  get and add to the list
        Class classType;
        String currentLocalName;
        while (parts.hasNext() && count < length) {
            Object objValue = parts.next();
            OMElement omElement;
            if (objValue instanceof OMElement) {
                omElement = (OMElement)objValue;
            } else {
                continue;
            }
            currentLocalName = omElement.getLocalName();
            classType = (Class)javaTypes[count];
            omElement = ProcessElement(classType, omElement, helper, parts,
                    currentLocalName, retObjs, count, objectSupplier);
            while (omElement != null) {
                count ++;
                omElement = ProcessElement((Class)javaTypes[count], omElement,
                        helper, parts, omElement.getLocalName(), retObjs, count,
                        objectSupplier);
            }
            count ++;
        }

        // Ensure that we have at least a zero element array
        for (int i = 0; i < length; i++) {
            Class clazz = (Class)javaTypes[i];
            if (retObjs[i] == null && clazz.isArray()) {
                retObjs[i] = Array.newInstance(clazz.getComponentType(), 0);
            }
        }

        helper.clean();
        return retObjs;
    }

    private static OMElement ProcessElement(Class classType, OMElement omElement,
                                            MultirefHelper helper, Iterator parts,
                                            String currentLocalName,
                                            Object[] retObjs,
                                            int count,
                                            ObjectSupplier objectSupplier) throws AxisFault {
        Object objValue;
        if (classType.isArray()) {
            boolean done = true;
            ArrayList valueList = new ArrayList();
            Class arrayClassType = classType.getComponentType();
            if ("byte".equals(arrayClassType.getName())) {
                retObjs[count] =
                        processObject(omElement, arrayClassType, helper, true, objectSupplier);
                return null;
            } else {
                valueList.add(processObject(omElement, arrayClassType, helper, true,
                        objectSupplier));
            }
            while (parts.hasNext()) {
                objValue = parts.next();
                if (objValue instanceof OMElement) {
                    omElement = (OMElement)objValue;
                } else {
                    continue;
                }
                if (!currentLocalName.equals(omElement.getLocalName())) {
                    done = false;
                    break;
                }
                Object o = processObject(omElement, arrayClassType,
                        helper, true, objectSupplier);
                valueList.add(o);
            }
            if(valueList.get(0)==null){
                retObjs[count] = null;
            } else {
                retObjs[count] = ConverterUtil.convertToArray(arrayClassType,
                        valueList);
            }
            if (!done) {
                return omElement;
            }
        } else {
            //handling refs
            retObjs[count] = processObject(omElement, classType, helper, false, objectSupplier);
        }
        return null;
    }

    public static Object processObject(OMElement omElement,
                                       Class classType,
                                       MultirefHelper helper,
                                       boolean isArrayType,
                                       ObjectSupplier objectSupplier) throws AxisFault {
        boolean hasRef = false;
        OMAttribute omatribute = MultirefHelper.processRefAtt(omElement);
        String ref = null;
        if (omatribute != null) {
            hasRef = true;
            ref = MultirefHelper.getAttvalue(omatribute);
        }
        if (OMElement.class.isAssignableFrom(classType)) {
            if (hasRef) {
                OMElement elemnt = helper.getOMElement(ref);
                if (elemnt == null) {
                    return helper.processOMElementRef(ref);
                } else {
                    return elemnt;
                }
            } else
                return omElement;
        } else {
            if (hasRef) {
                if (helper.getObject(ref) != null) {
                    return helper.getObject(ref);
                } else {
                    return helper.processRef(classType, ref, objectSupplier);
                }
            } else {
                OMAttribute attribute = omElement.getAttribute(
                        new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi"));
                if (attribute != null) {
                    return null;
                }
                if (SimpleTypeMapper.isSimpleType(classType)) {
                    if (isArrayType && "byte".equals(classType.getName())) {
                        String value = omElement.getText();
                        return Base64.decode(value);
                    } else {
                        return SimpleTypeMapper.getSimpleTypeObject(classType, omElement);
                    }
                } else if (SimpleTypeMapper.isCollection(classType)) {
                    return SimpleTypeMapper.getArrayList(omElement);
                } else if (SimpleTypeMapper.isDataHandler(classType)) {
                    return SimpleTypeMapper.getDataHandler(omElement);
                } else {
                    return BeanUtil.deserialize(classType, omElement, objectSupplier, null);
                }
            }
        }
    }






    public static OMElement getOMElement(QName opName,
                                         Object [] args,
                                         QName partName,
                                         boolean qualifed,
                                         TypeTable typeTable) {
        ArrayList objects;
        objects = new ArrayList();
        int argCount = 0;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                if (partName == null) {
                    objects.add("item" + argCount);
                } else {
                    objects.add(partName);
                }
                objects.add(arg);
                continue;
            }

            if (arg instanceof Object[]) {
                // at the client side the partname is always null. At client side this means user try to
                // invoke a service with an array argument.
                if (partName == null) {
                    Object array [] = (Object[]) arg;
                    for (int j = 0; j < array.length; j++) {
                        Object o = array[j];
                        if (o == null) {
                            objects.add("item" + argCount);
                            objects.add(o);
                        } else {
                            if (SimpleTypeMapper.isSimpleType(o)) {
                                objects.add("item" + argCount);
                                objects.add(SimpleTypeMapper.getStringValue(o));
                            } else {
                                objects.add(new QName("item" + argCount));
                                if (o instanceof OMElement) {
                                    OMFactory fac = OMAbstractFactory.getOMFactory();
                                    OMElement wrappingElement;
                                    if (partName == null) {
                                        wrappingElement = fac.createOMElement("item" + argCount, null);
                                        wrappingElement.addChild((OMElement) o);
                                    } else {
                                        wrappingElement = fac.createOMElement(partName, null);
                                        wrappingElement.addChild((OMElement) o);
                                    }
                                    objects.add(wrappingElement);
                                } else {
                                    objects.add(o);
                                }
                            }
                        }
                    }
                } else {
                    // this happens at the server side. this means it is an multidimentional array.
                    objects.add(partName);
                    objects.add(arg);
                }

            } else {
                if (SimpleTypeMapper.isSimpleType(arg)) {
                    if (partName == null) {
                        objects.add("arg" + argCount);
                    } else {
                        objects.add(partName);
                    }
                    objects.add(SimpleTypeMapper.getStringValue(arg));
                } else {
                    if (partName == null) {
                        objects.add(new QName("arg" + argCount));
                    } else {
                        objects.add(partName);
                    }
                    if (arg instanceof OMElement) {
                        OMFactory fac = OMAbstractFactory.getOMFactory();
                        OMElement wrappingElement;
                        if (partName == null) {
                            wrappingElement = fac.createOMElement("arg" + argCount, null);
                            wrappingElement.addChild((OMElement)arg);
                        } else {
                            wrappingElement = fac.createOMElement(partName, null);
                            wrappingElement.addChild((OMElement)arg);
                        }
                        objects.add(wrappingElement);
                    } else if (arg instanceof byte[]) {
                        objects.add(Base64.encode((byte[])arg));
                    } else if(SimpleTypeMapper.isDataHandler(arg.getClass())){
                        OMElement resElemt;
                        OMFactory fac = OMAbstractFactory.getOMFactory();
                        OMElement wrappingElement;
                        if (partName == null) {
                            wrappingElement = fac.createOMElement("arg" + argCount, null);
                        } else {
                            wrappingElement = fac.createOMElement(partName, null);
                        }
                        OMText text = fac.createOMText(arg, true);
                        wrappingElement.addChild(text);
                        objects.add(wrappingElement);
                    } else {
                        objects.add(arg);
                    }
                }
            }
            argCount ++;
        }

        XMLStreamReader xr =
                new ADBXMLStreamReaderImpl(opName, objects.toArray(), null, typeTable, qualifed);

        StreamWrapper parser = new StreamWrapper(xr);
        StAXOMBuilder stAXOMBuilder =
                OMXMLBuilderFactory.createStAXOMBuilder(
                        OMAbstractFactory.getSOAP11Factory(), parser);
        return stAXOMBuilder.getDocumentElement();
    }

    /** @deprecated Please use getUniquePrefix */
    public static synchronized String  getUniquePrifix() {
        return getUniquePrefix();
    }

    /**
     * increments the namespace counter and returns a new prefix
     *
     * @return unique prefix
     */
    public static synchronized String getUniquePrefix() {
        if (nsCount > 1000){
            nsCount = 1;
        }
        return "s" + nsCount++;
    }


    private static String getQualifiedName(Package packagez) {
        if (packagez != null) {
            return packagez.getName();
        } else {
            return "";
        }
    }

}
