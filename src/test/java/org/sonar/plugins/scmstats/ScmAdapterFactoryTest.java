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

import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.config.Settings;
import static org.fest.assertions.Assertions.assertThat;

public class ScmAdapterFactoryTest {

  private final Settings settings = new Settings();

  private ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final ScmConfiguration scmConfiguration = new ScmConfiguration(settings, scmUrlGuess);
  private final ScmFacade scmFacade = mock(ScmFacade.class);
  private final ScmRepository repo = mock(ScmRepository.class);
  private final ScmAdapterFactory adapterFactory = new ScmAdapterFactory(scmConfiguration, scmFacade);

  @Before
  public void setUp(){
    when(scmFacade.getScmRepository()).thenReturn(repo);
  }

  @Test
  public void shouldGetGenericAdapter() {
    when(repo.getProvider()).thenReturn("git");
    AbstractScmAdapter adapter = adapterFactory.getScmAdapter();
    assertThat(adapter).isInstanceOf(GenericScmAdapter.class);
  }

  @Test
  public void shouldGetHgAdapter() {
    when(repo.getProvider()).thenReturn("hg");
    AbstractScmAdapter adapter = adapterFactory.getScmAdapter();
    assertThat(adapter).isInstanceOf(HgScmAdapter.class);
  }

}
