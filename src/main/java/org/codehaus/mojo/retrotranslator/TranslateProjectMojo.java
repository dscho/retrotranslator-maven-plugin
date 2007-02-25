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

import net.sf.retrotranslator.transformer.Retrotranslator;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.handler.ArtifactHandler;

/**
 * Retrotranslates the artifact for the current project.
 * 
 * @goal translate-project
 * @phase package
 *
 * @noinspection UnusedDeclaration
 */
public class TranslateProjectMojo
    extends RetrotranslateMojoSupport
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Flag to enable/disable attaching retrotranslated artifacts.
     *
     * @parameter default-value="true"
     */
    private boolean attach;
    
    /**
     * The classifier used when attaching the retrotranslated project artifact.
     *
     * @parameter expression="jdk14"
     */
    private String classifier;

    protected void doExecute() throws Exception {
        // Only execute if the current project looks like its got Java bits in it
        ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
        if (!artifactHandler.getLanguage().equals("java")) {
            log.debug("Not executing on non-Java project");
        }
        else {
            super.doExecute();
        }
    }

    protected void configureRetrotranslator(final Retrotranslator retrotranslator) throws Exception {
        assert retrotranslator != null;

        retrotranslator.addSrcjar(project.getArtifact().getFile());
        
        File destJar = new File(project.getBuild().getDirectory(),
                                project.getArtifactId() + "-" + project.getVersion() + "-" + classifier + ".jar");

        retrotranslator.setDestjar(destJar);

        if (attach) {
            projectHelper.attachArtifact(project, "jar", classifier, destJar);
        }
    }
}
