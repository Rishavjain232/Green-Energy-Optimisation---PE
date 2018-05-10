package org.app.iiitb.peorig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class SimulationMain {
	
	static float area=10000;
	static float efficiency = 0.15f;

	public static void main(String[] args) 
			throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        
//		Running python prediction code
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()));
		boolean runpython = false;
		runPythonCodes(runpython);
        
//		Setting up variables - running simulation
    	String loadforwardfile = "./logs/loadforward.log";
    	String logfile = "./logs/simulation.log";
    	String tracefile = "./trace/high_service_time_bada_3.txt";	//high_service_time2
    	String datacenterxml = "datacenters.xml";
    	double threshold = 0.40;
    	String energy_prefix = "./PredEnergy/energy_pred_file";
    	String load_prefix = "./PredLoad/load_pred_file";
    	String act_energy_prefix = "./ActualEnergy/energy_act_file";
    	String jobloadstr = "./PowerConsumption/JobLoad_";
    	
        Simulator sim = new Simulator(tracefile, datacenterxml, threshold, 
        		loadforwardfile, logfile, energy_prefix, load_prefix, act_energy_prefix, jobloadstr);
        sim.runSimulation();

        System.out.println();
        System.out.println(sdf.format(Calendar.getInstance().getTime()));
        System.out.println();
        
//      Running simulation with no energy data - random assignment
        loadforwardfile = "./logs/loadforward_nosched.log";
    	logfile = "./logs/simulation_nosched.log";
    	energy_prefix = "./PredEnergy_nosched/energy_nosched";
    	jobloadstr = "./PowerConsumption/JobLoad_nosched_";
    	
    	Simulator nosched = new Simulator(tracefile, datacenterxml, threshold, 
    			loadforwardfile, logfile, energy_prefix, load_prefix, act_energy_prefix, jobloadstr);
        nosched.runSimulation();
        
		System.out.println(sdf.format(Calendar.getInstance().getTime()));
	}
	
	static void runPythonCodes(boolean runthis) throws IOException, InterruptedException {
//		if(args.length==0) {
//		if(args.length!=0 && args[0].equals("1")) {
		if(runthis) {
	        	System.out.println("Running python code");
	        	String energyPython = "python ./python_multiday/EnergyPrediction.py";
	            Process e1 = Runtime.getRuntime().exec(energyPython + " "
	            		+ "./Input_Energy/Input_Energy_1.csv "
	            		+ "./PredEnergy/energy_pred_file1 "
	            		+ "./ActualEnergy/energy_act_file1 "
	            		+ area +" "+
	            		+ efficiency);
	            Process e2 = Runtime.getRuntime().exec(energyPython + " "
	            		+ "./Input_Energy/Input_Energy_2.csv "
	            		+ "./PredEnergy/energy_pred_file2 "
	            		+ "./ActualEnergy/energy_act_file2 "
	            		+ area +" "+
	            		+ efficiency);
	            Process e3 = Runtime.getRuntime().exec(energyPython + " "
	            		+ "./Input_Energy/Input_Energy_3.csv "
	            		+ "./PredEnergy/energy_pred_file3 "
	            		+ "./ActualEnergy/energy_act_file3 "
	            		+ area +" "+
	            		+ efficiency);
	            Process e4 = Runtime.getRuntime().exec(energyPython + " "
	            		+ "./Input_Energy/Input_Energy_4.csv "
	            		+ "./PredEnergy/energy_pred_file4 "
	            		+ "./ActualEnergy/energy_act_file4 "
	            		+ area +" "+
	            		+ efficiency);
	            e1.waitFor(); e2.waitFor(); e3.waitFor(); e4.waitFor();
	            System.out.println("Energy prediction ended:\n"+e1.exitValue()+" "+e2.exitValue()+" "+e3.exitValue()+" "+e4.exitValue());
	            
	            String loadPython = "python ./python/LoadPrediction.py ";
	            Process l1 = Runtime.getRuntime().exec(loadPython + " "
	            		+ "./Input_Load/Load_input1.csv "
	            		+ "./PredLoad/load_pred_file1 "
	            		+ "./ActualLoad/load_act_file1");
	            BufferedReader in = new BufferedReader(new InputStreamReader(l1.getInputStream()));
	            while(l1.isAlive()) {
	            	System.out.println(in.readLine());
//	           	 System.out.println(ret);
	           	}
	            System.out.println(1);
	            Process l2 = Runtime.getRuntime().exec(loadPython + " "
	            		+ "./Input_Load/Load_input2.csv "
	            		+ "./PredLoad/load_pred_file2 "
	            		+ "./ActualLoad/load_act_file2");
	            in = new BufferedReader(new InputStreamReader(l2.getInputStream()));
	            while(l2.isAlive()) {
	            	System.out.println(in.readLine());
	            }
	            System.out.println(2);
	            Process l3 = Runtime.getRuntime().exec(loadPython + " "
	            		+ "./Input_Load/Load_input3.csv "
	            		+ "./PredLoad/load_pred_file3 "
	            		+ "./ActualLoad/load_act_file3");
	            in = new BufferedReader(new InputStreamReader(l3.getInputStream()));
	            while(l3.isAlive()) {
	            	System.out.println(in.readLine());
	            }
	            System.out.println(3);
	            Process l4 = Runtime.getRuntime().exec(loadPython + " "
	            		+ "./Input_Load/Load_input4.csv "
	            		+ "./PredLoad/load_pred_file4 "
	            		+ "./ActualLoad/load_act_file4");
	            in = new BufferedReader(new InputStreamReader(l4.getInputStream()));
	            while(l4.isAlive()) {
//	            	if(!in.ready() || in.readLine()==null)
//	              		 l4.destroy();
	            	System.out.println(in.readLine());
	            }
	            System.out.println(4);
	            System.out.println("Load prediction ended:\n"+l1.exitValue()+" "+l2.exitValue()+" "+l3.exitValue()+" "+l4.exitValue());
	        }
	        else
	            System.out.println("Not Running Python Code");
			System.out.println();
	}
}

//HPC2N-2002-2.2-cln_v3.swf
//SDSC-BLUE-2000-4.2-cln.swf"
//HPC2N-2002-2.1-cln_v2.swf"
