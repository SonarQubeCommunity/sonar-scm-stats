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

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

public class FileUtils {
  private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  public List<String> getFilesToProcess(Project project, Settings settings) throws IllegalStateException {
    String[] inclusions = settings.getStringArray("sonar.inclusions");
    String[] exclusions = settings.getStringArray("sonar.exclusions");
    if (exclusions.length == 0 && inclusions.length == 0) {
      return null;
    }
    LOG.info("Inclusions : " + inclusions);
    LOG.info("Inclusions : " + exclusions);
    LOG.info("Project Base Dir : " + project.getFileSystem().getBasedir());
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setIncludes(inclusions);
    scanner.setExcludes(exclusions);
    scanner.setBasedir(project.getFileSystem().getBasedir());
    scanner.setCaseSensitive(false);
    scanner.scan();
    String[] files = scanner.getIncludedFiles();
    List<String> filesToProcess = new ArrayList<String>();
    for (String file : files) {
      filesToProcess.add(file.replaceAll("\\\\", "/"));
    }
    return filesToProcess;
  }
}
