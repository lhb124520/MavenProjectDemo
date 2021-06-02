package yy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 两数之和
 * 数组中找出两个加起来等于目标值的数,返回这两个数字的下标,标是从1开始的
 * 输入： [3,2,4],6
 * 返回值： [2,3]
 */
public class TwoSum {
    /**
     * 遍历，每次判断 target - current 是否在 Map 中，若在直接返回；
     * 若不在将 current 和对应索引存入 Map。
     * 注意：因为每次找的是 target 和当前元素的差值，因此理论上不存在元素覆盖的问题。
     *
     * @param numbers int整型一维数组
     * @param target  int整型
     * @return int整型一维数组
     */
    public static int[] twoSum(int[] numbers, int target) {
        // write code here
        Map<Integer, Integer> map = new HashMap<>();
        for (int cur = 0, tmp; cur < numbers.length; cur++) {
            tmp = numbers[cur];
            if (map.containsKey(target - tmp)) {
                return new int[]{map.get(target - tmp) + 1, cur + 1};
            }
            map.put(tmp, cur);
        }
        return null;
    }

    public static void main(String[] args) {
        int[] numbers = new int[]{3,2,4};
        int[] ints = twoSum(numbers, 6);
        System.out.println(Arrays.toString(ints));
    }
}
