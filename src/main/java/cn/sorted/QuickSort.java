package cn.sorted;

/**
 * 快排
 * 最简单调用库函数sort；
 * Arrays.sort(arr);
 */
public class QuickSort {
    public static int[] MySort(int[] arr) {
        quickSort(arr , 0 , arr.length-1);
        return arr;
    }
    public static void quickSort(int[] list, int left, int right) {
        if (left < right) {
            // 分割数组，找到分割点
            int point = partition(list, left, right);
            // 递归调用，对左子数组进行快速排序
            quickSort(list, left, point - 1);
            // 递归调用，对右子数组进行快速排序
            quickSort(list, point + 1, right);
        }
    }

    /**
     * 分割数组，找到分割点
     */
    public static int partition(int[] list, int left, int right) {
        // 用数组的第一个元素作为基准数
        int first = list[left];
        while (left < right) {
            while (left < right && list[right] >= first) {
                right--;
            }

            // 交换
            swap(list, left, right);

            while (left < right && list[left] <= first) {
                left++;
            }

            // 交换
            swap(list, left, right);
        }
        // 返回分割点所在的位置
        return left;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
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
