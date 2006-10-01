/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codehaus.mojo.retrotranslator;

import org.apache.maven.plugin.logging.Log;

import net.sf.retrotranslator.transformer.Level;
import net.sf.retrotranslator.transformer.Message;
import net.sf.retrotranslator.transformer.MessageLogger;

/**
 * Logger bridge from Retrotranslator {@link MessageLogger} to Mojo {@link Log}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class RetrotranslatorLogger
    implements MessageLogger
{
    private Log log;

    public RetrotranslatorLogger(final Log log) {
        this.log = log;
    }
    
    public void log(final Message message) {
        boolean info = message.getLevel().compareTo(Level.INFO) >= 0;
        if (info) {
            log.info(message.toString());
        }
        else {
            log.warn(message.toString());
        }
    }

}
