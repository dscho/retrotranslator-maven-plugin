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

import java.util.Iterator;
import java.util.List;

import net.sf.retrotranslator.transformer.Retrotranslator;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Support for retrotranlsation mojos.
 * 
 * @noinspection UnusedDeclaration,MismatchedQueryAndUpdateOfCollection
 */
public abstract class RetrotranslateMojoSupport
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
     * The classpath for the verification including rt.jar, jce.jar, jsse.jar (from JRE 1.4).
     * The retrotranslator-runtime-n.n.n.jar, and backport-util-concurrent-n.n.jar
     * are included by default, they are not required to be defined here.
     * 
     * @parameter
     */
    private List verifyClasspath;

    /**
     * Asks the translator to strip signature (generics) information.
     * 
     * @parameter expression="${stripsign}" default-value="false"
     */
    private boolean stripsign;

    /**
     * Asks the translator for verbose output.
     * 
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Asks the translator to examine translated bytecode for references 
     * to classes, methods, or fields that cannot be found in the provided classpath.
     * 
     * @parameter expression="${verify}" default-value="false"
     */
    private boolean verify;

    /**
     * Asks the translator to only transform classes compiled 
     * with a target greater than the current one.
     * 
     * @parameter expression="${lazy}" default-value="false"
     */
    private boolean lazy;

    /**
     * Fails build when verification has failed.
     * 
     * @parameter expression="${failonwarning}" default-value="true"
     */
    private boolean failonwarning;
    
    /**
     * Whether to use alternative implementations of Java 1.4 
     * classes and methods for better Java 5 compatibility.
     *
     * @parameter expression="${advanced}" default-value="false"
     */
    private boolean advanced;

    /**
     * The package name for a private copy of retrotranslator-runtime-n.n.n.jar 
     * and backport-util-concurrent-n.n.jar to be put with translated classes.
     * 
     * @parameter expression="${embed}"
     */
    private String embed;

    /**
     * Informs the translator about user-defined backport packages.
     * Package names should be separated by semicolons.
     * 
     * @parameter expression="${backport}"
     */
    private String backport;

    /**
     * To make Java 6 classes compatible with Java 5 set this option to 1.5
     * and supply user-defined backport packages.
     * 
     * @parameter expression="${target}" default-value="1.4"
     */
    private String target;
    
    /**
     * Asks the translator to modify classes for JVM 1.4 compatibility 
     * but keep use of Java 5 API.
     *
     * @parameter expression="${retainapi}" default-value="false"
     */
    private boolean retainapi;

    /**
     * Asks the translator to keep Java 5 specific access modifiers.
     *
     * @parameter expression="${retainflags}" default-value="false"
     */
    private boolean retainflags;

    protected void doExecute() throws Exception {
        Retrotranslator retrotranslator = new Retrotranslator();
        retrotranslator.setLogger(new RetrotranslatorLogger(log));

        configureRetrotranslator(retrotranslator);

        retrotranslator.setVerbose(verbose);
        retrotranslator.setStripsign(stripsign);
        retrotranslator.setLazy(lazy);
        retrotranslator.setVerify(verify);
        retrotranslator.setAdvanced(advanced);
        retrotranslator.setEmbed(embed);
        retrotranslator.setBackport(backport);
        retrotranslator.setRetainapi(retainapi);
        retrotranslator.setRetainflags(retainflags);
        
        if (target != null) {
            retrotranslator.setTarget(target);
        }
        
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

    protected abstract void configureRetrotranslator(Retrotranslator retrotranslator) throws Exception;
}
