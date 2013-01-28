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

package org.sonar.plugins.scmstats.model;

import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ChangeLogInfo {
  
  private String author;
  private Date commitDate;
  private Map<String,Integer> activity;

  public ChangeLogInfo(String author, Date commitDate, Map<String,Integer> activity) {
    this.setAuthor(author);
    this.commitDate = commitDate;
    this.activity = activity;
  }
  
  public String getAuthor() {
    return author;
  }

  public final void setAuthor(String author) {
    this.author = StringUtils.substringBefore(author, "<").trim();
  }

  public Date getCommitDate() {
    return commitDate;
  }

  public void setCommitDate(Date commitDate) {
    this.commitDate = commitDate;
  }

  public Map<String, Integer> getActivity() {
    return activity;
  }

}