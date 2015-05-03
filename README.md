

SonarQube SCM Stats Plugin
==========================
Download and Version information: http://update.sonarsource.org/plugins/scmstats-confluence.html

## Build Status
[![Build Status](https://sonarplugins.ci.cloudbees.com/job/scm-stats/buildStatus/icon?job=check-manifest)](https://sonarplugins.ci.cloudbees.com/job/scms-tats)

## Upgrade from 0.1 to 0.2 or later
If you upgrade from 0.1 to 0.2 or later you'll need to run a new analysis to see the Authors activity and commits per author widgets.

## Description / Features
The plugin computes and feeds SonarQube with four new metrics : Commits / Author, Commits / Clock Hour, Commits / Week Day and Commits / Month.
Five project widgets ( under the SCM category ) display these metrics using graphical representations.

## Requirements
If you plan to use this plugin with non-Maven projects, or SCM access is available only with username/password or no SCM information is included in project's pom.xml you must also install the SCM Activity plugin ( http://docs.sonarqube.org/display/SONAR/SCM+Activity+Plugin ) .
### Compatibility Matrix:
<table>
<tr><th>Plugin Version</th><th>0.1</th><th>0.2</th><th>0.3.1</th></tr>
<tr><td>Source Control</td><td></td><td></td><td></td></tr>
<tr><td>SVN</td><td>Yes</td><td>Yes</td><td>Yes</td></tr>
<tr><td>Git</td><td>Yes</td><td>Yes</td><td>Yes</td></tr>
<tr><td>Mercurual</td><td>No</td><td>Authors activity not supported
see SONARPLUGINS-3108</td><td>Authors activity not supported
see SONARPLUGINS-3108</td></tr>
<tr><td>Perforce</td><td>No</td><td>No</td><td>Yes</td></tr>
<tr><td>CVS</td><td>No</td><td>Authors activity not supported
see SONARPLUGINS-3104</td><td>Authors activity not supported
see SONARPLUGINS-3104</td></tr>
<tr><td>Jazz</td><td>No</td><td>No</td><td>Maybe</td></tr>
</table>

## Installation
Install the plugin through the Update Center or download it into the SONARQUBE_HOME/extensions/plugins directory
Restart the SonarQube server

## Usage and Configuration
Set the SCM URL of your project (see SCM URL Format) by setting the sonar.scm.url property of SCM Activity plugin. For Maven projects this is automatically discovered if it's already set in pom.xml
Set the SCM user / password (if needed) by setting the sonar.scm.user.secured and sonar.scm.password.secured properties of SCM Activity plugin
Launch a new quality analysis and the metrics will be fed

## Grabbing stats for multiple periods
Since version 0.2, the plugin allows the collection of SCM stats for multiple (1-3) periods. 
By default the plugin collects stats for the whole history (sonar.scm-stats.period1 = 0 days).
You can have different periods on a global or project level by setting the number of days prior to the current date that the plugin will collect scm stats.

For example to collect scm stats for the last month, enter a value of 30, for the last week a value of 7, etc.
The properties for period 2 (sonar.scm-stats.period2) and period 3 (sonar.scm-stats.period3) can have the value of zero(0) 
but the plugin will ignore it. 
Stats for the entire history of a project will be collected only if sonar.scm-stats.period1 property is set to zero(0).
Negative values are ignored for all periods.
You can edit widget properties by setting the period number (acceptable value are 1-3). 
By default each widget shows stats for period 1.


## Ignoring / merging authors
Since version 0.3, the plugin allows you to set a list of authors to ignore (sonar.scm-stats.authors.ignore) and a list of author name synonyms to merge into single authors (sonar.scm-stats.authors.merge).
If you want to set them using the project / global settings, add one author (ignored or merged) in each value.
If you want to pass them as analysis arguments, authors must be comma-delimited. with aliases semicolon-delimited.

Example for ignored authors: -Dsonar.scm-stats.authors.ignore=author1,author2,author3

Example for merged authors: -Dsonar.scm-stats.authors.merge="author1=author;AUTHOR1,author2=author22;Author2;authOr2"

(Records for both "author" and "AUTHOR1" will be merged into "author1". Records for "author22", "Author2" and "auth0r2" will be merged into "author2")

## Perforce Configuration
Perforce is supported since version 0.3 and you need to set the Client Spec name property (sonar.scm-stats.perforce.clientspec) in order to be able to get scm stats

## Metrics Definitions
<table>
<tr><th>Name</th><th>Key</th><th>Description</th></tr>
<tr><td>Commits Per Week Day</td><td>scm-commits-per-weekday</td>
<td>Reports on the number of commits per week day.<br/>
</td></tr>
<tr><td>Commits Per Week MOnth</td><td>scm-commits-per-month</td>
<td>Reports on the number of commits per week month.<br/>
</td></tr>
<tr><td>Commits Per Clock Hour</td><td>scm-commits-per-clockhour</td>
<td>Reports on the number of commits per week clockhour.<br/>
</td></tr>
<tr><td>Commits Per Week Author</td><td>scm-commits-per-user</td>
<td>Reports on the number of commits per week author.<br/>
</td></tr>
</table>

## Known Limitations
Drill-down from the average abacus complexity of a module/project does not work due to the following issue in SonarQube: SONAR-3233.
Differential views are not implemented.
Compatibility with VIEWS: as each project can define its own abacus, this plug-in does not compute the abacus complexity and distribution for a view.
