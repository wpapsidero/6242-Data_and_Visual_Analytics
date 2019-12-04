// Databricks notebook source
// Q2 [25 pts]: Analyzing a Large Graph with Spark/Scala on Databricks

// STARTER CODE - DO NOT EDIT THIS CELL
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import spark.implicits._

// COMMAND ----------

// STARTER CODE - DO NOT EDIT THIS CELL
// Definfing the data schema
val customSchema = StructType(Array(StructField("answerer", IntegerType, true), StructField("questioner", IntegerType, true),
    StructField("timestamp", LongType, true)))

// COMMAND ----------

// STARTER CODE - YOU CAN LOAD ANY FILE WITH A SIMILAR SYNTAX.
// MAKE SURE THAT YOU REPLACE THE examplegraph.csv WITH THE mathoverflow.csv FILE BEFORE SUBMISSION.
val df = spark.read
   .format("com.databricks.spark.csv")
   .option("header", "false") // Use first line of all files as header
   .option("nullValue", "null")
   .schema(customSchema)
   .load("/FileStore/tables/mathoverflow.csv")
   .withColumn("date", from_unixtime($"timestamp"))
   .drop($"timestamp")

// COMMAND ----------

//display(df)
df.show()

// COMMAND ----------

// PART 1: Remove the pairs where the questioner and the answerer are the same person.

// ENTER THE CODE BELOW
val df1 = df.filter ($"answerer" =!= $"questioner" )
df1.show()


// COMMAND ----------

// PART 2: The top-3 individuals who answered the most number of questions - sorted in descending order - if tie, the one with lower node-id gets listed first : the nodes with the highest out-degrees.

// ENTER THE CODE BELOW
val ACdf1 = df1.groupBy($"answerer").count()
val ACdf2 = ACdf1.sort($"count".desc, $"answerer")
val ACdf3 = ACdf2.limit(3)
val newNames1 = Seq("answerer", "questions_answered" )
val df2Renamed = ACdf3.toDF(newNames1: _*)
df2Renamed.show()

// COMMAND ----------

// PART 3: The top-3 individuals who asked the most number of questions - sorted in descending order - if tie, the one with lower node-id gets listed first : the nodes with the highest in-degree.

// ENTER THE CODE BELOW
val QCdf1 = df1.groupBy($"questioner").count()
val QCdf2 = QCdf1.sort($"count".desc, $"questioner")
val QCdf3 = QCdf2.limit(3)
val newNames = Seq("questioner", "questions_asked" )
val df3Renamed = QCdf3.toDF(newNames: _*)
df3Renamed.show()

// COMMAND ----------

// PART 4: The top-5 most common asker-answerer pairs - sorted in descending order - if tie, the one with lower value node-id in the first column (u->v edge, u value) gets listed first.

// ENTER THE CODE BELOW

val pairsdf = df1.groupBy($"questioner", $"answerer").count()
val pairsdf2 = pairsdf.sort($"count".desc, $"questioner", $"answerer")

val pairsdf3 = pairsdf2.limit(5)
pairsdf3.show()


// COMMAND ----------

// PART 5: Number of interactions (questions asked/answered) over the months of September-2010 to December-2010 (i.e. from September 1, 2010 to December 31, 2010). List the entries by month from September to December.

// Reference: https://www.obstkel.com/blog/spark-sql-date-functions
// Read in the data and extract the month and year from the date column.
// Hint: Check how we extracted the date from the timestamp.

// ENTER THE CODE BELOW
val datedf = df1
            .withColumn("year", year($"date"))
            .withColumn("month", month($"date"))


val p5df = datedf.groupBy("month", "year").agg(expr("count(*) as total_interactions" ))
val part5df= p5df.select($"month", $"total_interactions").where($"month" >=9 and $"year" === 2010).orderBy(asc("month"))
part5df.show()


// COMMAND ----------

// PART 6: List the top-3 individuals with the maximum overall activity, i.e. total questions asked and questions answered.

val p6df = df2Renamed.join(df3Renamed, df2Renamed("answerer") === df3Renamed ("questioner"), "fullouter") 

val actdf = p6df.na.fill(0, Seq("questions_asked"))
                .na.fill(0, Seq("questions_answered"))
                .withColumn("answerer", when ($"answerer".isNull, $"questioner").otherwise($"answerer"))
                .withColumn("questioner", when ($"questioner".isNull, $"answerer").otherwise($"questioner"))
                .withColumn("total_activity", $"questions_asked" + $"questions_answered")

val part6df= actdf.select($"answerer" as ("userID"), $"total_activity").orderBy(desc("total_activity"),asc("userID")).limit(3)
part6df.show

// COMMAND ----------


