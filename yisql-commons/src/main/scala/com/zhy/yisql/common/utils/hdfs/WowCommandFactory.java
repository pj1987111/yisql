package com.zhy.yisql.common.utils.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.shell.CommandFactory;

/**
 * 2019-05-08 WilliamZhu(allwefantasy@gmail.com)
 */
public class WowCommandFactory extends CommandFactory {


    /**
     * Factory constructor for commands
     */
    public WowCommandFactory() {
        this(null);
    }

    /**
     * Factory constructor for commands
     *
     * @param conf the hadoop configuration
     */
    public WowCommandFactory(Configuration conf) {
        super(conf);
    }
}
