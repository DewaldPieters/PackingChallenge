package com.mobiquityinc.model;

/**
 * @author Dewald Pieters
 *
 */
/*
 * Item class which is a POJO representing the data structure of an item that
 * can be selected to pack in a package
 */
public class Item {

	private int indexNumber;
	private double cost;
	private double weight;

	public int getIndexNumber() {
		return indexNumber;
	}

	public void setIndexNumber(int indexNumber) {
		this.indexNumber = indexNumber;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Item(int indexNumber, double weight, double cost) {
		this.indexNumber = indexNumber;
		this.cost = cost;
		this.weight = weight;
	}

}
