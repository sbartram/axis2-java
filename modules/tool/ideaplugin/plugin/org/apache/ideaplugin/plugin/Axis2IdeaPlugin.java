package org.apache.ideaplugin.plugin;

import com.intellij.openapi.components.ApplicationComponent;
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
 * Author: Deepal Jayasinghe
 * Date: Sep 24, 2005
 * Time: 10:18:36 AM
 */
public class Axis2IdeaPlugin implements ApplicationComponent {
    /**
     * Method is called after plugin is already created and configured. Plugin can start to communicate with
     * other plugins only in this method.
     */
    public void initComponent() {

    }

    /**
     * This method is called on plugin disposal.
     */
    public void disposeComponent() {
    }

    /**
     * Returns the name of component
     *
     * @return String representing component name. Use PluginName.ComponentName notation
     *  to avoid conflicts.
     */
    public String getComponentName() {
        return "ActionsSample.ActionsPlugin";
    }
}
