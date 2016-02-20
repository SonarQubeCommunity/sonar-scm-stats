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

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.scmstats.utils.DateRange;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.MapUtils;

public abstract class AbstractScmAdapter implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractScmAdapter.class);

  private final ScmConfiguration configuration;
  private final ModuleFileSystem moduleFileSystem;
  private final PathPattern[] exclusionPatterns; 
  private final PathPattern[] inclusionPatterns; 
  
  public AbstractScmAdapter(ScmConfiguration configuration, FileExclusions fileExclusions, ModuleFileSystem moduleFileSystem) {
    this.configuration = configuration;
    this.moduleFileSystem = moduleFileSystem;
    exclusionPatterns = PathPattern.create(fileExclusions.sourceExclusions());
    inclusionPatterns = PathPattern.create(fileExclusions.sourceInclusions());
  }

  protected ScmConfiguration getConfiguration() {
    return configuration;
  }

  public abstract ChangeLogHandler getChangeLog(DateRange dateRange);
  public abstract boolean isResponsible(String scmType);
  
  protected ChangeLogHandler createChangeLogHolder() {
    return new ChangeLogHandler(
            getConfiguration().getIgnoreAuthorsList(),
            getConfiguration().getMergeAuthorsList());
  }
  
   protected final boolean isIncluded(Resource resource) {
    if (inclusionPatterns != null && inclusionPatterns.length > 0) {
      boolean matchInclusion = false;
      for (PathPattern pattern : inclusionPatterns) {
        matchInclusion |= pattern.match(resource);
      }
      if (!matchInclusion) {
        return false;
      }
    }
    if (exclusionPatterns != null && exclusionPatterns.length > 0) {
      for (PathPattern pattern : exclusionPatterns) {
        if (pattern.match(resource)) {
          return false;
        }
      }
    }
    return true;
  } 
   
  protected Map<String, Integer> updateActivity (String resourceName, Map<String, Integer> fileStatus, String activity) {
    Resource resource = createResource(resourceName);
    LOG.warn(resource.getKey() + " is " + (isIncluded(resource) ? "NOT " : " ") + "excluded");
    return isIncluded(resource) ? MapUtils.updateMap(fileStatus, activity) : fileStatus;
  }
  
  Resource createResource(String resourceName) {
    
    String mavenizedResourceName = StringUtils.remove(resourceName, configuration.getSourceDir());
    mavenizedResourceName = StringUtils.remove(mavenizedResourceName, configuration.getTestSourceDir());
    
    int index = StringUtils.lastIndexOf(mavenizedResourceName, "/");
    String directory = StringUtils.substring(mavenizedResourceName, 0, index);
    String filename = StringUtils.substring(mavenizedResourceName, index + 1);
    return new File(directory, filename);
  }

  protected ModuleFileSystem getModuleFileSystem() {
    return moduleFileSystem;
  }
  
  
}
