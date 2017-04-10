/**
 *
 */

package io.github.tuxmonteiro.planclab.tests.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        plugin= {"pretty", "json:target/cucumber.json", "html:target/cucumber"},
        glue = {"io.github.tuxmonteiro.planclab"},
        features= {"classpath:cucumber"},
        tags={"~@ignore"}
        )
public class CucumberTest {

}
