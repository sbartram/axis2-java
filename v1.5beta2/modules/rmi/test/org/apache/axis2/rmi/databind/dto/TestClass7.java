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

package org.apache.axis2.rmi.databind.dto;

public class TestClass7 {

    private TestClass4[] param1;
    private TestClass3 param2;

    public TestClass4[] getParam1() {
        return param1;
    }

    public void setParam1(TestClass4[] param1) {
        this.param1 = param1;
    }

    public TestClass3 getParam2() {
        return param2;
    }

    public void setParam2(TestClass3 param2) {
        this.param2 = param2;
    }
}
