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

import org.sonar.plugins.scmstats.utils.SupportedScm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;

import java.io.File;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

/**
 *
 * Class copied directly from SCM Actvity Plugin
 */
public class ScmUrlGuess implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(ScmUrlGuess.class);

  private final ModuleFileSystem moduleFileSystem;

  public ScmUrlGuess(ModuleFileSystem moduleFileSystem) {
    this.moduleFileSystem = moduleFileSystem;
  }

  public String guess() {
    LOG.info("Trying to guess scm provider from project layout...");

    File basedir = moduleFileSystem.baseDir();

    for (File dir = basedir; dir != null; dir = dir.getParentFile()) {
      for (SupportedScm scm : SupportedScm.values()) {
        if (scm.getGuessedUrl() != null) {
          LOG.debug("Search for: " + new File(dir, scm.getScmSpecificFilename()));
          if (new File(dir, scm.getScmSpecificFilename()).isDirectory()) {
            LOG.info("Found SCM type: " + scm.getType());
            return scm.getGuessedUrl();
          }
        }
      }
    }

    LOG.info("Didn't find which SCM provider is used. Fallback on configuration");
    return null;
  }
}
