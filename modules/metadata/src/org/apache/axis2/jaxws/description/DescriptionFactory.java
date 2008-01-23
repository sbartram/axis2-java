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


package org.apache.axis2.jaxws.description;

import org.apache.axis2.description.AxisService;
import org.apache.axis2.jaxws.ClientConfigurationFactory;
import org.apache.axis2.jaxws.description.builder.DescriptionBuilderComposite;
import org.apache.axis2.jaxws.description.impl.DescriptionFactoryImpl;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Creates the JAX-WS metadata description hierarchy from some combinations of WSDL, Java class
 * information including annotations, and (in the future) deployment descriptors.
 */
public class DescriptionFactory {
    /**
     * The type of update being done for a particular Port.  This is used by the JAX-WS service
     * delegate on the CLIENT side. This is used as a parameter to the updateEndpoint factory
     * method.  An EndpointDescription will be returned corresponding to the port. GET_PORT:
     * Return an SEI-based pre-existing port ADD_PORT:   Return a Dispatch-only non-pre-existing
     * port CREATE_DISPATCH: Return a Dispatch port; this is valid on either a pre-existing port
     * (e.g. GET_PORT) or dynamic port (ADD_PORT)
     */
    public static enum UpdateType {
        GET_PORT, ADD_PORT, CREATE_DISPATCH}

    /** A DescrptionFactory can not be instantiated; all methods are static. */
    private DescriptionFactory() {
    }

    /**
     * Create the initial ServiceDescription hierarchy on the CLIENT side.  This is intended to be
     * called when the client creates a ServiceDelegate.  Note that it will only create the
     * ServiceDescription at this point.  The EndpointDescription hierarchy under this
     * ServiceDescription will be created by the updateEndpoint factory method, which will be called
     * by the ServiceDelegate once the port is known (i.e. addPort, getPort, or createDispatch).
     *
     * @param wsdlURL      URL to the WSDL file to use; this may be null
     * @param serviceQName The ServiceQName for this service; may not be null
     * @param serviceClass The Service class; may not be null and must be assignable from
     *                     javax.xml.ws.Service
     * @return A ServiceDescription instance for a CLIENT access to the service.
     * @see #updateEndpoint(ServiceDescription, Class, QName, ServiceDescription.UpdateType)
     */
    public static ServiceDescription createServiceDescription(URL wsdlURL, QName serviceQName,
                                                              Class serviceClass) {
        return DescriptionFactoryImpl.createServiceDescription(wsdlURL, serviceQName, serviceClass);
    }
    
    
    /**
     * Create the initial ServiceDescripton hierarchy on the CLIENT side.  This allows a sparse DBC
     * to be specified in addition to the service class.  The sparse DBC can be used to override
     * the class annotation member values.  
     * 
     * @see #createServiceDescription(URL, QName, Class)
     *  
     * @param wsdlURL
     * @param serviceQName
     * @param serviceClass
     * @param sparseComposite
     * @param sparseCompositeKey
     * @return
     */
    public static ServiceDescription createServiceDescription(URL wsdlURL, 
            QName serviceQName, Class serviceClass, DescriptionBuilderComposite sparseComposite,
            Object sparseCompositeKey) {
        return DescriptionFactoryImpl.createServiceDescription(wsdlURL, serviceQName, 
                                                               serviceClass, sparseComposite,
                                                               sparseCompositeKey);
    }

    /**
     * Retrieve or create the EndpointDescription hierarchy associated with an existing CLIENT side
     * ServiceDescription for a particular port.  If an EndpointDescritption already exists, it will
     * be returned; if one does not already exist, it will be created.  Note that if the SEI is null
     * then the EndpointDescription returned will be for a Dispatch client only and it will not have
     * an EndpointInterfaceDescription hierarchy associated with it.  If, at a later point, the same
     * port is requested and an SEI is provided, the existing EndpointDescription will be updated
     * with a newly-created EndpointInterfaceDescription hierarchy.
     *
     * @param serviceDescription An existing client-side ServiceDescription.  This must not be
     *                           null.
     * @param sei                The ServiceInterface class.  This can be null for adding a port or
     *                           creating a Dispatch; it can not be null when getting a port.
     * @param portQName          The QName of the port.  If this is null, the runtime will attempt
     *                           to to select an appropriate port to use.
     * @param updateType         The type of the update: adding a port, creating a dispatch, or
     *                           getting an SEI-based port.
     * @return An EndpointDescription corresponding to the port.
     * @see #createServiceDescription(URL, QName, Class)
     * @see DescriptionFactory.UpdateType
     */
    public static EndpointDescription updateEndpoint(ServiceDescription serviceDescription,
                                                     Class sei, QName portQName,
                                                     DescriptionFactory.UpdateType updateType) {
        return DescriptionFactoryImpl
                   .updateEndpoint(serviceDescription, sei, portQName, updateType);
    }    
    
    /**
     * Retrieve or create the EndpointDescription hierarchy associated with an existing CLIENT side
     * ServiceDescription for a particular port.  This is identical to above, but this method has a 
     * reference back to the ServiceDelegate (which invoked it) for purposes of properly caching 
     * ServiceDescriptions that contain dynamic ports
     *
     * @param serviceDescription An existing client-side ServiceDescription.  This must not be
     *                           null.
     * @param sei                The ServiceInterface class.  This can be null for adding a port or
     *                           creating a Dispatch; it can not be null when getting a port.
     * @param portQName          The QName of the port.  If this is null, the runtime will attempt
     *                           to to select an appropriate port to use.
     * @param updateType         The type of the update: adding a port, creating a dispatch, or
     *                           getting an SEI-based port.
     * @param serviceDelegateKey A reference back to the ServiceDelegate that called it
     * @return An EndpointDescription corresponding to the port.
     * @see #createServiceDescription(URL, QName, Class)
     * @see DescriptionFactory.UpdateType
     */
    public static EndpointDescription updateEndpoint(ServiceDescription serviceDescription,
                                                     Class sei, 
                                                     QName portQName,
                                                     DescriptionFactory.UpdateType updateType,
                                                     Object serviceDelegateKey) {
        return DescriptionFactoryImpl
                   .updateEndpoint(serviceDescription, sei, portQName, updateType, serviceDelegateKey);
    }    
    
    /**
     * Retrieve or create an EndpointDescription hierarchy associated with an existing CLIENT side
     * ServiceDescription for a particular port.  Additional metadata may be specified in a sparse
     * composite.  That metadata may come from a JSR-109 client deployment descriptor, for example,
     * or from resource injection of an WebServiceRef or other Resource annotation.
     * 
     * @see #updateEndpoint(ServiceDescription, Class, QName, org.apache.axis2.jaxws.description.DescriptionFactory.UpdateType)
     *  
     * @param serviceDescription
     * @param sei
     * @param portQName
     * @param updateType
     * @param composite
     * @return
     */
    public static EndpointDescription updateEndpoint(ServiceDescription serviceDescription,
            Class sei, QName portQName,
            DescriptionFactory.UpdateType updateType,
            DescriptionBuilderComposite composite,
            Object sparseCompositeKey) {
        return DescriptionFactoryImpl
                   .updateEndpoint(serviceDescription, sei, portQName, updateType, composite, sparseCompositeKey);
}

    /**
     * Create a full ServiceDescription hierarchy on the SERVER side for EACH service implementation
     * entry in the DescriptionBuilderComposite (DBC) map.  Note that the associated SERVER side
     * Axis description objects are also created.  To create a single ServiceDescription hierarchy
     * for a single service implementation class, use the factory method that takes a single class
     * and returns a single ServiceDescription.
     * <p/>
     * A service implementation DBC entry is one that: (1) Is a class and not an interface (2)
     * Carries a WebService or WebServiceProvider annotation.
     * <p/>
     * A DBC represents the information found in the service implementation class.  There will be
     * other DBC entries in the map for classes and interfaces associated with the service
     * implementation, such as super classes, super interfaces, fault classes, and such.
     * <p/>
     * Note that map may contain > 1 service implementation DBC.  A full ServiceDescriptionhierarchy
     * will be created for each service implementation DBC entry.
     * <p/>
     * Note that each ServiceDescription will have exactly one EndpointDescription corresponding to
     * each service implementation.
     *
     * @param dbcMap A HashMap keyed on class name with a value for the DBC for that classname
     * @return A List of ServiceDescriptions with the associated SERVER side hierarchy created.
     */
    public static List<ServiceDescription> createServiceDescriptionFromDBCMap(
            HashMap<String, DescriptionBuilderComposite> dbcMap) {
        return DescriptionFactoryImpl.createServiceDescriptionFromDBCMap(dbcMap);
    }

    /**
     * Create a full ServiceDescription hierarchy on the SERVER side for a single service
     * implementation class.  To create process more than one service implementation at one time or
     * to process them without causing the service implementation classes to be loaded, use the
     * factory method that takes a collection of DescriptionBuilderComposite objects and returns a
     * collection of ServiceDescriptions.
     * <p/>
     * Note that the ServiceDescription will have exactly one EndpointDescription corresponding to
     * the service implementation.
     *
     * @param serviceImplClass A Web Service implementation class (i.e. one that carries an
     *                         WebService or WebServiceProvider annotation).
     * @return A ServiceDescription with the associated SERVER side hierarchy created.
     */
    public static ServiceDescription createServiceDescription(Class serviceImplClass) {
        return DescriptionFactoryImpl.createServiceDescription(serviceImplClass);
    }

    /**
     * This provide very convenient way of creating an AxisService from an annotated java class.
     *
     * @param serviceImplClass A Web Service implementation class (i.e. one that carries an
     *                         WebService or WebServiceProvider annotation).
     * @return An AxisService instance
     */
    public static AxisService createAxisService(Class serviceImplClass) {
        ServiceDescription serviceDescription = createServiceDescription(serviceImplClass);
        EndpointDescription[] edArray = serviceDescription.getEndpointDescriptions();
        AxisService axisService = edArray[0].getAxisService();
        return axisService;
    }

    /**
     * DO NOT USE THIS METHOD FOR PRODUCTION CODE.  It has been deprecated and is only used to drive
     * some testing.  Note that the AxisService and associated Axis description objects ARE NOT
     * created or updated by this factory method.
     *
     * @param serviceImplClass A service implementation class with annotations
     * @param axisService      A FULLY POPULATED AxisService including all of the underlying
     *                         description objects such as AxisOperations.
     * @return A ServiceDescription hierarchy constructed (via Java reflection) from the service
     *         implementation class and tied via properties to the existing AxisService object.
     * @deprecated Use {@link #createServiceDescriptionFromDBCMap(HashMap)}
     */
    public static ServiceDescription createServiceDescriptionFromServiceImpl(Class serviceImplClass,
                                                                             AxisService axisService) {
        return DescriptionFactoryImpl
                .createServiceDescriptionFromServiceImpl(serviceImplClass, axisService);
    }

    /**
     * Creates Client ConfigurationFactory used to create AxisConfiguration.
     *
     * @return A Client Configuration Factory's new instance. ClinetConfigurationFactory is
     *         Singleton.
     */
    public static ClientConfigurationFactory createClientConfigurationFactory() {
        return DescriptionFactoryImpl.getClientConfigurationFactory();
    }
}