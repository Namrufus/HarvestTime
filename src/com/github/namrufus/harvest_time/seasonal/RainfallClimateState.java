package com.github.namrufus.harvest_time.seasonal;

public enum RainfallClimateState {
	DROUGHT, NORMAL, MONSOON;
	
	private static final double defaultRainfallChance = 0.25;
	
	private double rainfallChance;
	
	private RainfallClimateState() {
		rainfallChance = defaultRainfallChance;
	}
	
	public void setRainfallChance(double rainfallChance) { this.rainfallChance = rainfallChance; }
	public double getRainfallChance() { return rainfallChance; }
}
