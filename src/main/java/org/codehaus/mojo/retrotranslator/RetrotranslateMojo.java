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
import java.util.Iterator;
import java.util.List;

import net.sf.retrotranslator.transformer.Retrotranslator;

import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * Goal which turns the bytecode into 1.4 compliant bytecode
 * 
 * @goal translate
 * @phase process-classes
 */
public class RetrotranslateMojo
    extends MojoSupport
{
    /**
     * Project classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List classpathElements;
    
    /**
     * The classpath to use for verification including rt.jar, jce.jar, jsse.jar 
     * (from JRE 1.4).  The retrotranslator-runtime-n.n.n.jar, and backport-util-concurrent.jar
     * are included by default, they are not required to be defined here.
     * 
     * @parameter
     */
    private List verifyClasspath;
    
    /**
     * Directory to place classes compatible with J2SE 1.4.
     * 
     * @parameter
     */
    private File destdir;

    /**
     * JAR file to place classes compatible with J2SE 1.4.
     * 
     * @parameter
     */
    private File destjar;

    /**
     * Asks the translator to strip signature (generics) information.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean stripsign;

    /**
     * Asks the translator for verbose output.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean verbose;

    /**
     * Asks the translator for warnings when references to unknown classes, 
     * methods, or fields are found.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean verify;

    /**
     * Asks the translator to transform only Java 5.0 classes.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean lazy;

    /**
     * Fails build when verification has failed.
     * 
     * @parameter default-value="true"
     * @required
     */
    private boolean failonwarning;

    /**
     * List the jar files and directorties to be included in the translation.
     *
     * @parameter
     * @required
     */
    private Include[] includes;
    
    protected void doExecute() throws Exception {
        Retrotranslator retrotranslator = new Retrotranslator();
        retrotranslator.setLogger(new RetrotranslatorLogger(log));
        
        for (int i=0; i < includes.length; i++) {
            File dir = includes[i].getDirectory();
            String pattern = includes[i].getPattern();

            if (pattern != null) {
                DirectoryScanner scanner = new DirectoryScanner();
                scanner.setBasedir(dir);
                scanner.setIncludes(new String[] { pattern });
                scanner.scan();

                List includedFiles = Arrays.asList(scanner.getIncludedFiles());
                for (Iterator j = includedFiles.iterator(); j.hasNext();) {
                    String name = (String) j.next();
                    File file = new File(dir, name);

                    if (file.isFile()) {
                        retrotranslator.addSrcjar(file);
                    }
                    else if (file.exists()) {
                        retrotranslator.addSrcdir(file);
                    }
                    else {
                        throw new MojoExecutionException("Path not found: " + file);
                    }
                }
            }
            else if (dir != null) {
                retrotranslator.addSrcdir(dir);
            }
        }

        if (destdir != null) {
            FileUtils.forceMkdir(destdir);
            retrotranslator.setDestdir(destdir);
        }
        if (destjar != null) {
            retrotranslator.setDestjar(destjar);
        }

        retrotranslator.setVerbose(verbose);
        retrotranslator.setStripsign(stripsign);
        retrotranslator.setLazy(lazy);
        retrotranslator.setVerify(verify);
        
        if (classpathElements != null) {
            Iterator iter = classpathElements.iterator();
            while (iter.hasNext()) {
                String path = (String)iter.next();
                File file = new File(path);
                retrotranslator.addClasspathElement(file);
            }
        }
        
        if (verifyClasspath != null) {
            Iterator iter = verifyClasspath.iterator();
            while (iter.hasNext()) {
                String path = (String)iter.next();
                if (path == null) {
                    throw new MojoExecutionException("Null element in <verifyClasspath>");
                }
                File file = new File(path);
                retrotranslator.addClasspathElement(file);
            }
        }
        
        boolean verified = retrotranslator.run();
        if (!verified && failonwarning) {
            throw new MojoExecutionException("Verification failed.");
        }
    }
}
