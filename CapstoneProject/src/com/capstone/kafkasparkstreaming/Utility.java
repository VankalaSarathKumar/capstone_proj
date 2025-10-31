package com.capstone.kafkasparkstreaming;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Utility {
        
        public static StructType AccountSchema() {
                StructField accountnumberField = DataTypes.createStructField("accountnumber", DataTypes.LongType, false);
                StructField customeridField = DataTypes.createStructField("customerid", DataTypes.LongType, false);
                StructField accounttypeField = DataTypes.createStructField("accounttype", DataTypes.StringType, false);
                StructField branchField = DataTypes.createStructField("branch", DataTypes.StringType, false);
                StructType accSchema=new StructType(new StructField[] {accountnumberField,customeridField,
                		accounttypeField,branchField});
                return accSchema;
        }

}