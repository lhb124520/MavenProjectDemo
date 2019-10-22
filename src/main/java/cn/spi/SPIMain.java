package cn.spi;

import java.util.ServiceLoader;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: com.spi
 * @Author: lhb
 * @CreateTime: 2019-10-14 11:24
 * @Description:
 */
public class SPIMain {
    public static void main(String[] args) {
        ServiceLoader<IShout> shouts = ServiceLoader.load(IShout.class);
        for (IShout s : shouts) {
            s.shout();
        }
    }
}
