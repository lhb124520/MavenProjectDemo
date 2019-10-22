package cn.spi;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: com.spi
 * @Author: lhb
 * @CreateTime: 2019-10-14 11:23
 * @Description:
 */
public class Dog implements IShout {
    @Override
    public void shout() {
        System.out.println("wang wang");
    }
}
