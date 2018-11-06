package com.mobiquityinc.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mobiquityinc.model.Item;
import com.mobiquityinc.model.Package;

public class BinaryLPAlgorithmStrategyTest {
	// Item with a weight which is more than the package weight should not be
	// selected
	@Test
	public void doNotSelectItemWithWeightMoreThanPackageWeight() {
		List<Item> availableItems = new ArrayList<Item>() {
			{
				add(new Item(1, 15.3, 34));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(8, availableItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.solveProblem(packages);
		assertThat(packages.stream().findFirst().orElse(null).getSelectedItems(), hasSize(equalTo(0)));
	}

	// Package has maximum weight of 100. If maximum weight provided as input is
	// more than 100, the linear algorithm will limit it to 100
	@Test
	public void limitPackageWeightMoreThan100To100() {
		List<Item> availableItems = new ArrayList<Item>() {
			{
				add(new Item(1, 105, 34));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(110, availableItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.solveProblem(packages);
		assertThat(packages.stream().findFirst().orElse(null).getSelectedItems(), hasSize(equalTo(0)));
	}

	// Item with weight more than 100 should not be selected
	@Test
	public void doNotSelectItemWithWeightMoreThan100() {
		List<Item> availableItems = new ArrayList<Item>() {
			{
				add(new Item(1, 105, 34));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(98, availableItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.solveProblem(packages);
		assertThat(packages.stream().findFirst().orElse(null).getSelectedItems(), hasSize(equalTo(0)));
	}

	// Item with cost more than 100 should not be selected
	@Test
	public void doNotSelectItemWithCostMoreThan100() {
		List<Item> availableItems = new ArrayList<Item>() {
			{
				add(new Item(1, 15, 101));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(98, availableItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.solveProblem(packages);
		assertThat(packages.stream().findFirst().orElse(null).getSelectedItems(), hasSize(equalTo(0)));
	}

	// The optimal combination of items is selected for the package and the cost
	// is maximized
	@Test
	public void selectOptimalCombinationOfItemsWithMaximumCost() {
		List<Item> availableItems = new ArrayList<Item>() {
			{
				add(new Item(1, 85.31, 29));
				add(new Item(2, 14.55, 74));
				add(new Item(3, 3.98, 16));
				add(new Item(4, 26.24, 55));
				add(new Item(5, 63.69, 52));
				add(new Item(6, 76.25, 75));
				add(new Item(7, 60.02, 74));
				add(new Item(8, 93.18, 35));
				add(new Item(9, 89.95, 78));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(75, availableItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.solveProblem(packages);
		Package pack = packages.stream().findFirst().orElse(null);
		assertThat(pack.getCostOfItems(), equalTo(148.0));
		assertThat(pack.getSelectedItems(), hasSize(equalTo(2)));
		assertThat(pack.getSelectedItems().parallelStream().filter(i -> i.getIndexNumber() == 2).findAny().orElse(null),
				notNullValue());
		assertThat(pack.getSelectedItems().parallelStream().filter(i -> i.getIndexNumber() == 7).findAny().orElse(null),
				notNullValue());
	}

	// Package must be sent if more than 1 item was selected by the algorithm
	@Test
	public void sendPackageIfOneOrMoreItemsWereSelected() {
		List<Item> selectedItems = new ArrayList<Item>() {
			{
				add(new Item(1, 85.31, 29));
				add(new Item(2, 14.55, 74));
			}
		};
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(8, new ArrayList<Item>(), selectedItems));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.choosePackagesToSend(packages);
		assertThat(packages.stream().findFirst().orElse(null).isSendPackage(), equalTo(true));
	}

	// Package must not be sent if no items were selected by the algorithm
	@Test
	public void doNotChoosePackageIfNoItemsWereSelected() {
		List<Package> packages = new ArrayList<Package>() {
			{
				add(new Package(8, new ArrayList<Item>(), new ArrayList<Item>()));
			}
		};
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.choosePackagesToSend(packages);
		assertThat(packages.stream().findFirst().orElse(null).isSendPackage(), equalTo(false));
	}

	// If there are more than one package with the same maximum cost benefit,
	// only send the package with the lowest weight
	@Test
	public void ifMultiplePackagesWithSameCostSendOneWithLowestWeight() {
		List<Item> selectedItems = new ArrayList<Item>() {
			{
				add(new Item(1, 85.31, 29));
			}
		};
		List<Package> packages = new ArrayList<>();
		Package pack1 = new Package();
		pack1.setCostOfItems(55.31);
		pack1.setWeightOfItems(22.9);
		pack1.setSelectedItems(selectedItems);
		Package pack2 = new Package();
		pack2.setCostOfItems(55.31);
		pack2.setWeightOfItems(22.89);
		pack2.setSelectedItems(selectedItems);
		packages.add(pack1);
		packages.add(pack2);
		BinaryLPAlgorithmStrategy algorithm = new BinaryLPAlgorithmStrategy();
		algorithm.choosePackagesToSend(packages);
		assertThat(pack1.isSendPackage(), equalTo(false));
		assertThat(pack2.isSendPackage(), equalTo(true));
	}

}
