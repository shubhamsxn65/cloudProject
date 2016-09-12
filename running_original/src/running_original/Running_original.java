
package running_original;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.Set;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


public class Running_original {
	private static List<Cloudlet> cloudletList1,cloudletList2;					/** The cloudlet list. */
	private static List<Vm> vmlist;												/** The VM list. */
        private static Map<Integer,Integer> weights;							/** Table for storing weights assigned */
        private static Map<Integer, Integer> assignedTasks;						/** Table for storing assigned tasks to each VM **/ 
	public static void main(String[] args) {

		Log.printLine("Starting Execution!!");

	        try {
			// First step: Initialize the CloudSim package. It should be called
	            	// before creating any entities.
	            	int num_user = 1;   // number of cloud users
	            	Calendar calendar = Calendar.getInstance();
	            	boolean trace_flag = false; //for errors

					// Initialize the CloudSim library
	            	CloudSim.init(num_user, calendar, trace_flag);

					// Second step: Create Datacenters
	            	//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
	            	@SuppressWarnings("unused")
                        Datacenter datacenter0 = createDatacenter("Datacenter_0");
                        Datacenter datacenter1 = createDatacenter("Datacenter_1");
                     //   Datacenter datacenter2 = createDatacenter("Datacenter_2");

					//Third step: Create Broker
	            	DatacenterBroker broker = createBroker();
	            	int brokerId = broker.getId();
                     
					//Fourth step: Create one virtual machine
	            	vmlist = new ArrayList<Vm>();
                         
					//VM description
	            	int vmid = 0;
	            	int mips = 200;
	            	long size = 10000; 										/** Image size (MB)**/
	            	int ram = 256;											/** vm memory (MB)**/
	            	long bw = 1000;
	            	int pesNumber = 1; 										/** number of CPUs**/
	            	String vmm = "Xen"; 
                        weights = new HashMap<Integer, Integer> ();
                        assignedTasks = new HashMap<Integer, Integer>();
					
                    //creating VMs
	            	Vm vm1 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                        weights.put(vm1.getId(), 1);
                        assignedTasks.put(vm1.getId(), 0);

                        vmid++;
                        ram*=2;
                        Vm vm2 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                        weights.put(vm2.getId(), 2);    
                        assignedTasks.put(vm2.getId(), 0);

                        vmid++;
                        ram*=2;
	            	Vm vm3 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                        weights.put(vm3.getId(), 4);
                        assignedTasks.put(vm3.getId(), 0);
                       
					//add the VMs to the vmList
	            	vmlist.add(vm1);
	            	vmlist.add(vm2);
                       vmlist.add(vm3);
	            	
					//submit vm list to the broker
                        broker.submitVmList(vmlist);


	            	cloudletList1 = new ArrayList<Cloudlet>();
			cloudletList2 = new ArrayList<Cloudlet>();
					
					//creating cloudlets (tasks to be done)
			cloudletList1 = createCloudlet(brokerId, 500, 0);
                        cloudletList2 = createCloudlet1(brokerId, 500, 0); 
                        
					//submitting list to broker
                        broker.submitCloudletList(cloudletList1);
			broker.submitCloudletList(cloudletList2);
	            	
			/*		//total number of tasks to be done
                        int totalCloudlets=cloudletList1.size() + cloudletList2.size();
                        int totalWorkingpower=0;

                        // calculating total power available for working
                        for(int i=0;i<vmlist.size();i++)
                                totalWorkingpower+=weights.get(vmlist.get(i).getId());
                        totalWorkingpower*=100;

                        //calculating per cloudlet work 
                        double perUnitwork=(double)totalWorkingpower/((totalCloudlets*(totalCloudlets+1))/2);  


                        //My algorithm here
                        Cloudlet cloudlet;

        //for each cloudlet in order of priority
                    for (int i = cloudletList1.size() -1; i >=0; i--)
                    {
                                    cloudlet = cloudletList1.get(i);
                                    int mostFreeVmIndex=-1;
                                    double mostFreeVmvalue=0;

                                    //finding the most suitable VM
                                    for(int vid = 0; vid<vmlist.size();vid++)
                                    {
                                            double freeValue= weights.get(vid)*100 - perUnitwork*assignedTasks.get(vid);
                                            if(freeValue > mostFreeVmvalue)
                                            {
                                                    //till now most situable
                                                    mostFreeVmIndex=vid;
                                                    mostFreeVmvalue=freeValue;
                                             }
                                    }
                        //updating the assigned tasks table
                        assignedTasks.put(mostFreeVmIndex, assignedTasks.get(mostFreeVmIndex) + i);

                        //binding the most suitable VM to this cloudlet
                        broker.bindCloudletToVm(cloudlet.getCloudletId(),vmlist.get(mostFreeVmIndex).getId());
                      }
                        //repeating the process for 2nd cloudlet list
                        Cloudlet cloudletx;
                        for (int j=cloudletList2.size()-1; j>=0; j--)
                        {
                                cloudletx = cloudletList2.get(j);
                                int mostFreeVmIndex=-1;
                                double mostFreeVmvalue=0;
                                for(int vid = 0; vid<vmlist.size();vid++)
                                {
                                    double freeValue= weights.get(vid)*100 - perUnitwork*assignedTasks.get(vid);
									if(freeValue > mostFreeVmvalue)
                                    {
                                        mostFreeVmIndex=vid;
                                        mostFreeVmvalue=freeValue;
                                    }
                                }
								assignedTasks.put(mostFreeVmIndex, assignedTasks.get(mostFreeVmIndex) + j);
                                broker.bindCloudletToVm(cloudletx.getCloudletId(),vmlist.get(mostFreeVmIndex).getId());
                        }*/
					// Sixth step: Starts the simulation
	            	CloudSim.startSimulation();
			List<Cloudlet> newList = broker.getCloudletReceivedList();

	            	CloudSim.stopSimulation();
					// Final step: Print results when simulation is over
	            	printCloudletList(newList);
                    Log.printLine("Execution finished!");
	        }
	        catch (Exception e) {
                    System.out.println(e.getMessage());
	            e.printStackTrace();
	            Log.printLine("The simulation has been terminated due to an unexpected error");
	        }
	    }
		private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift){
				// Creates a container to store Cloudlets
				LinkedList<Cloudlet> list1 = new LinkedList<Cloudlet>();
				//cloudlet parameters
				long length = 40000;
				long fileSize = 300;
				long outputSize = 300;
				int pesNumber = 1;
				UtilizationModel utilizationModel = new UtilizationModelFull();
				Cloudlet[] cloudlet = new Cloudlet[cloudlets];
				for(int i=0;i<cloudlets;i++)
				{
					cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
					cloudlet[i].setUserId(userId);
					list1.add(cloudlet[i]);
					length+=1000;
				}
				return list1;
		}
		private static List<Cloudlet> createCloudlet1(int userId, int cloudlets, int idShift){
			LinkedList<Cloudlet> list2 = new LinkedList<Cloudlet>();
			long length = 35000;
			long fileSize = 300;
			long outputSize = 300;
			int pesNumber = 1;
			UtilizationModel utilizationModel = new UtilizationModelFull();
			Cloudlet[] cloudlet = new Cloudlet[cloudlets];
			for(int i=0;i<cloudlets;i++)
			{
				cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
				cloudlet[i].setUserId(userId);
				list2.add(cloudlet[i]);
				length+=2000;
			}
			return list2;
		}
		private static Datacenter createDatacenter(String name){

	        List<Host> hostList = new ArrayList<Host>();
			List<Pe> peList = new ArrayList<Pe>();
			List<Pe> peList1 = new ArrayList<Pe>();
			int mips = 1200;
			peList.add(new Pe(0, new PeProvisionerSimple(800)));
			peList1.add(new Pe(0, new PeProvisionerSimple(mips)));
			peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
			int hostId=0;   
			int ram = 1024*2;  //host memory (MB)
			long storage = 1000000; //host storage
			int bw = 100000;
			hostList.add(
				new Host( hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerSpaceShared(peList))
			);
			hostId++;
			hostList.add(
                                new Host(
                                hostId,
                                new RamProvisionerSimple(ram),
                                new BwProvisionerSimple(bw),
                                storage,
                                peList1,
                                new VmSchedulerSpaceShared(peList))
			);
			String arch = "x86"; // system architecture
			String os = "Linux"; // operating system
			String vmm = "Xen";
			double time_zone = 10.0; // time zone this resource located
			double cost = 3.0; // the cost of using processing in this resource
			double costPerMem = 0.05; // the cost of using memory in this resource
			double costPerStorage = 0.001; // the cost of using storage in this resource
			double costPerBw = 0.0; // the cost of using bw in this resource
			LinkedList<Storage> storageList = new LinkedList<Storage>();
			DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, 6.0, 0.6,0.06, costPerBw);
			DatacenterCharacteristics characteristics1 = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, 4.0, 0.4,0.04, costPerBw);
			DatacenterCharacteristics characteristics2 = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, 5.0, 0.5,0.05, costPerBw);
			Datacenter datacenter = null;
			try {
			if(name == "Datacenter_0")
				datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
			else if (name== "Datacenter_1")
				datacenter = new Datacenter(name, characteristics1, new VmAllocationPolicySimple(hostList), storageList, 0);
			else
				datacenter = new Datacenter(name, characteristics2, new VmAllocationPolicySimple(hostList), storageList, 0);
			}
			catch (Exception e) {e.printStackTrace();}
			return datacenter;
		} 

	    private static DatacenterBroker createBroker(){

	    	DatacenterBroker broker = null;
	        try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	    	return broker;
	    }
      /**
	     * Prints the Cloudlet objects
	     * @param list  list of Cloudlets
	     */
	    private static void printCloudletList(List<Cloudlet> list) {
	        int size = list.size();
	        Cloudlet cloudlet;

	        String indent = "    ";
	        Log.printLine();
	        Log.printLine("========== OUTPUT ==========");
	        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

	        DecimalFormat dft = new DecimalFormat("###.##");
	        for (int i = 0; i < size; i++) {
	            cloudlet = list.get(i);
	            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

	            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
	                Log.print("SUCCESS");

	            	Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
	                     indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
                             indent + indent + dft.format(cloudlet.getFinishTime()));
	            }
	        }

	    }
}
