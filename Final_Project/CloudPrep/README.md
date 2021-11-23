1. Upload /uploads folder with contents into your dataproc bucket.
2. Upload WordCount.java to your dataproc bucket.
3. Ensure that the folders InvertedResults and TopNResults do not exist in your bucket.
4. Open a browser window of your cluster using the master node.
5. Run the following commands. Replace BUCKETNAME with the name of your bucket:
   1. export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar;
   2. gsutil cp -r gs://BUCKETNAME/uploads .;
   3. cd uploads;
   4. for f in *.tar.gz; do tar xf "$f"; rm -rf "$f"; done;
   5. cd ..;
   6. hadoop fs -mkdir /MapReduce
   7. hadoop fs -put uploads /MapReduce/uploads;
   8. gsutil cp gs://BUCKETNAME/WordCount.java .;
   9. hadoop com.sun.tools.javac.Main WordCount.java;
   10. jar cf  wc.jar  WordCount*.class;
   11. gsutil cp wc.jar gs://BUCKETNAME/;