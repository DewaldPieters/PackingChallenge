package com.mobiquityinc.packer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.model.Item;
import com.mobiquityinc.model.Package;

/**
 * @author Dewald Pieters
 *
 */
public class PackerTest {
	// Package read from file has valid format
	@Test
	public void lineHasValidPackage() throws APIException {
		String line = "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";
		Package pack = Packer.extractPackageData(Stream.of(line)).stream().findFirst().orElse(null);
		assertNotNull(pack);
		assertNotNull(pack.getAvailableItems());
		assertThat(pack.getAvailableItems(), not(IsEmptyCollection.empty()));
		assertThat(pack.getMaximumWeight(), notNullValue());
	}

	// All items for a package read from file have valid formats
	@Test
	public void lineHasValidPackageItems() throws APIException {
		String line = "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";
		Package pack = Packer.extractPackageData(Stream.of(line)).stream().findFirst().orElse(null);
		pack.getAvailableItems().parallelStream().forEach(item -> {
			assertThat(item.getWeight(), notNullValue());
			assertThat(item.getWeight(), greaterThan(0.0));
			assertThat(item.getIndexNumber(), notNullValue());
			assertThat(item.getCost(), notNullValue());
		});
	}

	@Test
	public void packReturnsSolutionInCorrectFormat() throws APIException {
		List<Item> selectedItems = new ArrayList<Item>() {
			{
				add(new Item(1, 85.31, 29));
				add(new Item(2, 85.31, 29));
			}
		};
		List<Package> packages = new ArrayList<>();
		Package pack1 = new Package();
		pack1.setSelectedItems(selectedItems);
		pack1.setSendPackage(true);
		Package pack2 = new Package();
		pack1.setSendPackage(true);
		packages.add(pack1);
		packages.add(pack2);
		String result = Packer.convertSolutionToString(packages);
		assertThat(result, not(""));
		String[] lines = result.split(System.getProperty("line.separator"));
		assertThat(lines[1], equalTo("-"));
		assertThat(lines[0], equalTo("1,2"));

	}

	@Test(expected = APIException.class)
	public void convertSolutionToStringFail() throws APIException {
		List<Package> packages = new ArrayList<>();
		Package pack1 = new Package();
		pack1.setSendPackage(true);
		packages.add(pack1);
		Packer.convertSolutionToString(packages);

	}

	// Package in line read from file has invalid weight
	@Test(expected = APIException.class)
	public void lineHasInvalidPackageWeight() throws APIException {
		String line = " : (1,53.38,€45) ";
		Packer.extractPackageData(Stream.of(line));
	}

	// Invalid absolute file path provided
	@Test(expected = APIException.class)
	public void invalidFilePath() throws APIException {
		Packer.readFile("abcd");
	}

	// Package in line read from file has invalid format
	@Test(expected = APIException.class)
	public void lineHasNoColonAfterPackage() throws APIException {
		String line = "81 (1,53.38,€45)";
		Packer.extractPackageData(Stream.of(line));
	}

	// Package items in line read from file have invalid format
	@Test(expected = APIException.class)
	public void lineHasInvalidItemWithIncorrectParentheses() throws APIException {
		String line = "81 : (1,53.38,€45) (2,88.62,€98 (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";
		Packer.extractPackageData(Stream.of(line));
	}

	// Package items in line read from file have invalid format
	@Test(expected = APIException.class)
	public void lineHasInvalidItemWithIncorrectCommaSeperation() throws APIException {
		String line = "81 : (1,53.38,€45) (2,88.62,€98) (378.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";
		Packer.extractPackageData(Stream.of(line));
	}

}
