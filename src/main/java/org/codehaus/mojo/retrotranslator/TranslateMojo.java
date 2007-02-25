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
     * @parameter
     */
    private File destdir;

    /**
     * The JAR file to place translated classes.
     * 
     * @parameter
     */
    private File destjar;

    /**
     * A set of source files to include in the translation.
     *
     * @parameter
     */
    private DirectoryScanner fileset;
    
    /**
     * A set of jar files to include in the translation.
     *
     * @parameter
     */
    private DirectoryScanner jarfileset;

    /**
     * The wildcard pattern specifying files that should be translated (either bytecode 
     * or UTF-8 text), e.g. "*.class;*.tld". There are three special characters: "*?;".
     * 
     * @parameter default-value="*.class"
     */
    private String srcmask;

    protected void configureRetrotranslator(final Retrotranslator retrotranslator) throws Exception {
        assert retrotranslator != null;

        if (fileset != null) {
            fileset.addDefaultExcludes();
            fileset.scan();

            String[] filenames = fileset.getIncludedFiles();
            if (filenames.length != 0) {
                List includes = Arrays.asList(filenames);
                retrotranslator.addSourceFiles(fileset.getBasedir(), includes);
            }
        }

        if (jarfileset != null) {
            jarfileset.addDefaultExcludes();
            jarfileset.scan();

            String[] filenames = jarfileset.getIncludedFiles();
            for (int i=0; i<filenames.length; i++) {
                File file = new File(jarfileset.getBasedir(), filenames[i]);
                retrotranslator.addSrcjar(file);
            }
        }

        if (destdir != null) {
            FileUtils.forceMkdir(destdir);
            retrotranslator.setDestdir(destdir);
        }
        if (destjar != null) {
            retrotranslator.setDestjar(destjar);
        }
        if (srcmask != null) {
            retrotranslator.setSrcmask(srcmask);
        }
    }
}
