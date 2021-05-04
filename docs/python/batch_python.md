```sql
set rawText='''
{"id":9,"content":"1","label":0.0}
{"id":10,"content":"2","label":0.0}
{"id":11,"content":"中国","label":0.0}
{"id":12,"content":"e","label":0.0}
{"id":13,"content":"5","label":0.0}
{"id":14,"content":"4","label":0.0}
''';

load jsonStr.`rawText` as orginal_text_corpus;

!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
!python conf "schema=st(field(content,string))";

!python on orginal_text_corpus '''

data = context.fetch_once_as_rows()
def process(data):
    for row in data:
        new_row = { }
        new_row["content"] = "---" + row["content"]+"---"
        yield new_row

context.build_result(process(data))

''' named out_temp_table;

--结果
select * from out_temp_table as output;
```


```sql
set rawText='''
{"id":9,"content":"1","label":0.0}
{"id":10,"content":"2","label":0.0}
{"id":11,"content":"中国","label":0.0}
{"id":12,"content":"e","label":0.0}
{"id":13,"content":"5","label":0.0}
{"id":14,"content":"4","label":0.0}
''';

load jsonStr.`rawText` as orginal_text_corpus;

!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
!python conf "schema=st(field(content,long))";

!python on orginal_text_corpus '''

import pandas as pd
import numpy as np
for item in data_manager.fetch_once():
    print(item)
df = pd.DataFrame({'content': [4, 5, 6, 7, 8]})
data_manager.set_output([df['content']])

''' named out_temp_table;

--结果
select * from out_temp_table as output;
```