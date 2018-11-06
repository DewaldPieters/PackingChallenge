package com.mobiquityinc.packer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mobiquityinc.algorithm.AlgorithmContext;
import com.mobiquityinc.algorithm.BinaryLPAlgorithmStrategy;
import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.model.Item;
import com.mobiquityinc.model.Package;
import com.mobiquityinc.util.StringUtil;

/**
 * @author Dewald Pieters
 *
 */
/*
 * The Packer class can be run as a java application. If no string input
 * variable is provided to main arguments, the user will be prompted to enter
 * the absolute path of the file containing the data
 * 
 * Strategy to develop program:
 * 
 * My strategy was to follow a TDD approach. I started by defining test cases
 * for reading the file and tests for validating the input of the file. I then
 * wrote the minimum amount of code to try and make the tests pass. for those
 * that failed, I refactored the code until the failed tests passed. This was an
 * iterative approach and I followed the same pattern for the other identified
 * requirements such as (Transforming the file into objects, solving the problem
 * using an algorithm, choosing packages to send etc.).
 * 
 * Algorithm used:
 * 
 * The problem is solved using the Integer linear programming algorithm and more
 * specifically a 0-1 Binary linear algorithm given that you either select or
 * don't select an item to pack. This will always produce the optimal answer for
 * problems such as this which are linear in nature. To use the linear
 * programming approach, a popular library is being used to solve the problem.
 * NOTE: the library user prints some of its calculations and decisions to the
 * console and does not provide the capability to switch this off.
 * 
 * Data Structures:
 * 
 * For each line read from the data file, a Package object is created (in
 * com.mobiquityinc.model package). This package has a List/ArrayList
 * (Collections framework) of Item objects, which relate to the given Package.
 * The inputs to the 0-1 Binary linear programming solver are double Arrays. The
 * package data can be transformed easily into this format to solve the problem.
 * This data structure was chosen to ensure that the application could be
 * extended to expose for example a RESTFul service that could return results as
 * JSON, XML or SOAP. From this data structure, basically any other data
 * structure can be derived which makes it very generic. Also, from a
 * maintainability point of view it is easy to understand and maintain.
 * 
 * Design Pattern:
 * 
 * For this problem I implemented the strategy pattern to ensure that the
 * packing problem can be solved using different algorithms. This in turn
 * ensures (extendibility, maintainability, single responsibility, interfaces
 * and applies open close principle). This behavioural pattern is best suited
 * for these kind of situations as it gives this application to solve the same
 * issue using different algorithms (even though there is currently only one).
 */

public class Packer {
	/*
	 * Main method to run as java program. The program takes in as its first
	 * argument the absolute file path to a test file and prints the results to
	 * the console.
	 */
	public static void main(String[] args) throws APIException {

		String eneterdAbsoluteFilePath = "";
		if (args.length == 0) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter the absolute path for txt data file: ");
			eneterdAbsoluteFilePath = scanner.nextLine();
			scanner.close();
			System.out.println(pack(eneterdAbsoluteFilePath));
		} else {
			System.out.println(pack(args[0]));
		}

	}

	/*
	 * This method performs the high level orchestration to solve the packing
	 * problem. It takes in the absolute file path of a file as a String with
	 * test data.
	 */
	public static String pack(String absoluteFilePath) throws APIException {
		// Read file into stream
		try (Stream<String> stream = readFile(absoluteFilePath);) {
			// Convert the stream into a list o Package objects
			List<Package> packages = extractPackageData(stream);
			// Instantiate the algorithm strategy context
			AlgorithmContext algorithm = new AlgorithmContext();
			// Set the strategy to user as the binary linear programming
			// strategy
			algorithm.setAlgorithmStrategy(new BinaryLPAlgorithmStrategy());
			// Solve the problem using the binary linear programming algorithm
			algorithm.solveProblem(packages);
			// Return the result as a string
			return convertSolutionToString(packages);
		} catch (Exception e) {
			throw new APIException(e.getMessage());
		}
	}

	// Produces the solution of the packing problem as a string
	protected static String convertSolutionToString(List<Package> packages) throws APIException {

		try {
			StringBuilder solution = new StringBuilder();
			packages.stream().forEach(p -> {
				if (p.isSendPackage()) {
					// If the package should be sent, map the selected item
					// indexes
					// to a comma separated string and add to the result
					solution.append(p.getSelectedItems().stream().map(i -> String.valueOf(i.getIndexNumber()))
							.collect(Collectors.joining(",")));
				} else {
					// If the package should not be send append "-" to the
					// string
					solution.append("-");
				}
				// Add a new line character to string to print each package on a
				// new
				// line
				solution.append(System.getProperty("line.separator"));
			});
			return solution.toString();
		} catch (Exception e) {
			throw new APIException("Unable to convert solution to string");
		}

	}

	// This method retrieves the provided file data and returns the list of
	// extracted packages required by the calling method to solve a packing
	// problem
	public static Stream<String> readFile(String absoluteFilePath) throws APIException {

		try {
			return Files.lines(Paths.get(absoluteFilePath));
		} catch (Exception e) {
			throw new APIException("Unable to read file");
		}
	}

	// Convert the stream and return a list of Package objects
	protected static List<Package> extractPackageData(Stream<String> stream) throws APIException {
		/*
		 * For each line string in the stream, map the line to a new Package
		 * objects and populate the package's maximum allowed weight, as well as
		 * items/things to choose from. The maximum allowed weight of theF
		 * package is extracted from the line using a generic method in the
		 * string utility class, whereas the available items to choose from for
		 * the given package is extracted and returned by another method.
		 */
		try {
			return stream.map(line -> {
				// Instantiate an regular expression instance with a pattern to
				// get
				// content between ( and ).
				Pattern pattern = Pattern.compile("\\((.*?)\\)");
				// Instantiate new list of items to be returned
				List<Item> availableItems = new ArrayList<>();
				// Instantiate matcher to be able to extract data using the
				// regular
				// expression
				Matcher match = pattern.matcher(line);
				while (match.find()) {
					/*
					 * For each matching sequence found create a new Item object
					 * as it is available for selection. The index number,
					 * weight and cost are extracted using generic methods in
					 * the string utility class.
					 */
					Item item = new Item(Integer.parseInt(StringUtil.getStringUptoString(match.group(1), ",")),
							Double.valueOf(Double
									.parseDouble(StringUtil.getStringBetweenTwoStrings(match.group(1), ",", ","))),
							Double.valueOf(Double.parseDouble(StringUtil.getStringAfterString(match.group(1), ","))));
					// Add the item object to list to be returned
					availableItems.add(item);
				}
				return new Package(Double.parseDouble(StringUtil.getStringUptoString(line, ":")), availableItems);
			}).collect(Collectors.toList());

		} catch (Exception e) {
			throw new APIException(
					"Unable to extract package and package item data due to a formatting error of data in file");
		}
	}
}
