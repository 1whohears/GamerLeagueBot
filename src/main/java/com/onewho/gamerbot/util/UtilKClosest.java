package com.onewho.gamerbot.util;

import javax.annotation.Nullable;
import java.util.Arrays;

/** 
 * Code stolen from Rajat Mishra 
 * https://www.geeksforgeeks.org/find-k-closest-elements-given-value/
 */

public class UtilKClosest { 
	
	/**
	 * 
	 * @param a
	 * @param index
	 * @param k
	 * @return
	 */
	@Nullable
	public static int[] getKClosestIndexArray(int[] a, int index, int k) {
		if (a == null || index >= a.length || index < 0) return null;
		if (k > a.length-1) k = a.length-1;
		
		int base = a[index];
		int[] b = new int[k];
        Arrays.fill(b, -1);
		
		for (int i = 0; i < b.length; ++i) {
			int min_diff = Integer.MAX_VALUE;
			int min_index = -1;
			for (int j = 0; j < a.length; ++j) {
				if (j == index || contains(b, j)) continue;
				int diff = Math.abs(a[j]-base);
				if (diff < min_diff) {
					min_diff = diff;
					min_index = j;
				}
			}
			b[i] = min_index;
		}
		return b;
	}
	
	private static boolean contains(int[] a, int b) {
        for (int j : a) if (j == b) return true;
		return false;
	}
	
	/*private static int[] removeIndex(int[] a, int index) {
		if (index >= a.length || index < 0) return null;
		int[] b = new int[a.length-1];
		for (int i = 0, x = 0; i < a.length; ++i) if (i != index) b[x++] = a[i];
		return b;
	}*/
	
    /* Function to find the cross over point (the point before 
       which elements are smaller than or equal to x and after 
       which greater than x)*/
	@Deprecated
    private static int findCrossOver(int[] arr, int low, int high, int x)
    { 
        // Base cases 
        if (arr[high] <= x) // x is greater than all 
            return high; 
        if (arr[low] > x)  // x is smaller than all 
            return low; 
  
        // Find the middle point 
        int mid = (low + high)/2;  /* low + (high - low)/2 */
  
        /* If x is same as middle element, then return mid */
        if (arr[mid] <= x && arr[mid+1] > x) 
            return mid; 
  
        /* If x is greater than arr[mid], then either arr[mid + 1] 
          is ceiling of x or ceiling lies in arr[mid+1...high] */
        if(arr[mid] < x) 
            return findCrossOver(arr, mid+1, high, x); 
  
        return findCrossOver(arr, low, mid - 1, x); 
    } 
  
    // This function prints k closest elements to x in arr[]. 
    // n is the number of elements in arr[] 
	@Deprecated
    public static int[] getKclosest(int[] arr, int x, int k) 
    { 
    	int n = arr.length;
        int[] b = new int[k];
        // Find the crossover point 
        int l = findCrossOver(arr, 0, n-1, x);  
        int r = l+1;   // Right index to search 
        int count = 0; // To keep track of count of elements 
                       // already printed 
  
        // If x is present in arr[], then reduce left index 
        // Assumption: all elements in arr[] are distinct 
        if (arr[l] == x) l--; 
  
        // Compare elements on left and right of crossover 
        // point to find the k closest elements 
        while (l >= 0 && r < n && count < k) 
        { 
            if (x - arr[l] < arr[r] - x) { 
                b[count] = arr[l];
                System.out.print(arr[l--]+" "); 
            }
            else {
                b[count] = arr[l];
                System.out.print(arr[r++]+" "); 
            }
            count++; 
        } 
  
        // If there are no more elements on right side, then 
        // print left elements 
        while (count < k && l >= 0) 
        { 
            b[count] = arr[l];
            System.out.print(arr[l--]+" "); 
            count++; 
        } 
  
  
        // If there are no more elements on left side, then 
        // print right elements 
        while (count < k && r < n) 
        { 
            b[count] = arr[l];
            System.out.print(arr[r++]+" "); 
            count++; 
        } 

        return b;
    }
	
	@Deprecated
    public static int[] getKclosestIndex(int[] arr, int x, int k) 
    { 
    	int n = arr.length;
        int[] b = new int[k];
        // Find the crossover point 
        int l = findCrossOver(arr, 0, n-1, x);  
        int r = l+1;   // Right index to search 
        int count = 0; // To keep track of count of elements 
                       // already printed 
  
        // If x is present in arr[], then reduce left index 
        // Assumption: all elements in arr[] are distinct 
        if (arr[l] == x) l--; 
  
        // Compare elements on left and right of crossover 
        // point to find the k closest elements 
        while (l >= 0 && r < n && count < k) 
        { 
            if (x - arr[l] < arr[r] - x) {
                //System.out.print(arr[l]+" ");
                b[count] = l--; 
            }
            else {
                //System.out.print(arr[r]+" "); 
                b[count] = r++;
            }
            count++; 
        } 
  
        // If there are no more elements on right side, then 
        // print left elements 
        while (count < k && l >= 0) 
        { 
            //System.out.print(arr[l]+" ");
            b[count] = l--; 
            count++; 
        } 
  
  
        // If there are no more elements on left side, then 
        // print right elements 
        while (count < k && r < n) 
        { 
            //System.out.print(arr[r]+" "); 
            b[count] = r++;
            count++; 
        } 

        return b;
    }
} 
