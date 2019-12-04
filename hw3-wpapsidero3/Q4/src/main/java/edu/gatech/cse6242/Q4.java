package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.util.StringTokenizer;

public class Q4 {

  public static void main(String[] args) throws Exception {
    //Configuration conf = new Configuration();
    //Job job = Job.getInstance(conf, "Q4");

    /* TODO: Needs to be implemented */
	public static class CountMapper
	
	extends Mapper<Object, Text, IntWritable, IntWritable>{ 
        private IntWritable Node_Out = new IntWritable();
        private IntWritable Node_In = new IntWritable();
        private IntWritable Degree_Out = new IntWritable(1);
        private IntWritable Degree_In = new IntWritable(-1);
        public void map(Object key, Text value, Context context
                        ) throws IOException, InterruptedException {
            Node_Out.set(Integer.parseInt(value.toString().split("\\t")[0]));
            Node_In.set(Integer.parseInt(value.toString().split("\\t")[1]));
            context.write(Node_Out, Degree_Out);
            context.write(Node_In, Degree_In);
       
        }
    }
  public static class NodeReducer
       extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {
			private IntWritable result = new IntWritable();
			public void reduce(IntWritable key, Iterable<IntWritable> values,
							Context context
							) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
		
		result.set(sum);
		context.write(key, result);
    }
  }
  public static class DifMapper
    extends Mapper<Object, Text, IntWritable, IntWritable>{
        private IntWritable Dif = new IntWritable();
        private IntWritable One = new IntWritable(1);
        public void map(Object key, Text value, Context context
                        ) throws IOException, InterruptedException {
            Dif.set(Integer.parseInt(value.toString().split("\\t")[1]));
            context.write(Dif, One);       
        }
    }
  public static class DifReducer
       extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {
    private IntWritable result = new IntWritable();
    public void reduce(IntWritable key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
    }
      result.set(sum);
      context.write(key, result);
    }
  }
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job counter = Job.getInstance(conf, "counter");
        counter.setJarByClass(Q4.class);
        counter.setMapperClass(CountMapper.class);
        counter.setCombinerClass(CountReducer.class);
        counter.setReducerClass(CountReducer.class);
        counter.setOutputKeyClass(IntWritable.class);
        counter.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(counter, new Path(args[0]));
        FileOutputFormat.setOutputPath(counter, new Path("ouput1"));
        counter.waitForCompletion(true);
        
        Job difference = Job.getInstance(conf, "difference");
        difference.setJarByClass(Q4.class);
        difference.setMapperClass(DifMapper.class);
        difference.setCombinerClass(DifReducer.class);
        difference.setReducerClass(DifReducer.class);
        difference.setOutputKeyClass(IntWritable.class);
        difference.setOutputValueClass(IntWritable.class);
    /* TODO: Needs to be implemented */
	FileInputFormat.addInputPath(difference, new Path("output1"));
    FileOutputFormat.setOutputPath(difference, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
