package com.mobiquityinc.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mobiquityinc.model.Item;
import com.mobiquityinc.model.Package;

import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

/**
 * @author Dewald Pieters
 *
 */
/*
 * This class represents a strategy/algorithm which is available to solve the
 * packing problem
 */
public class BinaryLPAlgorithmStrategy implements AlgorithmStrategy {

	/*
	 * This method solves the optimization problem for a package by maximizing
	 * the cost of selected items given the constraints.
	 */
	@Override
	public void solveProblem(List<Package> packages) {

		packages.stream().forEachOrdered(packageToPack -> {

			// Map the package's item costs to a double array required by the
			// model
			double[] itemCosts = packageToPack.getAvailableItems().stream().mapToDouble(item -> item.getCost())
					.toArray();
			// Map the corresponding item weights to a double array for the
			// model
			double[] itemWeights = packageToPack.getAvailableItems().stream().mapToDouble(item -> item.getWeight())
					.toArray();
			/*
			 * Instantiate an instance of the linear program and provide it with
			 * an objective function which is to maximize the total cost of all
			 * the items
			 */
			LinearProgram lp = new LinearProgram(itemCosts);
			// As we want to maximize, set the minimize option to false
			lp.setMinProblem(false);

			// For each item, set the constraint that an item should be 0 or 1
			// as we
			// should either select it or not.
			for (int i = 0; i < packageToPack.getAvailableItems().size(); i++) {
				lp.setBinary(i);
			}
			/*
			 * Add constraint that the total weight of selected items should be
			 * <= the maximum package weight OR equal to the 100 should the
			 * provided package weight be more than 100.
			 */
			lp.addConstraint(new LinearSmallerThanEqualsConstraint(itemWeights,
					packageToPack.getMaximumWeight() <= 100 ? packageToPack.getMaximumWeight() : 100,
					"constraint_totalItemWeightLessThanOrEqualToPackageMaximumWeight"));
			/*
			 * Add constraint to each item which states the weight of an item
			 * must be <= 100 and the cost must be <= 100 to be selected.
			 */
			for (int i = 0; i < packageToPack.getAvailableItems().size(); i++) {
				double[] itemWeightRestrictions = new double[itemWeights.length];
				double[] itemCostRestrictions = new double[itemCosts.length];
				itemWeightRestrictions[i] = itemWeights[i];
				itemCostRestrictions[i] = itemCosts[i];
				// For each item's cost and wight only the item's index in the
				// double[] should have a value. All remaining and previous
				// indexes
				// must be 0.
				for (int j = i + 1; j < packageToPack.getAvailableItems().size(); j++) {
					itemWeightRestrictions[j] = 0;
					itemCostRestrictions[j] = 0;
				}
				// Add the weight constraint for the item
				lp.addConstraint(new LinearSmallerThanEqualsConstraint(itemWeightRestrictions, 100,
						"constraint_itemWeightSmallerThanEquals100" + i));
				// Add the cost constraint for the item
				lp.addConstraint(new LinearSmallerThanEqualsConstraint(itemCostRestrictions, 100,
						"constraint_itemCostSmallerThanEquals100" + i));
			}

			// Instantiate a solver instance to solve the problem set up above
			LinearProgramSolver solver = SolverFactory.newDefault();
			/*
			 * Solve the problem. The solver returns an array with the items
			 * selected. A selected item is represented by a 1 and non selected
			 * item is represented by a 0. The items are returned in the same
			 * order as provided as input which makes specific selected items
			 * identifiable.
			 */
			double[] solution = solver.solve(lp);

			double totalPackageCost = 0;
			double totalPackageWeight = 0;
			// Loop through the items to get the result from the solution
			for (int i = 0; i < packageToPack.getAvailableItems().size(); i++) {
				// Add selected items to the Package object
				if ((int) solution[i] == 1) {
					Item selectedItem = packageToPack.getAvailableItems().get(i);
					packageToPack.getSelectedItems().add(selectedItem);
					// Add the selected item's cost to the total cost carried by
					// the
					// package
					totalPackageCost += selectedItem.getCost();
					// Add the selected item's weight to the total weight
					// carried by
					// the package
					totalPackageWeight += selectedItem.getWeight();
				}
			}
			// Set the total cost and weight of the package
			packageToPack.setCostOfItems(totalPackageCost);
			packageToPack.setWeightOfItems(totalPackageWeight);
		});

		choosePackagesToSend(packages);
	}

	/*
	 * In order to send a package in the list of provided packages from the
	 * file, there are a few restrictions that need to be applied: Only send a
	 * package if 1 or more items were selected. If there are multiple packages
	 * with with the same cost, the package which weighs less should be sent.
	 */
	protected void choosePackagesToSend(List<Package> packages) {
		/*
		 * Create a map with a key value pair. The key is the total cost of the
		 * package and the value the number of packages with the same cost
		 */
		Map<Double, Long> result = packages.stream()
				.collect(Collectors.groupingBy(Package::getCostOfItems, Collectors.counting()));

		List<Double> duplicatePackageCosts = new ArrayList<>();
		// Loop through each map entry
		for (Map.Entry<Double, Long> entry : result.entrySet()) {
			// Only consider costs which occur more than once and is not 0
			if (entry.getValue() > 1 && entry.getKey() != 0) {
				/*
				 * Find the package in the list the same total cost as the cost
				 * which occurs more than once with and has the lightest weight
				 */
				Package pack = packages.stream().filter(p -> p.getCostOfItems() == entry.getKey())
						.min(Comparator.comparingDouble(Package::getWeightOfItems)).get();
				// Set the package to be sent
				pack.setSendPackage(true);
				/*
				 * Add the cost of the package to the list of costs which occur
				 * in more than once package
				 */
				duplicatePackageCosts.add(pack.getCostOfItems());
			}
		}
		// Get all the packages that does not have a duplicate cost
		packages.stream().filter(p -> !duplicatePackageCosts.contains(p.getCostOfItems())).forEachOrdered(p -> {
			// Only select the package if 1 or more items exist in the package's
			// selected items
			if (p.getSelectedItems().size() > 0) {
				p.setSendPackage(true);
			}
		});
	}

}
