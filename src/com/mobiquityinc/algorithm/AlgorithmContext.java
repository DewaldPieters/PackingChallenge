package com.mobiquityinc.algorithm;

import java.util.List;

import com.mobiquityinc.model.Package;

/**
 * @author Dewald Pieters
 *
 */
/*
 * Create a Algorithm context that can be set to use a different algorithm to
 * solve the problem. This allows extendibility of the algorithms to solve the
 * problem
 */
public class AlgorithmContext {

	private AlgorithmStrategy algorithmStrategy;

	public void setAlgorithmStrategy(AlgorithmStrategy algorithmStrategy) {
		this.algorithmStrategy = algorithmStrategy;
	}

	public void solveProblem(List<Package> packages) {
		algorithmStrategy.solveProblem(packages);
	}

}
