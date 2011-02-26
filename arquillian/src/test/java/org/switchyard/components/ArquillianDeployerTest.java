package org.switchyard.components;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.spi.client.deployment.DeploymentScenario;

import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.DeploymentTarget;
import org.jboss.arquillian.api.Expected;
import org.jboss.arquillian.api.Protocol;
import org.jboss.arquillian.api.Target;
import org.jboss.arquillian.api.DeploymentTarget;
import org.jboss.arquillian.spi.TestClass;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.ArchivePaths;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.dependencies.Dependencies;
import org.jboss.shrinkwrap.dependencies.impl.filter.StrictFilter;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.log4j.Logger;

@RunWith(Arquillian.class)
public class ArquillianDeployerTest {

    private static Logger _logger = Logger.getLogger(ArquillianDeployerTest.class);
    
    private static File getArtifact(String artifact) throws Exception {
	File files[] = Dependencies.artifact(artifact).resolveAsFiles(new StrictFilter());
	if (files.length == 0) {
	    throw new Exception("Found no files for artifact " + artifact);
	} else if (files.length > 1) {
	    throw new Exception("Found too many files for artifact " + artifact);
	}
	
	_logger.debug("Found file " + files[0].toString() + " for artifact " + artifact);
	return files[0];
    }
    
    @Deployment
    public static JavaArchive createTestArchive() throws Exception {
	JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "m1app.jar");
	javaArchive.as(ZipImporter.class).importFrom(getArtifact("org.switchyard.quickstarts:m1app:1.0-SNAPSHOT"));
       
	System.out.println(javaArchive.toString(true));
	return javaArchive;
    }
    
   public static JavaArchive createDeployerArchive() throws Exception {
       /*
       File config[] = Dependencies.artifact("org.switchyard:switchyard-config:1.0-SNAPSHOT").resolveAsFiles();
       File api[] = Dependencies.artifact("org.switchyard:switchyard-api:1.0-SNAPSHOT").resolveAsFiles();
       File transform[] = Dependencies.artifact("org.switchyard:switchyard-transform:1.0-SNAPSHOT").resolveAsFiles();
       File runtime[] = Dependencies.artifact("org.switchyard:switchyard-runtime:1.0-SNAPSHOT").resolveAsFiles();
       File deploy[] = Dependencies.artifact("org.switchyard:switchyard-deploy:1.0-SNAPSHOT").resolveAsFiles();
       File deployJBossAS6[] = Dependencies.artifact("org.switchyard:switchyard-deploy-jboss-as6:1.0-SNAPSHOT").resolveAsFiles();
       */
       JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "deployer.jar")
       	.addAsResource(getArtifact("org.switchyard:switchyard-config:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-config-1.0.SNAPSHOT.jar")
       	.addAsResource(getArtifact("org.switchyard:switchyard-api:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-api-1.0.SNAPSHOT.jar")
       	.addAsResource(getArtifact("org.switchyard:switchyard-transform:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-transform-1.0-SNAPSHOT.jar")
       	.addAsResource(getArtifact("org.switchyard:switchyard-runtime:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-runtime-1.0-SNAPSHOT.jar")
       	.addAsResource(getArtifact("org.switchyard:switchyard-deploy:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-deployer-1.0-SNAPSHOT.jar")
      	.addAsResource(getArtifact("org.switchyard:switchyard-deploy-jboss-as6:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-deploy-jboss-as6-1.0.SNAPSHOT.jar")
      	.addAsResource("switchyard-deployers-jboss-beans.xml", "/switchyard.deployer/META-INF/switchyard-deployers-jboss-beans.xml");
      
       System.out.println(javaArchive.toString(true));
       
       return javaArchive;
   }
				
   @Test 
   public void emptyTest() {
   }

}

