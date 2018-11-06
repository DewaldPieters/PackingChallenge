package com.mobiquityinc.algorithm;

import java.util.List;

import com.mobiquityinc.model.Package;

/**
 * @author Dewald Pieters
 *
 */
/*
 * Interface that can be implemented by different types of algorithms. Currently
 * only the 0-1 binary algorithm implements this interface. Should it be decided
 * later down the line, that a dynamic programming approach with recursion
 * should be used to solve the problem. the algorithms can also implement this
 * interface. This interface abstracts the solving of problems using algorithms
 * from the algorithm implementations. It also ensures the Open/Close principle
 * is enforced as the implementation of news ways to solve the problem will not
 * affect the BinaryLPAlgorithmStrategy class code.
 */
public interface AlgorithmStrategy {

	void solveProblem(List<Package> packages);

}
