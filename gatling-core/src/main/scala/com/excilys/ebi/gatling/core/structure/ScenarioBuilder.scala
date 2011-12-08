/*
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
package com.excilys.ebi.gatling.core.structure

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.action.builder.AbstractActionBuilder
import com.excilys.ebi.gatling.core.action.builder.PauseActionBuilder._
import com.excilys.ebi.gatling.core.action.builder.IfActionBuilder._
import com.excilys.ebi.gatling.core.action.builder.WhileActionBuilder._
import com.excilys.ebi.gatling.core.action.builder.GroupActionBuilder._
import com.excilys.ebi.gatling.core.action.builder.GroupActionBuilder
import com.excilys.ebi.gatling.core.action.builder.EndActionBuilder._
import com.excilys.ebi.gatling.core.action.builder.StartActionBuilder._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.scenario.Scenario
import com.excilys.ebi.gatling.core.scenario.configuration.ScenarioConfigurationBuilder

/**
 * ScenarioBuilder class companion
 */
object ScenarioBuilder {
	def scenario(scenarioName: String) = new ScenarioBuilder(scenarioName, Nil).start
}
/**
 * The scenario builder is used in the DSL to define the scenario
 *
 * @param name the name of the scenario
 * @param actionBuilders the list of all the actions that compose the scenario
 * @param next the action that will be executed after this scenario (that can be a chain as well)
 * @param groups the groups for all the actions of this scenario
 */
class ScenarioBuilder(val name: String, actionBuilders: List[AbstractActionBuilder]) extends AbstractStructureBuilder[ScenarioBuilder](actionBuilders) {

	private[core] def newInstance(actionBuilders: List[AbstractActionBuilder]) = {
		new ScenarioBuilder(name, actionBuilders)
	}

	private[core] def getInstance = this

	def configure = new ScenarioConfigurationBuilder(this)

	/**
	 * Method that should not be used in a script. It adds a StartAction to the scenario
	 *
	 * @return a new builder with its first action added
	 */
	private[core] def start: ScenarioBuilder = newInstance(startActionBuilder :: actionBuilders)

	/**
	 * Method that should not be used in a script. It adds an EndAction that will
	 * tell the engine that the scenario is finished
	 *
	 * @param latch the countdown latch used to stop the engine
	 * @return a new builder with its last action added
	 */
	private[core] def end(latch: CountDownLatch): ScenarioBuilder = newInstance(endActionBuilder(latch) :: actionBuilders)

	/**
	 * Method that actually builds the scenario
	 *
	 * @param scenarioId the id of the current scenario
	 * @return the first action of the scenario to be executed
	 */
	private[core] def build: Scenario = new Scenario(name, buildActions(null))
}
