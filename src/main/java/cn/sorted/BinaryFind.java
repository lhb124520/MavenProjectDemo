package cn.sorted;

/**
 * 二分查找
 * 在一个有序数组中查找具体的某个数，如果找到了返回这个数的下标，找不到返回-1
 *
 * @author lhb
 */
public class BinaryFind {
    /**
     * @param sortedArray 数组
     * @param num         需要找到的数值
     * @return 数组下标，从0开始
     */
    private static int find(int[] sortedArray, int num) {
        int left = 0;
        int right = sortedArray.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (num < sortedArray[mid]) {
                //num数值在二分的左边
                right = mid - 1;
            } else if (num > sortedArray[mid]) {
                //num数值在二分的右边
                left = mid + 1;
            } else {
                //数值刚好在二分中点(num == mid)
                return mid;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int sortedArray[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int num = 8;
        int i = find(sortedArray, num);
        if (i == -1) {
            System.out.println("找不到\n");
        } else {
            System.out.println("找到了，元素下标是" + i + "\n");
        }
    }
}
