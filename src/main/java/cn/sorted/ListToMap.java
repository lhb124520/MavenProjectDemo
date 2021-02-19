package cn.sorted;

import cn.demo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 集合list转map
 *
 * @author lhb
 */
public class ListToMap {
    public static Map<Integer, User> getIdAccountMap(List<User> accounts) {
        return accounts.stream().collect(Collectors.toMap(User::getId, account -> account));
    }

    public static void main(String[] args) {
        List<User> accounts = new ArrayList<>();
        System.out.println(getIdAccountMap(accounts));
    }
}
