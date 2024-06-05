/**
 *     PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 *
 *     This file is part of PureEdgeSim Project.
 *
 *     PureEdgeSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PureEdgeSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 *     
 *     @author Charafeddine Mechalikh
 **/
package com.mechalikh.pureedgesim.network;

public class Bandwidth{
	
	
	protected String ApplicationName;
	protected double usedBandwidth = 0;
	protected double MaxBandwidth = 0;

	
	public Bandwidth (String name, double MaxBandwidth, double usedBandwidth) {
		this.ApplicationName = name;
		this.MaxBandwidth = MaxBandwidth;
		this.usedBandwidth = usedBandwidth;
	}
	
	public double getCustomUsedBandwidth() {
		// Return bandwidth usage in bits per second
		//System.out.println("VALORE RESTITUITO DALLA GETCUSTOMUSEDBAND: " + Math.min(MaxBandwidth, usedBandwidth));
		return Math.min(MaxBandwidth, usedBandwidth);
	}
}

