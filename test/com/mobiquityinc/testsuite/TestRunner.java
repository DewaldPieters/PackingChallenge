package com.mobiquityinc.testsuite;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * @author Dewald Pieters
 *
 */
public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(PackingChallengeTests.class);

		result.getFailures().stream().forEach(failure -> System.out.println(failure.toString()));
		System.out.println(result.wasSuccessful() ? "All Tests Passed" : "One or more tests failed");
	}
}
