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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Support for Mojo implementations.
 *
 * @version $Rev$ $Date$
 */
public abstract class MojoSupport
    extends AbstractMojo
{
    /**
     * Instance logger.  This is initialized to the value of {@link #getLog}
     * on execution.
     */
    protected Log log;

    /**
     * Initializes logging.  Called by {@link #execute}.
     */
    protected void init() {
        log = getLog();
    }

    /**
     * Main Mojo execution hook.  Sub-class should use {@link #doExecute} instead.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

        try {
            doExecute();
        }
        catch (Exception e) {
            //
            // NOTE: Wrap to avoid truncating the stacktrace
            //
            if (e instanceof MojoExecutionException) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            else if (e instanceof MojoFailureException) {
                MojoFailureException x = new MojoFailureException(e.getMessage());
                x.initCause(e);
                throw x;
            }
            else {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    /**
     * Sub-class should override to provide custom execution logic.
     *
     * @throws Exception
     */
    protected void doExecute() throws Exception {
        // Empty
    }
}
