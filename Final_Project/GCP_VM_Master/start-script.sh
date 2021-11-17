rm -rf MapReduce
rm -rf uploads
rm -rf collectedResultsFile
hadoop fs -rm -r -skipTrash /MapReduce
gsutil cp -r gs://dataproc-staging-us-east1-14405516972-cfyih5gn/MapReduce .
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
cd MapReduce
hadoop com.sun.tools.javac.Main WordCount.java
jar cf  wc.jar  WordCount*.class
cd ..
gsutil cp -r gs://dataproc-staging-us-east1-14405516972-cfyih5gn/uploads .
cd uploads
for f in *.tar.gz; do tar xf "$f"; rm -rf "$f"; done
cd ..
hadoop fs -mkdir /MapReduce
hadoop fs -put uploads /MapReduce
hadoop jar  MapReduce/wc.jar WordCount /MapReduce/uploads /MapReduce/Output
hadoop fs -getmerge /MapReduce/Output collectedResultsFile
cp collectedResultsFile gs://dataproc-staging-us-east1-14405516972-cfyih5gn/