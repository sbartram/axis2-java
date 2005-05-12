/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.wsdl.codegen.extension;

import org.apache.axis.wsdl.codegen.CodeGenConfiguration;


/**
 * @author chathura@opensource.lk
 *
 */
public class PolicyEvaluator implements CodeGenExtention{
	
	CodeGenConfiguration configuration;

	public PolicyEvaluator() {
	}
	
	/**
	 * Go through the WSDL and extract the WS-Policy elements
	 * and map it into a Axis Module. 
	 *
	 */
	public void init( CodeGenConfiguration configuration){
		this.configuration = configuration;		
	}
	
	public void engage(){
		
		
	}
	
}
