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

import org.sonar.plugins.scmstats.utils.DateRange;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.assertj.jodatime.api.Assertions.assertThat;

public class DateRangeTest {

  private DateTime from = new DateTime("2013-01-01");
  private DateTime to = new DateTime("2013-12-31");
  
  @Test
  public void should_return_true_if_date_IsIn_range() {

    DateTime dateToAssert = new DateTime("2013-05-05");
    DateRange dateRange = new DateRange(from, to);
    assertTrue(dateRange.isDateInRange(dateToAssert));
  }

  @Test
  public void should_return_true_if_date_IsGreater_than_range() {
    DateRange dateRange = new DateRange(from, to);
    assertFalse(dateRange.isDateInRange(to));
  }
  @Test
  public void should_return_true_if_date_IsSmallerr_than_range() {
    DateRange dateRange = new DateRange(from, to);
    assertFalse(dateRange.isDateInRange(from));
  }
  
  @Test
  public void should_Get_Date_Range_with_endDate() {
    int numDays = 0;
    DateTime endDate = new DateTime("2013-10-10");
    DateRange expected = new DateRange(new DateTime("1980-01-01"), endDate);
    DateRange result = DateRange.getDateRange(numDays, endDate);
    assertThat(result.getFrom()).isEqualToIgnoringHours(expected.getFrom());
    assertThat(result.getTo()).isEqualToIgnoringHours(expected.getTo());
  }

  @Test
  public void should_Get_Date_Range_without_endDate() {
    int numDays = 0;
    DateTime now = new DateTime();
    DateRange expected = new DateRange(new DateTime("1980-01-01"), now);
    DateRange result = DateRange.getDateRange(numDays, null);
    assertThat(result.getFrom()).isEqualToIgnoringHours(expected.getFrom());
    assertThat(result.getTo()).isEqualToIgnoringHours(expected.getTo());
  }

  @Test
  public void should_Get_Date_Range_with_numDays() {
    int numDays = 5;
    DateTime toDate = new DateTime("2013-10-10");
    DateRange expected = new DateRange(toDate.minusDays(numDays), toDate);
    DateRange result = DateRange.getDateRange(numDays, toDate);
    assertThat(result.getFrom()).isEqualToIgnoringHours(expected.getFrom());
    assertThat(result.getTo()).isEqualToIgnoringHours(expected.getTo());
  }

  @Test
  public void should_Get_Date_Range_with_numDays_andNoEndDate() {
    int numDays = 5;
    DateTime now = new DateTime();
    DateRange expected = new DateRange(now.minusDays(numDays), now);
    DateRange result = DateRange.getDateRange(numDays, null);
    assertThat(result.getFrom()).isEqualToIgnoringHours(expected.getFrom());
    assertThat(result.getTo()).isEqualToIgnoringHours(expected.getTo());
  }

}
