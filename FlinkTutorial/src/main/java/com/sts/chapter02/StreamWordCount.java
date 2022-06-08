package com.sts.chapter02;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * @author sts
 * @date 2022/6/8 11:21
 * @description :
 **/
public class StreamWordCount {

    public static void main(String[] args) throws Exception {
        // 1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 2.读取文本
        DataStreamSource<String> lineDSS = env.readTextFile("C:\\yilianyun\\study\\flinkstudy\\sts2022\\FlinkTutorial\\src\\main\\resources\\words.txt");
        // 3.转换数据格式
        SingleOutputStreamOperator<Tuple2<String,Long>> wordAndOne = lineDSS
                .flatMap((String line, Collector<String> word) -> {
                    Arrays.stream(line.split(" ")).forEach(word::collect);
                })
                .returns(Types.STRING)
                .map(word -> Tuple2.of(word,1L))
                .returns(Types.TUPLE(Types.STRING,Types.LONG));
        // 4.分组
        KeyedStream<Tuple2<String,Long>,String> wordAndOneKS = wordAndOne.keyBy(t -> t.f0);
        // 5.求和
        SingleOutputStreamOperator<Tuple2<String,Long>> result = wordAndOneKS.sum(1);
        // 6.打印
        result.print();
        // 7.执行
        env.execute();
    }
}
