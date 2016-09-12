/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim;

import java.util.*;

/**
 *
 * @author Shubham
 */
public class PriorityBasedLoadBalacing {
    protected List<? extends Vm> vmList;
    protected Map<Integer,Integer> VmWeights;
    protected Map<Integer,Integer> VmAssignedLoad;
    protected  Map<Cloudlet,Integer> cloudlet_assigned;
    Integer total_length,available_proc;
    public PriorityBasedLoadBalacing(List<? extends Vm> list, int l)
    {
        this.vmList=list;
        this.total_length=l;
        VmWeights = new HashMap<Integer,Integer> ();
        VmAssignedLoad = new HashMap<Integer,Integer> ();
        cloudlet_assigned = new HashMap<Cloudlet,Integer> ();
        assignWeights();
        initVmAsignedLoad();
    }
    protected void assignWeights()
    {
        int prev=1;
        available_proc=0;
        for(Vm vm : vmList)
        {
          System.out.println(vm.getId()+" <<<<<<");
          try{
          VmWeights.put(vm.getId(),prev);
          }
          catch(Exception e)
          {
              System.out.println(">>>"+e.getMessage());
          }
          //VmWeights.put
          available_proc+=prev;
          prev*=2; 
        }
    }
    protected void initVmAsignedLoad()
    {
        for(Vm vm : vmList)
            VmAssignedLoad.put(vm.getId(),0);
    }
    protected int allocateVm(Cloudlet cloudlet)
    {
        int vmId=-1,proc_comp=0;
        for(Vm vm: vmList)
        {
            
            
            //double free_value= VmWeights.get(vm.getId())*100 - perUnitWork*VmAssignedLoad.get(vm.getId());
            double proc_assigned=(VmAssignedLoad.get(vm.getId()))/(double)total_length;
            proc_assigned*=(100*available_proc);
            int proc_left= (int) (VmWeights.get(vm.getId()) *100 - (Math.floor(proc_assigned)));
          //  System.out.println(proc_left+"   "+ vm.getId());
            if(proc_left >= proc_comp)
            {
                proc_comp=proc_left;
                vmId=vm.getId();
            }
        }/*
        for(Vm vm: vmList)
        {
            int proc_assigned=(VmAssignedLoad.get(vm.getId()))/VmWeights.get(vm.getId());
           // proc_assigned*=(100*available_proc);
           // int proc_left= (int) (VmWeights.get(vm.getId()) *100 - (Math.floor(proc_assigned)));
            System.out.println(proc_assigned+"   "+ vm.getId());
            if(proc_assigned <= proc_comp)
            {
                proc_comp=proc_assigned;
                vmId=vm.getId();
            }
        }*/
        cloudlet_assigned.put(cloudlet, vmId);
        if(vmId == -1)
             System.out.println("Woooha");
        VmAssignedLoad.put(vmId,VmAssignedLoad.get(vmId)+(int) cloudlet.getCloudletLength());
        return vmId;
    }
    protected void deallocate(Cloudlet cloudlet)
    {
        int vmId=cloudlet_assigned.get(cloudlet);
        VmAssignedLoad.put(vmId,VmAssignedLoad.get(vmId)-(int) cloudlet.getCloudletLength());
        return ;
    }
    protected void clear()
    {
        VmAssignedLoad.clear();
        VmWeights.clear();
        cloudlet_assigned.clear();
    }
}
