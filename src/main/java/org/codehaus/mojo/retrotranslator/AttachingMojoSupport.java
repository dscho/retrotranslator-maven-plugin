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

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Support for mojos which attach.
 *
 * @noinspection UnusedDeclaration
 */
public abstract class AttachingMojoSupport
    extends RetrotranslateMojoSupport
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    /**
     * Where to put the translated artifact.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * The base-name of the generated artifact.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    protected String baseName;

    /**
     * Flag to enable/disable attaching retrotranslated artifacts.
     *
     * @parameter expression="${attach}" default-value="true"
     */
    protected boolean attach;

    /**
     * The classifier used when attaching the retrotranslated project artifact.
     *
     * @parameter expression="${classifier}" default-value="jdk14"
     */
    protected String classifier;
}
