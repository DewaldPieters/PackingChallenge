package com.mobiquityinc.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mobiquityinc.algorithm.BinaryLPAlgorithmStrategyTest;
import com.mobiquityinc.packer.PackerTest;

/**
 * @author Dewald Pieters
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ PackerTest.class, BinaryLPAlgorithmStrategyTest.class })
public class PackingChallengeTests {
}
