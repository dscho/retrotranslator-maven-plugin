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

import java.util.Arrays;
import java.util.List;

import net.sf.retrotranslator.transformer.Retrotranslator;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

/**
 * Retrotranslates jars and classes.
 * 
 * @goal translate
 * @phase process-classes
 *
 * @noinspection UnusedDeclaration,MismatchedReadAndWriteOfArray
 */
public class TranslateMojo
    extends RetrotranslateMojoSupport
{
    /**
     * The directory to place translated classes.
     * 
     * @parameter expression="${destdir}"
     */
    private File destdir;

    /**
     * The JAR file to place translated classes.
     * 
     * @parameter expression="${destjar}"
     */
    private File destjar;

    /**
     * Files to include in the translation.
     *
     * @parameter
     */
    private FileSet[] filesets;

    /**
     * Jar files to include in the translation.
     *
     * @parameter
     */
    private FileSet[] jarfilesets;

    /**
     * Directories to include in the translation.
     *
     * @parameter
     */
    private FileSet[] dirsets;

    protected void configureRetrotranslator(final Retrotranslator retrotranslator) throws Exception {
        assert retrotranslator != null;

        FileSetManager fsm = new FileSetManager(log, log.isDebugEnabled());

        if (filesets != null) {
            for (int i=0; i<filesets.length; i++) {
                File basedir = new File(filesets[i].getDirectory());
                String[] includes = fsm.getIncludedFiles(filesets[i]);

                retrotranslator.addSourceFiles(basedir, Arrays.asList(includes));
            }
        }

        if (jarfilesets != null) {
            for (int i=0; i<jarfilesets.length; i++) {
                File basedir = new File(jarfilesets[i].getDirectory());
                String[] includes = fsm.getIncludedFiles(jarfilesets[i]);

                for (int j=0; j < includes.length; j++) {
                    File file = new File(basedir, includes[j]);
                    retrotranslator.addSrcjar(file);
                }
            }
        }

        if (dirsets != null) {
            for (int i=0; i<dirsets.length; i++) {
                File basedir = new File(dirsets[i].getDirectory());
                String[] includes = fsm.getIncludedDirectories(dirsets[i]);

                for (int j=0; j < includes.length; j++) {
                    File dir = new File(basedir, includes[j]);
                    retrotranslator.addSrcdir(dir);
                }
            }
        }

        if (destdir != null) {
            FileUtils.forceMkdir(destdir);
            retrotranslator.setDestdir(destdir);
        }

        if (destjar != null) {
            retrotranslator.setDestjar(destjar);
        }
    }
}
