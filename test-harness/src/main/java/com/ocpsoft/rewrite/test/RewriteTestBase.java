/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.rewrite.test;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      JavaArchive rewrite = ShrinkWrap.create(JavaArchive.class, "rewrite-servlet.jar")
               .addAsResource(new File("../api/target/classes/com"))
               .addAsResource(new File("../api/target/classes/META-INF"))
               .addAsResource(new File("../impl-servlet/target/classes/com"))
               .addAsResource(new File("../impl-servlet/target/classes/META-INF"));

      return ShrinkWrap
               .create(WebArchive.class, "rewrite-test.war")
               .addAsLibraries(rewrite)
               .addAsLibraries(resolveDependencies("org.jboss.weld.servlet:weld-servlet:1.1.1.Final"))
               .addAsLibraries(resolveDependencies("org.jboss.logging:jboss-logging:3.0.0.Beta4"))
               .setWebXML("jetty-web.xml")
               .addAsWebResource(new StringAsset("<beans/>"), ArchivePaths.create("WEB-INF/beans.xml"))
               .addAsWebResource("jetty-env.xml", ArchivePaths.create("WEB-INF/jetty-env.xml"))
               .addAsResource("jetty-log4j.xml", ArchivePaths.create("/log4j.xml"));
   }

   private static Collection<GenericArchive> resolveDependencies(final String coords)
   {
      return DependencyResolvers.use(MavenDependencyResolver.class)
               .artifacts(coords)
               .loadReposFromPom("pom.xml")
               .resolveAs(GenericArchive.class);
   }

   // @ArquillianResource
   // private URL baseURL;

   /**
    * Request a resource from the deployed test-application. The {@link HttpServletRequest#getContextPath()} will be
    * automatically prepended to the given path.
    * <p>
    * E.g: A path of '/example' will be sent as '/rewrite-test/example'
    */
   protected HttpAction<HttpGet> get(final String path)
   {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      try
      {
         String url = baseURL().toExternalForm();
         if (url.endsWith("/"))
         {
            url = url.substring(0, url.length() - 1);
         }
         String baseUrlString = url;
         url = url + path;

         HttpGet httpGet = new HttpGet(url);
         HttpContext context = new BasicHttpContext();
         HttpResponse response = httpClient.execute(httpGet, context);

         return new HttpAction<HttpGet>(httpClient, httpGet, context, response, baseUrlString);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public URL baseURL()
   {
      try {
         return new URL("http://localhost:9090/rewrite-test/");
      }
      catch (MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }
}