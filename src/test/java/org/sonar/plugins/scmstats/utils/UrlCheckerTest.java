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
package org.sonar.plugins.scmstats.utils;

import org.junit.*;
import static org.fest.assertions.Assertions.assertThat;
public class UrlCheckerTest {

  @Test
  public void CheckForEmptyUrl() {
    String url = "";
    UrlChecker instance = new UrlChecker();
    assertThat( instance.check(url)).isFalse();
    
  }

  @Test
  public void CheckForInvalidUrl() {
    String url = "An invlid url";
    UrlChecker instance = new UrlChecker();
    assertThat( instance.check(url)).isFalse();
  }
  @Test
  public void CheckvalidUrl() {
    String url = "scm:svn:svn:\\";
    UrlChecker instance = new UrlChecker();
    assertThat( instance.check(url)).isTrue();
  }
  @Test
  public void CheckForUnSupportedUrl() {
    String url = "scm:starteam:hostname:23456/project/view/folder";
    UrlChecker instance = new UrlChecker();
    assertThat( instance.check(url)).isFalse();
  }
  
  
}
