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

public class ChangeLogInfo {
  
  private String author;
  private String clockHour;
  private String reviision;

  public ChangeLogInfo(String author, String clockHour, String reviision) {
    this.author = author;
    this.clockHour = clockHour;
    this.reviision = reviision;
  }
  
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getClockHour() {
    return clockHour;
  }

  public void setClockHour(String clockHour) {
    this.clockHour = clockHour;
  }

  public String getReviision() {
    return reviision;
  }

  public void setReviision(String reviision) {
    this.reviision = reviision;
  }

}