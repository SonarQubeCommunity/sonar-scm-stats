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
package org.sonar.plugins.scmstats.scm;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;
import java.util.Locale;
import org.joda.time.DateTime;
import org.junit.Test;

public class StatsHgChangeLogConsumerTest {
 
  @Test
  public void shouldParseDateWithDefaultPattern() {
    String date = "21/10/2012";
    String userPattern = null;
    String defaultPattern = "dd/MM/yyyy";
    Locale locale = Locale.ENGLISH;
    assertDateParsing(userPattern, date, defaultPattern, locale);
  }

  @Test
  public void shouldParseDateWithUuserPattern() {
    String date = "2012-10-21";
    String userPattern = "yyyy-MM-dd";
    String defaultPattern = "dd/MM/yyyy";
    Locale locale = Locale.ENGLISH;
    assertDateParsing(userPattern, date, defaultPattern, locale);
  }

  @Test
  public void shouldParseDateWithLocale() {
    String date = "Δευ, 2012-10-21";
    String userPattern = "EEE, yyyy-MM-dd";
    String defaultPattern = "dd/MM/yyyy";
    Locale locale = new Locale("el");
    assertDateParsing(userPattern, date, defaultPattern, locale);
  }

  @Test
  public void shouldParseDateWithLocaleByConstructor() {
    String date = "Δευ, 2012-10-21";
    String userPattern = "EEE, yyyy-MM-dd";
    String defaultPattern = "dd/MM/yyyy";
    Locale locale = new Locale("el");
    StatsHgChangeLogConsumer instance = new StatsHgChangeLogConsumer(null, userPattern, locale);
    Date result = instance.parseDate(date, userPattern, defaultPattern, null);
    
    DateTime dateTime = new DateTime(result);
    assertThat ( dateTime.getDayOfMonth()).isEqualTo(21);
    assertThat ( dateTime.getMonthOfYear()).isEqualTo(10);
    assertThat ( dateTime.getYear()).isEqualTo(2012);
  }

  @Test
  public void shouldParseDateWithDefaultLocale() {
    String date = "Mon, 2012-10-21";
    String userPattern = "EEE, yyyy-MM-dd";
    String defaultPattern = "dd/MM/yyyy";
    assertDateParsing(userPattern, date, defaultPattern, Locale.ENGLISH);
  }

  @Test
  public void shouldFailToParse() {
    String date = "Mon, 2012-10-21";
    String userPattern = "yyyy-MM-dd";
    String defaultPattern = "dd/MM/yyyy";
    StatsHgChangeLogConsumer instance = new StatsHgChangeLogConsumer(null, userPattern, null);
    Date result = instance.parseDate(date, userPattern, defaultPattern, Locale.ENGLISH);
  
    assertThat(result).isNull();
  }
  private void assertDateParsing(String userPattern, String date, String defaultPattern, Locale locale) {
    StatsHgChangeLogConsumer instance = new StatsHgChangeLogConsumer(null, userPattern, null);
    Date result = instance.parseDate(date, userPattern, defaultPattern, locale);
    
    DateTime dateTime = new DateTime(result);
    assertThat ( dateTime.getDayOfMonth()).isEqualTo(21);
    assertThat ( dateTime.getMonthOfYear()).isEqualTo(10);
    assertThat ( dateTime.getYear()).isEqualTo(2012);
  }

}