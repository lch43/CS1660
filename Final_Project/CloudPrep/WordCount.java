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

public class WordCount {

static String[] stopList = {"a", "an", "the", "and", "for", "is", "were", "was", "how", "of", "to", "in", "he", "that", "i", "his", "it", "you", "with", "not", "had", "her", "him", "my", "as", "but", "at", "this", "on", "be", "she", "the", "have", "and", "which", "all", "me", "what", "by", "so", "from", "your", "no", "they", "said", "there", "will", "are", "who", "do", "if", "we", "when", "would", "or", "their"};

  public static class TokenizerMapper
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
                    context.write(new Text(((FileSplit) context.getInputSplit()).getPath().toString()+"|"+str), one);
                	}
                }
        	}
        	
        }
        //------------------------------------------------
      }
    }
  }
  
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

	private Text word = new Text();
	private Text word2 = new Text();
	
	
	public void map(Object key, Text value, Context context
	               ) throws IOException, InterruptedException {
//	 StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
//	 while (itr.hasMoreTokens()) {
//	   word.set(itr.nextToken());
//	   String line = word.toString();
//	   context.write(new Text(line.split( ((char) 9)+"")[0]+"-"+line.split( ((char) 9)+"")[1]), new IntWritable(Integer.parseInt(line.split( ((char) 9)+"")[1])));	
//	   }
		String[] lines = value.toString().split("\n");
		for (String line:lines) {
			context.write(new Text(line.split( ((char) 9)+"")[0]+"'='"+line.split( ((char) 9)+"")[1]), new IntWritable(Integer.parseInt(line.split( ((char) 9)+"")[1])));
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
  
  public static class TopNKeyComparator extends WritableComparator {
	  protected TopNKeyComparator() {
		  super(Text.class, true);
	  }
	  
	  @Override
	  public int compare(WritableComparable w1, WritableComparable w2) {
		  Text ip1 = (Text) w1;
		  Text ip2 = (Text) w2;
		  
		  int int1 = Integer.parseInt(w1.toString().split("'='")[1]);
		  int int2 = Integer.parseInt(w2.toString().split("'='")[1]);
		  
		  if (int1 > int2) {
			  return -1;
		  }
		  else if(int2 > int1) {
			  return 1;
		  }
		  else {
			  return 0;
		  }
	  }
  }
  
  public static class TopNReducer
	  extends Reducer<Text,IntWritable,Text,IntWritable> {
	private IntWritable result = new IntWritable();
	
	public void reduce(Text key, Iterable<IntWritable> values,
	                  Context context
	                  ) throws IOException, InterruptedException {
	 int sum = 0;
	 for (IntWritable val : values) {
	   sum = val.get();
	 }
	 result.set(sum);
	 String keyText = key.toString();
	 Text newKey = new Text(keyText.split("'='")[0]);
	 context.write(newKey, result);
	}
}
  

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count inverted");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileSystem fs = FileSystem.get(conf);
    RemoteIterator<LocatedFileStatus> fileStatusListIterator = fs.listFiles(new Path(args[0]), true);
    while (fileStatusListIterator.hasNext()) {
    	LocatedFileStatus fileStatus = fileStatusListIterator.next();
    	if (fileStatus.isFile()) {
    		FileInputFormat.addInputPath(job, fileStatus.getPath());
    	}
    }
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    boolean job1Done = job.waitForCompletion(true);
    
    //Top N Master Count
    Job job2 = Job.getInstance(conf, "word count top n part 1");
    job2.setJarByClass(WordCount.class);
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
    FileOutputFormat.setOutputPath(job2, new Path(args[2]));
    boolean job2Done = job2.waitForCompletion(true);
    
    //Top N Master Results
    Job job3 = Job.getInstance(conf, "word count top n part 2");
    job3.setJarByClass(WordCount.class);
    job3.setMapperClass(TokenizerMapperTopN.class);
    job3.setSortComparatorClass(TopNKeyComparator.class);
    job3.setReducerClass(TopNReducer.class);
    job3.setOutputKeyClass(Text.class);
    job3.setOutputValueClass(IntWritable.class);
    job3.setNumReduceTasks(1);
    FileSystem fs3 = FileSystem.get(conf);
    RemoteIterator<LocatedFileStatus> fileStatusListIterator3 = fs3.listFiles(new Path(args[2]), true);
    while (fileStatusListIterator3.hasNext()) {
    	LocatedFileStatus fileStatus = fileStatusListIterator3.next();
    	if (fileStatus.toString().contains("_SUCCESS") == false) {
    		if (fileStatus.isFile()) {
        		FileInputFormat.addInputPath(job3, fileStatus.getPath());
        	}
    	}
    }
    FileOutputFormat.setOutputPath(job3, new Path(args[3]));
    boolean job3Done = job3.waitForCompletion(true);
    
    if (job1Done && job2Done && job3Done) {
    	System.exit(0);
    }
    else {
    	System.exit(1);
    }
  }
}