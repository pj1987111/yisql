package com.zhy.yisql.lag

import com.zhy.yisql.core.BaseTest
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-02
  *  \* Time: 10:52
  *  \* Description: 
  *  \*/
class PythonTest extends BaseTest {
  val pyCmd =
    """
      |!python on orginal_text_corpus '''
      |
      |data = context.fetch_once_as_rows()
      |def process(data):
      |    for row in data:
      |        new_row = { }
      |        new_row["content"] = "---" + row["content"]+"---"
      |        yield new_row
      |
      |context.build_result(process(data))
      |
      |''' named mlsql_temp_table;
    """

  val batchPyTest =
    """
      |set rawText='''
      |{"id":9,"content":"1","label":0.0}
      |{"id":10,"content":"2","label":0.0}
      |{"id":11,"content":"中国","label":0.0}
      |{"id":12,"content":"e","label":0.0}
      |{"id":13,"content":"5","label":0.0}
      |{"id":14,"content":"4","label":0.0}
      |''';
      |
      |load jsonStr.`rawText` as orginal_text_corpus;
      |
      |!python env "PYTHON_ENV=source activate taihao-python3";
      |!python conf "schema=st(field(content,string))";
      |
      |!python on orginal_text_corpus '''
      |
      |data = context.fetch_once_as_rows()
      |def process(data):
      |    for row in data:
      |        new_row = { }
      |        new_row["content"] = "---" + row["content"]+"---"
      |        yield new_row
      |
      |context.build_result(process(data))
      |
      |''' named out_temp_table;
      |
      |--结果
      |select * from out_temp_table as output;
    """.stripMargin

  val schemaShow =
    """
      |set rawText='''
      |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
      |{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
      |{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
      |''';
      |
      |load jsonStr.`rawText` as orginal_text_corpus;
      |!show schema orginal_text_corpus;
    """.stripMargin

  val batchPy2Test =
    """
      |set rawText='''
      |{"id":9,"content":"1","label":0.0}
      |{"id":10,"content":"2","label":0.0}
      |{"id":11,"content":"中国","label":0.0}
      |{"id":12,"content":"e","label":0.0}
      |{"id":13,"content":"5","label":0.0}
      |{"id":14,"content":"4","label":0.0}
      |''';
      |
      |load jsonStr.`rawText` as orginal_text_corpus;
      |
      |!python env "PYTHON_ENV=source activate taihao-python3";
      |!python conf "schema=st(field(con1,double),field(con2,long))";
      |
      |!python on orginal_text_corpus '''
      |
      |import pandas as pd
      |import numpy as np
      |for item in data_manager.fetch_once():
      |    print(item)
      |df = pd.DataFrame({'content': [4.5, 5.2, 6, 7, 8],'content1': [14, 15, 16, 17, 18]})
      |#print(df)
      |print([[df['content'],df['content1']]])
      |data_manager.set_output([[df['content'],df['content1']]])
      |
      |''' named out_temp_table;
      |
      |--结果
      |select * from out_temp_table as output;
    """.stripMargin

  val batchPy3_1Test =
    """
      |set rawText='''
      |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
      |{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
      |{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
      |{"id":"1104","name":"小明4","age":23,"message":"testmsg4","date":"20210112","version":1}
      |{"id":"1105","name":"小明5","age":24,"message":"testmsg5","date":"20210112","version":1}
      |{"id":"1106","name":"小明6","age":25,"message":"testmsg6","date":"20210112","version":1}
      |{"id":"1107","name":"小明7","age":26,"message":"testmsg7","date":"20210112","version":1}
      |{"id":"1108","name":"小明8","age":27,"message":"testmsg8","date":"20210112","version":1}
      |{"id":"1109","name":"小明9","age":28,"message":"testmsg9","date":"20210112","version":1}
      |{"id":"1110","name":"小明10","age":29,"message":"testmsg10","date":"20210112","version":2}
      |''';
      |
      |load jsonStr.`rawText` as orginal_text_corpus;
      |
      |!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
      |!python conf "schema=st(field(name,string),field(age,long),field(message,string),field(date,string),field(v,string),field(id,long))";
      |--!python conf "schema=st(field(id,string),field(name,string),field(age,long),field(message,string),field(date,string),field(version,long))";
      |
      |!python on orginal_text_corpus '''
      |
      |data = context.fetch_once_as_rows()
      |def process(data):
      |    for row in data:
      |        new_row = { }
      |        new_row["name"] = "---" + row["name"]+"---"
      |        new_row["age"] = row["age"]
      |        new_row["message"] = "---" + row["message"]+"---"
      |        new_row["date"] = row["date"]
      |        new_row["version"] = "ver:" + str(row["version"])
      |        new_row["id"] = int(row["id"])
      |        print("test:"+str(new_row))
      |        yield new_row
      |
      |context.build_result(process(data))
      |
      |''' named out_temp_table;
      |
      |--结果
      |select * from out_temp_table as output;
    """.stripMargin

  val batchPy3_2Test =
    """
      |set rawText='''
      |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
      |{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
      |{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
      |{"id":"1104","name":"小明4","age":23,"message":"testmsg4","date":"20210112","version":1}
      |{"id":"1105","name":"小明5","age":24,"message":"testmsg5","date":"20210112","version":1}
      |{"id":"1106","name":"小明6","age":25,"message":"testmsg6","date":"20210112","version":1}
      |{"id":"1107","name":"小明7","age":26,"message":"testmsg7","date":"20210112","version":1}
      |{"id":"1108","name":"小明8","age":27,"message":"testmsg8","date":"20210112","version":1}
      |{"id":"1109","name":"小明9","age":28,"message":"testmsg9","date":"20210112","version":1}
      |{"id":"1110","name":"小明10","age":29,"message":"testmsg10","date":"20210112","version":2}
      |''';
      |
      |load jsonStr.`rawText` as orginal_text_corpus;
      |
      |!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
      |--!python conf "schema=st(field(name,string),field(age,string),field(message,string),field(age,long),field(aaaaa,long),field(id,long))";
      |!python conf "schema=st(field(id,string),field(name,string),field(age,long),field(message,string),field(date,string),field(version,long))";
      |
      |!python on orginal_text_corpus '''
      |
      |data = context.fetch_once_as_rows()
      |def process(data):
      |    for row in data:
      |        new_row = { }
      |        new_row["id"] = row["id"]
      |        new_row["name"] = "---" + row["name"]+"---"
      |        new_row["age"] = row["age"]
      |        new_row["message"] = "---" + row["message"]+"---"
      |        new_row["date"] = row["date"]
      |        new_row["version"] = row["version"]
      |        print("test:"+str(new_row))
      |        yield new_row
      |
      |context.build_result(process(data))
      |
      |''' named out_temp_table;
      |
      |--结果
      |select * from out_temp_table as output;
    """.stripMargin

  val batchPy3_3Test =
    """
      |
      |select 1 as orginal_corpus;
      |
      |!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
      |!python conf "schema=st(field(c1,double),field(c2,double),field(c3,string))";
      |
      |!python on orginal_corpus '''
      |import pandas as pd
      |import numpy as np
      |data={"one":np.random.randn(4),"two":np.linspace(1,4,4),"three":['zhangsan','李四','999','0.1']}
      |df=pd.DataFrame(data)
      |print([[df['one'],df['two'],df['three']]])
      |
      |data_manager.set_output([[df['one'],df['two'],df['three']]])
      |
      |''' named out_temp_table;
      |
      |select * from out_temp_table;
    """.stripMargin

  @Test
  def batchPy(): Unit = {
    sqlParseInner(batchPyTest)
  }

  @Test
  def batchPy2(): Unit = {
    sqlParseInner(batchPy2Test)
  }

  @Test
  def batchPy31(): Unit = {
    sqlParseInner(batchPy3_1Test)
  }

  @Test
  def batchPy32(): Unit = {
    sqlParseInner(batchPy3_2Test)
  }

  @Test
  def batchPy33(): Unit = {
    sqlParseInner(batchPy3_3Test)
  }

  @Test
  def pythonCommand(): Unit = {
    sqlParseInner(pyCmd)
  }

  @Test
  def schemaShowTest(): Unit = {
    sqlParseInner(schemaShow)
  }
}
