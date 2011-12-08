/**
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.charts.loader
import java.util.{ Comparator => JComparator }
import java.util.{ HashMap => JHashMap, Collections => JCollections, ArrayList => JArrayList }

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.immutable.SortedMap
import scala.io.Source

import org.joda.time.DateTime

import com.excilys.ebi.gatling.charts.util.OrderingHelper.DateTimeOrdering
import com.excilys.ebi.gatling.core.action.EndAction.END_OF_SCENARIO
import com.excilys.ebi.gatling.core.action.StartAction.START_OF_SCENARIO
import com.excilys.ebi.gatling.core.config.GatlingConfig.CONFIG_ENCODING
import com.excilys.ebi.gatling.core.config.GatlingFiles.simulationLogFile
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.result.message.ResultStatus
import com.excilys.ebi.gatling.core.result.writer.FileDataWriter.{ GROUPS_SUFFIX, GROUPS_SEPARATOR, GROUPS_PREFIX }
import com.excilys.ebi.gatling.core.util.DateHelper.parseResultDate
import com.excilys.ebi.gatling.core.util.FileHelper.TABULATION_SEPARATOR
import scala.collection.mutable.{ Seq => MSeq }

class DataLoader(runOn: String) extends Logging {

	private val data: MSeq[ResultLine] = {

		val data = new JArrayList[ResultLine]

		// use caches in order to reuse String instances instead of holding multiple references of equal Strings
		val stringCache = new JHashMap[String, String]
		val intCache = new JHashMap[String, Int]
		val dateTimeCache = new JHashMap[String, DateTime]
		val groupsCache = new JHashMap[String, List[String]]

		def cachedString(string: String) = {
			if (!stringCache.containsKey(string)) {
				val newString = new String(string)
				stringCache.put(newString, newString)
			}
			stringCache.get(string)
		}

		def cachedInt(string: String) = {
			if (!intCache.containsKey(string)) {
				val newString = new String(string)
				intCache.put(newString, newString.toInt)
			}
			intCache.get(string)
		}

		def cachedDateTime(string: String) = {
			if (!dateTimeCache.containsKey(string)) {
				val newString = new String(string)
				dateTimeCache.put(newString, parseResultDate(newString))
			}
			dateTimeCache.get(string)
		}

		def cachedGroups(string: String) = {
			if (!groupsCache.containsKey(string)) {
				val newString = new String(string)
				groupsCache.put(newString, parseGroups(newString))
			}
			groupsCache.get(string)
		}

		def parseGroups(string: String) = string.stripPrefix(GROUPS_PREFIX).stripSuffix(GROUPS_SUFFIX).split(GROUPS_SEPARATOR).toList

		for (line <- Source.fromFile(simulationLogFile(runOn).jfile, CONFIG_ENCODING).getLines) {
			line.split(TABULATION_SEPARATOR) match {
				// If we have a well formated result
				case Array(runOn, scenarioName, userId, actionName, executionStartDate, executionDuration, resultStatus, resultMessage, groups) =>

					data.add(ResultLine(cachedString(runOn), cachedString(scenarioName), cachedInt(userId), cachedString(actionName), cachedDateTime(executionStartDate), cachedInt(executionDuration), ResultStatus.withName(resultStatus), cachedString(resultMessage), cachedGroups(groups)))
				// Else, if the resulting data is not well formated print an error message
				case _ => logger.warn("simulation.log had bad end of file, statistics will be generated but may not be accurate")
			}
		}

		JCollections.sort(data, new JComparator[ResultLine] {
			def compare(o1: ResultLine, o2: ResultLine) = {
				o1.executionStartDate.getMillis.compare(o2.executionStartDate.getMillis)
			}
		})
		data
	}

	val dataIndexedByDateInSeconds: SortedMap[DateTime, MSeq[ResultLine]] = SortedMap(data.groupBy(_.executionStartDate.withMillisOfSecond(0)).toSeq: _*)

	def dataIndexedByRequestName(requestName: String): MSeq[ResultLine] = data.filter(_.requestName == requestName)

	def dataIndexedByRequestNameAndDateInMilliseconds(requestName: String): SortedMap[DateTime, MSeq[ResultLine]] = SortedMap(dataIndexedByRequestName(requestName).groupBy(_.executionStartDate).toSeq: _*)

	def dataIndexedByRequestNameAndDateInSeconds(requestName: String): SortedMap[DateTime, MSeq[ResultLine]] = SortedMap(dataIndexedByRequestName(requestName).groupBy(_.executionStartDate.withMillisOfSecond(0)).toSeq: _*)

	def dataIndexedByScenarioNameAndDateInSeconds(scenarioName: String): SortedMap[DateTime, MSeq[ResultLine]] = SortedMap(data.filter(_.scenarioName == scenarioName).groupBy(_.executionStartDate.withMillisOfSecond(0)).toSeq: _*)

	val requestNames: MSeq[String] = data.map(_.requestName).distinct.filterNot(value => value == END_OF_SCENARIO || value == START_OF_SCENARIO)

	val groupNames: List[String] = data.map(_.groups).flatten.toList.distinct

	val scenarioNames: MSeq[String] = data.map(_.scenarioName).distinct
}