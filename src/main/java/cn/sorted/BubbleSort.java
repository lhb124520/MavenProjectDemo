package cn.sorted;

/**
 * 冒泡排序
 * 最简单调用库函数sort；
 * Arrays.sort(arr);
 */
public class BubbleSort {
    public static int[] MySort(int[] arr) {
        if(arr.length<2){
            return arr;
        }

        for(int i=0;i<arr.length-1;i++){
            for(int j=0;j<arr.length-i-1;j++){
                if(arr[j]>arr[j+1]){
                    swap(arr,j,j+1);
                }
            }
        }
        return arr;
    }

    public static void swap(int[] arr, int i, int j){
        int tmp;
        tmp=arr[i];
        arr[i]=arr[j];
        arr[j]=tmp;
    }

    public static void main(String[] args) {
        int array[] = {5, 10, 4, 3, 1, 8, 7, 6, 9, 2};
        int num = 8;
        int sortedArray[] = MySort(array);
        for (int i : sortedArray) {
            System.out.print(i + " ");
        }
    }
}
