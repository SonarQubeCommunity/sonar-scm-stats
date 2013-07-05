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

import java.util.ArrayList;
import java.util.List;

public class CommitsList implements Comparable<CommitsList> {
  private List<Integer> commits = new ArrayList<Integer>();

  public CommitsList(final List<Integer> commits) {
    this.commits = commits;
  }

  public List<Integer> getCommits() {
    return commits;
  }

  public void setCommits(final List<Integer> commits) {
    this.commits = commits;
  }

  public int compareTo(final CommitsList other) {
      return other.computeSum() - this.computeSum();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + (this.commits != null ? this.commits.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CommitsList other = (CommitsList) obj;
    if (this.commits != other.commits && (this.commits == null || !this.commits.equals(other.commits))) {
      return false;
    }
    return true;
  }

  public int computeSum(){
    int sum = 0;
    for (Integer commit : commits){
        sum += commit;
    }
    return sum;
  }

}
