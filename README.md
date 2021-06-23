todo list
1 清理temp table，保证稳定性。可用引用计数法
2 定时获取历史数据作为维表和流表join
调研发现维表后续发生变更并不会反映到流表的join sql中
可能需要重写源
https://blog.csdn.net/shirukai/article/details/88036952

