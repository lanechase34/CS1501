/*
    Implementation of a priority queue that utilizes car.java
    Chase Lane
*/
package cs1501_p3;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
//import java.util.ArrayList;

public class CarsPQ implements CarsPQ_Inter{
	//create the PQs
	//1 for price, 1 for mileage w/ default size of 10
	private Car pricePQ[] = new Car[10];
	private Car mileagePQ[] = new Car[10];

	private DLB priceDLB = new DLB();
	private DLB mileageDLB = new DLB();

	/* count will keep track of how many cars in the pq
	   count is always initialized to start at 0*/
	private int count;

	private final int priceConstant = 0;
	private final int mileageConstant = 1;

	//default constructor just sets count to 0
	public CarsPQ(){
		this.count = 0;
	}

    //constructor of CarsPQ accepts a string that is the location of a file containing information about cars
    public CarsPQ(String inputFile){
		this.count = 0;
        //populate the priority queue by using a scanner
		try( Scanner s = new Scanner(new File(inputFile))){
            while(s.hasNext()) {
            	String temp = s.nextLine();
				if(!temp.startsWith("#")){
					String arrInput[] = temp.split(":");
					String vin = arrInput[0];
					String make = arrInput[1];
					String model = arrInput[2];
					int price = Integer.parseInt(arrInput[3]);
					int mileage = Integer.parseInt(arrInput[4]);
					String color = arrInput[5];
					Car c = new Car(vin, make, model, price, mileage, color);
					add(c);
				}
			}
        }
        catch (IOException e) {
            System.out.println("Cars file not found");
            e.printStackTrace();
        }
    }
    
    /**
	 * Add a new Car to the data structure
	 * Should throw an `IllegalStateException` if there is already car with the
	 * same VIN in the datastructure.
	 *
	 * @param 	c Car to be added to the data structure
	 */
	public void add(Car c) throws IllegalStateException{
		if(c == null) throw new IllegalArgumentException("car c to add is null?");
		//if the DLB contains the car already throw an exception
		if(priceDLB.contains(c.getVIN())) throw new IllegalStateException("car c with VIN = " + c.getVIN() + " is already contained in the PQ");
		
		//first check if we need to resize our PQ
		if(count == pricePQ.length-1){
			pricePQ = resize(pricePQ);
			mileagePQ = resize(mileagePQ);
		}

		//add the car VIN to both DLBS
		priceDLB.add(c.getVIN(), c);
		mileageDLB.add(c.getVIN(), c);

		priceDLB.update(c.getVIN(), count);
		mileageDLB.update(c.getVIN(), count);

		//update pricePQ
		pricePQ[count] = c;
		swim(pricePQ, count, priceConstant);

		//update mileagePQ
		mileagePQ[count] = c;
		swim(mileagePQ, count, mileageConstant);

		//appending count
		count++;
	}

	/**
	 * Retrieve a new Car from the data structure
	 * Should throw a `NoSuchElementException` if there is no car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 */
	public Car get(String vin) throws NoSuchElementException{
		if(priceDLB.contains(vin) == false) throw new NoSuchElementException("car with vin: " + vin + " is not in the data structure");
		return pricePQ[priceDLB.getLocation(vin)];
	}

	/**
	 * Update the price attribute of a given car
	 * Should throw a `NoSuchElementException` if there is no car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newPrice The updated price value
	 */
	public void updatePrice(String vin, int newPrice) throws NoSuchElementException{
		if(priceDLB.contains(vin) == false) throw new NoSuchElementException("car with vin: " + vin + " is not in the data structure");

		//get the locations of where the cars are in the PQ from the DLB and update their prices
		pricePQ[priceDLB.getLocation(vin)].setPrice(newPrice);
		mileagePQ[mileageDLB.getLocation(vin)].setPrice(newPrice);

		//perform operations on the updated car to make sure it doesnt violate the heap rules
		swim(pricePQ, priceDLB.getLocation(vin), priceConstant);
		sink(pricePQ, priceDLB.getLocation(vin), priceConstant);
	}

	/**
	 * Update the mileage attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newMileage The updated mileage value
	 */
	public void updateMileage(String vin, int newMileage) throws NoSuchElementException{
		if(priceDLB.contains(vin) == false) throw new NoSuchElementException("car with vin: " + vin + " is not in the data structure");

		//get the locations of where the cars are in the PQ from the DLB and update their mileages
		pricePQ[priceDLB.getLocation(vin)].setMileage(newMileage);
		mileagePQ[mileageDLB.getLocation(vin)].setMileage(newMileage);

		//perform operations on the updated car to make sure it doesnt violate the heap rules
		swim(mileagePQ, mileageDLB.getLocation(vin), mileageConstant);
		sink(mileagePQ, mileageDLB.getLocation(vin), mileageConstant);
	}

	/**
	 * Update the color attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newColor The updated color value
	 */
	public void updateColor(String vin, String newColor) throws NoSuchElementException{
		if(priceDLB.contains(vin) == false) throw new NoSuchElementException("car with vin: " + vin + " is not in the data structure");

		//get the locations of where the cars are in the PQ from the DLB and update their colors
		pricePQ[priceDLB.getLocation(vin)].setColor(newColor);
		mileagePQ[mileageDLB.getLocation(vin)].setColor(newColor);
	}

	/**
	 * Remove a car from the data structure
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be removed
	 */
	public void remove(String vin) throws NoSuchElementException{
		if(priceDLB.contains(vin) == false) throw new NoSuchElementException("car with vin: " + vin + " is not in the data structure");

		String swapCarPriceVIN = pricePQ[(count-1)].getVIN();
		String swapCarMileageVIN = mileagePQ[(count-1)].getVIN();

		//swap the car we want deleted with the last car in the PQ
		exchange(pricePQ, priceDLB.getLocation(vin), (count-1), priceConstant);
		exchange(mileagePQ, mileageDLB.getLocation(vin), (count-1), mileageConstant);
		
		//remove this item from the PQ and decrement the count
		
		pricePQ[count-1] = null;
		mileagePQ[count-1] = null;
		count--;

		//perform the sink operations
		sink(pricePQ, priceDLB.getLocation(swapCarPriceVIN), priceConstant);
		sink(mileagePQ, mileageDLB.getLocation(swapCarMileageVIN), mileageConstant);

		//remove the vin from the DLB
		priceDLB.remove(vin);
		mileageDLB.remove(vin);
	}

	/**
	 * Get the lowest priced car (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return	Car object representing the lowest priced car
	 */
	public Car getLowPrice(){
		//if our PQ is empty
		if(count == 0) return null;
		//lowest item is always first in PQ
		return pricePQ[0];
	}

	/**
	 * Get the lowest priced car of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param	make The specified make
	 * @param	model The specified model
	 * 
	 * @return	Car object representing the lowest priced car
	 */
	public Car getLowPrice(String make, String model){
		//create new PQ to store only the specific make and model cars
		CarsPQ lowPricePQ = new CarsPQ();
		//populate this PQ by traversing the respective DLB
		Car makeModelList[] = priceDLB.traverse(make, model, count);
		if(makeModelList[0] == null) return null;
		for(int i = 0; i < makeModelList.length; i++){
			if(makeModelList[i] != null){
				lowPricePQ.add(makeModelList[i]);
			}
		}
		//get the min from this new PQ
		return lowPricePQ.getLowPrice();
	}

	/**
	 * Get the car with the lowest mileage (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return	Car object representing the lowest mileage car
	 */
	public Car getLowMileage(){
		//if our PQ is empty
		if(count == 0) return null;
		//lowest item is always first in PQ
		return mileagePQ[0];

	}

	/**
	 * Get the car with the lowest mileage of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param	make The specified make
	 * @param	model The specified model
	 *
	 * @return	Car object representing the lowest mileage car
	 */
	public Car getLowMileage(String make, String model){
		//create new PQ to store only the specific make and model cars
		CarsPQ lowMileagePQ = new CarsPQ();
		//populate this PQ by traversing the respective DLB
		Car makeModelList[] = mileageDLB.traverse(make, model, count);
		if(makeModelList[0] == null) return null;
		for(int i = 0; i < makeModelList.length; i++){
			if(makeModelList[i] != null){
				lowMileagePQ.add(makeModelList[i]);
			}
		}
		//get the min from this new PQ
		return lowMileagePQ.getLowMileage();
	}


	/**
	 * Helper method to upsize size of our priority queue array
	 * Will upsize to a size of 2* input pq size
	 * @param inputArr current priority queue
	 * 
	 * @return resizedPQ -- resized pq with size 2*
	 */
	private Car[] resize( Car[] inputPQ){
		Car[] resizedPQ = new Car[2 * inputPQ.length];
		for(int i = 0; i < inputPQ.length; i++){
			resizedPQ[i] = inputPQ[i];
		}
		return resizedPQ;
	}

	/**
	 * Swaps the car located at k with the car located at parent and updates the locations in the respective DLB
	 * @param inputPQ what PQ we are looking at
	 * @param parent where the parent car is located
	 * @param k where the current car is located
	 * @param constant are we looking at price or mileage
	 */
	private void exchange(Car[] inputPQ, int parent, int k, int constant){
		//perform the swap in the PQ
		Car temp = inputPQ[parent];
		inputPQ[parent] = inputPQ[k];
		inputPQ[k] = temp;

		//now we must update the values of these two in the DLB
		if(constant == priceConstant){
			priceDLB.update(inputPQ[parent].getVIN(), parent);
			priceDLB.update(inputPQ[k].getVIN(), k);
			return;
		}
		if(constant == mileageConstant){
			mileageDLB.update(inputPQ[parent].getVIN(), parent);
			mileageDLB.update(inputPQ[k].getVIN(), k);
			return;
		}
	}

	/**
	 * Helper method to make sure the PQ is not violated after adding a new item
	 * @param inputPQ PQ we are validating its status
	 * @param k the element that we are checking the validity of
	 * @param constant this is the constant to determine whether we are performing the action on price or mileage
	 */
	private void swim(Car[] inputPQ, int k, int constant){
		//since we have 2 PQs, we must determine which one we are performing the swim operation on
		//the parent in the PQ is given by the following equation
		int parent = (int)Math.floor((k-1)/2);
		if(constant == priceConstant){
			while(k > 0 && (inputPQ[parent].getPrice() > (inputPQ[k].getPrice()))){
				//since the parent is > current k we must perform a swap
				exchange(inputPQ, parent, k, constant);
				k = parent;
				parent = (int)Math.floor((k-1)/2);
			}
			return;
		}
		else if(constant == mileageConstant){
			while(k > 0 && (inputPQ[parent].getMileage() > (inputPQ[k].getMileage()))){
				//since the parent is > current k we must perform a swap
				exchange(inputPQ, parent, k, constant);
				k = parent;
				parent = (int)Math.floor((k-1)/2);
			}
			return;
		}
	}

	private void sink(Car[] inputPQ, int k, int constant){
		//since we have 2 PQs, we must determine which one we are performing the sink operation on
		//the parent in the PQ is given by the following equation
		if(constant == priceConstant){
			while((2 * k) + 1 <= (count-1)){
				int j = (2*k) + 1;
				//now we must determine which of the children of the current car has a smaller price
				//left child is given by 2k + 1, and right child is given by 2k+2
				//so if left child is greater than right, we will increment j

				if(j < (count-1) && (inputPQ[j].getPrice() > (inputPQ[j+1]).getPrice())) j++;

				//if k is not greater than its smallest child - j - we break
				if(!(inputPQ[k].getPrice() > inputPQ[j].getPrice())) break;
				//otherwise we must swap k and j
				exchange(inputPQ, k, j, constant);
				k = j;
			}
			return;
		}
		else if(constant == mileageConstant){
			while((2 * k) + 1 <= (count-1)){
				int j = (2*k) + 1;
				//now we must determine which of the children of the current car has a smaller mileage
				//left child is given by 2k + 1, and right child is given by 2k+2
				//so if left child is greater than right, we will increment j

				if(j < (count-1) && (inputPQ[j].getMileage() > (inputPQ[j+1]).getMileage())) j++;

				//if k is not greater than its smallest child - j - we break
				if(!(inputPQ[k].getMileage() > inputPQ[j].getMileage())) break;
				//otherwise we must swap k and j
				exchange(inputPQ, k, j, constant);
				k = j;
			}
			return;
		}
	}

	//test methods below

	// public ArrayList<String> testPrintDLB(){
	// 	return priceDLB.testprint();

	// }
	// public String testprintPQ(){
	// 	String temp = "";
       
    //     return  testprintPQ(temp, this.pricePQ);
	// }
	// private String testprintPQ(String temp, Car[] inputPQ){
	// 	if(inputPQ == null) return "no";
	// 	for(int i = 0; i < count; i++){
	// 		temp = temp + " " + inputPQ[i].getVIN() + ":" + inputPQ[i].getPrice();
	// 	}
	// 	return temp;
	// }
	// public int whatiscount(){
	// 	return this.count;
	// }
	// public int whatispqsize(){
	// 	return pricePQ.length;
	// }

	// public String testTraverse(){
	// 	String make1 = "Ford";
	// 	String model1 = "Fiesta";
	// 	String temp = "";
	// 	Car test[] = new Car[count];
	// 	test = priceDLB.traverse(make1, model1, count);
	// 	for(int i = 0; i < test.length; i++){
	// 		temp = temp + " i = " + i;
	// 		if(test[i] != null){
				
	// 			temp = temp + " " + i + ":" + test[i].getVIN();
	// 		}
	// 	}
	// 	return temp;
	// }
}
