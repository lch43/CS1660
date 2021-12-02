import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TopN {
	//A lot of this was adopted from the class given code and my term project
	
	static int TOP_N = 5;
	//As a hint for HW-5, please go ahead and use stop-word-list to prevent words like "he", "she", "they", "the", "a", an", "are", you", "of", "is", "and", and "or" from being displayed at the results.
static String[] stopList = {"a", "an", "the", "and", "for", "is", "were", "was", "how", "he", "she", "they", "are","you","of","or", "to", "in", "that", "i", "his", "her", "him", "hers"};
 
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
	
	HashMap<String, Integer> hashM = new HashMap<String, Integer>();
	
	@SuppressWarnings("deprecation")
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
	           		if (hashM.get(str) == null) {
	           			hashM.put(str, new Integer(1));
	           		}
	           		else {
	           			hashM.put(str, new Integer(hashM.get(str).intValue() + 1));
	           		}
	           	}
	           }
	   	}
	   	
	   }
	   //------------------------------------------------
	 }
	}
	
	@Override
    public void cleanup(Context context) throws IOException,
                                       InterruptedException
    {
		String[] topNWords = new String[TOP_N];
		
		for (String s : hashM.keySet()) {
		      Integer kVal = hashM.get(s).intValue();
		      
		      for (int i=0; i<topNWords.length;i++) {
					if (topNWords[i] != null && kVal > hashM.get(topNWords[i]).intValue()) {
						
						if (i == 0) {
							topNWords[i] = s;
						}
						else {
							topNWords[i-1] = topNWords[i];
							topNWords[i] = s;
						}
						
					}
					else if(topNWords[i] == null) {
						if (i == 0) {
							topNWords[i] = s;
						}
						else {
							topNWords[i-1] = topNWords[i];
							topNWords[i] = s;
						}
					}
					else {
						break;
					}
				}
		    }
		
		
		for (int i=TOP_N-1; i>=0; i--) {
			if (topNWords[i] != null && topNWords[i].equals("") == false) {
				context.write(new Text(topNWords[i]), new IntWritable(hashM.get(topNWords[i]).intValue()));
			}
		}
    }
}
  
  
  
  public static class TopNReducer
	  extends Reducer<Text,IntWritable,Text,IntWritable> {
	  
	  HashMap<String, Integer> hashM = new HashMap<String, Integer>();
	
	@SuppressWarnings("deprecation")
	public void reduce(Text key, Iterable<IntWritable> values,
	                  Context context
	                  ) throws IOException, InterruptedException {
	 int sum = 0;
	 for (IntWritable val : values) {
	   sum += val.get();
	 }
	 if (hashM.get(key.toString()) == null) {
		 hashM.put(key.toString(), new Integer(sum));
	 }
	 else {
		 hashM.put(key.toString(), new Integer(hashM.get(key.toString()).intValue() + sum));
	 }
		//context.write(new Text(key), new IntWritable(sum));
	}
	
	
	@Override
    public void cleanup(Context context) throws IOException,
                                       InterruptedException
    {
		String[] topNWords = new String[TOP_N];
		
		for (String s : hashM.keySet()) {
		      Integer kVal = hashM.get(s).intValue();
		      
		      for (int i=0; i<topNWords.length;i++) {
					if (topNWords[i] != null && kVal > hashM.get(topNWords[i]).intValue()) {
						
						if (i == 0) {
							topNWords[i] = s;
						}
						else {
							topNWords[i-1] = topNWords[i];
							topNWords[i] = s;
						}
						
					}
					else if(topNWords[i] == null) {
						if (i == 0) {
							topNWords[i] = s;
						}
						else {
							topNWords[i-1] = topNWords[i];
							topNWords[i] = s;
						}
					}
					else {
						break;
					}
				}
		    }
		
		
		for (int i=TOP_N-1; i>=0; i--) {
			if (topNWords[i] != null && topNWords[i].equals("") == false) {
				context.write(new Text(topNWords[i]), new IntWritable(hashM.get(topNWords[i]).intValue()));
			}
		}
    }
}
  

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count inaccurate");
    job.setJarByClass(TopN.class);
    job.setMapperClass(TokenizerMapperGeneral.class);
    job.setReducerClass(TopNReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    job.setNumReduceTasks(1);
    FileSystem fs2 = FileSystem.get(conf);
    RemoteIterator<LocatedFileStatus> fileStatusListIterator2 = fs2.listFiles(new Path(args[0]), true);
    while (fileStatusListIterator2.hasNext()) {
    	LocatedFileStatus fileStatus = fileStatusListIterator2.next();
		if (fileStatus.isFile()) {
    		FileInputFormat.addInputPath(job, fileStatus.getPath());
    	}
    }
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    boolean jobDone = job.waitForCompletion(true);
   
    if (jobDone) {
    	System.exit(0);
    }
    else {
    	System.exit(1);
    }
  }
}