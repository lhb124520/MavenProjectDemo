package yy;

import java.util.ArrayList;

/**
 * 最小的K个数,直接插入排序，然后取前k数据。
 */
public class GetLeastNumbers {
    public static ArrayList<Integer> GetLeastNumbers_Solution(int[] input, int k) {
        ArrayList<Integer> result = new ArrayList<>();
        if(k<= 0 || k > input.length)return result;
        //初次排序，完成k个元素的排序
        for(int i = 1; i< k; i++){
            int j = i-1;
            int unFindElement = input[i];
            while(j >= 0 && input[j] > unFindElement){
                input[j+1] = input[j];
                j--;
            }

            input[j+1] = unFindElement;
        }
        //遍历后面的元素 进行k个元素的更新和替换
        for(int i = k; i < input.length; i++){
            if(input[i] < input[k-1]){
                int newK = input[i];
                int j = k-1;
                while(j >= 0 && input[j] > newK){
                    input[j+1] = input[j];
                    j--;
                }
                input[j+1] = newK;
            }
        }
        //把前k个元素返回
        for(int i=0; i < k; i++)
            result.add(input[i]);
        return result;
    }

    public static void main(String[] args) {
        int [] input = new int[]{4,5,1,6,2,7,3,8};
        ArrayList<Integer> resultList = GetLeastNumbers_Solution(input, 4);
        System.out.println(resultList.toString());
    }
}
