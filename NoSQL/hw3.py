import boto3
import csv

bucket_name = 'datacont-hw3-lch43'
region = 'us-east-2'
table_name = 'DataTable'
access=''
if access == '':
    access = input('Please enter the access key. (This is included in the comment on canvas):')
secret=''
if secret == '':
    secret = input('Please enter the secret key. (This is included in the comment on canvas):')

s3 = boto3.resource('s3',
    aws_access_key_id=access,
    aws_secret_access_key=secret)


try:
    s3.create_bucket(Bucket=bucket_name, CreateBucketConfiguration={
        'LocationConstraint': region
    })
except Exception as e:
    print(e)

bucket = s3.Bucket(bucket_name)
bucket_response = bucket.Acl().put(ACL='public-read')

body = open('exp1.csv','rb')

o = s3.Object(bucket_name, 'test').put(Body=body)
upload_response = s3.Object(bucket_name, 'test').Acl().put(ACL='public-read')

dyndb = boto3.resource('dynamodb', 
    region_name=region,
    aws_access_key_id=access,
    aws_secret_access_key=secret)

# The first time that we define a table we use
try:
    table = dyndb.create_table(
        TableName=table_name,
        KeySchema=[
            { 'AttributeName': 'PartitionKey', 'KeyType': 'HASH'},
            { 'AttributeName': 'RowKey', 'KeyType': 'RANGE'}
        ],
        AttributeDefinitions=[
            { 'AttributeName': 'PartitionKey', 'AttributeType': 'S'},
            { 'AttributeName': 'RowKey', 'AttributeType': 'S'}
        ],
        ProvisionedThroughput={
        'ReadCapacityUnits': 5,
        'WriteCapacityUnits': 5
        }
    )
except Exception as e:
    print (e)
    table = dyndb.Table(table_name)

# Wait for table to be created
table.meta.client.get_waiter('table_exists').wait(TableName=table_name)

# print(table.item_count)

with open('experiments.csv', 'r') as csvfile:
    csvf = csv.reader(csvfile, delimiter=',', quotechar='|')
    skipFirst = True
    for item in csvf:
        if skipFirst:
            skipFirst = False
            continue
        print(item)
        body = open(item[4], 'rb')
        s3.Object(bucket_name, item[4]).put(Body=body )
        md = s3.Object(bucket_name, item[4]).Acl().put(ACL='public-read')

        url = "https://s3-"+region+".amazonaws.com/"+bucket_name+"/"+item[4]
        metadata_item = {'PartitionKey': 'Experiments', 'RowKey':item[4], 'Id': item[0], 'Temp': item[1],
            'Conductivity' : item[2], 'Concentration' : item[3], 'url':url}
        try:
            table.put_item(Item=metadata_item)
        except Exception as e:
            print(e)
            print("item may already be there or another failure")

print('Now let\'s search for an item\n')

query_response = table.get_item(
 Key={
 'PartitionKey': 'Experiments',
 'RowKey': 'exp1.csv'
 }
)
item = query_response['Item']
print(item)
