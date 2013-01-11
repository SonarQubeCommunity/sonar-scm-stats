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

import com.google.common.base.Suppliers;
import com.google.common.base.Supplier;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.provider.svn.util.SvnUtil;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;
import java.io.File;

public class ScmFacade implements BatchExtension {
  private final SonarScmManager scmManager;
  private final ScmConfiguration configuration;
  private Supplier<ScmRepository> repository;

  public ScmFacade(SonarScmManager scmManager, ScmConfiguration configuration) {
    this.scmManager = scmManager;
    this.configuration = configuration;
    repository = Suppliers.memoize(new ScmRepositorySupplier());
  }

  public ChangeLogScmResult getChangeLog(File file) throws ScmException {
    return scmManager.changeLog(getScmRepository(), new ScmFileSet(file), null , null);
  }

  @VisibleForTesting
  ScmRepository getScmRepository() {
    return repository.get();
  }

  private class ScmRepositorySupplier implements Supplier<ScmRepository> {
    public ScmRepository get() {
      try {
        String connectionUrl = configuration.getUrl();
        String scmProvider = configuration.getScmProvider();
        String user = configuration.getUser();
        String password = configuration.getPassword();
        initSvn(scmProvider);

        ScmRepository scmRepository = scmManager.makeScmRepository(connectionUrl);

        if (!StringUtils.isBlank(user)) {
          ScmProviderRepository providerRepository = scmRepository.getProviderRepository();
          providerRepository.setUser(user);
          providerRepository.setPassword(password);
        }

        return scmRepository;
      } catch (ScmRepositoryException e) {
        throw new SonarException(e.getValidationMessages().toString(), e);
      } catch (ScmException e) {
        throw new SonarException(e);
      }
    }
  }

  /*
   * http://jira.codehaus.org/browse/SONARPLUGINS-1082
   * The goal is to always trust SSL certificates. It's partially implemented with the SVN property --trust-server-cert.
   * However it bypasses ONLY the "CA is unknown" check. It doesn't bypass hostname and expiry checks
   */
  private void initSvn(String scmProvider) {
    if (StringUtils.equals(scmProvider, "svn")) {
      SvnUtil.getSettings().setTrustServerCert(true);
    }
  }
}
