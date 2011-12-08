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
package com.excilys.ebi.gatling.http.test

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import org.joda.time.DateTime
import com.sun.corba.se.impl.protocol.NotExistent

object CompileTest {

	def runSimulations = runSimFunction(DateTime.now)

	val iterations = 10
	val pause1 = 1
	val pause2 = 2
	val pause3 = 3

	val baseUrl = "http://localhost:3000"

	val httpConf = httpConfig.baseURL(baseUrl).proxy("91.121.211.157", 80).httpsPort(4443).credentials("rom", "test")

	val usersInformation = new TSVFeeder("user_information")

	val loginChain = chain.exec(http("First Request Chain").get("/")).pause(1, 2)

	val loginGroup = "Login"
	val doStuffGroup = "Do Stuff"

	val testData = new TSVFeeder("test-data")

	val lambdaUser = scenario("Standard User")
		.insertChain(loginChain)
		// First request outside iteration
		.loop(
			chain
				.feed(testData)
				.exec(http("Catégorie Poney").get("/").queryParam("omg").queryParam("socool").check(xpath("//input[@id='text1']/@value") saveAs "aaaa_value")))
		.times(2)
		.pause(pause2, pause3)
		// Loop
		.loop(
			// What will be repeated ?
			chain
				// First request to be repeated
				.exec((s: Session) => println("iterate: " + getCounterValue(s, "titi")))
				.exec(
					http("Page accueil").get("http://localhost:3000")
						.check(
							xpath("//input[@value='${aaaa_value}']/@id").exists.saveAs("sessionParam"),
							xpath("//input[@id='${aaaa_value}']/@value").notExists,
							regex("""<input id="text1" type="text" value="aaaa" />"""),
							regex("""<input id="text1" type="test" value="aaaa" />""").notExists,
							status.in(200 to 210) saveAs "blablaParam",
							xpath("//input[@value='aaaa']/@id").neq("omg"),
							xpath("//input[@id='text1']/@value") eq "aaaa" saveAs "test2"))
				.loop(chain
					.exec(http("In During 1").get("http://localhost:3000/aaaa"))
					.pause(2)
					.loop(chain.exec((s: Session) => println("--nested loop: " + getCounterValue(s, "tutu")))).counterName("tutu").times(2)
					.exec((s: Session) => println("-loopDuring: " + getCounterValue(s, "toto")))
					.exec(http("In During 2").get("/"))
					.pause(2))
				.counterName("toto").during(12000, MILLISECONDS)
				.pause(pause2)
				.loop(
					chain
						.exec(http("In During 1").get("/"))
						.pause(2)
						.exec((s: Session) => println("-iterate1: " + getCounterValue(s, "titi") + ", doFor: " + getCounterValue(s, "hehe")))
						.loop(
							chain
								.exec((s: Session) => println("--iterate1: " + getCounterValue(s, "titi") + ", doFor: " + getCounterValue(s, "hehe") + ", iterate2: " + getCounterValue(s, "hoho"))))
						.counterName("hoho").times(2)
						.exec(http("In During 2").get("/"))
						.pause(2))
				.counterName("hehe").during(12000, MILLISECONDS)
				.startGroup(loginGroup)
				.exec((s: Session) => s.setAttribute("test2", "bbbb"))
				.doIf("test2", "aaaa",
					chain.exec(http("IF=TRUE Request").get("/")), chain.exec(http("IF=FALSE Request").get("/")))
				.pause(pause2)
				.exec(http("Url from session").get("/aaaa"))
				.pause(1000, 3000, MILLISECONDS)
				// Second request to be repeated
				.exec(http("Create Thing blabla").post("/things").queryParam("login").queryParam("password").fileBody("create_thing", Map("name" -> "blabla")).asJSON)
				.pause(pause1)
				.endGroup(loginGroup)
				// Third request to be repeated
				.exec(http("Liste Articles") get ("/things") queryParam "firstname" queryParam "lastname")
				.pause(pause1)
				.exec(http("Test Page") get ("/tests") check (header(CONTENT_TYPE).eq("text/html; charset=utf-8") saveAs "sessionParam"))
				// Fourth request to be repeated
				.exec(http("Create Thing omgomg")
					.post("/things").queryParam("postTest", "${sessionParam}").fileBody("create_thing", Map("name" -> "${sessionParam}")).asJSON
					.check(status.eq(201) saveAs "status"))).counterName("titi").times(iterations)
		// Second request outside iteration
		.startGroup(doStuffGroup)
		.exec(http("Ajout au panier") get ("/") check (regex("""<input id="text1" type="text" value="(.*)" />""") saveAs "input"))
		.pause(pause1)
		.endGroup(doStuffGroup)

	runSimulations(
		lambdaUser.configure.users(5).ramp(10).protocolConfig(httpConf))
}