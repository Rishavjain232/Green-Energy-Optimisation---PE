package org.app.iiitb.peorig.multipledays;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class SimulationMain {
	
	static final int runfor = 30;
	static boolean runpython = false;
	static float area=10000;
	static float efficiency = 0.15f;
	static final String resultfile = "./logs/resultfile.log";
	static PrintWriter wr;
    
	public static void main(String[] args) 
			throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        
		String tracefile = "./trace/high_service_time2.txt";	//  "./trace/high_service_time2.txt";
		
    	PrintWriter writer = new PrintWriter(resultfile, "UTF-8");
    	
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Simulation Started: "+sdf.format(Calendar.getInstance().getTime()));
		writer.println("Simulation Started: "+sdf.format(Calendar.getInstance().getTime()));
		
		if(runpython)
			wr = new PrintWriter("./logs/pythonlogs.log");
		
//		Setting up variables - running simulation
    	final String datacenterxml = "datacenters.xml";
    	final double threshold = 0.40;
    	
    	String loadforwardfile = "./logs/loadforward.log";
    	String logfile = "./logs/simulation.log";
    	
		FileOutputStream fos =  new FileOutputStream(loadforwardfile); fos.close();
		fos = new FileOutputStream(logfile); fos.close();
		
		PrintWriter renpWriter = new PrintWriter("./logforplot/RenPercents.csv", "UTF-8");
		PrintWriter energyWriter = new PrintWriter("./logforplot/RenTotalEnergy.csv", "UTF-8");
		renpWriter.println("DC1,DC2,DC3,DC4");
		energyWriter.println("DC1_TotalEnergy, DC1_RenEnergy, DC2_TotalEnergy, DC2_RenEnergy, "
				+ "DC3_TotalEnergy, DC3_RenEnergy, DC4_TotalEnergy, DC4_RenEnergy,");
		
		writer.println("\nWith Predictions ");
		System.out.println("\nWith Predictions ");
		
		for(int day=1;day<=runfor;day++) {
			String energy_prefix = "./PredEnergy/energy_pred_file_"+day+"_";
	    	String load_prefix = "./PredLoad/load_pred_file";
	    	String act_energy_prefix = "./ActualEnergy/energy_act_file_"+day+"_";
	    	String jobloadstr = "./PowerConsumption/JobLoad_"+day+"_";
	    	
	    	predictionModule(runpython, day);			// Running python prediction code
			
			writer.println("Day: "+day+" : "+sdf.format(Calendar.getInstance().getTime()));
			System.out.println("Day: "+day+" : "+sdf.format(Calendar.getInstance().getTime()));
			
			Simulator sim = new Simulator(tracefile, datacenterxml, threshold, 
	        		loadforwardfile, logfile, energy_prefix, load_prefix, act_energy_prefix, jobloadstr, 
	        		writer, renpWriter, energyWriter);
	        sim.runSimulation();
		}
		renpWriter.close();
		energyWriter.close();
		
//      Running simulation with no energy data - random assignment
        loadforwardfile = "./logs/loadforward_nosched.log";
    	logfile = "./logs/simulation_nosched.log";
    	
    	fos =  new FileOutputStream(loadforwardfile);  fos.close();
		fos = new FileOutputStream(logfile);  fos.close();
        
		renpWriter = new PrintWriter("./logforplot/RenPercents_nosched.csv", "UTF-8");
		energyWriter = new PrintWriter("./logforplot/RenTotalEnergy_nosched.csv", "UTF-8");
		renpWriter.println("DC1, DC2, DC3, DC4,");
		energyWriter.println("DC1_TotalEnergy, DC1_RenEnergy, DC2_TotalEnergy, DC2_RenEnergy, "
				+ "DC3_TotalEnergy, DC3_RenEnergy, DC4_TotalEnergy, DC4_RenEnergy,");
		
		writer.println("\nWithout Predictions (Random)");
		System.out.println("\nWithout Predictions (Random)");
        for(int day=1;day<=runfor;day++) {
//			String tracefile = "./trace/trace_200000.txt";
			String energy_prefix = "./PredEnergy_nosched/energy_nosched";
	    	String load_prefix = "./PredLoad/load_pred_file";
	    	String act_energy_prefix = "./ActualEnergy/energy_act_file_"+day+"_";	//same as the one for previous
	    	String jobloadstr = "./PowerConsumption/JobLoad_nosched_"+day+"_";

			writer.println("Day: "+day+" : "+sdf.format(Calendar.getInstance().getTime()));
			System.out.println("Day: "+day+" : "+sdf.format(Calendar.getInstance().getTime()));
			Simulator nosched = new Simulator(tracefile, datacenterxml, threshold, 
	        		loadforwardfile, logfile, energy_prefix, load_prefix, act_energy_prefix, jobloadstr,
	        		writer, renpWriter, energyWriter);
	        nosched.runSimulation();
		}
        renpWriter.close();
		energyWriter.close();
		
		writer.close();
		if(runpython)
			wr.close();
		
		String figOutFilePrefix = "./z_plots/RenPercent_trace3_fig_";
		printRenPercent(figOutFilePrefix);
		
		System.out.println("Simulation Ended: "+sdf.format(Calendar.getInstance().getTime()));
	}
	
	static void printRenPercent(String figOutFilePrefix) throws IOException, InterruptedException {
		System.out.println("Plotting graphs.");
    	String plotPython = "python ./python/PlotResults.py ";
    	String csv1 = "./logforplot/RenPercents.csv ";
    	String csv2 = "./logforplot/RenPercents_nosched.csv ";
        Process plt = Runtime.getRuntime().exec(plotPython + csv1 + csv2 + figOutFilePrefix);
        plt.waitFor();
        System.out.println("Renpercent plot figures saved:\t"+plt.exitValue());
	}
	
	static void predictionModule(boolean runthis,int daynum) throws IOException, InterruptedException {
		if(runthis) {
			wr.println("***************Energy Prediction***************");
        	System.out.println("Running python code");
        	String energyPython = "python ./python/EnergyPrediction.py ";
        	int a=1;
            Process e1 = Runtime.getRuntime().exec(energyPython 
            		+ "./Input_Energy/Input_Energy_"+daynum+"_"+a+".csv "
            		+ "./PredEnergy/energy_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualEnergy/energy_act_file_"+daynum+"_"+a+" "
            		+ area +" "+
            		+ efficiency);
            BufferedReader in = new BufferedReader(new InputStreamReader(e1.getInputStream()));
            while(e1.isAlive()) {
            	wr.println(in.readLine());
           	}
            wr.println(a);
            a++;
            Process e2 = Runtime.getRuntime().exec(energyPython 
            		+ "./Input_Energy/Input_Energy_"+daynum+"_"+a+".csv "
            		+ "./PredEnergy/energy_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualEnergy/energy_act_file_"+daynum+"_"+a+" "
            		+ area +" "+
            		+ efficiency);
            in = new BufferedReader(new InputStreamReader(e2.getInputStream()));
            while(e2.isAlive()) {
            	wr.println(in.readLine());
           	}
            wr.println(a);
            a++;
            Process e3 = Runtime.getRuntime().exec(energyPython 
            		+ "./Input_Energy/Input_Energy_"+daynum+"_"+a+".csv "
            		+ "./PredEnergy/energy_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualEnergy/energy_act_file_"+daynum+"_"+a+" "
            		+ area +" "+
            		+ efficiency);
            in = new BufferedReader(new InputStreamReader(e3.getInputStream()));
            while(e3.isAlive()) {
            	wr.println(in.readLine());
           	}
            wr.println(a);
            a++;
            Process e4 = Runtime.getRuntime().exec(energyPython 
            		+ "./Input_Energy/Input_Energy_"+daynum+"_"+a+".csv "
            		+ "./PredEnergy/energy_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualEnergy/energy_act_file_"+daynum+"_"+a+" "
            		+ area +" "+
            		+ efficiency);
            in = new BufferedReader(new InputStreamReader(e4.getInputStream()));
            while(e4.isAlive()) {
            	wr.println(in.readLine());
           	}
            wr.println(a);
//            e1.waitFor(); e2.waitFor(); e3.waitFor(); e4.waitFor();
            System.out.println("Energy prediction ended:\t"+e1.exitValue()+" "+e2.exitValue()+" "+e3.exitValue()+" "+e4.exitValue());

            wr.println("***************Load Prediction***************");
//            PrintWriter wr = new PrintWriter("error.log");
            String loadPython = "python ./python/LoadPrediction.py ";
            a=1;
            Process l1 = Runtime.getRuntime().exec(loadPython + " "
            		+ "./Input_Load/Load_input_"+daynum+"_"+a+".csv "
            		+ "./PredLoad/load_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualLoad/load_act_file_"+daynum+"_"+a+" ");
            in = new BufferedReader(new InputStreamReader(l1.getInputStream()));
            while(l1.isAlive()) {
            	wr.println(in.readLine());
           	}
//            System.out.println(1);
            wr.println(a);
            a++;
            Process l2 = Runtime.getRuntime().exec(loadPython + " "
            		+ "./Input_Load/Load_input_"+daynum+"_"+a+".csv "
            		+ "./PredLoad/load_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualLoad/load_act_file_"+daynum+"_"+a+" ");
            in = new BufferedReader(new InputStreamReader(l2.getInputStream()));
            while(l2.isAlive()) {
            	wr.println(in.readLine());
            }
//            System.out.println(2);
            wr.println(a);
            a++;
            Process l3 = Runtime.getRuntime().exec(loadPython + " "
            		+ "./Input_Load/Load_input_"+daynum+"_"+a+".csv "
            		+ "./PredLoad/load_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualLoad/load_act_file_"+daynum+"_"+a+" ");
            in = new BufferedReader(new InputStreamReader(l3.getInputStream()));
            while(l3.isAlive()) {
            	wr.println(in.readLine());
            }
//            System.out.println(3);
            wr.println(a);
            a++;
            Process l4 = Runtime.getRuntime().exec(loadPython + " "
            		+ "./Input_Load/Load_input_"+daynum+"_"+a+".csv "
            		+ "./PredLoad/load_pred_file_"+daynum+"_"+a+" "
            		+ "./ActualLoad/load_act_file_"+daynum+"_"+a+" ");
            in = new BufferedReader(new InputStreamReader(l4.getInputStream()));
            while(l4.isAlive()) {
//	            	if(!in.ready() || in.readLine()==null)
//	              		 l4.destroy();
            	wr.println(in.readLine());
            }
//            System.out.println(4);
            wr.println(4);
            System.out.println("Load prediction ended:\t"+l1.exitValue()+" "+l2.exitValue()+" "+l3.exitValue()+" "+l4.exitValue());
//            wr.close();
		}
        else
            System.out.println("Not Running Python Code!");
		System.out.println();
	}
}

//HPC2N-2002-2.2-cln_v3.swf
//SDSC-BLUE-2000-4.2-cln.swf"
//HPC2N-2002-2.1-cln_v2.swf"
