package cn.spi;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: com.spi
 * @Author: lhb
 * @CreateTime: 2019-10-14 11:22
 * @Description:
 */
public class Cat implements IShout {
    @Override
    public void shout() {
        System.out.println("miao miao");
    }
}
