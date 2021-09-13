/**
 * HeftyInteger for CS1501 Project 5
 * @author	Dr. Farnan
 */
package cs1501_p5;

import java.util.Random;
import java.math.BigInteger;

import org.checkerframework.checker.units.qual.degrees;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	public byte getByte(int i){
		return val[i];
	}

	public boolean isZero(){
		boolean zero = true;
		for(int i = 0; i < this.getVal().length; i++){
			if(this.getVal()[i] != 0){
				zero = false;
				break;
			}
		}
		return zero;
	}
	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		HeftyInteger firstHefty, secondHefty, product;
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}
		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}
		firstHefty = new HeftyInteger(a);
		secondHefty = new HeftyInteger(b);		
		//before multiplication, make both numbers positive and set booleans to negative to make sure the result ends up negative
		boolean firstNegative = false, secondNegative = false;
		if(firstHefty.isNegative()){
			firstNegative = true;
			firstHefty = firstHefty.negate();
		}
		if(secondHefty.isNegative()){
			secondNegative = true;
			secondHefty = secondHefty.negate();
		}

		product = new HeftyInteger(new byte[firstHefty.length() * 2]);
		byte[] temp;
		int firstCurr;
		int secondCurr;
		//gradeschool algorithm
		for(int i = 0; i < firstHefty.length(); i++){
			for(int j = 0; j < secondHefty.length(); j++){
				temp = new byte[firstHefty.length() * 2];
				//retrieve the current bytes we are calculating the partial product for
				firstCurr = firstHefty.getByte(i);
				secondCurr = secondHefty.getByte(j);
				
				//only work with positive numbers for mult
				if(firstCurr < 0){
					//adding 256 will get the correct 2s complement representation of negative bytes
					firstCurr += 256;
				}
				if(secondCurr < 0){
					secondCurr += 256;
				}
				//computing the partial product
				int mult = (firstCurr * secondCurr);
				//deciding where to put the partial product and the remainder of the product if there is any
				int location = i + j + 1;
				temp[location--] = (byte) mult;
				//shifting right to include only the remainder portion of the product
				temp[location] = (byte) ((mult >>> 8) & 0xFF);

				//add the partial product to the product
				product = product.add(new HeftyInteger(temp));
			}
		}
		//if either number was initially negative, we make the product negative
		//do not make product negative if both were negative
		if((firstNegative == false && secondNegative == true) || (firstNegative == true && secondNegative == false)){
			product = product.negate();
		}
		return product;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	public HeftyInteger[] XGCD(HeftyInteger other) {
		return XGCDHelper(this, other);
	}

	/**
	 * returns array such that [GCD, s, t] GCD = a * s + b * t
	 * @param a this
	 * @param b other
	 * @return [GCD, s, t] GCD = a * s + b * t
	 */
	private HeftyInteger[] XGCDHelper(HeftyInteger first, HeftyInteger second){
		int curr = 0;
		HeftyInteger firstHefty, secondHefty;
		boolean flip = false;
		//first determine which is the dividend (larger) and divisor (smaller)
		//if first - second is positive, first is larger
		if(first.subtract(second).isNegative() == false){
			firstHefty = new HeftyInteger(first.getVal());
			secondHefty = new HeftyInteger(second.getVal());
		}
		//otherwise first-second is negative which means first is smaller
		else{
			firstHefty = new HeftyInteger(second.getVal());
			secondHefty = new HeftyInteger(first.getVal());
			flip = true;
		}
		
		//matrix follows structure:
		//a | b | a/b | a%b | GCD | s | t|
		HeftyInteger[][] xGCD = new HeftyInteger[10][7];
		//populate first line of matrix
		xGCD[curr][0] = firstHefty;
		xGCD[curr][1] = secondHefty;
		HeftyInteger[] division = firstHefty.getDivision(secondHefty);
		xGCD[curr][2] = division[0];
		xGCD[curr][3] = division[1];
		byte[] empty = {0};
		HeftyInteger emptyH1 = new HeftyInteger(empty);
		xGCD[curr][4] = emptyH1;
		xGCD[curr][5] = emptyH1;
		xGCD[curr][6] = emptyH1;
		curr++;

		//work through the matrix until the GCD is calculated
		while(true){
			//if the matrix is full, double its size
			if(curr == (xGCD.length)){
				xGCD = resize(xGCD);
			}
			//a becomes the previous b
			xGCD[curr][0] = xGCD[curr-1][1];
			//b becomes the previous a%b
			xGCD[curr][1] = xGCD[curr-1][3];
			//calculate the new division
			division = xGCD[curr][0].getDivision(xGCD[curr][1]);
			//update a/b
			xGCD[curr][2] = division[0];
			//update a%b
			xGCD[curr][3] = division[1];
			HeftyInteger emptyH = new HeftyInteger(empty);
			xGCD[curr][4] = emptyH;
			xGCD[curr][5] = emptyH;
			xGCD[curr][6] = emptyH;

			//if the current a%b becomes 0, we have found the GCD and it is the current b
			if(xGCD[curr][3].isZero()){
				xGCD[curr][4] = xGCD[curr][1];
				break;
			}
			curr++;
		}

		//at this point we know the GCD so we can do the extended euclidean algorithm to calculate s and t
		//[GCD, s, t] GCD = a * s + b * t
		//initialize the last s and t, s starts at 0, t starts at 1
		byte[] one = {1};
		xGCD[curr][5] = emptyH1;
		xGCD[curr][6] = new HeftyInteger(one);
		//work through the end of the matrix until we reach the beginning at 0
		HeftyInteger temp;
		for(int i = curr-1; i >= 0; i--){
			//s becomes previous t
			xGCD[i][5] = xGCD[i+1][6];
			//t becomes = sprevious - (a/b)*tprevious
			temp = xGCD[i+1][5].subtract(xGCD[i][2].multiply(xGCD[i+1][6]));
			//trim the unnecessary 0s from multiplication operation before putting back into matrix to prevent unnecessary growth and slowdown
			//should not trim to a smaller size than the first input
			xGCD[i][6] = trimLeadingZeroes(temp, firstHefty.length());
		}

		//if we flipped the incoming a and b, we must flip the returning s and t
		if(flip == false){
			HeftyInteger[] toReturn = {xGCD[curr][4], xGCD[0][5], xGCD[0][6]};
			return toReturn;
		}
		else{
			HeftyInteger[] toReturn = {xGCD[curr][4], xGCD[0][6], xGCD[0][5]};
			return toReturn;
		}
	}

	/**
	 * @param toTrim hefty integer that needs leading 0s cut off
	 * @param length The minimum length we should not trim below
	 * @return trimmed hefty integer
	 */
	private HeftyInteger trimLeadingZeroes(HeftyInteger toTrim, int length){
		int size = toTrim.length() - 1;
		//if the first byte is a 0 and we are still above the size length, trim
		while(toTrim.getVal()[0] == 0 && size > length){
			//create new byte array with size one less
			byte[] replace = new byte[size];

			//for p-1<trim length, everything but msb
			for(int p=1; p<toTrim.length(); p++){
				replace[p-1] = toTrim.getVal()[p];
			}
			toTrim = new HeftyInteger(replace);
			size--;
		}
		return toTrim;
	}
	
	/**
	 * upsize heftyinteger[][] by *2
	 */
	private HeftyInteger[][] resize(HeftyInteger[][] heftyMatrix){
		HeftyInteger[][] newMatrix = new HeftyInteger[heftyMatrix.length * 2][7];
		for(int i = 0; i < heftyMatrix.length; i ++){
			for(int j = 0; j < heftyMatrix[i].length; j++){
				newMatrix[i][j] = heftyMatrix[i][j];
			}
		}
		return newMatrix;
	}

	public HeftyInteger[] testDivision(HeftyInteger other){
		return this.getDivision(other);
	}

	/**
	 * Will calculate a/b and get the remainder
	 * Helper method for XGCD method
	 * @param this dividend
	 * @param other divisor
	 * @return heftyinteger array 0- quotient 1- remainder of a/b (a%b)
	 */
	private HeftyInteger[] getDivision(HeftyInteger other){
		//divided | divisor | quotient
		//20 / 4 = 5
		//this is dividend
		//other is divisor
		byte[] dividend = this.getVal();
		byte[] divisor = other.getVal();

		//divisor << n bits in dividend
		divisor = shiftLeft(divisor, dividend.length);

		//initialize quotient to be 0
		byte[] quotient = {0};
		HeftyInteger quotientHefty = new HeftyInteger(quotient);

		//start with the remainder = dividend
		HeftyInteger remainderHefty = new HeftyInteger(dividend);

		//for n bits in dividend (same amount we shifted)
		for(int n = 0; n < dividend.length; n++) {
			//shift the divisor right one
			divisor = shiftRight(divisor, 1);
			HeftyInteger divisorHefty1 = new HeftyInteger(divisor);

			//while we can fit the current divisor into the remaining number
			while(remainderHefty.subtract(divisorHefty1).isNegative() == false){
				//subtract by amount of divisor
				remainderHefty = remainderHefty.subtract(divisorHefty1);

				//calculating the correct amount of shifts for where the quotient should go
				//ex: 700/25 on step 700/250 the "2" that 250 goes into 700 should go at position 1 in the quotient array
				//next step 200/25 the "8" that 25 goes into 700 should go at position 0 in the quotient array
				//calculate the correct shifts by doing the length of dividend - n amount of shifts so far
				byte[] tempdivide = new byte[dividend.length-n];
				tempdivide[0] = 1;
				for(int i = 1; i < tempdivide.length-1; i++){
					tempdivide[i] = 0;
				}
				//add the quotient to the total quotient
				quotientHefty = quotientHefty.add(new HeftyInteger(tempdivide));
			}
		}
		HeftyInteger[] toReturn = {quotientHefty, remainderHefty};
		return toReturn;
	}

	/**
	 * @param byteArray is given bytearray
	 * @param shiftAmount amount to shift byte array by
	 * @return byteArray shifted by amount shiftAmount
	 */
	private byte[] shiftLeft(byte[] byteArray, int shiftAmount){
		//shifted array is shiftAmount longer than original
		byte[] shifted = new byte[byteArray.length + shiftAmount];

		//copy old values to shift array
		int i;
		for(i = 0; i < byteArray.length; i++){
			shifted[i] = byteArray[i];
		}
		//assigning a 0 to the least significant bytes
		for(int j = 0; j < shiftAmount; j++){
			shifted[i] = 0;
			i++;
		}
		return shifted;
	}

	/**
	 * @param byteArray is given bytearray
	 * @param shiftAmount amount to shift byte array by
	 * @return byteArray shifted by amount shiftAmount
	 */
	private byte[] shiftRight(byte[] byteArray, int shiftAmount){
		//shifted array is shiftAmount shorter than original
		byte[] shifted = new byte[byteArray.length - shiftAmount];

		//copy old values into new array
		for(int i = 0; i < shifted.length; i++){
			shifted[i] = byteArray[i];
		}
		return shifted;
	}
}
