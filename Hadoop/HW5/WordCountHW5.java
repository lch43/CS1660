import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountHW5 {
	//A lot of this was adopted from the class given code and my term project
	
	static int TOP_N = 5;
	//As a hint for HW-5, please go ahead and use stop-word-list to prevent words like "he", "she", "they", "the", "a", an", "are", you", "of", "is", "and", and "or" from being displayed at the results.
static String[] stopList = {"a", "an", "the", "and", "for", "is", "were", "was", "how", "he", "she", "they", "are","you","of","or", "to", "in", "that", "i", "his", "her", "him", "hers"};
  //There are some duplicates in the above list due to copying and pasting from the email sent on 11/29
  public static class TokenizerMapperGeneral
  extends Mapper<Object, Text, Text, IntWritable>{

private final static IntWritable one = new IntWritable(1);
private Text word = new Text();

public String myTrim(String word) {
	word = word.toLowerCase();
	int left = 0;
	int right = word.length();
	while (left < word.length() && ((int)word.charAt(left) < 97 || (int)word.charAt(left) > 122)) {
		left++;
	}
	while (right > 0 && ((int)word.charAt(right-1) < 97 || (int)word.charAt(right-1) > 122)) {
		right--;
	}
	if (right ==  0 || right == word.length() || left == right) {
		return word.substring(left);
	}
	if (left < right) {
		return word.substring(left, right);
	}
	else {
		return "";
	}
}

public void map(Object key, Text value, Context context
               ) throws IOException, InterruptedException {
 StringTokenizer itr = new StringTokenizer(value.toString());
 while (itr.hasMoreTokens()) {
   word.set(itr.nextToken());
   
   //Stop list/clean up data -------------------------
   String[] split1 = word.toString().split("--");
   for (String s: split1) {
   	String[] split2 = s.split("'s");
   	
   	for (String str: split2) {
   		if (str.equals("")) {
   			continue;
   		}
   		
   		boolean stop = false;
           for (String stopWord: stopList) {
           	if (str.equals(stopWord)) {
           		stop = true;
           		break;
           	}
           }
           if (stop == false) {
           	str = myTrim(str);
           	if (str.equals("") == false) {
               context.write(new Text(str), one);
           	}
           }
   	}
   	
   }
   //------------------------------------------------
 }
}
}
  
  
  
  public static class TokenizerMapperTopN
  extends Mapper<Object, Text, Text, IntWritable>{

	public void map(Object key, Text value, Context context
	               ) throws IOException, InterruptedException {
		String[] lines = value.toString().split("\n");
		int [] values = new int[TOP_N];
		String[] keys = new String[TOP_N];
		for (String line:lines) {
			String k = line.split( ((char) 9)+"")[0]+"'='"+line.split(((char) 9)+"")[1];
			int v = Integer.parseInt(line.split(((char) 9)+"")[1]);
			
			for (int i=0; i<values.length;i++) {
				if (v > values[i]) {
					
					if (i == 0) {
						values[i] = v;
						keys[i] = k;
					}
					else {
						values[i-1] = values[i];
						keys[i-1] = keys[i];
						values[i] = v;
						keys[i] = k;
					}
					
				}
				else {
					break;
				}
			}
		}
		
		for (int i=0;i<keys.length;i++) {
			if (keys[i] != null) {
				context.write(new Text(keys[i]),new IntWritable(values[i]));
			}
		}
		
	}
}

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
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
  
  
  public static class TopNReducer
	  extends Reducer<Text,IntWritable,Text,IntWritable> {
	  
	  int[] vals = new int[TOP_N];
	  String[] keys = new String[TOP_N];
	
	public void reduce(Text key, Iterable<IntWritable> values,
	                  Context context
	                  ) throws IOException, InterruptedException {
	 int sum = 0;
	 for (IntWritable val : values) {
	   sum = val.get();
	 }
	 int v = sum;
	 String keyText = key.toString();
	 String k = keyText.split("'='")[0];
	 
	 for (int i=0; i<vals.length;i++) {
			if (v > vals[i]) {
				
				if (i == 0) {
					vals[i] = v;
					keys[i] = k;
				}
				else {
					vals[i-1] = vals[i];
					keys[i-1] = keys[i];
					vals[i] = v;
					keys[i] = k;
				}
				
			}
			else {
				break;
			}
		}
	}
	
	
	//Help from https://www.geeksforgeeks.org/how-to-find-top-n-records-using-mapreduce/
	@Override
    public void cleanup(Context context) throws IOException,
                                       InterruptedException
    {
		for (int i=TOP_N-1; i>=0; i--) {
			context.write(new Text(keys[i]), new IntWritable(vals[i]));
		}
    }
}
  

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    //Count all words. Adopted from my semester project
    Job job2 = Job.getInstance(conf, "word count all");
    job2.setJarByClass(WordCountHW5.class);
    job2.setMapperClass(TokenizerMapperGeneral.class);
    job2.setCombinerClass(IntSumReducer.class);
    job2.setReducerClass(IntSumReducer.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(IntWritable.class);
    FileSystem fs2 = FileSystem.get(conf);
    RemoteIterator<LocatedFileStatus> fileStatusListIterator2 = fs2.listFiles(new Path(args[0]), true);
    while (fileStatusListIterator2.hasNext()) {
    	LocatedFileStatus fileStatus = fileStatusListIterator2.next();
		if (fileStatus.isFile()) {
    		FileInputFormat.addInputPath(job2, fileStatus.getPath());
    	}
    }
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
    boolean job2Done = job2.waitForCompletion(true);
    
    //Get top TOP_N
    Job job3 = Job.getInstance(conf, "word count top TOP_N");
    job3.setJarByClass(WordCountHW5.class);
    job3.setMapperClass(TokenizerMapperTopN.class);
    job3.setReducerClass(TopNReducer.class);
    job3.setOutputKeyClass(Text.class);
    job3.setOutputValueClass(IntWritable.class);
    job3.setNumReduceTasks(1);
    FileSystem fs3 = FileSystem.get(conf);
    RemoteIterator<LocatedFileStatus> fileStatusListIterator3 = fs3.listFiles(new Path(args[1]), true);
    while (fileStatusListIterator3.hasNext()) {
    	LocatedFileStatus fileStatus = fileStatusListIterator3.next();
    	if (fileStatus.toString().contains("_SUCCESS") == false) {
    		if (fileStatus.isFile()) {
        		FileInputFormat.addInputPath(job3, fileStatus.getPath());
        	}
    	}
    }
    FileOutputFormat.setOutputPath(job3, new Path(args[2]));
    boolean job3Done = job3.waitForCompletion(true);
    
    if (job2Done && job3Done) {
    	System.exit(0);
    }
    else {
    	System.exit(1);
    }
  }
}