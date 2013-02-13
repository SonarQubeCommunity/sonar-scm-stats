/*
 * Sonar SCM Stats Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.scmstats;

import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.junit.*;
import static org.junit.Assert.*;

public class SonarScmManagerTest {
  private SonarScmManager scmManager = new SonarScmManager();
  
  @Test
  public void testScmManagerSvnProvider() {
    try {
      scmManager.getProviderByType(new SvnExeScmProvider().getScmType());
    } catch (NoSuchScmProviderException ex) {
      fail (ex.getProviderName() + " Provider should be registered" );
    }
  }
  
  @Test
  public void testScmManagerGitProvider() {
    try {
      scmManager.getProviderByType(new GitExeScmProvider().getScmType());
    } catch (NoSuchScmProviderException ex) {
      fail (ex.getProviderName() + " Provider should be registered" );
    }
  }

  @Test
  public void testScmManagerHgProvider() {
    try {
      scmManager.getProviderByType(new HgScmProvider().getScmType());
    } catch (NoSuchScmProviderException ex) {
      fail (ex.getProviderName() + " Provider should be registered" );
    }
  }

  @Test
  public void testScmManagerCvsProvider() {
    try {
      scmManager.getProviderByType(new CvsExeScmProvider().getScmType());
    } catch (NoSuchScmProviderException ex) {
      fail (ex.getProviderName() + " Provider should be registered" );
    }
  }
}
