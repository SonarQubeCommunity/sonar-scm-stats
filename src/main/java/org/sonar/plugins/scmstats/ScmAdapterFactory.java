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

import java.util.ArrayList;
import java.util.List;
import org.sonar.api.BatchExtension;
import org.tmatesoft.hg.core.HgRepoFacade;

public class ScmAdapterFactory implements BatchExtension {

  private final ScmConfiguration config;
  private final ScmFacade scmFacade;

  public ScmAdapterFactory(ScmConfiguration config, ScmFacade scmFacade) {
    this.config = config;
    this.scmFacade = scmFacade;
  }

  public AbstractScmAdapter getScmAdapter() {
    List<AbstractScmAdapter> availableAdapters = new ArrayList();
    AbstractScmAdapter genericAdapter = new GenericScmAdapter(scmFacade, config);
    availableAdapters.add(new HgScmAdapter(new HgRepoFacade(), config));
    availableAdapters.add(genericAdapter);

    for (AbstractScmAdapter adapter : availableAdapters) {
      if (adapter.isResponsible(scmFacade.getScmRepository().getProvider())) {
        return adapter;
      }
    }
    return genericAdapter;

  }

}
