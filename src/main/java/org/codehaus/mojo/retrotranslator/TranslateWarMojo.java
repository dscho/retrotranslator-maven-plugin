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

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;

import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.archiver.war.WarArchiver;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * Retrotranslates the classes in the war, as well as all jars in WEB-INF/lib.
 * Creates a new war with the specified classifier with these retrotranslations.
 * 
 * @goal translate-war
 * @phase package
 *
 * @noinspection UnusedDeclaration
 */
public class TranslateWarMojo
    extends AttachingMojoSupport
{
    //
    // TODO: Maybe use a FileSet here instead... ?
    //
    
    /**
     * A set of jar files to include in the translation.  Note: any basedir will
     * be ignored and reset to WEB-INF/lib
     *
     * @parameter
     */
    private DirectoryScanner jarfileset;

    /**
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#war}"
     * @required
     * @readonly
     */
    private WarArchiver warArchiver;
    
    private File transformedWarDir;
    
    protected void doExecute() throws Exception {
        if (!"war".equals(project.getPackaging())) {
            log.debug("Not executing on non-WAR project");
            return;
        }
        
        // Create a copy of the exploded war directory - we will perform translation on this directory
        File warDir = new File(outputDirectory, project.getBuild().getFinalName());
        if (!warDir.exists() || !warDir.isDirectory()) {
            throw new MojoExecutionException("Invalid WAR build directory: " + warDir);
        }
        transformedWarDir = new File(outputDirectory, baseName + "-" + classifier);
        FileUtils.copyDirectoryStructure(warDir, transformedWarDir);

        // Do the actual translation
        super.doExecute();

        // Create the transformed war file
        File outWar = new File(outputDirectory, baseName + "-" + classifier + ".war");

        MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
        archive.setAddMavenDescriptor(true);

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(warArchiver);
        archiver.setOutputFile(outWar);
        warArchiver.addDirectory(transformedWarDir);
        warArchiver.setWebxml(new File(transformedWarDir, "WEB-INF/web.xml"));
        archiver.createArchive(project, archive);
        
        // if attach specified, attach the artifact
        if (attach) {
            projectHelper.attachArtifact(project, "war", classifier, outWar);
        }
    }

    protected void configureRetrotranslator(final Retrotranslator retrotranslator) throws Exception {
        // add the classes directory
        retrotranslator.addSrcdir(new File(transformedWarDir, "WEB-INF/classes"));

        // if no jarfileset specified, create a default one...including all jar files
        if (jarfileset == null) {
            jarfileset = new DirectoryScanner();
            jarfileset.setIncludes(new String[] { "*.jar" });
        }
        
        // setup the basedir for the jarfileset
        jarfileset.setBasedir(new File(transformedWarDir, "WEB-INF/lib"));
        jarfileset.scan();

        String[] jarFiles = jarfileset.getIncludedFiles();

        // add all of the jars to translator
        for (int i = 0; i < jarFiles.length; i++) {
            retrotranslator.addSrcjar(new File(jarfileset.getBasedir(), jarFiles[i]));
        }
    }
}
