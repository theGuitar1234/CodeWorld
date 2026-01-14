package az.codeworld.springboot.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.utilities.constants.accountstatus;

public class Util {
    public static void main(String[] args) {
        User user = new User();
        user.setAccountStatus(accountstatus.ACTIVE);
        //user.setAge((byte) 10);
        user.setBanned(true);

        Field[] fields = user.getClass().getDeclaredFields();
        Method[] methods = user.getClass().getDeclaredMethods();

        for (Field i : fields) {
            String capitalized = i.getName().substring(0, 1).toUpperCase() + i.getName().substring(1);
            for (Method j : methods) {
                if (j.getName().matches("^get" + capitalized + "$")) {
                    try {
                        if (j.invoke(user) != null) {
                            System.out.println(j.invoke(user));
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}



