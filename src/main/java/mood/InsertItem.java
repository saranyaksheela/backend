//snippet-sourcedescription:[PutItem.java demonstrates how to put an item in a DynamoDB table.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
//snippet-sourceauthor:[soo-aws]
/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package mood;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

/**
* Put an item in a DynamoDB table.
*
* Takes the name of the table, a name (primary key value) and a greeting
* (associated with the key value).
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
*/
public class InsertItem
{
    public static void main(String[] args)
    {
        String table_name = "Moods";
        Date date = new Date();
        String name = String.valueOf(date.getDay());
        ArrayList<String[]> extra_fields = new ArrayList<String[]>();

        // any additional args (fields to add to database)?

        System.out.format("Adding \"%s\" to \"%s\"", name, table_name);
        if (extra_fields.size() > 0) {
            System.out.println("Additional fields:");
            for (String[] field : extra_fields) {
                System.out.format("  %s: %s\n", field[0], field[1]);
            }
        }

        HashMap<String,AttributeValue> item_values =
            new HashMap<String,AttributeValue>();

        item_values.put("Date", new AttributeValue(name));

        item_values.put("mood", new AttributeValue("Depressed,Lonely"));
        item_values.put("mood", new AttributeValue("Happy,Energetic"));

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            ddb.putItem(table_name, item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", table_name);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done! Inserting data");
        try {
            TableDescription table_info =
               ddb.describeTable(table_name).getTable();

            if (table_info != null) {
                System.out.format("Table name  : %s\n",
                      table_info.getTableName());
                System.out.format("Table ARN   : %s\n",
                      table_info.getTableArn());
                System.out.format("Status      : %s\n",
                      table_info.getTableStatus());
                System.out.format("Item count  : %d\n",
                      table_info.getItemCount().longValue());
                System.out.format("Size (bytes): %d\n",
                      table_info.getTableSizeBytes().longValue());

                ProvisionedThroughputDescription throughput_info =
                   table_info.getProvisionedThroughput();
                System.out.println("Throughput");
                System.out.format("  Read Capacity : %d\n",
                      throughput_info.getReadCapacityUnits().longValue());
                System.out.format("  Write Capacity: %d\n",
                      throughput_info.getWriteCapacityUnits().longValue());

                List<AttributeDefinition> attributes =
                   table_info.getAttributeDefinitions();
                System.out.println("Attributes");
                for (AttributeDefinition a : attributes) {
                    System.out.format("  %s (%s)\n",
                          a.getAttributeName(), a.getAttributeType());
                }
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("\nDone!");
        System.out.println("Query String!");
       
        try {
        	QueryRequest request = new QueryRequest();
        	 request.setTableName(table_name);
        	 QueryResult result = ddb.query(request);
        	System.out.println("result");
        	System.out.println(result.getItems().get(0));
        } catch (AmazonDynamoDBException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}