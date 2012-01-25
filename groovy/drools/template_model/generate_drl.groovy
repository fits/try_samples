@Grab("org.drools:drools-core:5.4.0.Beta1")
@Grab("org.drools:drools-templates:5.4.0.Beta1")
import org.drools.template.model.*

def rule = new Rule("テストルール", null, 1)
rule.addCondition(new Condition(snippet: '$obj : Object()'))
rule.addConsequence(new Consequence(snippet: "System.out.println(\"test\")"))

def pkg = new Package("test")
pkg.addRule(rule)

def drl = new DRLOutput()
pkg.renderDRL(drl)

println drl
