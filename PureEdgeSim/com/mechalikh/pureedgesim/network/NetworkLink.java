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

import java.util.ArrayList;
import java.util.List;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.energy.EnergyModelNetworkLink;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationengine.Event;
import com.mechalikh.pureedgesim.simulationengine.SimEntity;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.network.Bandwidth;
import org.apache.fontbox.afm.CharMetric;
import org.w3c.dom.Element;

import javax.imageio.metadata.IIOMetadataNode;


/**
 * Link between two compute nodes in the infrastructure graph
 */
public class NetworkLink extends SimEntity {
	public static final int UPDATE_PROGRESS = 1;
	protected double latency = 0;
	protected double bandwidth = 0;
	protected List<TransferProgress> transferProgressList = new ArrayList<>();
	protected ComputingNode src = ComputingNode.NULL;
	protected ComputingNode dst = ComputingNode.NULL;
	protected SimulationManager simulationManager;
	protected double usedBandwidth = 0;
	protected List<Bandwidth> UsedBandwidthList = new ArrayList<>();
	protected double totalTrasferredData = 0;
	protected EnergyModelNetworkLink energyModel = EnergyModelNetworkLink.NULL;
	protected boolean scheduled = false;

	public enum NetworkLinkTypes {
		WAN, FIBER, MAN, LAN, IGNORE
	}

	protected NetworkLinkTypes type;

	public static final NetworkLink NULL = new NetworkLinkNull();

	public NetworkLink(ComputingNode src, ComputingNode dst, SimulationManager simulationManager,
			NetworkLinkTypes type) {
		super(simulationManager.getSimulation());
		this.simulationManager = simulationManager;
		this.src = src;
		this.dst = dst;
		this.setType(type);
	}

	public NetworkLink() {
	}

	public double getLatency() {
		return latency;
	}

	public NetworkLink setLatency(double latency) {
		this.latency = latency;
		return this;
	}

	public NetworkLink setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}

	/**
	 * Defines the logic to be performed by the network link when the simulation
	 * starts.
	 */
	@Override
	public void startInternal() {
		// Do nothing for now.
	}

	public ComputingNode getSrc() {
		return src;
	}

	public void setSrc(ComputingNode src) {
		this.src = src;
	}

	public ComputingNode getDst() {
		return dst;
	}

	public void setDst(ComputingNode node) {
		this.dst = node;
	}

	@Override
	public void processEvent(Event evt) {
		if (evt.getTag() == UPDATE_PROGRESS) {
			// Update the progress of the current transfers and their allocated bandwidth
			updateTransfersProgress();
			if (this.transferProgressList.size() != 0)
				schedule(this, SimulationParameters.networkUpdateInterval, UPDATE_PROGRESS);
			else
				scheduled = false;
		}

	}

	protected void updateTransfersProgress() {
		usedBandwidth = 0;
		for (Bandwidth bandwidth : UsedBandwidthList) {
			bandwidth.usedBandwidth = 0;
		}
		
		double allocatedBandwidth = 0; 
		
		for (int i = 0; i < transferProgressList.size(); i++) {
			
			if (!SimulationParameters.BandwidthAllocationOnApplicationType) {
				allocatedBandwidth = getBandwidth(transferProgressList.size());
			} else {
				allocatedBandwidth = getBandwidthOnType(transferProgressList.get(i));
			}
			
			// Allocate bandwidth
			usedBandwidth += transferProgressList.get(i).getRemainingFileSize();
			
			if (this.getType()==NetworkLinkTypes.FIBER)
				UpdateBandwidth(transferProgressList.get(i));
				
			transferProgressList.get(i).setCurrentBandwidth(allocatedBandwidth);
			updateTransfer(transferProgressList.get(i));
		}
	}
	
	protected void UpdateBandwidth(TransferProgress T) {
		boolean found = false;
        for (Bandwidth bandwidth : UsedBandwidthList) {
            if (bandwidth.ApplicationName.equals(T.getTask().getAssociatedAppName())) {
                bandwidth.usedBandwidth += T.getRemainingFileSize();
                found = true;
                break;
            }
        }
        if (!found) {
            UsedBandwidthList.add(new Bandwidth(T.getTask().getAssociatedAppName(), bandwidth, T.getRemainingFileSize()));
            //System.out.println("Nuovo oggetto bandwidth relativo all'app" + T.getTask().getAssociatedAppName() + "aggiunto al link: " + this.getId());
        }		
	}

	protected double getBandwidth(double remainingTasksCount) {
		return (bandwidth / (remainingTasksCount > 0 ? remainingTasksCount : 1));
	}
	
	protected double getBandwidthOnType(TransferProgress task) { 
		double BandwidthValue = 0;
		
		if (SimulationParameters.AllocationValue.equals("LATENCY")) {
			
			double latency = task.getTask().getMaxLatency();
			
			if (latency <= 0.05) {
				BandwidthValue = bandwidth*50/100;
			} else if (latency > 0.05 && latency <= 0.2) {
				BandwidthValue = bandwidth*25/100;
			} else if (latency > 0.2) {
				BandwidthValue = bandwidth*10/100;
			}
			
		} else {
			double task_length = task.getTask().getLength();
			
			if (task_length > 1000) {
				BandwidthValue = bandwidth*50/100;
			} else if (task_length > 100 && task_length <= 1000) {
				BandwidthValue = bandwidth*25/100;
			} else if (task_length <= 100) {
				BandwidthValue = bandwidth*10/100;
			}
			
		}
	
		return BandwidthValue;
	}

	protected void updateTransfer(TransferProgress transfer) {

		double oldRemainingSize = transfer.getRemainingFileSize();

		// Update progress (remaining file size)
		if (SimulationParameters.realisticNetworkModel)
			transfer.setRemainingFileSize(transfer.getRemainingFileSize()
					- (SimulationParameters.networkUpdateInterval * transfer.getCurrentBandwidth()));
		else
			transfer.setRemainingFileSize(0);

		double transferDelay = (oldRemainingSize - transfer.getRemainingFileSize()) / transfer.getCurrentBandwidth();
		double x1 = SimulationParameters.dc1_x;
		double y1 = SimulationParameters.dc1_y;
		double x2 = SimulationParameters.dc2_x;
		double y2 = SimulationParameters.dc2_y;
		// Set the task network delay to decide whether it has failed due to latency or
		// not.

		if (type == NetworkLinkTypes.LAN)
			transferDelay+= SimulationParameters.ethernetLatency;

		if (type == NetworkLinkTypes.FIBER)
			transferDelay+= SimulationParameters.FiberLatency;

		if (type == NetworkLinkTypes.MAN) {
			double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

			//System.out.print("distance:" + distance + "\n");

			// Velocità di propagazione della luce in fibra ottica (200,000 km/s)
			double propagationSpeed = 200000; // in km/s
			double distanceInKm = distance / 1000; // Conversione distanza in km

			// Latenza basata sulla distanza
			double distanceLatency = distanceInKm / propagationSpeed; // in secondi

			//System.out.print("Latenza su distanza" + distanceLatency + "\n");

			double latency_const = SimulationParameters.manLatency;
			latency = distanceLatency;
			transferDelay += (latency + latency_const);
		}

		if (type == NetworkLinkTypes.WAN)
			transferDelay+= SimulationParameters.wanLatency;

		transfer.getTask().addActualNetworkTime(transferDelay);

		// Update network usage delay
		if (type == NetworkLinkTypes.LAN){
			transfer.setLanNetworkUsage(transfer.getLanNetworkUsage() + transferDelay);
		}
		
		// Update FIBER network usage delay
		else if (type == NetworkLinkTypes.FIBER){
			transfer.setFiberNetworkUsage(transfer.getFiberNetworkUsage() + transferDelay);
		}

		// Update MAN network usage delay
		else if (type == NetworkLinkTypes.MAN){
			transfer.setManNetworkUsage(transfer.getManNetworkUsage() + transferDelay);
		}

		// Update WAN network usage delay
		else if (type == NetworkLinkTypes.WAN){
			transfer.setWanNetworkUsage(transfer.getWanNetworkUsage() + transferDelay);
		}

		if (transfer.getRemainingFileSize() <= 0) { // Transfer finished
			transfer.setRemainingFileSize(0); // if < 0 set it to 0
			transferFinished(transfer);
		}
	}

	protected void transferFinished(TransferProgress transfer) {

		this.transferProgressList.remove(transfer);

		// Add the network link latency to the task network delay
		double transferDelay = 0.0;

		transfer.getTask().addActualNetworkTime(transferDelay);

		// Remove the previous hop (data has been transferred one hop)
		transfer.getVertexList().remove(0);
		transfer.getEdgeList().remove(0);

		// Data has reached the destination
		if (transfer.getVertexList().size() == 1) {
			// Update logger parameters
			simulationManager.getSimulationLogger().updateNetworkUsage(transfer);

			schedule(simulationManager.getNetworkModel(), latency, NetworkModel.TRANSFER_FINISHED, transfer);
		} else {
			// Still did not reach destination, send it to the next hop
			transfer.setRemainingFileSize(transfer.getFileSize());
			transfer.getEdgeList().get(0).addTransfer(transfer);
		}
	}

	public double getUsedBandwidth() {
		// Return bandwidth usage in bits per second
		//System.out.println("VALORE RESTITUITO DALLA GETUSEDBAND: " + Math.min(bandwidth, usedBandwidth));
		return Math.min(bandwidth, usedBandwidth);
	}

	public NetworkLinkTypes getType() {
		return type;
	}

	public void setType(NetworkLinkTypes type) {
		this.type = type;
	}

	public void addTransfer(TransferProgress transfer) {
		// Used by the energy model to get the total energy consumed by this network
		// link
		totalTrasferredData += transfer.getFileSize();
		transferProgressList.add(transfer);

		if (!scheduled) {
			scheduleNow(this, UPDATE_PROGRESS);
			scheduled = true;
		}
	}

	public EnergyModelNetworkLink getEnergyModel() {
		return energyModel;
	}

	protected void setEnergyModel(EnergyModelNetworkLink energyModel) {
		this.energyModel = energyModel;
	}

	public double getTotalTransferredData() {
		return totalTrasferredData;
	}
	
	public List<Bandwidth> getUsedBandwidthList(){
		return this.UsedBandwidthList;
	}

	/**
	 * Defines the logic to be performed by the network link when the simulation
	 * ends.
	 */
	@Override
	public void onSimulationEnd() {
		// Do something when the simulation finishes.
	}
}
