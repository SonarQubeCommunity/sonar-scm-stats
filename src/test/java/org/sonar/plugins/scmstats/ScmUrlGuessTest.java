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

import org.apache.maven.scm.provider.ScmUrlUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

/**
 *
 * Class copied directly from SCM Actvity Plugin
 */

public class ScmUrlGuessTest {
  ScmUrlGuess scmUrlGuess;
  ModuleFileSystem moduleFileSystem = mock(ModuleFileSystem.class);

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    scmUrlGuess = new ScmUrlGuess(moduleFileSystem);
  }

  @Test
  public void shouldnt_guess_url_from_empty_project() throws IOException {
    File baseDir = temporaryFolder.newFolder();
    when(moduleFileSystem.baseDir()).thenReturn(baseDir);

    String url = scmUrlGuess.guess();

    assertThat(url).isNull();
  }

  @Test
  public void should_guess_from_git_project() {
    when(moduleFileSystem.baseDir()).thenReturn(project(".git"));

    String url = scmUrlGuess.guess();

    assertThat(url).isEqualTo("scm:git:");
    assertThat(ScmUrlUtils.isValid(url)).isTrue();
  }

  @Test
  public void should_guess_from_mercurial_subsubproject() {
    File rootDir = project(".hg", "subproject/subsubproject");
    when(moduleFileSystem.baseDir()).thenReturn(new File(rootDir, "subproject/subsubproject"));

    String url = scmUrlGuess.guess();

    assertThat(url).isEqualTo("scm:hg:");
    assertThat(ScmUrlUtils.isValid(url)).isTrue();
  }

  @Test
  public void guess_from_directory_not_file() throws IOException {
    File fileWithMisleadingName = temporaryFolder.newFile(".git");
    when(moduleFileSystem.baseDir()).thenReturn(fileWithMisleadingName.getParentFile());

    String url = scmUrlGuess.guess();

    assertThat(url).isNull();
  }

  File project(String... folders) {
    File rootDir = temporaryFolder.getRoot();
    for (String folder : folders) {
      new File(rootDir, folder).mkdirs();
    }
    return rootDir;
  }
}
