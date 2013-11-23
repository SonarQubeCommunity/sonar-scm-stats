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

import java.util.Date;
import org.joda.time.DateTime;

public class DateRange {

  private DateTime from;
  private DateTime to;

  public DateRange(DateTime from, DateTime to) {
    this.from = from;
    this.to = to;
  }

  public DateTime getFrom() {
    return from;
  }

  public DateTime getTo() {
    return to;
  }

  public boolean isDateInRange(DateTime date) {
    return date.isBefore(to.getMillis()) && date.isAfter(from.getMillis());
  }

  public static DateRange getDateRange(int numDays, DateTime toDate) {
    DateTime now = new DateTime();
    DateTime earliest = new DateTime("1980-01-01");
    DateTime end = toDate != null ? toDate : now;
    DateTime start = numDays == 0 ? earliest : end.minusDays(numDays);
    return new DateRange(start, end);
  }
}
