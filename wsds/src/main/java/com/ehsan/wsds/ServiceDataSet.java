package com.ehsan.wsds;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceDataSet {


	public void extractAverageServices (String inputfile, String outputfile, double ignoreValue) {

		HashMap <Integer, HashMap<Integer, Stat>> serviceDataTime = new HashMap <Integer, HashMap<Integer, Stat>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			String line;
			long num = 0;				
			while ((line = br.readLine()) != null) {
				num++;
				if (line.trim().isEmpty()) continue;
				String[] splits = line.split("\\s+");

				int time, serviceId;
				double value;
				try {										
					//					int userId = Integer.parseInt(splits[0]);
					serviceId = Integer.parseInt(splits[1]);
					time = Integer.parseInt(splits[2]);
					value = Double.parseDouble(splits[3]);
				} catch (Exception e) {
					System.out.println("Line number: " + num);
					e.printStackTrace();
					continue;
				}

				if (value == ignoreValue) continue;

				HashMap<Integer, Stat> serviceData = serviceDataTime.get(time);
				if (serviceData == null) {
					Stat stat = new Stat();
					stat.observeStat(value);
					serviceData = new HashMap<Integer, Stat>();
					serviceData.put (serviceId, stat);
					serviceDataTime.put(time, serviceData);
				} else {
					if (serviceData.get(serviceId) == null) {
						Stat stat = new Stat();
						stat.observeStat(value);
						serviceData.put (serviceId, stat);
					} else {
						serviceData.get(serviceId).observeStat(value);
					}
				}

				if (num % 1000000 == 1) System.out.println("Reading line #" + num);			
			}
			br.close();

			System.out.println("Time Size: " + serviceDataTime.keySet().size());

			// OUTPUT
			for (int time: serviceDataTime.keySet()) {
				PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));		
				HashMap<Integer, Stat> serviceData = serviceDataTime.get(time);
				for (int serviceId: serviceData.keySet()) {
					pw.printf("%d %f %d\n", 
							serviceId, 
							(double)serviceData.get(serviceId).getTotalValue() / (double)serviceData.get(serviceId).getNum(), 
							serviceData.get(serviceId).getNum());

				}
				pw.close();
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//	public void wsCount (String inputfile) {
	//		HashMap <Integer, HashMap<Integer, Stat>> serviceDataTime = new HashMap <Integer, HashMap<Integer, Stat>>();
	//		try {
	//			BufferedReader br = new BufferedReader(new FileReader(inputfile));
	//			String line;
	//			long num = 0;			
	//			int maxServiceNumber = 0;
	//			while ((line = br.readLine()) != null) {
	//				num++;
	//				if (line.trim().isEmpty()) continue;
	//				String[] splits = line.split("\\s+");
	//
	//				int time, serviceId, userId;
	//				double value;
	//				try {					
	//					serviceId = Integer.parseInt(splits[0]);
	//					userId = Integer.parseInt(splits[1]);
	//					time = Integer.parseInt(splits[2]);
	//					value = Double.parseDouble(splits[3]);
	//				} catch (Exception e) {
	//					System.out.println("Line number: " + num);
	//					e.printStackTrace();
	//					continue;
	//				}
	//
	//				if (maxServiceNumber < userId)
	//					maxServiceNumber = userId;
	//			}
	//			System.out.println(maxServiceNumber);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}			
	//	}

	public void extractAvailability(String inputfile, String outputfile) {
		for (int time = 0; time < 64; time++) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(inputfile+time));
				PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));
				String line;			
				while ((line = br.readLine()) != null) {
					if (line.trim().isEmpty()) continue;
					String[] splits = line.split("\\s+");
					int avail = Integer.parseInt(splits[2]);
					int serviceId = Integer.parseInt(splits[0]);										
					pw.printf("%d %.6f\n", serviceId, (double)avail/142.0);					
				}
				br.close();
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}


	public void makeServiceVector(String rtInputFilename, String tpInputFilename, String avInputFilename) {

		int idCounter = 0;
		// Load Services
		List<Service> services = new ArrayList<Service>();

		// Load Single Services from input files
		

		List<Integer> mySet = new ArrayList<Integer>();
		mySet.add(1);
		mySet.add(2);
		mySet.add(3);
		mySet.add(4);
		for (List<Integer> s : powerSet(mySet)) {
			System.out.println(s);
		}
	}


	public <T> List<List<T>> powerSet(List<T> originalList) {
		List<List<T>> Lists = new ArrayList<List<T>>();
		if (originalList.isEmpty()) {
			Lists.add(new ArrayList<T>());
			return Lists;
		}
		List<T> list = new ArrayList<T>(originalList);
		T head = list.get(0);
		List<T> rest = new ArrayList<T>(list.subList(1, list.size())); 
		for (List<T> List : powerSet(rest)) {
			List<T> newList = new ArrayList<T>();
			newList.add(head);
			newList.addAll(List);
			Lists.add(newList);
			Lists.add(List);
		}		
		return Lists;
	}

	public void run() {
		//extractAverageServices("../wsdsinput/rtRate","data/service_rt_t", 0.0);
		//extractAverageServices("../wsdsinput/tpRate","data/service_tp_t", 20.0);
		//wsCount("../wsdsinput/tpRate");
		//extractAvailability("data/service_rt_t","data/service_av_t");
		makeServiceVector("data/service_rt_t", "data/service_tp_t", "data/service_av_t");

	}



}
