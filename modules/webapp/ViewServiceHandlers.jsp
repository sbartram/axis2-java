<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisOperation,
                 org.apache.axis2.description.AxisService,
                 org.apache.axis2.engine.Handler,
                 org.apache.axis2.engine.Phase,
                 java.util.ArrayList,
                 java.util.Collection,
                 java.util.HashMap"%>
<%@ page import="java.util.Iterator"%>
  <%
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
*
*
*/

/**
 * Author : Deepal Jayasinghe
 * Date: May 26, 2005
 * Time: 7:14:26 PM
 */
        %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="include/adminheader.jsp"></jsp:include>
<h1>View Operation specific Chains</h1>
   <%
            AxisService axisService = (AxisService)request.getSession().
                    getAttribute(Constants.SERVICE_HANDLERS);
             if(axisService != null ){
                 ArrayList handlers ;
                HashMap operations =  axisService.getOperations();
                Collection ops = operations.values();
                 for (Iterator iterator = ops.iterator(); iterator.hasNext();) {
                     AxisOperation axisOperationon = (AxisOperation) iterator.next();
                     %><h2>Operation Name : <%=axisOperationon.getName().getLocalPart()%></h2><%
                     ArrayList phases = axisOperationon.getRemainingPhasesInFlow();
                 %>
                 <h3> In Flow </h3>
                 <ul>
                 <%
                 for (int i = 0; i < phases.size(); i++) {
                     Phase phase = (Phase) phases.get(i);
                     %>
                     <li>Phase Name :  <%=phase.getPhaseName()%></li>
                     <ul>
                     <%
                      handlers = phase.getHandlers();
                      for (int j = 0; j < handlers.size(); j++) {
                          Handler handler = (Handler) handlers.get(j);
                          %>
                          <li>Handler Name : <%=handler.getHandlerDesc().getName().getLocalPart()%></li>
                          <%
                      }
                     %>
                     </ul>
                 <%
                 }
                 %>
                 </ul>
                 <%
                 phases = axisOperationon.getPhasesInFaultFlow();
                 %>
                 <h3> In Fault Flow </h3>
                 <ul>
                 <%
                 for (int i = 0; i < phases.size(); i++) {
                     Phase phase = (Phase) phases.get(i);
                     %>
                     <li>Phase Name  : <%=phase.getPhaseName()%></li>
                     <ul>
                     <%
                      handlers = phase.getHandlers();
                      for (int j = 0; j < handlers.size(); j++) {
                          Handler handler = (Handler) handlers.get(j);
                          %>
                          <li>Handler Name : <%=handler.getHandlerDesc().getName().getLocalPart()%></li>
                          <%
                      }
                     %>
                     </ul>
                 <%
                 }
                 %>
                 </ul>
                 <%

                 phases = axisOperationon.getPhasesOutFlow();
                 %>
                 <h3> Out Flow </h3>
                 <ul>
                 <%
                 for (int i = 0; i < phases.size(); i++) {
                     Phase phase = (Phase) phases.get(i);
                     %>
                     <li>Phase Name : <%=phase.getPhaseName()%></li>
                     <ul>
                     <%
                      handlers = phase.getHandlers();
                      for (int j = 0; j < handlers.size(); j++) {
                          Handler handler = (Handler) handlers.get(j);
                          %>
                          <li>Handler Name : <%=handler.getHandlerDesc().getName().getLocalPart()%></li>
                          <%
                      }
                     %>
                     </ul>
                 <%
                 }
                 %>
                 </ul>
                 <%
                 phases = axisOperationon.getPhasesOutFaultFlow();
                 %>
                 <h3> Out Fault Flow </h3>
                 <ul>
                 <%
                 for (int i = 0; i < phases.size(); i++) {
                     Phase phase = (Phase) phases.get(i);
                     %>
                     <li>Phase Name : <%=phase.getPhaseName()%></li>
                     <ul>
                     <%
                      handlers = phase.getHandlers();
                      for (int j = 0; j < handlers.size(); j++) {
                          Handler handler = (Handler) handlers.get(j);
                          %>
                          <li>Handler Name : <%=handler.getHandlerDesc().getName().getLocalPart()%></li>
                          <%
                      }
                     %>
                     </ul>
                 <%
                 }
                 %>
                 </ul>
                 <%

             }
                 }

              %>
<jsp:include page="include/adminfooter.jsp"></jsp:include>