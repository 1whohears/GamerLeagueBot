package com.onewho.gamerbot.util;

/** 
 * Code stolen from Rajat Mishra 
 * https://www.geeksforgeeks.org/find-k-closest-elements-given-value/
 */

public class UtilKClosest 
{ 
    /* Function to find the cross over point (the point before 
       which elements are smaller than or equal to x and after 
       which greater than x)*/
    private static int findCrossOver(int arr[], int low, int high, int x) 
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
    public static int[] getKclosest(int arr[], int x, int k, int n) 
    { 
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

    public static int[] getKclosestIndex(int arr[], int x, int k, int n) 
    { 
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
