package com.mobiquityinc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dewald Pieters
 *
 */
/*
 * Package class which is a POJO representing the data structure of a package
 * and its variables
 */
public class Package {

	private double maximumWeight;
	private double costOfItems;
	private double weightOfItems;
	private boolean sendPackage = false;
	private List<Item> selectedItems;
	private List<Item> availableItems;

	public Package(double maximumWeight, List<Item> availableItems) {
		this.maximumWeight = maximumWeight;
		this.availableItems = availableItems;
		this.selectedItems = new ArrayList<>();
	}

	public Package(double maximumWeight, List<Item> availableItems, List<Item> selectedItems) {
		this.maximumWeight = maximumWeight;
		this.availableItems = availableItems;
		this.selectedItems = selectedItems;
	}

	public Package() {

	}

	public double getMaximumWeight() {
		return maximumWeight;
	}

	public void setMaximumWeight(double maximumWeight) {
		this.maximumWeight = maximumWeight;
	}

	public double getCostOfItems() {
		return costOfItems;
	}

	public void setCostOfItems(double costOfItems) {
		this.costOfItems = costOfItems;
	}

	public double getWeightOfItems() {
		return weightOfItems;
	}

	public void setWeightOfItems(double weightOfItems) {
		this.weightOfItems = weightOfItems;
	}

	public List<Item> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<Item> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public List<Item> getAvailableItems() {
		return availableItems;
	}

	public void setAvailableItems(List<Item> availableItems) {
		this.availableItems = availableItems;
	}

	public boolean isSendPackage() {
		return sendPackage;
	}

	public void setSendPackage(boolean sendPackage) {
		this.sendPackage = sendPackage;
	}

}
